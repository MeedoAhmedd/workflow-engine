package com.example.mini_workflow_engine.model;

// Imports JPA annotations
import jakarta.persistence.*;


// Tells JPA that this class should become a database table
@Entity
public class Transition {

    // Primary key of the transition
    @Id

    // Automatically generates the transition ID
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The action that causes the transition
    //
    // Example:
    // "submit_for_review"
    private String action;


    // The state we are leaving
    //
    // Example:
    // APPLIED
    @ManyToOne
    @JoinColumn(name = "from_state_id")
    private State fromState;


    // The state we are moving to
    //
    // Example:
    // UNDER_REVIEW
    @ManyToOne
    @JoinColumn(name = "to_state_id")
    private State toState;


    // The workflow this transition belongs to
    //
    // Example:
    // Job Application Workflow
    @ManyToOne
    @JoinColumn(name = "workflow_definition_id")
    private WorkflowDefinition workflowDefinition;


    // Optional guard: if set, this transition only fires when the
    // instance's variable named guardVariable compares to guardValue
    // using guardOperator. A transition with no guard (guardVariable is
    // null) always matches, preserving old unguarded-workflow behavior.
    private String guardVariable;

    @Enumerated(EnumType.STRING)
    private GuardOperator guardOperator;

    private String guardValue;


    // Optional: if set, only a caller declaring this exact role
    // (via X-Caller-Role) may execute this transition. A transition with
    // no required role (null) can be executed by anyone with the right
    // owner, preserving old unrestricted-workflow behavior.
    private String requiredRole;


    // Empty constructor required by JPA
    public Transition() {
    }


    // Constructor used when creating a transition
    public Transition(
            String action,
            State fromState,
            State toState,
            WorkflowDefinition workflowDefinition
    ) {
        this.action = action;
        this.fromState = fromState;
        this.toState = toState;
        this.workflowDefinition = workflowDefinition;
    }


    // Constructor used when creating a guarded transition
    public Transition(
            String action,
            State fromState,
            State toState,
            WorkflowDefinition workflowDefinition,
            String guardVariable,
            GuardOperator guardOperator,
            String guardValue
    ) {
        this.action = action;
        this.fromState = fromState;
        this.toState = toState;
        this.workflowDefinition = workflowDefinition;
        this.guardVariable = guardVariable;
        this.guardOperator = guardOperator;
        this.guardValue = guardValue;
    }


    // Returns the transition ID
    public Long getId() {
        return id;
    }


    // Returns the action
    public String getAction() {
        return action;
    }


    // Changes the action
    public void setAction(String action) {
        this.action = action;
    }


    // Returns the state we are coming FROM
    public State getFromState() {
        return fromState;
    }


    // Changes the FROM state
    public void setFromState(State fromState) {
        this.fromState = fromState;
    }


    // Returns the state we are going TO
    public State getToState() {
        return toState;
    }


    // Changes the TO state
    public void setToState(State toState) {
        this.toState = toState;
    }


    // Returns the workflow this transition belongs to
    public WorkflowDefinition getWorkflowDefinition() {
        return workflowDefinition;
    }


    // Changes the workflow this transition belongs to
    public void setWorkflowDefinition(WorkflowDefinition workflowDefinition) {
        this.workflowDefinition = workflowDefinition;
    }


    public String getGuardVariable() {
        return guardVariable;
    }

    public void setGuardVariable(String guardVariable) {
        this.guardVariable = guardVariable;
    }

    public GuardOperator getGuardOperator() {
        return guardOperator;
    }

    public void setGuardOperator(GuardOperator guardOperator) {
        this.guardOperator = guardOperator;
    }

    public String getGuardValue() {
        return guardValue;
    }

    public void setGuardValue(String guardValue) {
        this.guardValue = guardValue;
    }

    // A transition with no guard variable always matches.
    public boolean hasGuard() {
        return guardVariable != null;
    }

    public String getRequiredRole() {
        return requiredRole;
    }

    public void setRequiredRole(String requiredRole) {
        this.requiredRole = requiredRole;
    }
}