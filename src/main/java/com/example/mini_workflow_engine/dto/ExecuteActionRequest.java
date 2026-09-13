package com.example.mini_workflow_engine.dto;

// JSON body for POST /workflow-instances/{id}/execute
// Example: { "action": "submit" }
public class ExecuteActionRequest {

    private String action;

    public ExecuteActionRequest() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
