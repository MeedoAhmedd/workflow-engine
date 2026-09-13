package com.example.mini_workflow_engine.dto;

// One state inside a CreateWorkflowRequest.
// Example: { "name": "PENDING", "initial": true }
public class StateRequest {

    private String name;
    private boolean initial;

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
