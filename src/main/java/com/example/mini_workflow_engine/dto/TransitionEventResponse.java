package com.example.mini_workflow_engine.dto;

import com.example.mini_workflow_engine.model.TransitionEvent;

import java.time.Instant;

// JSON returned for one transition event
public class TransitionEventResponse {

    private Long instanceId;
    private String workflowName;
    private String action;
    private String fromState;
    private String toState;
    private Instant occurredAt;

    // Whether a webhook/notification has been sent for this event yet —
    // always false for now, since no dispatcher exists (see TransitionEvent)
    private boolean dispatched;

    // Empty constructor needed for Spring to build this object when
    // converting it to JSON
    public TransitionEventResponse() {
    }

    public TransitionEventResponse(
            Long instanceId,
            String workflowName,
            String action,
            String fromState,
            String toState,
            Instant occurredAt,
            boolean dispatched
    ) {
        this.instanceId = instanceId;
        this.workflowName = workflowName;
        this.action = action;
        this.fromState = fromState;
        this.toState = toState;
        this.occurredAt = occurredAt;
        this.dispatched = dispatched;
    }

    // Converts one TransitionEvent entity into this response shape
    public static TransitionEventResponse from(TransitionEvent event) {
        return new TransitionEventResponse(
                event.getInstanceId(),
                event.getWorkflowName(),
                event.getAction(),
                event.getFromState(),
                event.getToState(),
                event.getOccurredAt(),
                event.isDispatched()
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getFromState() {
        return fromState;
    }

    public void setFromState(String fromState) {
        this.fromState = fromState;
    }

    public String getToState() {
        return toState;
    }

    public void setToState(String toState) {
        this.toState = toState;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public boolean isDispatched() {
        return dispatched;
    }

    public void setDispatched(boolean dispatched) {
        this.dispatched = dispatched;
    }
}
