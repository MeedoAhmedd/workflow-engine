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
@Entity
public class TransitionHistoryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workflow_instance_id")
    private WorkflowInstance instance;

    private String action;

    private String fromState;

    private String toState;

    private Instant occurredAt;

    public TransitionHistoryEntry() {
    }

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

    public Long getId() {
        return id;
    }

    public WorkflowInstance getInstance() {
        return instance;
    }

    public void setInstance(WorkflowInstance instance) {
        this.instance = instance;
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
}
