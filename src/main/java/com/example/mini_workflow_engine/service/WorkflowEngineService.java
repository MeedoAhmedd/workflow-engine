package com.example.mini_workflow_engine.service;

// Tells Spring that this class contains business logic
import org.springframework.stereotype.Service;

// Imports our State entity
import com.example.mini_workflow_engine.model.State;

// Imports our Transition entity
import com.example.mini_workflow_engine.model.Transition;

// Imports the repository used to find transitions
import com.example.mini_workflow_engine.repository.TransitionRepository;


// Marks this class as a Spring Service
@Service
public class WorkflowEngineService {

    // Repository used to search for transitions
    private final TransitionRepository transitionRepository;


    // Constructor used by Spring to provide the repository
    public WorkflowEngineService(
            TransitionRepository transitionRepository
    ) {
        this.transitionRepository = transitionRepository;
    }


    // Executes an action from the current state
    public State executeTransition(
            State currentState,
            String action
    ) {

        // Remove accidental spaces from the request
        String trimmedAction = action.trim();


        // Ask the database directly for the transition that starts at the
        // current state and matches this action, instead of loading every
        // transition in the system and filtering them in Java.
        Transition transition =
                transitionRepository
                        .findByFromStateIdAndActionIgnoreCase(
                                currentState.getId(),
                                trimmedAction
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid transition: " + trimmedAction
                                )
                        );


        // Return the state that the transition leads to
        return transition.getToState();
    }
}
