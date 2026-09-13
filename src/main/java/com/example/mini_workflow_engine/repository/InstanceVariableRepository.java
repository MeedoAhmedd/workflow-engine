package com.example.mini_workflow_engine.repository;

import com.example.mini_workflow_engine.model.InstanceVariable;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InstanceVariableRepository
        extends JpaRepository<InstanceVariable, Long> {

    List<InstanceVariable> findByInstanceId(Long instanceId);

    Optional<InstanceVariable> findByInstanceIdAndName(Long instanceId, String name);
}
