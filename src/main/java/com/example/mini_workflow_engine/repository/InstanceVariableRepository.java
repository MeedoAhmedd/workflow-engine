package com.example.mini_workflow_engine.repository;

// Imports our InstanceVariable entity
import com.example.mini_workflow_engine.model.InstanceVariable;

// Imports Spring Data's JpaRepository
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Repository for working with InstanceVariable objects (the business
// data attached to a WorkflowInstance, e.g. "score" = "85").
public interface InstanceVariableRepository
        extends JpaRepository<InstanceVariable, Long> {

    // Returns every variable belonging to one instance — used to build
    // the full set of data a guard might need to check.
    List<InstanceVariable> findByInstanceId(Long instanceId);

    // Finds one specific variable by name on one specific instance.
    // Used when saving data: if a variable with this name already
    // exists, its value is updated instead of creating a duplicate row.
    Optional<InstanceVariable> findByInstanceIdAndName(Long instanceId, String name);
}
