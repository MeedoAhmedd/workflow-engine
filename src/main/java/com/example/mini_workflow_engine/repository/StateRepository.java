package com.example.mini_workflow_engine.repository;

// Imports our State entity
import com.example.mini_workflow_engine.model.State;

// Imports Spring Data JPA repository
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


// Repository used to communicate with the State table
public interface StateRepository extends JpaRepository<State, Long> {

    // Finds the single state marked as the entry point of a workflow.
    // Lets the database find it directly instead of looping through
    // workflow.getStates() in Java.
    Optional<State> findByWorkflowDefinitionIdAndInitialTrue(Long workflowDefinitionId);

    // Used for validation: a workflow must have exactly one initial state.
    List<State> findAllByWorkflowDefinitionIdAndInitialTrue(Long workflowDefinitionId);
}
