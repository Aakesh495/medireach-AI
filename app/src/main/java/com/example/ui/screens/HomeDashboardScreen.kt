package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DataSaverOn
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.HealthRecord
import com.example.data.model.HealthReminder
import com.example.data.remote.AiToolMode
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeDashboardScreen(
    selectedLanguage: String,
    lowBandwidthMode: Boolean,
    highContrastMode: Boolean,
    healthRecordsState: UiState<List<HealthRecord>>,
    remindersState: UiState<List<HealthReminder>>,
    onLanguageChange: (String) -> Unit,
    onToggleLowBandwidth: (Boolean) -> Unit,
    onToggleHighContrast: (Boolean) -> Unit,
    onNavigateToAiTool: (AiToolMode) -> Unit,
    onNavigateToTab: (Int) -> Unit,
    onSeedSampleData: () -> Unit
) {
    val context = LocalContext.current
    val recordsCount = (healthRecordsState as? UiState.Success)?.data?.size ?: 0
    val activeRemindersCount = (remindersState as? UiState.Success)?.data?.count { !it.takenToday } ?: 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_dashboard_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Hero Header with Multilingual & Low-Bandwidth Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (lowBandwidthMode) {
                                Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            } else {
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.65f)
                                    )
                                )
                            }
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.HealthAndSafety,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = when (selectedLanguage) {
                                        "HI" -> "मेडीरीच एआई (MediReach AI)"
                                        "OR" -> "ମେଡିରିଚ୍ ଏଆଇ (MediReach AI)"
                                        else -> "MediReach AI"
                                    },
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = when (selectedLanguage) {
                                        "HI" -> "स्मार्ट स्वास्थ्य मार्गदर्शन • हर नागरिक के लिए"
                                        "OR" -> "ସ୍ମାର୍ଟ ସ୍ୱାସ୍ଥ୍ୟ ସୂଚନା • ସମସ୍ତଙ୍କ ପାଇଁ ଉପলବ୍ଧ"
                                        else -> "One Platform. Smarter Healthcare. Accessible to Everyone."
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Language & Accessibility Quick Chips (Features #3, #14, #20)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedLanguage == "EN",
                            onClick = { onLanguageChange("EN") },
                            label = { Text("English") },
                            modifier = Modifier.testTag("lang_chip_en")
                        )
                        FilterChip(
                            selected = selectedLanguage == "HI",
                            onClick = { onLanguageChange("HI") },
                            label = { Text("हिन्दी") },
                            modifier = Modifier.testTag("lang_chip_hi")
                        )
                        FilterChip(
                            selected = selectedLanguage == "OR",
                            onClick = { onLanguageChange("OR") },
                            label = { Text("ଓଡ଼ିଆ") },
                            modifier = Modifier.testTag("lang_chip_or")
                        )
                        FilterChip(
                            selected = lowBandwidthMode,
                            onClick = { onToggleLowBandwidth(!lowBandwidthMode) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DataSaverOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = { Text(if (lowBandwidthMode) "2G Lite: ON" else "Low-Bandwidth") },
                            modifier = Modifier.testTag("toggle_low_bandwidth_chip")
                        )
                        FilterChip(
                            selected = highContrastMode,
                            onClick = { onToggleHighContrast(!highContrastMode) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Contrast,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = { Text("High Contrast") },
                            modifier = Modifier.testTag("toggle_high_contrast_chip")
                        )
                    }
                }
            }
        }

        // 2. Emergency Connect Strip (Feature #7)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Emergency,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = when (selectedLanguage) {
                                        "HI" -> "आपातकालीन सहायता (Emergency Connect)"
                                        "OR" -> "ଜରୁରୀକାଳୀନ ସହାୟତା (Emergency Connect)"
                                        else -> "Emergency Connect — Instant Toll-Free Dial"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Chest pain, breathing trouble, snakebite, or pregnancy danger signs",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EmergencyDialButton(
                            label = "108 Ambulance",
                            phone = "108",
                            tag = "dial_108_button",
                            onDial = {
                                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:108")))
                            }
                        )
                        EmergencyDialButton(
                            label = "112 National SOS",
                            phone = "112",
                            tag = "dial_112_button",
                            onDial = {
                                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:112")))
                            }
                        )
                        EmergencyDialButton(
                            label = "104 Health Advice",
                            phone = "104",
                            tag = "dial_104_button",
                            onDial = {
                                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:104")))
                            }
                        )
                        EmergencyDialButton(
                            label = "102 Janani Maternal",
                            phone = "102",
                            tag = "dial_102_button",
                            onDial = {
                                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:102")))
                            }
                        )
                    }
                }
            }
        }

        // 3. Health Safety Boundary Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Safety Boundary: MediReach AI is an educational information & care navigation tool, not a clinical diagnostic or prescription service. Sources: MoHFW, ICMR & WHO.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 4. AI Healthcare Intelligence Hub (Features #1, #2, #3, #4, #5, #14)
        item {
            Text(
                text = when (selectedLanguage) {
                    "HI" -> "एआई स्वास्थ्य सहायक (AI Health Intelligence)"
                    "OR" -> "AI ସ୍ୱାସ୍ଥ୍ୟ ସହାୟକ (AI Health Intelligence)"
                    else -> "AI Healthcare Intelligence (Sourced & Multilingual)"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickAiFeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "AI Symptom Guide",
                        subtitle = "Red flags & PHC triage",
                        icon = Icons.Default.Psychology,
                        tag = "quick_ai_symptom",
                        onClick = { onNavigateToAiTool(AiToolMode.SYMPTOM_GUIDE) }
                    )
                    QuickAiFeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "Report Explainer",
                        subtitle = "CBC, HbA1c, Thyroid",
                        icon = Icons.Default.MedicalInformation,
                        tag = "quick_ai_report",
                        onClick = { onNavigateToAiTool(AiToolMode.REPORT_EXPLAINER) }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickAiFeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "Patient Translator",
                        subtitle = "English • हिन्दी • ଓଡ଼ିଆ",
                        icon = Icons.Default.Translate,
                        tag = "quick_ai_translator",
                        onClick = { onNavigateToAiTool(AiToolMode.PATIENT_TRANSLATOR) }
                    )
                    QuickAiFeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "Medicine Safety",
                        subtitle = "NLEM & Jan Aushadhi",
                        icon = Icons.Default.HealthAndSafety,
                        tag = "quick_ai_medicine",
                        onClick = { onNavigateToAiTool(AiToolMode.MEDICINE_SAFETY) }
                    )
                }
            }
        }

        // 5. Personal Health Workspace Snapshot (Features #8, #9, #10, #13)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Personal Health Workspace (Cloud Synced)",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "$recordsCount Timeline/Visit Records • $activeRemindersCount Pending Reminders Today",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onNavigateToTab(3) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("open_workspace_button")
                        ) {
                            Text("Open Workspace")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        if (recordsCount == 0 && activeRemindersCount == 0) {
                            OutlinedButton(
                                onClick = onSeedSampleData,
                                modifier = Modifier.testTag("seed_sample_workspace_button")
                            ) {
                                Text("Add Starter Items")
                            }
                        }
                    }
                }
            }
        }

        // 6. All 20 Planned MediReach AI Modules Directory
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Complete 20-Feature MediReach AI Platform Map",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tap any capability cluster below to jump directly to its interactive module:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AssistChip(
                            onClick = { onNavigateToTab(1) },
                            leadingIcon = { Icon(Icons.Default.Psychology, null, Modifier.size(16.dp)) },
                            label = { Text("1-5 & 14: AI & Voice Suite") }
                        )
                        AssistChip(
                            onClick = { onNavigateToTab(2) },
                            leadingIcon = { Icon(Icons.Default.LocalHospital, null, Modifier.size(16.dp)) },
                            label = { Text("6, 7, 12, 16: Care, Capacity & Deserts") }
                        )
                        AssistChip(
                            onClick = { onNavigateToTab(3) },
                            leadingIcon = { Icon(Icons.Default.NotificationsActive, null, Modifier.size(16.dp)) },
                            label = { Text("8, 9, 10, 13: Timeline & Visit Prep") }
                        )
                        AssistChip(
                            onClick = { onNavigateToTab(4) },
                            leadingIcon = { Icon(Icons.Default.Public, null, Modifier.size(16.dp)) },
                            label = { Text("11, 15, 17-20: Schemes, Offline & Privacy") }
                        )
                        AssistChip(
                            onClick = { onNavigateToTab(5) },
                            leadingIcon = { Icon(Icons.Default.Public, null, Modifier.size(16.dp)) },
                            label = { Text("🌐 MediReach Website / Web PWA") },
                            modifier = Modifier.testTag("open_website_tab_chip")
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmergencyDialButton(
    label: String,
    phone: String,
    tag: String,
    onDial: () -> Unit
) {
    Button(
        onClick = onDial,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag(tag)
    ) {
        Icon(
            imageVector = Icons.Default.Call,
            contentDescription = "Dial $phone",
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun QuickAiFeatureCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    tag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag(tag),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
