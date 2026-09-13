package com.example.mini_workflow_engine.dto;

import com.example.mini_workflow_engine.model.State;
import com.example.mini_workflow_engine.model.Transition;
import com.example.mini_workflow_engine.model.WorkflowDefinition;

import java.util.List;
import java.util.stream.Collectors;

// JSON returned for a workflow definition
// Example:
// {
//   "id": 1,
//   "name": "Order Processing",
//   "version": 1,
//   "states": [
//     { "name": "PENDING", "initial": true },
//     { "name": "PAID", "initial": false }
//   ],
//   "transitions": [
//     { "action": "pay", "fromState": "PENDING", "toState": "PAID" }
//   ]
// }
public class WorkflowDefinitionResponse {

    private Long id;
    private String name;
    private int version;
    private List<StateResponse> states;
    private List<TransitionResponse> transitions;

    // Empty constructor needed for Spring to build this object when
    // converting it to JSON
    public WorkflowDefinitionResponse() {
    }

    public WorkflowDefinitionResponse(
            Long id,
            String name,
            int version,
            List<StateResponse> states,
            List<TransitionResponse> transitions
    ) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.states = states;
        this.transitions = transitions;
    }

    // Converts a WorkflowDefinition entity (with its full list of State
    // and Transition objects) into this JSON-friendly response shape.
    // The .stream().map(...).collect(...) pattern here just means:
    // "for every State in the workflow, turn it into a StateResponse,
    // and collect all of them into a new list" — same idea for transitions.
    public static WorkflowDefinitionResponse from(WorkflowDefinition workflow) {
        List<StateResponse> stateResponses = workflow.getStates().stream()
                .map(state -> new StateResponse(state.getName(), state.isInitial()))
                .collect(Collectors.toList());

        List<TransitionResponse> transitionResponses = workflow.getTransitions().stream()
                .map(transition -> new TransitionResponse(
                        transition.getAction(),
                        transition.getFromState().getName(),
                        transition.getToState().getName()
                ))
                .collect(Collectors.toList());

        return new WorkflowDefinitionResponse(
                workflow.getId(),
                workflow.getName(),
                workflow.getVersion(),
                stateResponses,
                transitionResponses
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<StateResponse> getStates() {
        return states;
    }

    public void setStates(List<StateResponse> states) {
        this.states = states;
    }

    public List<TransitionResponse> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<TransitionResponse> transitions) {
        this.transitions = transitions;
    }

    // Nested response for a single state.
    // Defined inside WorkflowDefinitionResponse (rather than its own file)
    // because it only ever makes sense as part of a workflow's response —
    // nothing else needs a standalone "StateResponse".
    public static class StateResponse {
        private String name;
        private boolean initial;

        public StateResponse() {
        }

        public StateResponse(String name, boolean initial) {
            this.name = name;
            this.initial = initial;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isInitial() {
            return initial;
        }

        public void setInitial(boolean initial) {
            this.initial = initial;
        }
    }

    // Nested response for a single transition. Note this only shows
    // action/fromState/toState — guard and requiredRole details from the
    // real Transition entity aren't exposed here yet.
    public static class TransitionResponse {
        private String action;
        private String fromState;
        private String toState;

        public TransitionResponse() {
        }

        public TransitionResponse(String action, String fromState, String toState) {
            this.action = action;
            this.fromState = fromState;
            this.toState = toState;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getFromState() {
            return fromState;
        }

        public void setFromState(String fromState) {
            this.fromState = fromState;
        }

        public String getToState() {
            return toState;
        }

        public void setToState(String toState) {
            this.toState = toState;
        }
    }
}
