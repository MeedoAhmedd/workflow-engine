package com.example.mini_workflow_engine.repository;

// Imports Spring Data's JpaRepository
import org.springframework.data.jpa.repository.JpaRepository;

// Imports our WorkflowDefinition entity
import com.example.mini_workflow_engine.model.WorkflowDefinition;

import java.util.Optional;

// Repository for working with WorkflowDefinition objects.
// Extending JpaRepository<WorkflowDefinition, Long> automatically gives
// this interface methods like save(), findById(), findAll(), delete()
// for free — no implementation needed, Spring generates it.
public interface WorkflowDefinitionRepository
        extends JpaRepository<WorkflowDefinition, Long> {

    // Finds a workflow by ID, but only if it belongs to the given owner.
    // Scopes the lookup to a specific owner, so one integrator can never
    // read another integrator's workflow just by guessing an ID — if the
    // ID exists but belongs to someone else, this returns empty, exactly
    // as if the ID didn't exist at all.
    Optional<WorkflowDefinition> findByIdAndOwnerId(Long id, String ownerId);
}
