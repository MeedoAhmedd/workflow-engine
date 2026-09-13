package com.example.mini_workflow_engine.model;

import jakarta.persistence.*;

import java.time.Instant;

// Records one executed transition on a WorkflowInstance, so the full
// history of "what happened, when" survives even after the instance
// moves on to another state.
//
// fromState/toState are stored as plain name strings (a snapshot at the
// time the transition ran) rather than @ManyToOne references to State.
// That keeps history readable and stable even if the workflow definition
// is later changed or a State is renamed/removed.
// Tells JPA that this class should become a database table
@Entity
public class TransitionHistoryEntry {

    // Primary key of this history row
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // Which instance this history entry belongs to
    //
    // Many history entries can belong to ONE instance
    // (every action it ever executed gets its own row)
    @ManyToOne
    @JoinColumn(name = "workflow_instance_id")
    private WorkflowInstance instance;


    // The action that was executed
    // Example: "submit"
    private String action;


    // The state the instance was in before this action
    // Example: "APPLIED"
    private String fromState;


    // The state the instance moved into after this action
    // Example: "UNDER_REVIEW"
    private String toState;


    // Exactly when this transition happened
    private Instant occurredAt;


    // Empty constructor required by JPA
    public TransitionHistoryEntry() {
    }


    // Constructor used when recording a transition that just happened.
    // occurredAt is set automatically to "right now".
    public TransitionHistoryEntry(
            WorkflowInstance instance,
            String action,
            String fromState,
            String toState
    ) {
        this.instance = instance;
        this.action = action;
        this.fromState = fromState;
        this.toState = toState;
        this.occurredAt = Instant.now();
    }


    // Returns this row's own ID
    public Long getId() {
        return id;
    }


    // Returns the instance this entry belongs to
    public WorkflowInstance getInstance() {
        return instance;
    }


    // Changes which instance this entry belongs to
    public void setInstance(WorkflowInstance instance) {
        this.instance = instance;
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


    // Returns when this transition happened
    public Instant getOccurredAt() {
        return occurredAt;
    }


    // Changes when this transition is recorded as having happened
    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }
}
