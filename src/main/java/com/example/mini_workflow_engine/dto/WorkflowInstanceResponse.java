package com.example.mini_workflow_engine.dto;

import com.example.mini_workflow_engine.model.WorkflowInstance;

// JSON returned for a workflow instance
// Example: { "instanceId": 4, "workflowName": "Job Application", "currentState": "UNDER_REVIEW" }
public class WorkflowInstanceResponse {

    private Long instanceId;
    private String workflowName;
    private String currentState;
    private String externalReferenceId;

    public WorkflowInstanceResponse() {
    }

    public WorkflowInstanceResponse(
            Long instanceId,
            String workflowName,
            String currentState,
            String externalReferenceId
    ) {
        this.instanceId = instanceId;
        this.workflowName = workflowName;
        this.currentState = currentState;
        this.externalReferenceId = externalReferenceId;
    }

    // Builds the response directly from the entity, so the mapping
    // lives in one place instead of being repeated in every controller method.
    public static WorkflowInstanceResponse from(WorkflowInstance instance) {
        return new WorkflowInstanceResponse(
                instance.getId(),
                instance.getWorkflowDefinition().getName(),
                instance.getCurrentState().getName(),
                instance.getExternalReferenceId()
        );
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public String getExternalReferenceId() {
        return externalReferenceId;
    }

    public void setExternalReferenceId(String externalReferenceId) {
        this.externalReferenceId = externalReferenceId;
    }
}
