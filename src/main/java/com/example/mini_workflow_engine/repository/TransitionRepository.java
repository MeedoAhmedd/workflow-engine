package com.example.mini_workflow_engine.repository;

// Imports Spring Data's JpaRepository
import org.springframework.data.jpa.repository.JpaRepository;

// Imports our Transition entity
import com.example.mini_workflow_engine.model.Transition;

import java.util.List;


// Repository for working with Transition objects
public interface TransitionRepository
        extends JpaRepository<Transition, Long> {

    // Finds every transition that starts at the given state and matches
    // the given action, case-insensitively. Usually this is a single
    // transition, but guarded transitions let several share the same
    // fromState + action, routing to different destinations depending on
    // instance data — so the caller picks the first whose guard matches.
    // Since a Transition's fromState already belongs to exactly one
    // workflow, this single condition is enough to scope the lookup to
    // the right workflow too — the database does the matching instead of
    // scanning every transition in the system.
    List<Transition> findByFromStateIdAndActionIgnoreCase(
            Long fromStateId,
            String action
    );
}
