package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MediReachRepositoryRuleTest : FirestoreEmulatorTestBase() {

    private fun Throwable.findFirestoreException(): FirebaseFirestoreException? {
        return generateSequence(this) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
    }

    @Test
    fun createAndObserveHealthRecord_authenticatedOwner_succeeds() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val repository = MediReachRepository(firestore, auth)

        val createResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createHealthRecord(
                title = "Fever & Joint Pain Follow-up",
                category = "VISIT_PREP",
                details = "3 days of 101F fever, mild rash.",
                facilityOrDoctor = "AIIMS Bhubaneswar",
                questionsForDoctor = "Do I need a platelet count check?",
                severityOrPriority = "MODERATE"
            )
        }
        assertTrue(createResult.isSuccess)
        val recordId = createResult.getOrThrow()

        val emittedRecords = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeHealthRecords(aliceUid).first { list -> list.any { it.id == recordId } }
        }
        assertTrue(emittedRecords.any { it.id == recordId && it.title == "Fever & Joint Pain Follow-up" })
    }

    @Test
    fun getHealthRecordById_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = MediReachRepository(firestore, auth)
        val recordId = withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.createHealthRecord(
                title = "Private Lab Report",
                category = "LAB_NOTE",
                details = "Thyroid TSH 3.2 mIU/L",
                facilityOrDoctor = "District Hospital",
                questionsForDoctor = "Is dose adjustment required?",
                severityOrPriority = "ROUTINE"
            ).getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = MediReachRepository(firestore, auth)
        val crossResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.getHealthRecordById(aliceUid, recordId)
        }
        assertTrue(crossResult.isFailure)
        val exception = crossResult.exceptionOrNull()?.findFirestoreException()
        assertNotNull(exception)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, exception?.code)
    }

    @Test
    fun observeHealthRecords_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        auth.signOut()
        val repository = MediReachRepository(firestore, auth)

        var caughtException: FirebaseFirestoreException? = null
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeHealthRecords(aliceUid).first()
            }
        } catch (t: Throwable) {
            caughtException = t.findFirestoreException()
        }
        assertNotNull(caughtException)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, caughtException?.code)
    }

    private companion object {
        const val ALICE_EMAIL = "alice.medireach@test.com"
        const val BOB_EMAIL = "bob.medireach@test.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
