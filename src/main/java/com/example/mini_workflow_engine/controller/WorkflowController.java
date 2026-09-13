package com.example.mini_workflow_engine.controller;

import com.example.mini_workflow_engine.dto.CreateWorkflowRequest;
import com.example.mini_workflow_engine.dto.WorkflowDefinitionResponse;
import com.example.mini_workflow_engine.model.WorkflowDefinition;
import com.example.mini_workflow_engine.service.WorkflowService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping("/workflows")
    public ResponseEntity<WorkflowDefinitionResponse> createWorkflow(
            @RequestHeader("X-Owner-Id") String ownerId,
            @RequestBody CreateWorkflowRequest request
    ) {
        WorkflowDefinition workflow = workflowService.createWorkflow(request, ownerId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(WorkflowDefinitionResponse.from(workflow));
    }

    @GetMapping("/workflows/{workflowId}")
    public WorkflowDefinitionResponse getWorkflow(
            @RequestHeader("X-Owner-Id") String ownerId,
            @PathVariable Long workflowId
    ) {
        WorkflowDefinition workflow = workflowService.getWorkflow(workflowId, ownerId);

        return WorkflowDefinitionResponse.from(workflow);
    }
}
