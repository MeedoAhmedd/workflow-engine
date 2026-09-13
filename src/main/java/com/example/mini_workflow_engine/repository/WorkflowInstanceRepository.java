package com.example.mini_workflow_engine.repository;

import com.example.mini_workflow_engine.model.WorkflowInstance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkflowInstanceRepository
        extends JpaRepository<WorkflowInstance, Long> {

    // Scopes the lookup to a specific owner, so one integrator can never
    // read or execute another integrator's instance just by guessing an ID.
    Optional<WorkflowInstance> findByIdAndOwnerId(Long id, String ownerId);

    // Lets the calling application look an instance up by its own
    // reference ID (e.g. its own Order row ID) instead of tracking ours.
    Optional<WorkflowInstance> findByOwnerIdAndExternalReferenceId(
            String ownerId,
            String externalReferenceId
    );
}
