package com.example.mini_workflow_engine.dto;

// One state inside a CreateWorkflowRequest.
// Example: { "name": "PENDING", "initial": true }
public class StateRequest {

    // The state's name, e.g. "PENDING"
    private String name;

    // Whether this is the workflow's starting state
    private boolean initial;

    // Empty constructor Spring uses when converting JSON into this object
    public StateRequest() {
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
