package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.GovtHealthScheme
import com.example.data.model.OfflineHealthGuide
import com.example.data.model.PrivacyConsent
import com.example.data.model.PublicHealthAlert
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PublicHealthAndTrustScreen(
    selectedLanguage: String,
    lowBandwidthMode: Boolean,
    highContrastMode: Boolean,
    schemes: List<GovtHealthScheme>,
    offlineGuides: List<OfflineHealthGuide>,
    publicHealthAlerts: List<PublicHealthAlert>,
    privacyConsentState: UiState<PrivacyConsent?>,
    onSaveConsentSettings: (
        aiGuidanceConsent: Boolean,
        emergencyLocationConsent: Boolean,
        anonymousPublicHealthAnalytics: Boolean,
        lowBandwidthMode: Boolean,
        highContrastMode: Boolean,
        preferredLanguage: String
    ) -> Unit,
    onExportJsonData: () -> String,
    onDeleteAllCloudData: () -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    var activeSubTab by rememberSaveable { mutableIntStateOf(0) } // 0: Govt Schemes, 1: Offline Guides, 2: Public Health, 3: Trust & Privacy
    var selectedEligibilityTag by rememberSaveable { mutableStateOf("ALL") }

    val consentData = (privacyConsentState as? UiState.Success)?.data
    var aiConsent by rememberSaveable(consentData) { mutableStateOf(consentData?.aiGuidanceConsent ?: true) }
    var locationConsent by rememberSaveable(consentData) { mutableStateOf(consentData?.emergencyLocationConsent ?: true) }
    var analyticsConsent by rememberSaveable(consentData) { mutableStateOf(consentData?.anonymousPublicHealthAnalytics ?: false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = activeSubTab) {
            Tab(
                selected = activeSubTab == 0,
                onClick = { activeSubTab = 0 },
                text = { Text("Schemes") },
                icon = { Icon(Icons.Default.AccountBalance, null, Modifier.size(18.dp)) },
                modifier = Modifier.testTag("subtab_schemes")
            )
            Tab(
                selected = activeSubTab == 1,
                onClick = { activeSubTab = 1 },
                text = { Text("Offline") },
                icon = { Icon(Icons.Default.OfflinePin, null, Modifier.size(18.dp)) },
                modifier = Modifier.testTag("subtab_offline")
            )
            Tab(
                selected = activeSubTab == 2,
                onClick = { activeSubTab = 2 },
                text = { Text("Advisories") },
                icon = { Icon(Icons.Default.Public, null, Modifier.size(18.dp)) },
                modifier = Modifier.testTag("subtab_advisories")
            )
            Tab(
                selected = activeSubTab == 3,
                onClick = { activeSubTab = 3 },
                text = { Text("Privacy") },
                icon = { Icon(Icons.Default.VerifiedUser, null, Modifier.size(18.dp)) },
                modifier = Modifier.testTag("subtab_privacy")
            )
        }

        when (activeSubTab) {
            0 -> {
                // Government Healthcare Benefits Assistant (Feature #11)
                val filteredSchemes = schemes.filter { scheme ->
                    selectedEligibilityTag == "ALL" || scheme.eligibilityCriteria.any {
                        it.contains(selectedEligibilityTag, ignoreCase = true)
                    }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text = "Government Healthcare Benefits Assistant",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "Check family eligibility & required documents for PM-JAY, BSKY Odisha, JSY & Jan Aushadhi.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                "ALL" to "All Schemes",
                                "Ration Card" to "Ration Card / NFSA",
                                "Senior Citizen" to "Senior 70+",
                                "Women" to "Women & Maternal",
                                "Odisha" to "Odisha BSKY"
                            ).forEach { (tag, label) ->
                                FilterChip(
                                    selected = selectedEligibilityTag == tag,
                                    onClick = { selectedEligibilityTag = tag },
                                    label = { Text(label) }
                                )
                            }
                        }
                    }

                    items(filteredSchemes, key = { it.id }) { scheme ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = when (selectedLanguage) {
                                        "HI" -> "${scheme.hindiName} (${scheme.name})"
                                        "OR" -> "${scheme.odiaName} (${scheme.name})"
                                        else -> scheme.name
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Coverage: ${scheme.coverageAmount}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Who is Eligible: ${scheme.targetBeneficiaries}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                scheme.keyBenefits.forEach { benefit ->
                                    Text(text = "• $benefit", style = MaterialTheme.typography.bodySmall)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "Documents to Carry to Hospital Mitra Desk:",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        scheme.requiredDocuments.forEach { doc ->
                                            Text(text = "✓ $doc", style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                OutlinedButton(
                                    onClick = {
                                        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${scheme.helpline}")))
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Call Scheme Helpline (${scheme.helpline})")
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Offline Healthcare Mode (Feature #15)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text = "Offline Healthcare Mode (Zero-Internet First Aid)",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "Pre-bundled MoHFW & WHO emergency action cards available without mobile network.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    items(offlineGuides, key = { it.id }) { guide ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = when (selectedLanguage) {
                                        "HI" -> guide.titleHi
                                        "OR" -> guide.titleOr
                                        else -> guide.titleEn
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = guide.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "🚨 Danger Signs (Call 108 if present):",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.error
                                )
                                guide.redFlags.forEach { flag ->
                                    Text(text = "• $flag", style = MaterialTheme.typography.bodySmall)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "✅ Safe Action Steps:",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                guide.actionSteps.forEach { step ->
                                    Text(text = "• $step", style = MaterialTheme.typography.bodySmall)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "❌ Do NOT Do:",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                guide.doNotDo.forEach { dont ->
                                    Text(
                                        text = "• $dont",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Source: ${guide.sourceCitation}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // Public Health Insights & Community Health Resources (Features #18, #19)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text = "Public Health Insights & Community Resources",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "Regional disease surveillance, ASHA/Anganwadi VHSND drives & eSanjeevani telemedicine.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    items(publicHealthAlerts, key = { it.id }) { alert ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${alert.alertLevel} • ${alert.region}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = alert.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = alert.summary, style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                alert.preventionChecklist.forEach { item ->
                                    Text(text = "✓ $item", style = MaterialTheme.typography.bodySmall)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Community Resource: ${alert.communityResourceContact} • Source: ${alert.source}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            3 -> {
                // Trust & Privacy Center (Features #17, #20)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("trust_privacy_list"),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Text(
                            text = "Trust, Consent & Privacy Center",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "DPDP-aligned patient data ownership: control AI consent, export your records, or permanently erase cloud data.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ConsentToggleRow(
                                    title = "AI Health Information Consent",
                                    subtitle = "Allow non-diagnostic symptom & report guidance",
                                    checked = aiConsent,
                                    onCheckedChange = { aiConsent = it }
                                )
                                ConsentToggleRow(
                                    title = "Emergency & Care Finder Location Consent",
                                    subtitle = "Allow district/block filtering for hospitals & desert detection",
                                    checked = locationConsent,
                                    onCheckedChange = { locationConsent = it }
                                )
                                ConsentToggleRow(
                                    title = "Anonymous Public Health Trends",
                                    subtitle = "Contribute de-identified seasonal fever statistics",
                                    checked = analyticsConsent,
                                    onCheckedChange = { analyticsConsent = it }
                                )

                                Button(
                                    onClick = {
                                        onSaveConsentSettings(
                                            aiConsent,
                                            locationConsent,
                                            analyticsConsent,
                                            lowBandwidthMode,
                                            highContrastMode,
                                            selectedLanguage
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("save_consent_button")
                                ) {
                                    Text("Save Consent Preferences to Cloud")
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Data Portability, Export & Account Controls",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                OutlinedButton(
                                    onClick = {
                                        val exportedJson = onExportJsonData()
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "application/json"
                                            putExtra(Intent.EXTRA_SUBJECT, "MediReach AI Personal Health Export")
                                            putExtra(Intent.EXTRA_TEXT, exportedJson)
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Export Health Data JSON"))
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("export_data_button")
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Export My Health Records (JSON)")
                                }

                                Button(
                                    onClick = onDeleteAllCloudData,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("delete_all_data_button")
                                ) {
                                    Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Delete All My Cloud Health Data")
                                }

                                OutlinedButton(
                                    onClick = onSignOut,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("sign_out_button")
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Sign Out of MediReach AI")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConsentToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
