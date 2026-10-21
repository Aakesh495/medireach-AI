package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class HealthRecord(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val category: String = "TIMELINE", // TIMELINE, VISIT_PREP, LAB_NOTE, SYMPTOM_LOG
    val details: String = "",
    val facilityOrDoctor: String = "",
    val questionsForDoctor: String = "",
    val severityOrPriority: String = "ROUTINE", // LOW, MODERATE, HIGH, ROUTINE
    val completed: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class HealthReminder(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val dosageOrNote: String = "",
    val scheduleTime: String = "08:00 AM Daily",
    val reminderType: String = "MEDICINE", // MEDICINE, APPOINTMENT, VACCINE, CHECKUP
    val isActive: Boolean = true,
    val takenToday: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class PrivacyConsent(
    @DocumentId val id: String = "",
    val userId: String = "",
    val aiGuidanceConsent: Boolean = true,
    val emergencyLocationConsent: Boolean = true,
    val anonymousPublicHealthAnalytics: Boolean = false,
    val lowBandwidthMode: Boolean = false,
    val highContrastMode: Boolean = false,
    val preferredLanguage: String = "EN", // EN, HI, OR
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

// Curated India-focused reference data for Care Finder, Hospital Capacity, Desert Detector, Govt Schemes & Offline First-Aid
data class HospitalFacility(
    val id: String,
    val name: String,
    val stateAndDistrict: String,
    val facilityType: String, // AIIMS / District Hospital / CHC / PHC / Jan Aushadhi
    val distanceKm: Double,
    val emergencyPhone: String,
    val generalBedsAvailable: Int,
    val generalBedsTotal: Int,
    val icuBedsAvailable: Int,
    val icuBedsTotal: Int,
    val oxygenStatus: String, // "Adequate (98%)", "Moderate (74%)", "Critical Reserve"
    val opdWaitMinutes: Int,
    val ayushmanEmpanelled: Boolean,
    val specialties: List<String>,
    val isUnderservedZone: Boolean = false,
    val nearestReferralKm: Int = 0
)

data class GovtHealthScheme(
    val id: String,
    val name: String,
    val hindiName: String,
    val odiaName: String,
    val coverageAmount: String,
    val targetBeneficiaries: String,
    val keyBenefits: List<String>,
    val requiredDocuments: List<String>,
    val helpline: String,
    val eligibilityCriteria: List<String>
)

data class OfflineHealthGuide(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val titleOr: String,
    val category: String,
    val redFlags: List<String>,
    val actionSteps: List<String>,
    val doNotDo: List<String>,
    val sourceCitation: String
)

data class MedicineSafetyEntry(
    val id: String,
    val genericName: String,
    val commonBrandsIndia: String,
    val janAushadhiPriceApprox: String,
    val primaryUse: String,
    val dosageGuidanceInfo: String,
    val keyWarnings: List<String>,
    val interactions: String,
    val pregnancySafety: String,
    val sourceCitation: String
)

data class PublicHealthAlert(
    val id: String,
    val region: String,
    val title: String,
    val alertLevel: String, // ADVISORY, WATCH, ACTION_NEEDED
    val summary: String,
    val preventionChecklist: List<String>,
    val communityResourceContact: String,
    val source: String
)
