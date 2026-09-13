package com.example.mini_workflow_engine.model;

// Imports JPA annotations
import jakarta.persistence.*;


// Tells JPA that this class should become a database table
@Entity
public class WorkflowInstance {

    // Primary key of the workflow instance
    @Id

    // Automatically generates the instance ID
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // The workflow definition this instance is following
    //
    // Example:
    // Job Application Workflow
    @ManyToOne
    @JoinColumn(name = "workflow_definition_id")
    private WorkflowDefinition workflowDefinition;


    // The current state of this workflow instance
    //
    // Example:
    // APPLIED
    @ManyToOne
    @JoinColumn(name = "current_state_id")
    private State currentState;

    // Identifies which external application/website this instance
    // belongs to, so multiple integrators can share one engine
    // deployment without seeing each other's instances.
    // Example: "shop-abc"
    private String ownerId;

    // Optional ID the calling application already uses for this process
    // on its own side (e.g. its own Order/Application row ID), so it can
    // look the instance back up without tracking our internal instanceId.
    // Example: "order-1234"
    private String externalReferenceId;


    // Empty constructor required by JPA
    public WorkflowInstance() {
    }


    // Constructor used when creating a workflow instance
    public WorkflowInstance(
            WorkflowDefinition workflowDefinition,
            State currentState
    ) {
        this.workflowDefinition = workflowDefinition;
        this.currentState = currentState;
    }


    // Constructor used when creating a workflow instance that belongs to
    // a specific owner and is linked to the caller's own reference ID
    public WorkflowInstance(
            WorkflowDefinition workflowDefinition,
            State currentState,
            String ownerId,
            String externalReferenceId
    ) {
        this.workflowDefinition = workflowDefinition;
        this.currentState = currentState;
        this.ownerId = ownerId;
        this.externalReferenceId = externalReferenceId;
    }


    // Returns the instance ID
    public Long getId() {
        return id;
    }


    // Returns the workflow definition
    public WorkflowDefinition getWorkflowDefinition() {
        return workflowDefinition;
    }


    // Changes the workflow definition
    public void setWorkflowDefinition(
            WorkflowDefinition workflowDefinition
    ) {
        this.workflowDefinition = workflowDefinition;
    }


    // Returns the current state
    public State getCurrentState() {
        return currentState;
    }


    // Changes the current state
    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }


    // Returns the ID of the external application/website that owns this instance
    public String getOwnerId() {
        return ownerId;
    }


    // Changes the owning application/website ID
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }


    // Returns the caller's own reference ID for this instance, if any
    public String getExternalReferenceId() {
        return externalReferenceId;
    }


    // Changes the caller's own reference ID for this instance
    public void setExternalReferenceId(String externalReferenceId) {
        this.externalReferenceId = externalReferenceId;
    }
}

