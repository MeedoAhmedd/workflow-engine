package com.example.mini_workflow_engine.model;

// Comparison operators a Transition's guard can use against an
// instance variable. Kept as a small fixed set (rather than a free-text
// expression) so guards can never execute arbitrary code.
//
// Example: a guard with operator GREATER_THAN and value "70" checks
// whether the instance's variable is greater than 70.
public enum GuardOperator {
    EQUALS,                 // variable == value
    NOT_EQUALS,             // variable != value
    GREATER_THAN,           // variable > value
    LESS_THAN,              // variable < value
    GREATER_THAN_OR_EQUAL,  // variable >= value
    LESS_THAN_OR_EQUAL      // variable <= value
}
