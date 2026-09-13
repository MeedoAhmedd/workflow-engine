package com.example.mini_workflow_engine.repository;

// Imports Spring Data's JpaRepository
import org.springframework.data.jpa.repository.JpaRepository;

// Imports our Transition entity
import com.example.mini_workflow_engine.model.Transition;

import java.util.Optional;


// Repository for working with Transition objects
public interface TransitionRepository
        extends JpaRepository<Transition, Long> {

    // Finds the transition (if any) that starts at the given state and
    // matches the given action, case-insensitively. Since a Transition's
    // fromState already belongs to exactly one workflow, this single
    // condition is enough to scope the lookup to the right workflow too —
    // the database does the matching instead of scanning every transition
    // in the system.
    Optional<Transition> findByFromStateIdAndActionIgnoreCase(
            Long fromStateId,
            String action
    );
}
