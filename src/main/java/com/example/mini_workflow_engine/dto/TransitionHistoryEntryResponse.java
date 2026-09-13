package com.example.mini_workflow_engine.dto;

import com.example.mini_workflow_engine.model.TransitionHistoryEntry;

import java.time.Instant;

// JSON returned for one entry in a workflow instance's history
// Example:
// { "action": "pay", "fromState": "PENDING", "toState": "PAID", "occurredAt": "2026-09-13T12:00:00Z" }
public class TransitionHistoryEntryResponse {

    private String action;
    private String fromState;
    private String toState;
    private Instant occurredAt;

    public TransitionHistoryEntryResponse() {
    }

    public TransitionHistoryEntryResponse(
            String action,
            String fromState,
            String toState,
            Instant occurredAt
    ) {
        this.action = action;
        this.fromState = fromState;
        this.toState = toState;
        this.occurredAt = occurredAt;
    }

    public static TransitionHistoryEntryResponse from(TransitionHistoryEntry entry) {
        return new TransitionHistoryEntryResponse(
                entry.getAction(),
                entry.getFromState(),
                entry.getToState(),
                entry.getOccurredAt()
        );
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
