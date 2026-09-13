package com.example.mini_workflow_engine.controller;

import com.example.mini_workflow_engine.dto.CreateWorkflowInstanceRequest;
import com.example.mini_workflow_engine.dto.ExecuteActionRequest;
import com.example.mini_workflow_engine.dto.WorkflowInstanceResponse;
import com.example.mini_workflow_engine.model.WorkflowInstance;
import com.example.mini_workflow_engine.service.WorkflowInstanceService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkflowEngineController {

    private final WorkflowInstanceService workflowInstanceService;

    public WorkflowEngineController(
            WorkflowInstanceService workflowInstanceService
    ) {
        this.workflowInstanceService = workflowInstanceService;
    }

    @PostMapping("/workflow-instances")
    public WorkflowInstanceResponse createWorkflowInstance(
            @RequestHeader("X-Owner-Id") String ownerId,
            @RequestBody CreateWorkflowInstanceRequest request
    ) {
        WorkflowInstance instance =
                workflowInstanceService.createInstance(
                        request.getWorkflowDefinitionId(),
                        ownerId,
                        request.getExternalReferenceId()
                );

        return WorkflowInstanceResponse.from(instance);
    }

    @PostMapping("/workflow-instances/{instanceId}/execute")
    public WorkflowInstanceResponse executeWorkflow(
            @RequestHeader("X-Owner-Id") String ownerId,
            @PathVariable Long instanceId,
            @RequestBody ExecuteActionRequest request
    ) {
        WorkflowInstance instance =
                workflowInstanceService.executeAction(
                        instanceId,
                        request.getAction(),
                        ownerId
                );

        return WorkflowInstanceResponse.from(instance);
    }

    @GetMapping("/workflow-instances/{instanceId}")
    public WorkflowInstanceResponse getWorkflowInstance(
            @RequestHeader("X-Owner-Id") String ownerId,
            @PathVariable Long instanceId
    ) {
        WorkflowInstance instance =
                workflowInstanceService.getInstance(instanceId, ownerId);

        return WorkflowInstanceResponse.from(instance);
    }

    @GetMapping("/workflow-instances/by-reference/{externalReferenceId}")
    public WorkflowInstanceResponse getWorkflowInstanceByReference(
            @RequestHeader("X-Owner-Id") String ownerId,
            @PathVariable String externalReferenceId
    ) {
        WorkflowInstance instance =
                workflowInstanceService.getInstanceByExternalReference(
                        ownerId,
                        externalReferenceId
                );

        return WorkflowInstanceResponse.from(instance);
    }
}
