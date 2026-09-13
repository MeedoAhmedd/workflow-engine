package com.example.mini_workflow_engine.service;

// Tells Spring that this class contains business logic
import org.springframework.stereotype.Service;

// Imports the DTOs that carry an incoming "create workflow" request
import com.example.mini_workflow_engine.dto.CreateWorkflowRequest;
import com.example.mini_workflow_engine.dto.StateRequest;
import com.example.mini_workflow_engine.dto.TransitionRequest;

// Imports the entities this service builds and saves
import com.example.mini_workflow_engine.model.State;
import com.example.mini_workflow_engine.model.Transition;
import com.example.mini_workflow_engine.model.WorkflowDefinition;

// Imports the repository used to save/find workflows
import com.example.mini_workflow_engine.repository.WorkflowDefinitionRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This service owns creating and reading WorkflowDefinitions —
// the "workflow template" side of the engine, as opposed to
// WorkflowInstanceService which handles running instances of them.
@Service
public class WorkflowService {

    // Repository used to save and find workflow definitions
    private final WorkflowDefinitionRepository workflowDefinitionRepository;

    // Constructor used by Spring to provide the repository
    public WorkflowService(
            WorkflowDefinitionRepository workflowDefinitionRepository
    ) {
        this.workflowDefinitionRepository = workflowDefinitionRepository;
    }

    // Scoped to ownerId so one integrator can never read another
    // integrator's workflow just by guessing an ID.
    public WorkflowDefinition getWorkflow(Long workflowId, String ownerId) {
        return workflowDefinitionRepository.findByIdAndOwnerId(workflowId, ownerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Workflow definition not found")
                );
    }

    // Builds a full workflow (states + transitions) from a JSON request,
    // validates it, and persists it. Nothing invalid is ever saved:
    // every check below runs before workflowDefinitionRepository.save().
    public WorkflowDefinition createWorkflow(CreateWorkflowRequest request, String ownerId) {

        List<StateRequest> stateRequests = request.getStates();
        List<TransitionRequest> transitionRequests = request.getTransitions();

        if (stateRequests == null || stateRequests.isEmpty()) {
            throw new IllegalArgumentException("Workflow must have at least one state");
        }

        WorkflowDefinition workflow =
                new WorkflowDefinition(request.getName(), request.getVersion(), ownerId);

        // Create every state up front and index them by name, so
        // transitions can resolve their fromState/toState references.
        Map<String, State> statesByName = new HashMap<>();
        State initialState = null;

        for (StateRequest stateRequest : stateRequests) {

            String stateName = stateRequest.getName();

            if (stateName == null || stateName.isBlank()) {
                throw new IllegalArgumentException("Every state must have a name");
            }

            if (statesByName.containsKey(stateName)) {
                throw new IllegalArgumentException(
                        "Duplicate state name: " + stateName
                );
            }

            State state = new State(stateName, workflow, stateRequest.isInitial());

            if (stateRequest.isInitial()) {
                if (initialState != null) {
                    throw new IllegalArgumentException(
                            "Workflow must have exactly one initial state, found more than one"
                    );
                }
                initialState = state;
            }

            statesByName.put(stateName, state);
            workflow.getStates().add(state);
        }

        if (initialState == null) {
            throw new IllegalArgumentException(
                    "Workflow must have exactly one initial state, found none"
            );
        }

        // Resolve and create transitions, now that every state exists.
        if (transitionRequests != null) {
            for (TransitionRequest transitionRequest : transitionRequests) {

                String action = transitionRequest.getAction();
                if (action == null || action.isBlank()) {
                    throw new IllegalArgumentException("Every transition must have an action");
                }

                State fromState = statesByName.get(transitionRequest.getFromState());
                if (fromState == null) {
                    throw new IllegalArgumentException(
                            "Transition references unknown fromState: "
                                    + transitionRequest.getFromState()
                    );
                }

                State toState = statesByName.get(transitionRequest.getToState());
                if (toState == null) {
                    throw new IllegalArgumentException(
                            "Transition references unknown toState: "
                                    + transitionRequest.getToState()
                    );
                }

                // A guard needs all three fields together, or none at all —
                // a partially-specified guard is almost certainly a mistake
                // (e.g. forgetting guardValue), so it's rejected rather than
                // silently treated as unguarded.
                boolean hasAnyGuardField =
                        transitionRequest.getGuardVariable() != null
                                || transitionRequest.getGuardOperator() != null
                                || transitionRequest.getGuardValue() != null;
                boolean hasAllGuardFields =
                        transitionRequest.getGuardVariable() != null
                                && transitionRequest.getGuardOperator() != null
                                && transitionRequest.getGuardValue() != null;

                if (hasAnyGuardField && !hasAllGuardFields) {
                    throw new IllegalArgumentException(
                            "A transition guard requires guardVariable, guardOperator, "
                                    + "and guardValue together, or none of them"
                    );
                }

                Transition transition = hasAllGuardFields
                        ? new Transition(
                                action,
                                fromState,
                                toState,
                                workflow,
                                transitionRequest.getGuardVariable(),
                                transitionRequest.getGuardOperator(),
                                transitionRequest.getGuardValue()
                          )
                        : new Transition(action, fromState, toState, workflow);

                transition.setRequiredRole(transitionRequest.getRequiredRole());

                workflow.getTransitions().add(transition);
            }
        }

        // Because CascadeType.ALL is used on WorkflowDefinition's states
        // and transitions, saving the workflow saves the whole graph.
        return workflowDefinitionRepository.save(workflow);
    }
}
