package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.remote.GeminiHealthService
import com.example.data.repository.MediReachRepository
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.signOutUser
import com.example.ui.screens.AiHealthAssistantScreen
import com.example.ui.screens.CareFinderScreen
import com.example.ui.screens.HealthWorkspaceScreen
import com.example.ui.screens.HomeDashboardScreen
import com.example.ui.screens.PublicHealthAndTrustScreen
import com.example.ui.screens.WebPortalScreen
import com.example.ui.theme.MediReachTheme
import com.example.ui.viewmodel.MediReachViewModel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
        enableEdgeToEdge()
        setContent {
            MediReachAppRoot()
        }
    }
}

@Composable
fun MediReachAppRoot() {
    var currentUser by remember {
        mutableStateOf(
            try {
                Firebase.auth.currentUser
            } catch (_: Exception) {
                null
            }
        )
    }
    var showWebsiteFromAuth by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val authInstance = try {
            Firebase.auth
        } catch (_: Exception) {
            null
        }
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        authInstance?.addAuthStateListener(listener)
        onDispose {
            authInstance?.removeAuthStateListener(listener)
        }
    }

    val user = currentUser
    if (user == null) {
        MediReachTheme {
            if (showWebsiteFromAuth) {
                BackHandler {
                    showWebsiteFromAuth = false
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        WebPortalScreen(
                            onSwitchToNativeAndroid = { showWebsiteFromAuth = false }
                        )
                    }
                }
            } else {
                AuthScreen(
                    onAuthSuccess = { currentUser = Firebase.auth.currentUser },
                    onOpenWebsiteMode = { showWebsiteFromAuth = true }
                )
            }
        }
    } else {
        AuthenticatedMediReachShell(
            currentUserId = user.uid,
            onSignedOut = { currentUser = null }
        )
    }
}

