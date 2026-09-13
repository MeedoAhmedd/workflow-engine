package com.example.mini_workflow_engine.model;

// Imports JPA annotations
import jakarta.persistence.*;


// Tells JPA that this class should become a database table
@Entity
public class State {

    // Primary key of the state
    @Id

    // Automatically generates the state ID
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Name of the state
    // Example: "APPLIED"
    private String name;

    // Marks this state as the entry point of its workflow.
    // Exactly one state per WorkflowDefinition should have this set to true.
    private boolean initial;

    // Many states can belong to ONE workflow
    //
    // Example:
    // Job Application
    //      ↓
    //   APPLIED
    //   REVIEW
    //   INTERVIEW
    //
    @ManyToOne

    // Creates a column in the database
    // that connects this state to its workflow
    @JoinColumn(name = "workflow_definition_id")
    private WorkflowDefinition workflowDefinition;


    // Empty constructor required by JPA
    public State() {
    }


    // Constructor used when creating a state
    public State(String name, WorkflowDefinition workflowDefinition) {
        this.name = name;
        this.workflowDefinition = workflowDefinition;
    }


    // Constructor used when creating a state and immediately
    // marking whether it is the workflow's entry point
    public State(String name, WorkflowDefinition workflowDefinition, boolean initial) {
        this.name = name;
        this.workflowDefinition = workflowDefinition;
        this.initial = initial;
    }


    // Returns the state ID
    public Long getId() {
        return id;
    }


    // Returns the state name
    public String getName() {
        return name;
    }


    // Changes the state name
    public void setName(String name) {
        this.name = name;
    }


    // Returns whether this state is the workflow's entry point
    public boolean isInitial() {
        return initial;
    }


    // Changes whether this state is the workflow's entry point
    public void setInitial(boolean initial) {
        this.initial = initial;
    }


    // Returns the workflow this state belongs to
    public WorkflowDefinition getWorkflowDefinition() {
        return workflowDefinition;
    }


    // Changes the workflow this state belongs to
    public void setWorkflowDefinition(WorkflowDefinition workflowDefinition) {
        this.workflowDefinition = workflowDefinition;
    }
}