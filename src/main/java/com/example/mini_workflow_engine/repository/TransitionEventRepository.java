package com.example.mini_workflow_engine.repository;

import com.example.mini_workflow_engine.model.TransitionEvent;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransitionEventRepository extends JpaRepository<TransitionEvent, Long> {

    List<TransitionEvent> findByOwnerIdOrderByOccurredAtAsc(String ownerId);
}
