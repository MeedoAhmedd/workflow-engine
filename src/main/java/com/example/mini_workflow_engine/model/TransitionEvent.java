package com.example.mini_workflow_engine.model;

import jakarta.persistence.*;

import java.time.Instant;

// Records that a transition happened, in a shape meant for a future
// webhook/notification dispatcher to read — distinct from
// TransitionHistoryEntry, which is a per-instance human-readable audit
// log. This is a system-wide event feed, scoped by owner, with a
// dispatched flag a future background job could flip once it has
// actually notified someone.
// Tells JPA that this class should become a database table
@Entity
public class TransitionEvent {

    // Primary key of this event row
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // Which owner (integrating application) this event belongs to.
    // Not a @ManyToOne relationship like other models — this table is
    // meant to be read independently by a future dispatcher, so it
    // carries a plain copy of the owner ID rather than a database link.
    private String ownerId;


    // Which instance this event happened on (plain ID, same reasoning as ownerId)
    private Long instanceId;


    // The name of the workflow the instance belongs to, captured at the
    // time of the event (so this row stays meaningful even if the
    // workflow is later renamed)
    private String workflowName;


    // The action that was executed
    // Example: "approve"
    private String action;


    // The state the instance was in before this action
    private String fromState;


    // The state the instance moved into after this action
    private String toState;


    // Exactly when this transition happened
    private Instant occurredAt;


    // Whether a notification/webhook has been sent for this event yet.
    // Always false for now — no dispatcher exists yet to flip it. Kept
    // so a future webhook worker has something to query and update
    // without needing a schema change.
    private boolean dispatched;


    // Empty constructor required by JPA
    public TransitionEvent() {
    }

    // Constructor used when recording an event that just happened.
    // occurredAt is set to "now" and dispatched always starts false.
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


    // Returns this row's own ID
    public Long getId() {
        return id;
    }


    // Returns which owner this event belongs to
    public String getOwnerId() {
        return ownerId;
    }


    // Changes which owner this event belongs to
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }


    // Returns which instance this event happened on
    public Long getInstanceId() {
        return instanceId;
    }


    // Changes which instance this event happened on
    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }


    // Returns the workflow's name at the time of the event
    public String getWorkflowName() {
        return workflowName;
    }


    // Changes the recorded workflow name
    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }


    // Returns the action that was executed
    public String getAction() {
        return action;
    }


    // Changes the recorded action
    public void setAction(String action) {
        this.action = action;
    }


    // Returns the state the instance was in before this action
    public String getFromState() {
        return fromState;
    }


    // Changes the recorded "from" state
    public void setFromState(String fromState) {
        this.fromState = fromState;
    }


    // Returns the state the instance moved into
    public String getToState() {
        return toState;
    }


    // Changes the recorded "to" state
    public void setToState(String toState) {
        this.toState = toState;
    }


    // Returns when this event happened
    public Instant getOccurredAt() {
        return occurredAt;
    }


    // Changes when this event is recorded as having happened
    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }


    // Returns whether a webhook/notification has been sent for this event
    public boolean isDispatched() {
        return dispatched;
    }


    // Marks whether a webhook/notification has been sent for this event
    public void setDispatched(boolean dispatched) {
        this.dispatched = dispatched;
    }
}
