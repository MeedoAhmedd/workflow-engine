package com.example.mini_workflow_engine.controller;

// Imports the JSON shapes this controller accepts and returns
import com.example.mini_workflow_engine.dto.CreateWorkflowRequest;
import com.example.mini_workflow_engine.dto.WorkflowDefinitionResponse;

// Imports the entity this controller works with
import com.example.mini_workflow_engine.model.WorkflowDefinition;

// Imports the service that actually does the work
import com.example.mini_workflow_engine.service.WorkflowService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

// Tells Spring this class handles HTTP requests and returns JSON
// (not HTML pages). This controller only handles the "/workflows"
// endpoints — creating and reading workflow templates. The separate
// WorkflowEngineController handles running instances of them.
@RestController
public class WorkflowController {

    // The service that contains the actual create/validate/lookup logic —
    // this controller does no business logic of its own, it just
    // translates HTTP <-> service calls.
    private final WorkflowService workflowService;

    // Constructor used by Spring to provide the service
    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    // Handles: POST /workflows
    // Creates a brand new workflow (states + transitions) from JSON.
    @PostMapping("/workflows")
    public ResponseEntity<WorkflowDefinitionResponse> createWorkflow(
            // Every request must identify which owner/integrator it's for
            @RequestHeader("X-Owner-Id") String ownerId,
            // The JSON body is automatically converted into this object
            @RequestBody CreateWorkflowRequest request
    ) {
        // Delegate all the actual work (validation, building the
        // states/transitions, saving) to the service
        WorkflowDefinition workflow = workflowService.createWorkflow(request, ownerId);

        // Return HTTP 201 Created (the standard status for "something new
        // was made") along with the newly created workflow as JSON
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(WorkflowDefinitionResponse.from(workflow));
    }

    // Handles: GET /workflows/{workflowId}
    // Reads back one workflow's full shape (its states and transitions).
    @GetMapping("/workflows/{workflowId}")
    public WorkflowDefinitionResponse getWorkflow(
            @RequestHeader("X-Owner-Id") String ownerId,
            // {workflowId} in the URL is bound to this parameter
            @PathVariable Long workflowId
    ) {
        WorkflowDefinition workflow = workflowService.getWorkflow(workflowId, ownerId);

        return WorkflowDefinitionResponse.from(workflow);
    }
}
