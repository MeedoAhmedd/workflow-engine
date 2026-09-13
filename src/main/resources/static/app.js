// Base URL of the Spring Boot backend. Empty string = same origin
// (the page is served by Spring Boot itself), so requests go to
// http://localhost:8080/workflow-instances etc.
const API_BASE = "";

// The workflow definition ID for our test workflow ("Job Application").
const WORKFLOW_DEFINITION_ID = 1;

// Identifies this frontend as an "owner" of its workflows/instances to the
// engine. Every request must send this so the engine knows whose data to
// read/write; a different integrator would use a different owner ID.
const OWNER_ID = "demo";

// Ordered list of states so we can render progression.
const STATE_ORDER = ["APPLIED", "UNDER_REVIEW", "APPROVED"];

// Holds the ID of the instance we are currently working with.
let currentInstanceId = null;

// --- Element references -------------------------------------------------
const createBtn = document.getElementById("createBtn");
const submitBtn = document.getElementById("submitBtn");
const approveBtn = document.getElementById("approveBtn");
const instanceIdEl = document.getElementById("instanceId");
const currentStateEl = document.getElementById("currentState");
const statusEl = document.getElementById("status");
const progressSteps = document.querySelectorAll("#progress .step");

// --- Helpers ----------------------------------------------------------

// Show a message in the status area.
function setStatus(message, type) {
  statusEl.textContent = message;
  statusEl.className = "status " + (type || "status-idle");
}

// Update the ID / state display and the progression diagram.
function render(instanceId, state) {
  if (instanceId !== null && instanceId !== undefined) {
    currentInstanceId = instanceId;
    instanceIdEl.textContent = instanceId;
  }
  if (state) {
    currentStateEl.textContent = state;
    updateProgress(state);
    updateButtons(state);
  }
}

// Highlight the current state and mark earlier states as done.
function updateProgress(state) {
  const currentIndex = STATE_ORDER.indexOf(state);
  progressSteps.forEach((step) => {
    const stepIndex = STATE_ORDER.indexOf(step.dataset.state);
    step.classList.remove("active", "done");
    if (stepIndex < currentIndex) {
      step.classList.add("done");
    } else if (stepIndex === currentIndex) {
      step.classList.add("active");
    }
  });
}

// Enable only the actions that make sense for the current state.
function updateButtons(state) {
  submitBtn.disabled = state !== "APPLIED";
  approveBtn.disabled = state !== "UNDER_REVIEW";
}

// Read the body of a failed response and surface the backend's
// structured error message: { "error": "...", "message": "..." }
async function readError(response) {
  try {
    const body = await response.json();
    if (body && body.message) return body.message;
  } catch (e) {
    // not JSON, fall through
  }
  return "HTTP " + response.status;
}

// --- API calls ------------------------------------------------------

// POST /workflow-instances  with body: { "workflowDefinitionId": 1 }
async function createInstance() {
  createBtn.disabled = true;
  setStatus("Creating workflow instance...", "status-idle");
  try {
    const response = await fetch(API_BASE + "/workflow-instances", {
      method: "POST",
      headers: { "Content-Type": "application/json", "X-Owner-Id": OWNER_ID },
      body: JSON.stringify({ workflowDefinitionId: WORKFLOW_DEFINITION_ID }),
    });

    if (!response.ok) {
      setStatus("Error: " + (await readError(response)), "status-err");
      return;
    }

    const data = await response.json();
    render(data.instanceId, data.currentState);
    setStatus("Instance " + data.instanceId + " created. State: " + data.currentState, "status-ok");
  } catch (err) {
    setStatus("Network error: " + err.message, "status-err");
  } finally {
    createBtn.disabled = false;
  }
}

// POST /workflow-instances/{id}/execute  with body: { "action": "submit" | "approve" }
async function executeAction(action) {
  if (currentInstanceId === null) {
    setStatus("Create an instance first.", "status-err");
    return;
  }

  submitBtn.disabled = true;
  approveBtn.disabled = true;
  setStatus('Executing "' + action + '"...', "status-idle");

  try {
    const response = await fetch(
      API_BASE + "/workflow-instances/" + currentInstanceId + "/execute",
      {
        method: "POST",
        headers: { "Content-Type": "application/json", "X-Owner-Id": OWNER_ID },
        body: JSON.stringify({ action: action }),
      }
    );

    if (!response.ok) {
      setStatus("Error: " + (await readError(response)), "status-err");
      // Re-enable buttons based on the state we still believe we're in.
      updateButtons(currentStateEl.textContent);
      return;
    }

    const data = await response.json();
    render(currentInstanceId, data.currentState);
    setStatus('Action "' + action + '" succeeded. New state: ' + data.currentState, "status-ok");
  } catch (err) {
    setStatus("Network error: " + err.message, "status-err");
    updateButtons(currentStateEl.textContent);
  }
}

// --- Wiring -----------------------------------------------------------
createBtn.addEventListener("click", createInstance);
submitBtn.addEventListener("click", () => executeAction("submit"));
approveBtn.addEventListener("click", () => executeAction("approve"));
