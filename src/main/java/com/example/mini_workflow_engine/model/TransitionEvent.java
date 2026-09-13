package com.example.mini_workflow_engine.model;

import jakarta.persistence.*;

import java.time.Instant;

// Records that a transition happened, in a shape meant for a future
// webhook/notification dispatcher to read — distinct from
// TransitionHistoryEntry, which is a per-instance human-readable audit
// log. This is a system-wide event feed, scoped by owner, with a
// dispatched flag a future background job could flip once it has
// actually notified someone.
@Entity
public class TransitionEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ownerId;

    private Long instanceId;

    private String workflowName;

    private String action;

    private String fromState;

    private String toState;

    private Instant occurredAt;

    // Always false for now — no dispatcher exists yet to flip it. Kept
    // so a future webhook worker has something to query and update
    // without needing a schema change.
    private boolean dispatched;

    public TransitionEvent() {
    }

    public TransitionEvent(
            String ownerId,
            Long instanceId,
            String workflowName,
            String action,
            String fromState,
            String toState
    ) {
        this.ownerId = ownerId;
        this.instanceId = instanceId;
        this.workflowName = workflowName;
        this.action = action;
        this.fromState = fromState;
        this.toState = toState;
        this.occurredAt = Instant.now();
        this.dispatched = false;
    }

    public Long getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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
