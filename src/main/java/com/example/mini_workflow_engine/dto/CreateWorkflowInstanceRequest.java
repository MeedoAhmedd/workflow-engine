package com.example.mini_workflow_engine.dto;

import java.util.Map;

// JSON body for POST /workflow-instances
// Example: { "workflowDefinitionId": 1, "externalReferenceId": "order-1234", "data": { "score": "85" } }
public class CreateWorkflowInstanceRequest {

    private Long workflowDefinitionId;

    // Optional: the caller's own ID for this process (e.g. its Order row ID),
    // so it can look the instance back up without tracking our instanceId.
    private String externalReferenceId;

    // Optional: initial business data for this instance, used by guarded
    // transitions later (e.g. { "score": "85" }).
    private Map<String, String> data;

    public CreateWorkflowInstanceRequest() {
    }

    public Long getWorkflowDefinitionId() {
        return workflowDefinitionId;
    }

    public void setWorkflowDefinitionId(Long workflowDefinitionId) {
        this.workflowDefinitionId = workflowDefinitionId;
    }

    public String getExternalReferenceId() {
        return externalReferenceId;
    }

    public void setExternalReferenceId(String externalReferenceId) {
        this.externalReferenceId = externalReferenceId;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
