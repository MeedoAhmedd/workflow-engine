package com.example.mini_workflow_engine.service;

// Imports the workflow instance entity
import com.example.mini_workflow_engine.model.WorkflowInstance;

// Imports the workflow definition entity
import com.example.mini_workflow_engine.model.WorkflowDefinition;

// Imports the state entity
import com.example.mini_workflow_engine.model.State;

// Imports the workflow instance repository
import com.example.mini_workflow_engine.repository.WorkflowInstanceRepository;

// Imports the workflow definition repository
import com.example.mini_workflow_engine.repository.WorkflowDefinitionRepository;

// Imports the state repository, used to find a workflow's initial state
import com.example.mini_workflow_engine.repository.StateRepository;

// Imports the transition history entity and repository
import com.example.mini_workflow_engine.model.TransitionHistoryEntry;
import com.example.mini_workflow_engine.repository.TransitionHistoryEntryRepository;

// Imports the instance variable entity and repository, used for guard data
import com.example.mini_workflow_engine.model.InstanceVariable;
import com.example.mini_workflow_engine.repository.InstanceVariableRepository;

// Imports the transition event entity and repository, used by future hooks/notifications
import com.example.mini_workflow_engine.model.TransitionEvent;
import com.example.mini_workflow_engine.repository.TransitionEventRepository;

// Imports Spring's service annotation
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


// Tells Spring that this class contains business logic
@Service
public class WorkflowInstanceService {

    // Repository used to find and save workflow instances
    private final WorkflowInstanceRepository workflowInstanceRepository;

    // Repository used to find workflow definitions
    private final WorkflowDefinitionRepository workflowDefinitionRepository;

    // Repository used to find a workflow's initial state
    private final StateRepository stateRepository;

    // Repository used to read and record transition history
    private final TransitionHistoryEntryRepository transitionHistoryEntryRepository;

    // Repository used to read and record instance data used by guards
    private final InstanceVariableRepository instanceVariableRepository;

    // Repository used to record transition events for future hooks/notifications
    private final TransitionEventRepository transitionEventRepository;

    // Service used to execute workflow transitions
    private final WorkflowEngineService workflowEngineService;


    // Constructor used by Spring to provide all required dependencies
    public WorkflowInstanceService(
            WorkflowInstanceRepository workflowInstanceRepository,
            WorkflowDefinitionRepository workflowDefinitionRepository,
            StateRepository stateRepository,
            TransitionHistoryEntryRepository transitionHistoryEntryRepository,
            InstanceVariableRepository instanceVariableRepository,
            TransitionEventRepository transitionEventRepository,
            WorkflowEngineService workflowEngineService
    ) {
        this.workflowInstanceRepository = workflowInstanceRepository;
        this.workflowDefinitionRepository = workflowDefinitionRepository;
        this.stateRepository = stateRepository;
        this.transitionHistoryEntryRepository = transitionHistoryEntryRepository;
        this.instanceVariableRepository = instanceVariableRepository;
        this.transitionEventRepository = transitionEventRepository;
        this.workflowEngineService = workflowEngineService;
    }


