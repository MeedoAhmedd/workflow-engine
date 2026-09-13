package com.example.mini_workflow_engine.service;

import com.example.mini_workflow_engine.model.GuardOperator;
import com.example.mini_workflow_engine.model.InstanceVariable;
import com.example.mini_workflow_engine.model.Transition;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

// Decides whether a Transition's guard (if any) holds true against an
// instance's current variables. Kept separate from WorkflowEngineService
// so transition lookup and guard comparison logic don't get tangled.
@Component
public class GuardEvaluator {

    // A transition with no guard always matches.
    public boolean matches(Transition transition, List<InstanceVariable> variables) {

        if (!transition.hasGuard()) {
            return true;
        }

        Optional<InstanceVariable> variable = variables.stream()
                .filter(v -> v.getName().equals(transition.getGuardVariable()))
                .findFirst();

        if (variable.isEmpty()) {
            // The guard references a variable the instance doesn't have.
            // Treat as not matching rather than throwing, so a workflow
            // with optional variables doesn't blow up on every action.
            return false;
        }

        return compare(
                variable.get().getValue(),
                transition.getGuardOperator(),
                transition.getGuardValue()
        );
    }

    private boolean compare(String actualValue, GuardOperator operator, String expectedValue) {

        // Try numeric comparison first (needed for >, <, >=, <=).
        // Fall back to string comparison (only meaningful for ==, !=)
        // if either side isn't a valid number.
        Double actualNumber = tryParseNumber(actualValue);
        Double expectedNumber = tryParseNumber(expectedValue);

        if (actualNumber != null && expectedNumber != null) {
            int comparison = actualNumber.compareTo(expectedNumber);
            return switch (operator) {
                case EQUALS -> comparison == 0;
                case NOT_EQUALS -> comparison != 0;
                case GREATER_THAN -> comparison > 0;
                case LESS_THAN -> comparison < 0;
                case GREATER_THAN_OR_EQUAL -> comparison >= 0;
                case LESS_THAN_OR_EQUAL -> comparison <= 0;
            };
        }

        return switch (operator) {
            case EQUALS -> actualValue.equals(expectedValue);
            case NOT_EQUALS -> !actualValue.equals(expectedValue);
            default -> throw new IllegalArgumentException(
                    "Guard operator " + operator + " requires numeric values, but got: "
                            + actualValue + ", " + expectedValue
            );
        };
    }

    private Double tryParseNumber(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
