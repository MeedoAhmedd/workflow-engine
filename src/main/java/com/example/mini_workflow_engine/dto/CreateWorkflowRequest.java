package com.example.mini_workflow_engine.dto;

import java.util.List;

// JSON body for POST /workflows
// Example:
// {
//   "name": "Order Processing",
//   "version": 1,
//   "states": [
//     { "name": "PENDING", "initial": true },
//     { "name": "PAID" },
//     { "name": "SHIPPED" },
//     { "name": "DELIVERED" }
//   ],
//   "transitions": [
//     { "action": "pay", "fromState": "PENDING", "toState": "PAID" },
//     { "action": "ship", "fromState": "PAID", "toState": "SHIPPED" },
//     { "action": "deliver", "fromState": "SHIPPED", "toState": "DELIVERED" }
//   ]
// }
public class CreateWorkflowRequest {

    // The new workflow's name, e.g. "Order Processing"
    private String name;

    // The workflow's version number (not enforced/compared anywhere yet)
    private int version;

    // Every state the workflow should have — must include exactly one
    // with initial = true
    private List<StateRequest> states;

    // Every transition the workflow should have — each one's
    // fromState/toState must match a name in "states" above
    private List<TransitionRequest> transitions;

    // Empty constructor Spring uses when converting JSON into this object
    public CreateWorkflowRequest() {
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

    public List<StateRequest> getStates() {
        return states;
    }

    public void setStates(List<StateRequest> states) {
        this.states = states;
    }

    public List<TransitionRequest> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<TransitionRequest> transitions) {
        this.transitions = transitions;
    }
}
