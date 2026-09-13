package com.example.mini_workflow_engine.repository;

import com.example.mini_workflow_engine.model.TransitionHistoryEntry;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransitionHistoryEntryRepository
        extends JpaRepository<TransitionHistoryEntry, Long> {

    // Returns an instance's full transition history in the order it happened.
    List<TransitionHistoryEntry> findByInstanceIdOrderByOccurredAtAsc(Long instanceId);
}
