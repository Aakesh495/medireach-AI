package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.HealthRecord
import com.example.data.model.HealthReminder
import com.example.data.model.PrivacyConsent
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class MediReachRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        FirebaseAuth.getInstance()
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    // --- 1. Health Records (Personal Health Timeline & Doctor Visit Preparation Workspace) ---

    fun observeHealthRecords(userId: String): Flow<List<HealthRecord>> {
        val path = "users/$userId/health_records"
        return db.collection("users").document(userId).collection("health_records")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(HealthRecord::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }.sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
    }

    suspend fun createHealthRecord(
        title: String,
        category: String,
        details: String,
        facilityOrDoctor: String,
        questionsForDoctor: String,
        severityOrPriority: String
    ): Result<String> {
        val uid = requireUserId()
        val recordId = "rec_${UUID.randomUUID().toString().replace("-", "").take(16)}"
        val docRef = db.collection("users").document(uid).collection("health_records").document(recordId)
        val payload = mapOf(
            "userId" to uid,
            "title" to title.trim().take(150),
            "category" to category,
            "details" to details.trim().take(2000),
            "facilityOrDoctor" to facilityOrDoctor.trim().take(150),
            "questionsForDoctor" to questionsForDoctor.trim().take(1000),
            "severityOrPriority" to severityOrPriority,
            "completed" to false,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(recordId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun getHealthRecordById(targetUserId: String, recordId: String): Result<HealthRecord?> {
        val docRef = db.collection("users").document(targetUserId).collection("health_records").document(recordId)
        return try {
            val snap = docRef.get().await()
            Result.success(snap.toObject(HealthRecord::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun toggleHealthRecordCompleted(record: HealthRecord): Result<Unit> {
        val uid = requireUserId()
        require(record.id.isNotBlank()) { "Record ID must not be blank" }
        val docRef = db.collection("users").document(uid).collection("health_records").document(record.id)
        return try {
            docRef.update(
                mapOf(
                    "completed" to !record.completed,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteHealthRecord(recordId: String): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("health_records").document(recordId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    // --- 2. Smart Health Reminders ---

    fun observeReminders(userId: String): Flow<List<HealthReminder>> {
        val path = "users/$userId/reminders"
        return db.collection("users").document(userId).collection("reminders")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(HealthReminder::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                }.sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
    }

    suspend fun createReminder(
        title: String,
        dosageOrNote: String,
        scheduleTime: String,
        reminderType: String
    ): Result<String> {
        val uid = requireUserId()
        val reminderId = "rem_${UUID.randomUUID().toString().replace("-", "").take(16)}"
        val docRef = db.collection("users").document(uid).collection("reminders").document(reminderId)
        val payload = mapOf(
            "userId" to uid,
            "title" to title.trim().take(150),
            "dosageOrNote" to dosageOrNote.trim().take(300),
            "scheduleTime" to scheduleTime.trim().take(60),
            "reminderType" to reminderType,
            "isActive" to true,
            "takenToday" to false,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(reminderId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun toggleReminderTaken(reminder: HealthReminder): Result<Unit> {
        val uid = requireUserId()
        require(reminder.id.isNotBlank()) { "Reminder ID must not be blank" }
        val docRef = db.collection("users").document(uid).collection("reminders").document(reminder.id)
        return try {
            docRef.update(
                mapOf(
                    "takenToday" to !reminder.takenToday,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteReminder(reminderId: String): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("reminders").document(reminderId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    // --- 3. Trust & Privacy Consent Settings ---

    fun observePrivacyConsent(userId: String): Flow<PrivacyConsent?> {
        val path = "users/$userId/privacy_settings/consent_main"
        return db.collection("users").document(userId).collection("privacy_settings")
            .document("consent_main")
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(PrivacyConsent::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                } else {
                    null
                }
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.GET, path)
                throw error
            }
    }

    suspend fun savePrivacyConsent(
        existingConsent: PrivacyConsent?,
        aiGuidanceConsent: Boolean,
        emergencyLocationConsent: Boolean,
        anonymousPublicHealthAnalytics: Boolean,
        lowBandwidthMode: Boolean,
        highContrastMode: Boolean,
        preferredLanguage: String
    ): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("privacy_settings").document("consent_main")
        return try {
            if (existingConsent == null || existingConsent.createdAt == null) {
                val createPayload = mapOf(
                    "userId" to uid,
                    "aiGuidanceConsent" to aiGuidanceConsent,
                    "emergencyLocationConsent" to emergencyLocationConsent,
                    "anonymousPublicHealthAnalytics" to anonymousPublicHealthAnalytics,
                    "lowBandwidthMode" to lowBandwidthMode,
                    "highContrastMode" to highContrastMode,
                    "preferredLanguage" to preferredLanguage,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(createPayload).await()
            } else {
                val updatePayload = mapOf(
                    "aiGuidanceConsent" to aiGuidanceConsent,
                    "emergencyLocationConsent" to emergencyLocationConsent,
                    "anonymousPublicHealthAnalytics" to anonymousPublicHealthAnalytics,
                    "lowBandwidthMode" to lowBandwidthMode,
                    "highContrastMode" to highContrastMode,
                    "preferredLanguage" to preferredLanguage,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(updatePayload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteAllUserData(): Result<Unit> {
        val uid = requireUserId()
        return try {
            val recordsSnap = db.collection("users").document(uid).collection("health_records")
                .whereEqualTo("userId", uid).get().await()
            for (doc in recordsSnap.documents) {
                doc.reference.delete().await()
            }
            val remindersSnap = db.collection("users").document(uid).collection("reminders")
                .whereEqualTo("userId", uid).get().await()
            for (doc in remindersSnap.documents) {
                doc.reference.delete().await()
            }
            val consentRef = db.collection("users").document(uid).collection("privacy_settings").document("consent_main")
            val consentSnap = consentRef.get().await()
            if (consentSnap.exists()) {
                consentRef.delete().await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, "users/$uid")
            Result.failure(e)
        }
    }
}
