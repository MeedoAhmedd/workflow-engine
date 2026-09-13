package com.example.mini_workflow_engine.model;

import jakarta.persistence.*;

// One key/value pair of business data attached to a WorkflowInstance
// (e.g. "score" = "85"). Values are stored as plain strings; guard
// evaluation decides at comparison time whether to treat a value as a
// number or a string, so no schema is imposed on instance data here.
// Tells JPA that this class should become a database table
@Entity
public class InstanceVariable {

    // Primary key of this variable row
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // Which instance this piece of data belongs to
    //
    // Many variables can belong to ONE instance
    // (e.g. an instance can have both "score" and "region")
    @ManyToOne
    @JoinColumn(name = "workflow_instance_id")
    private WorkflowInstance instance;


    // The variable's name
    // Example: "score"
    private String name;


    // The variable's value, always stored as text
    // Example: "85"
    //
    // Column named explicitly because "value" is a reserved word in H2's
    // SQL grammar and fails table creation if used as a bare column name.
    @Column(name = "variable_value")
    private String value;


    // Empty constructor required by JPA
    public InstanceVariable() {
    }


    // Constructor used when creating or updating a variable
    public InstanceVariable(WorkflowInstance instance, String name, String value) {
        this.instance = instance;
        this.name = name;
        this.value = value;
    }


    // Returns this row's own ID
    public Long getId() {
        return id;
    }


    // Returns the instance this variable belongs to
    public WorkflowInstance getInstance() {
        return instance;
    }


    // Changes which instance this variable belongs to
    public void setInstance(WorkflowInstance instance) {
        this.instance = instance;
    }


    // Returns the variable's name
    public String getName() {
        return name;
    }


    // Changes the variable's name
    public void setName(String name) {
        this.name = name;
    }


    // Returns the variable's value
    public String getValue() {
        return value;
    }


    // Changes the variable's value
    public void setValue(String value) {
        this.value = value;
    }
}
