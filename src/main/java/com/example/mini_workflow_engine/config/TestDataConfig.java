package com.example.mini_workflow_engine.config;

// Imports our workflow entity
import com.example.mini_workflow_engine.model.WorkflowDefinition;

// Imports our state entity
import com.example.mini_workflow_engine.model.State;

// Imports our transition entity
import com.example.mini_workflow_engine.model.Transition;

// Imports the workflow repository
import com.example.mini_workflow_engine.repository.WorkflowDefinitionRepository;

// Imports Spring configuration annotations
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Imports CommandLineRunner
import org.springframework.boot.CommandLineRunner;


// Tells Spring this class contains configuration
@Configuration
public class TestDataConfig {

    // Creates the demo workflow definition when the application starts.
    // No WorkflowInstance is seeded here anymore, so the first instance
    // created through the API gets ID 1 instead of ID 2.
    @Bean
    public CommandLineRunner createTestData(
            WorkflowDefinitionRepository workflowDefinitionRepository
    ) {

        return args -> {

            // Create a workflow, owned by the demo frontend's owner ID.
            // Real integrators would use their own ownerId instead.
            WorkflowDefinition workflow =
                    new WorkflowDefinition("Job Application", 1, "demo");


            // Create the first state, marked as the workflow's entry point
            State applied =
                    new State("APPLIED", workflow, true);


            // Create the second state
            State underReview =
                    new State("UNDER_REVIEW", workflow);


            // Create the third state
            State approved =
                    new State("APPROVED", workflow);


            // Add the states to the workflow
            workflow.getStates().add(applied);
            workflow.getStates().add(underReview);
            workflow.getStates().add(approved);


            // Create a transition from APPLIED to UNDER_REVIEW
            Transition submitTransition =
                    new Transition(
                            "submit",
                            applied,
                            underReview,
                            workflow
                    );


            // Create a transition from UNDER_REVIEW to APPROVED
            Transition approveTransition =
                    new Transition(
                            "approve",
                            underReview,
                            approved,
                            workflow
                    );


            // Add both transitions to the workflow
            workflow.getTransitions().add(submitTransition);
            workflow.getTransitions().add(approveTransition);


            // Save the workflow
            //
            // Because CascadeType.ALL is used,
            // the states and transitions will also be saved.
            workflowDefinitionRepository.save(workflow);
        };
    }
}
