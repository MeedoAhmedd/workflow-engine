package com.example.mini_workflow_engine.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mini_workflow_engine.model.WorkflowDefinition;

import java.util.Optional;

public interface WorkflowDefinitionRepository
        extends JpaRepository<WorkflowDefinition, Long> {

    // Scopes the lookup to a specific owner, so one integrator can never
    // read another integrator's workflow just by guessing an ID.
    Optional<WorkflowDefinition> findByIdAndOwnerId(Long id, String ownerId);
}
