package com.example.mini_workflow_engine.dto;

import com.example.mini_workflow_engine.model.GuardOperator;

// One transition inside a CreateWorkflowRequest.
// fromState/toState reference states by name within the same request.
// guard is optional — when present, this transition only fires if the
// instance's variable named guardVariable compares to guardValue using
// guardOperator (e.g. score > 70).
// Example:
// {
//   "action": "decide",
//   "fromState": "UNDER_REVIEW",
//   "toState": "APPROVED",
//   "guardVariable": "score",
//   "guardOperator": "GREATER_THAN",
//   "guardValue": "70"
// }
public class TransitionRequest {

    // The action that triggers this transition, e.g. "pay"
    private String action;

    // Name of the state this transition starts from (must match a
    // StateRequest.name elsewhere in the same CreateWorkflowRequest)
    private String fromState;

    // Name of the state this transition leads to (same rule as above)
    private String toState;

    // The three guard fields together — either all three are set, or
    // none are (checked in WorkflowService). If set, this transition
    // only fires when the instance's guardVariable compares to
    // guardValue using guardOperator.
    private String guardVariable;
    private GuardOperator guardOperator;
    private String guardValue;

    // Optional: if set, only a caller declaring this exact role
    // (via X-Caller-Role) may execute this transition
    private String requiredRole;

    // Empty constructor Spring uses when converting JSON into this object
    public TransitionRequest() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getFromState() {
        return fromState;
    }

    public void setFromState(String fromState) {
        this.fromState = fromState;
    }

    public String getToState() {
        return toState;
    }

    public void setToState(String toState) {
        this.toState = toState;
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

    public String getRequiredRole() {
        return requiredRole;
    }

    public void setRequiredRole(String requiredRole) {
        this.requiredRole = requiredRole;
    }
}
