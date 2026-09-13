package com.example.mini_workflow_engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication is a shortcut for three annotations at once:
// it enables auto-configuration (Spring sets up the database connection,
// web server, etc. based on what's on the classpath), and it tells
// Spring to scan this package and every package under it for classes
// like @Entity, @Service, @RestController, @Repository — which is why
// every class in model/, service/, controller/, repository/, config/
// gets picked up automatically without being registered anywhere by hand.
@SpringBootApplication
public class MiniWorkflowEngineApplication {

	// The actual entry point of the whole program — this is the first
	// method that runs when you start the app. It hands control over to
	// Spring Boot, which then starts the embedded web server (Tomcat),
	// connects to the database, and wires together every @Service,
	// @Repository, and @RestController in the project.
	public static void main(String[] args) {
		SpringApplication.run(MiniWorkflowEngineApplication.class, args);
	}

}
