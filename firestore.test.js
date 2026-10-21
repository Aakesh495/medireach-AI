const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function nowTimestamp() {
  return new Date(Date.now() - 1000);
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// --- SECURITY BOUNDS & DIRTY DOZEN TESTS ---

test("1. Unauthenticated user cannot read or list health records", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection(`users/${ALICE_UID}/health_records`).get());
});

test("2. Authenticated user can create valid health record", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const ts = nowTimestamp();
  await assertSucceeds(
    aliceDb.collection(`users/${ALICE_UID}/health_records`).doc("rec_1").set({
      userId: ALICE_UID,
      title: "Persistent Fever & Cough Checkup",
      category: "VISIT_PREP",
      details: "Mild fever for 3 days, dry cough at night.",
      facilityOrDoctor: "Bhubaneswar District Hospital",
      questionsForDoctor: "Should I get a CBC blood test or Dengue NS1 test?",
      severityOrPriority: "MODERATE",
      completed: false,
      createdAt: ts,
      updatedAt: ts,
    })
  );
});

test("3. Cross-user access: Bob cannot read Alice's health record", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const ts = nowTimestamp();
  await assertSucceeds(
    aliceDb.collection(`users/${ALICE_UID}/health_records`).doc("rec_1").set({
      userId: ALICE_UID,
      title: "Blood Sugar Report",
      category: "LAB_NOTE",
      details: "HbA1c is 6.1%",
      facilityOrDoctor: "CHC Khordha",
      questionsForDoctor: "Diet modifications needed?",
      severityOrPriority: "ROUTINE",
      completed: false,
      createdAt: ts,
      updatedAt: ts,
    })
  );

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection(`users/${ALICE_UID}/health_records`).doc("rec_1").get());
  await assertFails(bobDb.collection(`users/${ALICE_UID}/health_records`).where("userId", "==", ALICE_UID).get());
});

test("4. Shadow update / ghost field injection is rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const ts = nowTimestamp();
  await assertFails(
    aliceDb.collection(`users/${ALICE_UID}/health_records`).doc("rec_shadow").set({
      userId: ALICE_UID,
      title: "Shadow Record",
      category: "TIMELINE",
      details: "Testing ghost field",
      facilityOrDoctor: "AIIMS",
      questionsForDoctor: "",
      severityOrPriority: "LOW",
      completed: false,
      isAdmin: true,
      createdAt: ts,
      updatedAt: ts,
    })
  );
});

test("5. Authenticated user can create and update valid health reminder", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const docRef = aliceDb.collection(`users/${ALICE_UID}/reminders`).doc("rem_1");
  const ts = nowTimestamp();
  await assertSucceeds(
    docRef.set({
      userId: ALICE_UID,
      title: "Metformin 500mg",
      dosageOrNote: "1 tablet after morning breakfast",
      scheduleTime: "08:30 AM Daily",
      reminderType: "MEDICINE",
      isActive: true,
      takenToday: false,
      createdAt: ts,
      updatedAt: ts,
    })
  );

  await assertSucceeds(
    docRef.update({
      takenToday: true,
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("6. Authenticated user can manage privacy & consent settings", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const docRef = aliceDb.collection(`users/${ALICE_UID}/privacy_settings`).doc("consent_main");
  const ts = nowTimestamp();
  await assertSucceeds(
    docRef.set({
      userId: ALICE_UID,
      aiGuidanceConsent: true,
      emergencyLocationConsent: true,
      anonymousPublicHealthAnalytics: false,
      lowBandwidthMode: true,
      highContrastMode: false,
      preferredLanguage: "HI",
      createdAt: ts,
      updatedAt: ts,
    })
  );
});
