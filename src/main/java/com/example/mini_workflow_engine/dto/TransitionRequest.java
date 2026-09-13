package com.example.mini_workflow_engine.dto;

// One transition inside a CreateWorkflowRequest.
// fromState/toState reference states by name within the same request.
// Example: { "action": "pay", "fromState": "PENDING", "toState": "PAID" }
public class TransitionRequest {

    private String action;
    private String fromState;
    private String toState;

    public TransitionRequest() {
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
