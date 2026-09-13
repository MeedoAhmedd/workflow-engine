package com.example.mini_workflow_engine.service;

// Tells Spring that this class contains business logic
import org.springframework.stereotype.Service;

// Imports our State entity
import com.example.mini_workflow_engine.model.InstanceVariable;
import com.example.mini_workflow_engine.model.State;

// Imports our Transition entity
import com.example.mini_workflow_engine.model.Transition;

// Imports the repository used to find transitions
import com.example.mini_workflow_engine.repository.TransitionRepository;

import java.util.Collections;
import java.util.List;


// Marks this class as a Spring Service
@Service
public class WorkflowEngineService {

    // Repository used to search for transitions
    private final TransitionRepository transitionRepository;

    // Decides whether a guarded transition's condition holds
    private final GuardEvaluator guardEvaluator;


    // Constructor used by Spring to provide the repository
    public WorkflowEngineService(
            TransitionRepository transitionRepository,
            GuardEvaluator guardEvaluator
    ) {
        this.transitionRepository = transitionRepository;
        this.guardEvaluator = guardEvaluator;
    }


    // Executes an action from the current state, with no instance data
    // and no caller role. Only unguarded, unrestricted transitions can
    // ever match here.
    public State executeTransition(State currentState, String action) {
        return executeTransition(currentState, action, Collections.emptyList(), null);
    }


    // Executes an action from the current state, considering the
    // instance's current variables (for guarded transitions) and the
    // calling user's declared role (for role-restricted transitions).
    public State executeTransition(
            State currentState,
            String action,
            List<InstanceVariable> variables,
            String callerRole
    ) {

        // Remove accidental spaces from the request
        String trimmedAction = action.trim();


        // Ask the database for every transition that starts at the
        // current state and matches this action, instead of loading every
        // transition in the system and filtering them in Java. Usually
        // there's exactly one; guarded transitions allow several to share
        // the same fromState + action.
        List<Transition> candidates =
                transitionRepository.findByFromStateIdAndActionIgnoreCase(
                        currentState.getId(),
                        trimmedAction
                );

        // Among the candidates, pick the first whose guard holds (an
        // unguarded transition always holds). Declaration order decides
        // priority when more than one could match.
        Transition matchingTransition = candidates.stream()
                .filter(candidate -> guardEvaluator.matches(candidate, variables))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid transition: " + trimmedAction
                        )
                );

        // A transition with no required role can be executed by anyone.
        // One that requires a role denies a caller with no role or the
        // wrong role — a missing X-Caller-Role header is treated as "no
        // role", not "check skipped".
        String requiredRole = matchingTransition.getRequiredRole();
        if (requiredRole != null && !requiredRole.equals(callerRole)) {
            throw new ForbiddenActionException(
                    "Action \"" + trimmedAction + "\" requires role \"" + requiredRole + "\""
            );
        }


        // Return the state that the transition leads to
        return matchingTransition.getToState();
    }
}
