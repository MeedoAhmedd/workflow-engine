package com.example.mini_workflow_engine.repository;

// Imports our TransitionHistoryEntry entity
import com.example.mini_workflow_engine.model.TransitionHistoryEntry;

// Imports Spring Data's JpaRepository
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repository for working with TransitionHistoryEntry objects (the
// per-instance log of every action that has ever been executed on it).
public interface TransitionHistoryEntryRepository
        extends JpaRepository<TransitionHistoryEntry, Long> {

    // Returns an instance's full transition history in the order it happened —
    // oldest first, so it reads top-to-bottom like a timeline.
    List<TransitionHistoryEntry> findByInstanceIdOrderByOccurredAtAsc(Long instanceId);
}