@Composable
fun AuthenticatedMediReachShell(
    currentUserId: String,
    onSignedOut: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val viewModel: MediReachViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                MediReachViewModel(
                    repository = MediReachRepository(db),
                    geminiService = GeminiHealthService(),
                    currentUserId = currentUserId
                )
            }
        }
    )

    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val lowBandwidthMode by viewModel.lowBandwidthMode.collectAsStateWithLifecycle()
    val highContrastMode by viewModel.highContrastMode.collectAsStateWithLifecycle()
    val aiToolMode by viewModel.aiToolMode.collectAsStateWithLifecycle()
    val aiLoading by viewModel.aiLoading.collectAsStateWithLifecycle()
    val aiHistory by viewModel.aiHistory.collectAsStateWithLifecycle()
    val healthRecordsState by viewModel.healthRecordsState.collectAsStateWithLifecycle()
    val remindersState by viewModel.remindersState.collectAsStateWithLifecycle()
    val privacyConsentState by viewModel.privacyConsentState.collectAsStateWithLifecycle()
    val statusBannerMessage by viewModel.statusBannerMessage.collectAsStateWithLifecycle()

    var currentTab by rememberSaveable { mutableIntStateOf(0) }
    var prefilledFacility by rememberSaveable { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusBannerMessage) {
        val msg = statusBannerMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearBanner()
        }
    }

    if (currentTab != 0) {
        BackHandler {
            currentTab = 0
        }
    }

    MediReachTheme(highContrastOverride = highContrastMode) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWideScreen = maxWidth >= 600.dp

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                contentWindowInsets = WindowInsets.safeDrawing,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                bottomBar = {
                    if (!isWideScreen) {
                        NavigationBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .testTag("main_bottom_nav")
                        ) {
                            NavigationBarItem(
                                selected = currentTab == 0,
                                onClick = { currentTab = 0 },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home Dashboard") },
                                label = { Text("Home") },
                                modifier = Modifier.testTag("nav_home")
                            )
                            NavigationBarItem(
                                selected = currentTab == 1,
                                onClick = { currentTab = 1 },
                                icon = { Icon(Icons.Default.Psychology, contentDescription = "AI Health Assistant") },
                                label = { Text("AI Guide") },
                                modifier = Modifier.testTag("nav_ai")
                            )
                            NavigationBarItem(
                                selected = currentTab == 2,
                                onClick = { currentTab = 2 },
                                icon = { Icon(Icons.Default.LocalHospital, contentDescription = "Care Finder & Capacity") },
                                label = { Text("Care") },
                                modifier = Modifier.testTag("nav_care")
                            )
                            NavigationBarItem(
                                selected = currentTab == 3,
                                onClick = { currentTab = 3 },
                                icon = { Icon(Icons.AutoMirrored.Filled.EventNote, contentDescription = "Personal Health Workspace") },
                                label = { Text("Workspace") },
                                modifier = Modifier.testTag("nav_workspace")
                            )
                            NavigationBarItem(
                                selected = currentTab == 4,
                                onClick = { currentTab = 4 },
                                icon = { Icon(Icons.Default.Public, contentDescription = "Schemes, Offline & Privacy") },
                                label = { Text("Schemes") },
                                modifier = Modifier.testTag("nav_public_trust")
                            )
                            NavigationBarItem(
                                selected = currentTab == 5,
                                onClick = { currentTab = 5 },
                                icon = { Icon(Icons.Default.Language, contentDescription = "Website / Web App") },
                                label = { Text("Website") },
                                modifier = Modifier.testTag("nav_website")
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (isWideScreen) {
                        NavigationRail(
                            modifier = Modifier
                                .fillMaxHeight()
                                .testTag("main_nav_rail")
                        ) {
                            NavigationRailItem(
                                selected = currentTab == 0,
                                onClick = { currentTab = 0 },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home Dashboard") },
                                label = { Text("Home") },
                                modifier = Modifier.testTag("rail_home")
                            )
                            NavigationRailItem(
                                selected = currentTab == 1,
                                onClick = { currentTab = 1 },
                                icon = { Icon(Icons.Default.Psychology, contentDescription = "AI Health Assistant") },
                                label = { Text("AI Guide") },
                                modifier = Modifier.testTag("rail_ai")
                            )
                            NavigationRailItem(
                                selected = currentTab == 2,
                                onClick = { currentTab = 2 },
                                icon = { Icon(Icons.Default.LocalHospital, contentDescription = "Care Finder & Capacity") },
                                label = { Text("Care Finder") },
                                modifier = Modifier.testTag("rail_care")
                            )
                            NavigationRailItem(
                                selected = currentTab == 3,
                                onClick = { currentTab = 3 },
                                icon = { Icon(Icons.AutoMirrored.Filled.EventNote, contentDescription = "Personal Health Workspace") },
                                label = { Text("Workspace") },
                                modifier = Modifier.testTag("rail_workspace")
                            )
                            NavigationRailItem(
                                selected = currentTab == 4,
                                onClick = { currentTab = 4 },
                                icon = { Icon(Icons.Default.Public, contentDescription = "Schemes, Offline & Privacy") },
                                label = { Text("Public & Trust") },
                                modifier = Modifier.testTag("rail_public_trust")
                            )
                            NavigationRailItem(
                                selected = currentTab == 5,
                                onClick = { currentTab = 5 },
                                icon = { Icon(Icons.Default.Language, contentDescription = "Website / Web App") },
                                label = { Text("Website") },
                                modifier = Modifier.testTag("rail_website")
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        when (currentTab) {
                            0 -> HomeDashboardScreen(
                                selectedLanguage = selectedLanguage,
                                lowBandwidthMode = lowBandwidthMode,
                                highContrastMode = highContrastMode,
                                healthRecordsState = healthRecordsState,
                                remindersState = remindersState,
                                onLanguageChange = viewModel::setLanguage,
                                onToggleLowBandwidth = viewModel::toggleLowBandwidth,
                                onToggleHighContrast = viewModel::toggleHighContrast,
                                onNavigateToAiTool = { mode ->
                                    viewModel.setAiToolMode(mode)
                                    currentTab = 1
                                },
                                onNavigateToTab = { tab -> currentTab = tab },
                                onSeedSampleData = viewModel::seedStarterWorkspaceIfEmpty
                            )

                            1 -> AiHealthAssistantScreen(
                                selectedMode = aiToolMode,
                                selectedLanguage = selectedLanguage,
                                isLoading = aiLoading,
                                history = aiHistory,
                                medicineSafetyList = viewModel.medicineSafetyList,
                                onSelectMode = viewModel::setAiToolMode,
                                onSubmitQuery = viewModel::submitAiQuery,
                                onSaveToWorkspace = { title, details ->
                                    viewModel.addHealthRecord(
                                        title = title,
                                        category = "SYMPTOM_LOG",
                                        details = details,
                                        facilityOrDoctor = "MediReach AI Guidance Log",
                                        questionsForDoctor = "Verify AI guidance with OPD Doctor",
                                        severityOrPriority = "ROUTINE"
                                    )
                                }
                            )

                            2 -> CareFinderScreen(
                                hospitals = viewModel.hospitals,
                                onPrepareVisitForFacility = { facilityName ->
                                    prefilledFacility = facilityName
                                    currentTab = 3
                                }
                            )

                            3 -> HealthWorkspaceScreen(
                                healthRecordsState = healthRecordsState,
                                remindersState = remindersState,
                                prefilledFacility = prefilledFacility,
                                onAddHealthRecord = viewModel::addHealthRecord,
                                onToggleRecordCompleted = viewModel::toggleRecordCompleted,
                                onDeleteHealthRecord = viewModel::deleteHealthRecord,
                                onAddReminder = viewModel::addReminder,
                                onToggleReminderTaken = viewModel::toggleReminderTaken,
                                onDeleteReminder = viewModel::deleteReminder,
                                onSeedSampleData = viewModel::seedStarterWorkspaceIfEmpty
                            )

                            4 -> PublicHealthAndTrustScreen(
                                selectedLanguage = selectedLanguage,
                                lowBandwidthMode = lowBandwidthMode,
                                highContrastMode = highContrastMode,
                                schemes = viewModel.schemes,
                                offlineGuides = viewModel.offlineGuides,
                                publicHealthAlerts = viewModel.publicHealthAlerts,
                                privacyConsentState = privacyConsentState,
                                onSaveConsentSettings = viewModel::updateConsentSettings,
                                onExportJsonData = viewModel::buildExportJsonString,
                                onDeleteAllCloudData = viewModel::deleteAllCloudData,
                                onSignOut = {
                                    signOutUser(
                                        context = context,
                                        onSignOutComplete = onSignedOut,
                                        scope = coroutineScope
                                    )
                                }
                            )

                            5 -> WebPortalScreen(
                                onSwitchToNativeAndroid = { currentTab = 0 }
                            )
                        }
                    }
                }
            }
        }
    }
}
