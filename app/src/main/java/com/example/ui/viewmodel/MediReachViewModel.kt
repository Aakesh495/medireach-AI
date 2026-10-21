package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.IndiaHealthcareCatalog
import com.example.data.model.HealthRecord
import com.example.data.model.HealthReminder
import com.example.data.model.PrivacyConsent
import com.example.data.remote.AiToolMode
import com.example.data.remote.GeminiHealthService
import com.example.data.repository.MediReachRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

data class AiExchange(
    val id: Long = System.currentTimeMillis(),
    val mode: AiToolMode,
    val prompt: String,
    val response: String,
    val languageCode: String
)

class MediReachViewModel(
    private val repository: MediReachRepository,
    private val geminiService: GeminiHealthService,
    private val currentUserId: String
) : ViewModel() {

    val healthRecordsState: StateFlow<UiState<List<HealthRecord>>> =
        repository.observeHealthRecords(currentUserId)
            .map<List<HealthRecord>, UiState<List<HealthRecord>>> { UiState.Success(it) }
            .catch { error ->
                Log.w(TAG, "Error observing health records", error)
                emit(UiState.Error(error.message ?: "Failed to load health records"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    val remindersState: StateFlow<UiState<List<HealthReminder>>> =
        repository.observeReminders(currentUserId)
            .map<List<HealthReminder>, UiState<List<HealthReminder>>> { UiState.Success(it) }
            .catch { error ->
                Log.w(TAG, "Error observing reminders", error)
                emit(UiState.Error(error.message ?: "Failed to load reminders"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    val privacyConsentState: StateFlow<UiState<PrivacyConsent?>> =
        repository.observePrivacyConsent(currentUserId)
            .map<PrivacyConsent?, UiState<PrivacyConsent?>> { UiState.Success(it) }
            .catch { error ->
                Log.w(TAG, "Error observing privacy consent", error)
                emit(UiState.Error(error.message ?: "Failed to load privacy settings"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    private val _selectedLanguage = MutableStateFlow("EN") // EN, HI, OR
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _lowBandwidthMode = MutableStateFlow(false)
    val lowBandwidthMode: StateFlow<Boolean> = _lowBandwidthMode.asStateFlow()

    private val _highContrastMode = MutableStateFlow(false)
    val highContrastMode: StateFlow<Boolean> = _highContrastMode.asStateFlow()

    private val _aiToolMode = MutableStateFlow(AiToolMode.SYMPTOM_GUIDE)
    val aiToolMode: StateFlow<AiToolMode> = _aiToolMode.asStateFlow()

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    private val _aiHistory = MutableStateFlow<List<AiExchange>>(emptyList())
    val aiHistory: StateFlow<List<AiExchange>> = _aiHistory.asStateFlow()

    private val _statusBannerMessage = MutableStateFlow<String?>(null)
    val statusBannerMessage: StateFlow<String?> = _statusBannerMessage.asStateFlow()

    val hospitals = IndiaHealthcareCatalog.hospitals
    val schemes = IndiaHealthcareCatalog.governmentSchemes
    val offlineGuides = IndiaHealthcareCatalog.offlineGuides
    val medicineSafetyList = IndiaHealthcareCatalog.medicineSafetyList
    val publicHealthAlerts = IndiaHealthcareCatalog.publicHealthAlerts

    init {
        viewModelScope.launch {
            privacyConsentState.collect { state ->
                if (state is UiState.Success && state.data != null) {
                    _selectedLanguage.value = state.data.preferredLanguage
                    _lowBandwidthMode.value = state.data.lowBandwidthMode
                    _highContrastMode.value = state.data.highContrastMode
                }
            }
        }
    }

    fun clearBanner() {
        _statusBannerMessage.value = null
    }

    fun setAiToolMode(mode: AiToolMode) {
        _aiToolMode.value = mode
    }

    fun setLanguage(lang: String) {
        _selectedLanguage.value = lang
        persistPreferences(preferredLanguage = lang)
    }

    fun toggleLowBandwidth(enabled: Boolean) {
        _lowBandwidthMode.value = enabled
        persistPreferences(lowBandwidthMode = enabled)
    }

    fun toggleHighContrast(enabled: Boolean) {
        _highContrastMode.value = enabled
        persistPreferences(highContrastMode = enabled)
    }

    fun updateConsentSettings(
        aiGuidanceConsent: Boolean,
        emergencyLocationConsent: Boolean,
        anonymousPublicHealthAnalytics: Boolean,
        lowBandwidthMode: Boolean,
        highContrastMode: Boolean,
        preferredLanguage: String
    ) {
        _lowBandwidthMode.value = lowBandwidthMode
        _highContrastMode.value = highContrastMode
        _selectedLanguage.value = preferredLanguage

        val currentConsent = (privacyConsentState.value as? UiState.Success)?.data
        viewModelScope.launch {
            val result = repository.savePrivacyConsent(
                existingConsent = currentConsent,
                aiGuidanceConsent = aiGuidanceConsent,
                emergencyLocationConsent = emergencyLocationConsent,
                anonymousPublicHealthAnalytics = anonymousPublicHealthAnalytics,
                lowBandwidthMode = lowBandwidthMode,
                highContrastMode = highContrastMode,
                preferredLanguage = preferredLanguage
            )
            if (result.isSuccess) {
                _statusBannerMessage.value = "Privacy & accessibility settings synced to cloud."
            } else {
                _statusBannerMessage.value = "Could not save settings: ${result.exceptionOrNull()?.localizedMessage}"
            }
        }
    }

    private fun persistPreferences(
        preferredLanguage: String = _selectedLanguage.value,
        lowBandwidthMode: Boolean = _lowBandwidthMode.value,
        highContrastMode: Boolean = _highContrastMode.value
    ) {
        val currentConsent = (privacyConsentState.value as? UiState.Success)?.data
        viewModelScope.launch {
            repository.savePrivacyConsent(
                existingConsent = currentConsent,
                aiGuidanceConsent = currentConsent?.aiGuidanceConsent ?: true,
                emergencyLocationConsent = currentConsent?.emergencyLocationConsent ?: true,
                anonymousPublicHealthAnalytics = currentConsent?.anonymousPublicHealthAnalytics ?: false,
                lowBandwidthMode = lowBandwidthMode,
                highContrastMode = highContrastMode,
                preferredLanguage = preferredLanguage
            )
        }
    }

    fun submitAiQuery(userQuery: String) {
        val trimmed = userQuery.trim()
        if (trimmed.isEmpty()) return

        val currentConsent = (privacyConsentState.value as? UiState.Success)?.data
        if (currentConsent != null && !currentConsent.aiGuidanceConsent) {
            _statusBannerMessage.value = "AI Health Guidance is paused in Trust & Privacy Center. Enable AI Consent to proceed."
            return
        }

        val mode = _aiToolMode.value
        val lang = _selectedLanguage.value
        _aiLoading.value = true
        viewModelScope.launch {
            val answer = geminiService.askHealthAssistant(mode, trimmed, lang)
            val exchange = AiExchange(
                mode = mode,
                prompt = trimmed,
                response = answer,
                languageCode = lang
            )
            _aiHistory.value = listOf(exchange) + _aiHistory.value
            _aiLoading.value = false
        }
    }

    fun addHealthRecord(
        title: String,
        category: String,
        details: String,
        facilityOrDoctor: String,
        questionsForDoctor: String,
        severityOrPriority: String
    ) {
        if (title.isBlank() || details.isBlank()) {
            _statusBannerMessage.value = "Please enter both a title and clinical notes."
            return
        }
        viewModelScope.launch {
            val res = repository.createHealthRecord(
                title = title,
                category = category,
                details = details,
                facilityOrDoctor = facilityOrDoctor,
                questionsForDoctor = questionsForDoctor,
                severityOrPriority = severityOrPriority
            )
            if (res.isSuccess) {
                _statusBannerMessage.value = "Saved to Personal Health Workspace."
            } else {
                _statusBannerMessage.value = "Failed to save record: ${res.exceptionOrNull()?.localizedMessage}"
            }
        }
    }

    fun toggleRecordCompleted(record: HealthRecord) {
        viewModelScope.launch {
            repository.toggleHealthRecordCompleted(record)
        }
    }

    fun deleteHealthRecord(recordId: String) {
        viewModelScope.launch {
            repository.deleteHealthRecord(recordId)
        }
    }

    fun addReminder(
        title: String,
        dosageOrNote: String,
        scheduleTime: String,
        reminderType: String
    ) {
        if (title.isBlank() || scheduleTime.isBlank()) {
            _statusBannerMessage.value = "Please enter medicine/checkup name and schedule time."
            return
        }
        viewModelScope.launch {
            val res = repository.createReminder(
                title = title,
                dosageOrNote = dosageOrNote,
                scheduleTime = scheduleTime,
                reminderType = reminderType
            )
            if (res.isSuccess) {
                _statusBannerMessage.value = "Smart Health Reminder added."
            } else {
                _statusBannerMessage.value = "Failed to add reminder: ${res.exceptionOrNull()?.localizedMessage}"
            }
        }
    }

    fun toggleReminderTaken(reminder: HealthReminder) {
        viewModelScope.launch {
            repository.toggleReminderTaken(reminder)
        }
    }

    fun deleteReminder(reminderId: String) {
        viewModelScope.launch {
            repository.deleteReminder(reminderId)
        }
    }

    fun seedStarterWorkspaceIfEmpty() {
        viewModelScope.launch {
            repository.createHealthRecord(
                title = "OPD Visit Prep: Persistent Fever & Fatigue",
                category = "VISIT_PREP",
                details = "Evening fever 100.8°F for 3 days, mild body ache, taking ORS.",
                facilityOrDoctor = "Capital District Hospital — General Medicine OPD",
                questionsForDoctor = "1. Should I get CBC and Dengue NS1 test under Free Nidan Scheme?\n2. Can Paracetamol 500mg be taken every 6 hours?",
                severityOrPriority = "MODERATE"
            )
            repository.createReminder(
                title = "WHO ORS + Iron Folic Acid (IFA)",
                dosageOrNote = "1 glass ORS morning; 1 IFA tablet after lunch",
                scheduleTime = "09:00 AM Daily",
                reminderType = "MEDICINE"
            )
            _statusBannerMessage.value = "Sample Doctor Visit Prep & Smart Reminder added to your workspace."
        }
    }

    fun deleteAllCloudData() {
        viewModelScope.launch {
            val res = repository.deleteAllUserData()
            if (res.isSuccess) {
                _aiHistory.value = emptyList()
                _statusBannerMessage.value = "All personal health records, reminders, and consent records deleted."
            } else {
                _statusBannerMessage.value = "Delete failed: ${res.exceptionOrNull()?.localizedMessage}"
            }
        }
    }

    fun buildExportJsonString(): String {
        val records = (healthRecordsState.value as? UiState.Success)?.data ?: emptyList()
        val reminders = (remindersState.value as? UiState.Success)?.data ?: emptyList()
        val consent = (privacyConsentState.value as? UiState.Success)?.data
        return buildString {
            appendLine("{")
            appendLine("  \"platform\": \"MediReach AI — Patient Data Export\",")
            appendLine("  \"userId\": \"$currentUserId\",")
            appendLine("  \"preferredLanguage\": \"${consent?.preferredLanguage ?: _selectedLanguage.value}\",")
            appendLine("  \"healthRecordsCount\": ${records.size},")
            appendLine("  \"healthRecords\": [")
            records.forEachIndexed { index, r ->
                val comma = if (index < records.lastIndex) "," else ""
                appendLine("    {\"title\": \"${r.title.replace("\"", "'")}\", \"category\": \"${r.category}\", \"facility\": \"${r.facilityOrDoctor.replace("\"", "'")}\", \"priority\": \"${r.severityOrPriority}\"}$comma")
            }
            appendLine("  ],")
            appendLine("  \"remindersCount\": ${reminders.size},")
            appendLine("  \"reminders\": [")
            reminders.forEachIndexed { index, rem ->
                val comma = if (index < reminders.lastIndex) "," else ""
                appendLine("    {\"title\": \"${rem.title.replace("\"", "'")}\", \"schedule\": \"${rem.scheduleTime.replace("\"", "'")}\", \"type\": \"${rem.reminderType}\"}$comma")
            }
            appendLine("  ]")
            appendLine("}")
        }
    }

    private companion object {
        const val TAG = "MediReachVM"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
