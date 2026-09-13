package com.example.mini_workflow_engine.model;

import jakarta.persistence.*;

// One key/value pair of business data attached to a WorkflowInstance
// (e.g. "score" = "85"). Values are stored as plain strings; guard
// evaluation decides at comparison time whether to treat a value as a
// number or a string, so no schema is imposed on instance data here.
@Entity
public class InstanceVariable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workflow_instance_id")
    private WorkflowInstance instance;

    private String name;

    // Column named explicitly because "value" is a reserved word in H2's
    // SQL grammar and fails table creation if used as a bare column name.
    @Column(name = "variable_value")
    private String value;

    public InstanceVariable() {
    }

    public InstanceVariable(WorkflowInstance instance, String name, String value) {
        this.instance = instance;
        this.name = name;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public WorkflowInstance getInstance() {
        return instance;
    }

    public void setInstance(WorkflowInstance instance) {
        this.instance = instance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