    // Finds an instance, scoped to its owner so one integrator can never
    // read or execute another integrator's instance just by guessing an ID.
    public WorkflowInstance getInstance(Long instanceId, String ownerId) {

        return workflowInstanceRepository.findByIdAndOwnerId(instanceId, ownerId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Workflow instance not found"
                        )
                );
    }


    // Finds an instance by the caller's own reference ID
    // (e.g. its own Order row ID) instead of our internal instanceId.
    public WorkflowInstance getInstanceByExternalReference(
            String ownerId,
            String externalReferenceId
    ) {

        return workflowInstanceRepository
                .findByOwnerIdAndExternalReferenceId(ownerId, externalReferenceId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Workflow instance not found"
                        )
                );
    }


    public WorkflowInstance createInstance(
            Long workflowDefinitionId,
            String ownerId,
            String externalReferenceId,
            Map<String, String> data
    ) {

        // Find the workflow definition using its ID, scoped to this owner
        // so an instance can never be created against another owner's workflow.
        WorkflowDefinition workflow =
                workflowDefinitionRepository.findByIdAndOwnerId(workflowDefinitionId, ownerId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Workflow definition not found"
                                )
                        );


        // Find the state explicitly marked as this workflow's entry point.
        // This is where the new instance starts.
        State initialState =
                stateRepository.findByWorkflowDefinitionIdAndInitialTrue(workflowDefinitionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Workflow definition has no initial state configured"
                                )
                        );


        // Create a new workflow instance, tagged with the same owner as
        // its workflow, and linked to the caller's own reference ID (if any).
        WorkflowInstance instance =
                new WorkflowInstance(
                        workflow,
                        initialState,
                        ownerId,
                        externalReferenceId
                );


        // Save the new workflow instance
        WorkflowInstance savedInstance = workflowInstanceRepository.save(instance);


        // Persist any initial data the caller provided, so guarded
        // transitions can be evaluated against it later.
        saveVariables(savedInstance, data);


        return savedInstance;
    }


    // Executes an action on an existing workflow instance. Any data
    // provided here is saved before the transition is evaluated, so a
    // guard can reference a value supplied in this very call (e.g.
    // submitting a review score at the same time as the "decide" action).
    public WorkflowInstance executeAction(
            Long instanceId,
            String action,
            String ownerId,
            Map<String, String> data,
            String callerRole
    ) {

        // Find the workflow instance using its ID, scoped to this owner
        WorkflowInstance instance =
                workflowInstanceRepository.findByIdAndOwnerId(instanceId, ownerId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Workflow instance not found"
                                )
                        );


        // Persist any data provided with this action before evaluating
        // guards, so the new values are visible to them immediately.
        saveVariables(instance, data);
        List<InstanceVariable> variables =
                instanceVariableRepository.findByInstanceId(instance.getId());


        // Get the current state of the instance
        State currentState = instance.getCurrentState();


        // Ask the workflow engine to find the next state, considering this
        // instance's data (for guarded transitions) and the caller's role
        // (for role-restricted transitions). Throws ForbiddenActionException
        // (mapped to HTTP 403) if the caller lacks the required role —
        // that check happens here, before anything is changed or saved.
        String trimmedAction = action.trim();
        State nextState =
                workflowEngineService.executeTransition(
                        currentState,
                        trimmedAction,
                        variables,
                        callerRole
                );


        // Change the instance's current state
        instance.setCurrentState(nextState);


        // Save the updated workflow instance
        workflowInstanceRepository.save(instance);


        // Record this transition in the instance's history, so the full
        // sequence of what happened survives even after the state moves on.
        // This only ever runs after a successful transition above, so
        // history can never contain a transition that didn't actually happen.
        TransitionHistoryEntry historyEntry = new TransitionHistoryEntry(
                instance,
                trimmedAction,
                currentState.getName(),
                nextState.getName()
        );
        transitionHistoryEntryRepository.save(historyEntry);


        // Record a system-wide event too, in a shape a future
        // webhook/notification dispatcher could read and act on.
        TransitionEvent event = new TransitionEvent(
                ownerId,
                instance.getId(),
                instance.getWorkflowDefinition().getName(),
                trimmedAction,
                currentState.getName(),
                nextState.getName()
        );
        transitionEventRepository.save(event);


        // Return the updated instance
        return instance;
    }


    // Returns an instance's full transition history, scoped to its owner
    // so one integrator can never read another integrator's audit trail.
    public List<TransitionHistoryEntry> getHistory(Long instanceId, String ownerId) {

        // Reuse the owner-scoped lookup so a wrong owner gets the same
        // "not found" behavior here as everywhere else, instead of leaking
        // whether an instance with that ID exists at all.
        WorkflowInstance instance = getInstance(instanceId, ownerId);

        return transitionHistoryEntryRepository
                .findByInstanceIdOrderByOccurredAtAsc(instance.getId());
    }


    // Returns an instance's current data, scoped to its owner.
    public List<InstanceVariable> getVariables(Long instanceId, String ownerId) {

        WorkflowInstance instance = getInstance(instanceId, ownerId);

        return instanceVariableRepository.findByInstanceId(instance.getId());
    }


    // Returns every transition event across all of this owner's
    // instances, in the order they happened. A future webhook/notification
    // dispatcher would poll this (or the underlying repository) to know
    // what needs to be sent out.
    public List<TransitionEvent> getEvents(String ownerId) {
        return transitionEventRepository.findByOwnerIdOrderByOccurredAtAsc(ownerId);
    }


    // Creates or updates instance variables. Each key either updates an
    // existing InstanceVariable row (if that name already exists on this
    // instance) or creates a new one — never leaves duplicate rows for
    // the same variable name.
    private void saveVariables(WorkflowInstance instance, Map<String, String> data) {

        if (data == null) {
            return;
        }

        for (Map.Entry<String, String> entry : data.entrySet()) {

            InstanceVariable variable = instanceVariableRepository
                    .findByInstanceIdAndName(instance.getId(), entry.getKey())
                    .orElse(new InstanceVariable(instance, entry.getKey(), null));

            variable.setValue(entry.getValue());
            instanceVariableRepository.save(variable);
        }
    }
}
