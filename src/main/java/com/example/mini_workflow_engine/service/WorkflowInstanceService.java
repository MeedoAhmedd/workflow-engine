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

// Imports Spring's service annotation
import org.springframework.stereotype.Service;

import java.util.List;


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

    // Service used to execute workflow transitions
    private final WorkflowEngineService workflowEngineService;


    // Constructor used by Spring to provide all required dependencies
    public WorkflowInstanceService(
            WorkflowInstanceRepository workflowInstanceRepository,
            WorkflowDefinitionRepository workflowDefinitionRepository,
            StateRepository stateRepository,
            TransitionHistoryEntryRepository transitionHistoryEntryRepository,
            WorkflowEngineService workflowEngineService
    ) {
        this.workflowInstanceRepository = workflowInstanceRepository;
        this.workflowDefinitionRepository = workflowDefinitionRepository;
        this.stateRepository = stateRepository;
        this.transitionHistoryEntryRepository = transitionHistoryEntryRepository;
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
            String externalReferenceId
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
        return workflowInstanceRepository.save(instance);
    }


    // Executes an action on an existing workflow instance
    public WorkflowInstance executeAction(
            Long instanceId,
            String action,
            String ownerId
    ) {

        // Find the workflow instance using its ID, scoped to this owner
        WorkflowInstance instance =
                workflowInstanceRepository.findByIdAndOwnerId(instanceId, ownerId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Workflow instance not found"
                                )
                        );


        // Get the current state of the instance
        State currentState = instance.getCurrentState();


        // Ask the workflow engine to find the next state
        String trimmedAction = action.trim();
        State nextState =
                workflowEngineService.executeTransition(
                        currentState,
                        trimmedAction
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
}
