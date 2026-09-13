package com.example.mini_workflow_engine.dto;

// JSON body for POST /workflow-instances
// Example: { "workflowDefinitionId": 1, "externalReferenceId": "order-1234" }
public class CreateWorkflowInstanceRequest {

    private Long workflowDefinitionId;

    // Optional: the caller's own ID for this process (e.g. its Order row ID),
    // so it can look the instance back up without tracking our instanceId.
    private String externalReferenceId;

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
}
