package com.example.mini_workflow_engine.controller;

// Imports the JSON shapes this controller accepts and returns
import com.example.mini_workflow_engine.dto.CreateWorkflowInstanceRequest;
import com.example.mini_workflow_engine.dto.ExecuteActionRequest;
import com.example.mini_workflow_engine.dto.TransitionEventResponse;
import com.example.mini_workflow_engine.dto.TransitionHistoryEntryResponse;
import com.example.mini_workflow_engine.dto.WorkflowInstanceResponse;

// Imports the entity this controller works with
import com.example.mini_workflow_engine.model.WorkflowInstance;

// Imports the service that actually does the work
import com.example.mini_workflow_engine.service.WorkflowInstanceService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

// Tells Spring this class handles HTTP requests and returns JSON.
// Despite the name, this is the controller for everything
// instance-related: creating, running, and reading WorkflowInstances —
// not the engine's internal decision logic (that's WorkflowEngineService).
@RestController
public class WorkflowEngineController {

    // The service that contains the actual instance logic — this
    // controller does no business logic of its own, it just translates
    // HTTP <-> service calls.
    private final WorkflowInstanceService workflowInstanceService;

    // Constructor used by Spring to provide the service
    public WorkflowEngineController(
            WorkflowInstanceService workflowInstanceService
    ) {
        this.workflowInstanceService = workflowInstanceService;
    }

    // Handles: POST /workflow-instances
    // Creates a new running instance of an existing workflow.
    @PostMapping("/workflow-instances")
    public WorkflowInstanceResponse createWorkflowInstance(
            @RequestHeader("X-Owner-Id") String ownerId,
            @RequestBody CreateWorkflowInstanceRequest request
    ) {
        WorkflowInstance instance =
                workflowInstanceService.createInstance(
                        request.getWorkflowDefinitionId(),
                        ownerId,
                        request.getExternalReferenceId(),
                        request.getData()
                );

        // The instance's data (InstanceVariable rows) isn't part of the
        // WorkflowInstance object itself, so it has to be fetched
        // separately here and passed into the response builder.
        return WorkflowInstanceResponse.from(
                instance,
                workflowInstanceService.getVariables(instance.getId(), ownerId)
        );
    }

    // Handles: POST /workflow-instances/{instanceId}/execute
    // Runs one action on an existing instance, moving it to its next state.
    @PostMapping("/workflow-instances/{instanceId}/execute")
    public WorkflowInstanceResponse executeWorkflow(
            @RequestHeader("X-Owner-Id") String ownerId,
            // Optional header: which role the caller claims to have, used
            // for role-restricted transitions. "required = false" means
            // requests without this header are still accepted — callerRole
            // just comes through as null.
            @RequestHeader(value = "X-Caller-Role", required = false) String callerRole,
            @PathVariable Long instanceId,
            @RequestBody ExecuteActionRequest request
    ) {
        WorkflowInstance instance =
                workflowInstanceService.executeAction(
                        instanceId,
                        request.getAction(),
                        ownerId,
                        request.getData(),
                        callerRole
                );

        return WorkflowInstanceResponse.from(
                instance,
                workflowInstanceService.getVariables(instanceId, ownerId)
        );
    }

    // Handles: GET /workflow-instances/{instanceId}
    // Reads back one instance's current state and data.
    @GetMapping("/workflow-instances/{instanceId}")
    public WorkflowInstanceResponse getWorkflowInstance(
            @RequestHeader("X-Owner-Id") String ownerId,
            @PathVariable Long instanceId
    ) {
        WorkflowInstance instance =
                workflowInstanceService.getInstance(instanceId, ownerId);

        return WorkflowInstanceResponse.from(
                instance,
                workflowInstanceService.getVariables(instanceId, ownerId)
        );
    }

    // Handles: GET /workflow-instances/by-reference/{externalReferenceId}
    // Same as above, but looks the instance up by the caller's own
    // reference ID instead of our internal instanceId.
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

        return WorkflowInstanceResponse.from(
                instance,
                workflowInstanceService.getVariables(instance.getId(), ownerId)
        );
    }

    // Handles: GET /workflow-instances/{instanceId}/history
    // Returns every action ever executed on this instance, in order.
    @GetMapping("/workflow-instances/{instanceId}/history")
    public List<TransitionHistoryEntryResponse> getWorkflowInstanceHistory(
            @RequestHeader("X-Owner-Id") String ownerId,
            @PathVariable Long instanceId
    ) {
        // getHistory returns a list of TransitionHistoryEntry entities;
        // .stream().map(...).collect(...) converts each one into its
        // JSON-friendly Response form, same pattern used everywhere
        // a list of entities needs to become a list of DTOs.
        return workflowInstanceService.getHistory(instanceId, ownerId).stream()
                .map(TransitionHistoryEntryResponse::from)
                .collect(Collectors.toList());
    }

    // Handles: GET /events
    // Returns every transition event across ALL of this owner's
    // instances (not scoped to one instance) — the system-wide activity
    // feed intended for a future webhook dispatcher to read.
    @GetMapping("/events")
    public List<TransitionEventResponse> getEvents(
            @RequestHeader("X-Owner-Id") String ownerId
    ) {
        return workflowInstanceService.getEvents(ownerId).stream()
                .map(TransitionEventResponse::from)
                .collect(Collectors.toList());
    }
}
