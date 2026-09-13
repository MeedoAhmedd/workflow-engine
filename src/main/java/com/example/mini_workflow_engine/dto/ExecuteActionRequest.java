package com.example.mini_workflow_engine.dto;

import java.util.Map;

// JSON body for POST /workflow-instances/{id}/execute
// Example: { "action": "decide", "data": { "score": "85" } }
public class ExecuteActionRequest {

    private String action;

    // Optional: data to save on the instance before this action is
    // evaluated, so a guard on this very action can see it (e.g.
    // submitting a review score at the same time as the "decide" action).
    private Map<String, String> data;

    public ExecuteActionRequest() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
