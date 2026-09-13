package com.example.mini_workflow_engine.dto;

import com.example.mini_workflow_engine.model.InstanceVariable;
import com.example.mini_workflow_engine.model.WorkflowInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// JSON returned for a workflow instance
// Example: { "instanceId": 4, "workflowName": "Job Application", "currentState": "UNDER_REVIEW", "data": { "score": "85" } }
public class WorkflowInstanceResponse {

    private Long instanceId;
    private String workflowName;
    private String currentState;
    private String externalReferenceId;

    // The instance's business data (from InstanceVariable rows), shown
    // as a plain name -> value map rather than a list of objects
    private Map<String, String> data;

    // Empty constructor needed for Spring to build this object when
    // converting it to JSON
    public WorkflowInstanceResponse() {
    }

    public WorkflowInstanceResponse(
            Long instanceId,
            String workflowName,
            String currentState,
            String externalReferenceId,
            Map<String, String> data
    ) {
        this.instanceId = instanceId;
        this.workflowName = workflowName;
        this.currentState = currentState;
        this.externalReferenceId = externalReferenceId;
        this.data = data;
    }

    // Builds the response directly from the entity, with no instance data
    // (used where the caller hasn't fetched variables — data comes back empty).
    public static WorkflowInstanceResponse from(WorkflowInstance instance) {
        return from(instance, List.of());
    }

    // Builds the response including the instance's current data. The
    // loop below converts the flat list of InstanceVariable rows (each
    // one just a name+value pair) into a single name -> value map, which
    // is a friendlier JSON shape than a list of {name, value} objects.
    public static WorkflowInstanceResponse from(
            WorkflowInstance instance,
            List<InstanceVariable> variables
    ) {
        Map<String, String> data = new HashMap<>();
        for (InstanceVariable variable : variables) {
            data.put(variable.getName(), variable.getValue());
        }

        return new WorkflowInstanceResponse(
                instance.getId(),
                instance.getWorkflowDefinition().getName(),
                instance.getCurrentState().getName(),
                instance.getExternalReferenceId(),
                data
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

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
