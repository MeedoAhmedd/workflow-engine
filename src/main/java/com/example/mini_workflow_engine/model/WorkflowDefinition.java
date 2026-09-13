package com.example.mini_workflow_engine.model;

// Imports all JPA annotations such as @Entity, @Id, @OneToMany, etc.
import jakarta.persistence.*;

// Used to create and work with lists
import java.util.ArrayList;
import java.util.List;


// Tells JPA that this class should become a database table
@Entity
public class WorkflowDefinition {

    // Primary key of the workflow
    @Id

    // Tells the database to automatically generate the ID
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The name of our workflow
    // Example: "Job Application"
    private String name;

    // The version of this workflow
    // Example: 1, 2, 3...
    private int version;

    // Identifies which external application/website this workflow
    // belongs to, so multiple integrators can share one engine deployment
    // without seeing each other's workflows.
    // Example: "shop-abc"
    private String ownerId;


    // One workflow can have MANY states
    //
    // mappedBy = "workflowDefinition" means:
    // The State class owns this relationship
    //
    // cascade = ALL means operations on the workflow
    // can also be applied to its states
    @OneToMany(
            mappedBy = "workflowDefinition",
            cascade = CascadeType.ALL
    )
    private List<State> states = new ArrayList<>();


    // One workflow can have MANY transitions
    //
    // The Transition class owns this relationship
    @OneToMany(
            mappedBy = "workflowDefinition",
            cascade = CascadeType.ALL
    )
    private List<Transition> transitions = new ArrayList<>();


    // Empty constructor required by JPA
    public WorkflowDefinition() {
    }


    // Constructor used when we want to create a workflow
    public WorkflowDefinition(String name, int version) {
        this.name = name;
        this.version = version;
    }


    // Constructor used when we want to create a workflow that
    // belongs to a specific external application/website
    public WorkflowDefinition(String name, int version, String ownerId) {
        this.name = name;
        this.version = version;
        this.ownerId = ownerId;
    }


    // Returns the workflow ID
    public Long getId() {
        return id;
    }


    // Returns the workflow name
    public String getName() {
        return name;
    }


    // Changes the workflow name
    public void setName(String name) {
        this.name = name;
    }


    // Returns the workflow version
    public int getVersion() {
        return version;
    }


    // Changes the workflow version
    public void setVersion(int version) {
        this.version = version;
    }


    // Returns the ID of the external application/website that owns this workflow
    public String getOwnerId() {
        return ownerId;
    }


    // Changes the owning application/website ID
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }


    // Returns all states belonging to this workflow
    public List<State> getStates() {
        return states;
    }


    // Replaces the workflow's list of states
    public void setStates(List<State> states) {
        this.states = states;
    }


    // Returns all transitions belonging to this workflow
    public List<Transition> getTransitions() {
        return transitions;
    }


    // Replaces the workflow's list of transitions
    public void setTransitions(List<Transition> transitions) {
        this.transitions = transitions;
    }
}