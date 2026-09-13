package com.example.mini_workflow_engine.model;

// Comparison operators a Transition's guard can use against an
// instance variable. Kept as a small fixed set (rather than a free-text
// expression) so guards can never execute arbitrary code.
public enum GuardOperator {
    EQUALS,
    NOT_EQUALS,
    GREATER_THAN,
    LESS_THAN,
    GREATER_THAN_OR_EQUAL,
    LESS_THAN_OR_EQUAL
}
