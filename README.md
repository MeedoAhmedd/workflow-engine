# Workflow Engine

A reusable, integratable workflow engine built with Spring Boot. Any
application can define its own workflow (states + transitions) as JSON,
create instances of it, and drive those instances through actions —
without writing any workflow-specific code.

The "Job Application" workflow (APPLIED → UNDER_REVIEW → APPROVED) is a
demo/test case seeded on startup, not a hardcoded feature of the engine.
Any workflow shape (e.g. order processing, approval chains, onboarding
flows) can be created the same way, purely through the API.

## Stack

- Java 25
- Spring Boot 4.1.1 (Web MVC, Spring Data JPA)
- H2 in-memory database
- Maven

## Running locally

```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`. A demo workflow ("Job
Application", owned by `demo`) is seeded automatically on startup — see
`TestDataConfig`. A minimal HTML/JS frontend for it is served at
`http://localhost:8080/index.html`.

## Core concepts

- **WorkflowDefinition** — a named, versioned workflow template made up
  of states and transitions.
- **State** — one step in a workflow. Exactly one state per workflow is
  marked `initial` — that's where new instances start.
- **Transition** — an `action` that moves a workflow from one state to
  another (e.g. `"submit"` moves `APPLIED` → `UNDER_REVIEW`).
- **WorkflowInstance** — a single running execution of a
  `WorkflowDefinition`, tracking its current state.
- **Owner (`ownerId`)** — every workflow and instance belongs to an
  owner. This is how multiple separate applications can share one
  engine deployment without ever seeing each other's data. It is
  currently a self-declared identifier (no authentication yet) — see
  [Known limitations](#known-limitations).

## Multi-tenancy: the `X-Owner-Id` header

Every endpoint below **requires** an `X-Owner-Id` header identifying the
calling application. A request is only ever able to read or modify
workflows/instances created under that same owner ID — a different
owner gets a `404`-style "not found" response, never another owner's
data.

```
X-Owner-Id: shop-abc
```

## API reference

All request/response bodies are JSON. All error responses share this
shape:

```json
{
  "timestamp": "2026-09-13T12:00:00Z",
  "status": 400,
  "error": "INVALID_REQUEST",
  "message": "Invalid transition: approve"
}
```

### Create a workflow

```
POST /workflows
X-Owner-Id: shop-abc
```

```json
{
  "name": "Order Processing",
  "version": 1,
  "states": [
    { "name": "PENDING", "initial": true },
    { "name": "PAID" },
    { "name": "SHIPPED" },
    { "name": "DELIVERED" }
  ],
  "transitions": [
    { "action": "pay", "fromState": "PENDING", "toState": "PAID" },
    { "action": "ship", "fromState": "PAID", "toState": "SHIPPED" },
    { "action": "deliver", "fromState": "SHIPPED", "toState": "DELIVERED" }
  ]
}
```

Returns `201 Created` with the created workflow (including its new
`id`). Rejected with `400` if: there are zero states, zero or more than
one state is marked `initial`, two states share a name, or a
transition references a `fromState`/`toState` name that doesn't exist.

### Get a workflow

```
GET /workflows/{workflowId}
X-Owner-Id: shop-abc
```

Returns the workflow's full shape (states, transitions, which state is
initial).

### Create a workflow instance

```
POST /workflow-instances
X-Owner-Id: shop-abc
```

```json
{
  "workflowDefinitionId": 2,
  "externalReferenceId": "order-1234"
}
```

`externalReferenceId` is optional — it lets the calling application
store its own ID (e.g. its own Order row's ID) alongside the instance,
so it doesn't have to track the engine's internal `instanceId`
separately.

Returns the new instance, starting at the workflow's initial state:

```json
{
  "instanceId": 1,
  "workflowName": "Order Processing",
  "currentState": "PENDING",
  "externalReferenceId": "order-1234"
}
```

### Execute an action on an instance

```
POST /workflow-instances/{instanceId}/execute
X-Owner-Id: shop-abc
```

```json
{ "action": "pay" }
```

Returns the instance with its updated state, or `400` if the action
isn't valid from the instance's current state.

### Get an instance

```
GET /workflow-instances/{instanceId}
X-Owner-Id: shop-abc
```

### Get an instance by external reference

```
GET /workflow-instances/by-reference/{externalReferenceId}
X-Owner-Id: shop-abc
```

Looks an instance up by the `externalReferenceId` given at creation
time, instead of the engine's internal `instanceId`.

### Get an instance's transition history

```
GET /workflow-instances/{instanceId}/history
X-Owner-Id: shop-abc
```

Returns every action executed on the instance, in order:

```json
[
  { "action": "pay", "fromState": "PENDING", "toState": "PAID", "occurredAt": "2026-09-13T12:00:00Z" },
  { "action": "ship", "fromState": "PAID", "toState": "SHIPPED", "occurredAt": "2026-09-13T12:05:00Z" }
]
```

History is empty immediately after creation and only records actions
that actually executed successfully.

## Known limitations

This is a work in progress. Current gaps, in rough order of what's
being worked on next:

- **No real authentication** — `X-Owner-Id` is self-declared, not
  verified. It provides data isolation between integrators, not
  security against a malicious caller.
- **No permissions/roles** — any caller with a given owner ID can
  execute any action on any of that owner's instances.
- **No guards/conditions** — transitions can't yet be blocked based on
  data (e.g. "only allow `approve` if `score > 70`").
- **No hooks/events** — nothing fires (webhook, notification, etc.)
  when a transition happens, beyond the history log.
- **Frontend is not data-driven** — the demo HTML/JS page still hardcodes
  the Job Application workflow's shape instead of rendering whatever
  workflow it's pointed at.
- **No workflow update/delete endpoints** — workflows can be created
  and read, not modified or removed, once persisted.

## Project structure

```
src/main/java/com/example/mini_workflow_engine/
├── MiniWorkflowEngineApplication.java
├── config/          — startup demo-data seeding
├── controller/      — REST endpoints
├── dto/             — request/response JSON shapes
├── model/           — JPA entities
├── repository/      — Spring Data JPA repositories
└── service/         — business logic / the engine itself
```
