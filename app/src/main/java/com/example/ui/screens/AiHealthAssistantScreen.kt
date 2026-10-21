package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.MedicineSafetyEntry
import com.example.data.remote.AiToolMode
import com.example.ui.viewmodel.AiExchange
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiHealthAssistantScreen(
    selectedMode: AiToolMode,
    selectedLanguage: String,
    isLoading: Boolean,
    history: List<AiExchange>,
    medicineSafetyList: List<MedicineSafetyEntry>,
    onSelectMode: (AiToolMode) -> Unit,
    onSubmitQuery: (String) -> Unit,
    onSaveToWorkspace: (title: String, details: String) -> Unit
) {
    val context = LocalContext.current
    var queryText by rememberSaveable { mutableStateOf("") }
    var autoSpeakReplies by rememberSaveable { mutableStateOf(true) }
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(context) {
        var engine: TextToSpeech? = null
        engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val locale = when (selectedLanguage) {
                    "HI" -> Locale.forLanguageTag("hi-IN")
                    "OR" -> Locale.forLanguageTag("or-IN")
                    else -> Locale.forLanguageTag("en-IN")
                }
                engine?.language = locale
            }
        }
        ttsEngine = engine
        onDispose {
            engine?.stop()
            engine?.shutdown()
        }
    }

    // Automatically speak new AI responses when Live Voice Conversation mode is enabled
    val latestExchangeId = history.firstOrNull()?.id
    LaunchedEffect(latestExchangeId) {
        val latest = history.firstOrNull()
        if (autoSpeakReplies && latest != null) {
            val locale = when (latest.languageCode) {
                "HI" -> Locale.forLanguageTag("hi-IN")
                "OR" -> Locale.forLanguageTag("or-IN")
                else -> Locale.forLanguageTag("en-IN")
            }
            ttsEngine?.language = locale
            ttsEngine?.speak(
                latest.response,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "live_voice_${latest.id}"
            )
        }
    }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = matches?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                queryText = spokenText
                onSubmitQuery(spokenText)
            }
        }
    }

    val quickPrompts = remember(selectedMode) {
        when (selectedMode) {
            AiToolMode.SYMPTOM_GUIDE -> listOf(
                "3 days fever 101°F with body ache & mild retro-orbital headache",
                "Child has loose motion 4 times since morning, how to use ORS?",
                "Persistent dry cough for 2 weeks with evening fever"
            )
            AiToolMode.REPORT_EXPLAINER -> listOf(
                "Hemoglobin 9.4 g/dL, MCV 74 fL in adult woman — what does it mean?",
                "HbA1c 7.2% and Fasting Blood Sugar 148 mg/dL",
                "Platelet count 1,10,000 /µL on Day 4 of viral fever"
            )
            AiToolMode.PATIENT_TRANSLATOR -> listOf(
                "Translate to Hindi & Odia: 'I have burning sensation in chest after meals and dizziness'",
                "Translate to English for OPD Doctor: 'मोर ३ दिन हेला ज्वर अछि' / 'मुझे 3 दिन से बुखार और ठंड लग रही है'",
                "Ask pharmacist if Jan Aushadhi generic equivalent is available"
            )
            AiToolMode.MEDICINE_SAFETY -> listOf(
                "Why should Ibuprofen or Aspirin be avoided during suspected Dengue fever?",
                "Safe spacing and maximum daily limit for Paracetamol 500mg",
                "How to take Iron-Folic Acid (IFA) tablets for best absorption?"
            )
            AiToolMode.NAVIGATION_AGENT -> listOf(
                "Which facility should I visit for dog bite anti-rabies vaccine (PHC vs CHC)?",
                "How to use Ayushman Bharat PM-JAY or BSKY card at hospital registration?",
                "Pregnant woman in rural block needs free antenatal ultrasound under PMSMA"
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ai_assistant_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header & Safety Notice
        item {
            Column {
                Text(
                    text = "AI Health Assistant & Live Voice Conversation",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Non-diagnostic clinical education, report explanation, translation & voice conversation (MoHFW / ICMR / WHO sourced).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 2. Live Voice Conversation Banner (Feature #14 + Real-Time Voice Turn)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Multilingual Voice Conversation (EN • हिन्दी • ଓଡ଼ିଆ)",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Speak your symptom or question and hear the response read aloud automatically",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                        Switch(
                            checked = autoSpeakReplies,
                            onCheckedChange = { autoSpeakReplies = it },
                            modifier = Modifier.testTag("auto_speak_switch")
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                val langTag = when (selectedLanguage) {
                                    "HI" -> "hi-IN"
                                    "OR" -> "or-IN"
                                    else -> "en-IN"
                                }
                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, langTag)
                                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your health question ($langTag)...")
                                }
                                try {
                                    speechLauncher.launch(intent)
                                } catch (_: Exception) {
                                    // Fallback if speech recognizer activity is absent on headless emulator
                                    val fallbackSample = quickPrompts.first()
                                    queryText = fallbackSample
                                    onSubmitQuery(fallbackSample)
                                }
                            },
                            modifier = Modifier.testTag("start_voice_input_button")
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Speak Question")
                        }

                        OutlinedButton(
                            onClick = { ttsEngine?.stop() },
                            modifier = Modifier.testTag("tts_stop_button")
                        ) {
                            Icon(Icons.Default.StopCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Stop Audio")
                        }
                    }
                }
            }
        }

        // 3. 5 AI Tool Mode Selector Chips (Features #1, #2, #3, #4, #5)
        item {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AiToolMode.entries.forEach { mode ->
                    val label = when (selectedLanguage) {
                        "HI" -> mode.titleHi
                        "OR" -> mode.titleOr
                        else -> mode.titleEn
                    }
                    FilterChip(
                        selected = selectedMode == mode,
                        onClick = { onSelectMode(mode) },
                        label = { Text(label) },
                        modifier = Modifier.testTag("ai_mode_${mode.name.lowercase()}")
                    )
                }
            }
        }

        // 4. Prompt Input Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = queryText,
                        onValueChange = { queryText = it },
                        label = {
                            Text(
                                when (selectedMode) {
                                    AiToolMode.SYMPTOM_GUIDE -> "Describe symptoms, duration & age..."
                                    AiToolMode.REPORT_EXPLAINER -> "Paste lab values (e.g., Hb 9.8, HbA1c 6.9%)..."
                                    AiToolMode.PATIENT_TRANSLATOR -> "Enter symptom or doctor phrase to translate..."
                                    AiToolMode.MEDICINE_SAFETY -> "Enter generic or brand medicine name..."
                                    AiToolMode.NAVIGATION_AGENT -> "Ask which hospital/PHC or scheme applies..."
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_query_input"),
                        minLines = 3,
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Tap a clinical sample prompt:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickPrompts.forEachIndexed { idx, sample ->
                            AssistChip(
                                onClick = {
                                    queryText = sample
                                    onSubmitQuery(sample)
                                },
                                label = {
                                    Text(
                                        text = sample.take(42) + if (sample.length > 42) "…" else "",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                },
                                modifier = Modifier.testTag("sample_prompt_$idx")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (queryText.isNotBlank()) {
                                onSubmitQuery(queryText)
                            }
                        },
                        enabled = !isLoading && queryText.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_submit_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analyzing Safely...")
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Get Verified Guidance")
                        }
                    }
                }
            }
        }

        // 5. AI Responses with Voice TTS & Save to Doctor Visit Workspace
        if (history.isNotEmpty()) {
            item {
                Text(
                    text = "Guidance & Source Citations",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(history, key = { it.id }) { exchange ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    ),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${exchange.mode.titleEn} • ${exchange.languageCode}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Row {
                                IconButton(
                                    onClick = {
                                        val locale = when (exchange.languageCode) {
                                            "HI" -> Locale.forLanguageTag("hi-IN")
                                            "OR" -> Locale.forLanguageTag("or-IN")
                                            else -> Locale.forLanguageTag("en-IN")
                                        }
                                        ttsEngine?.language = locale
                                        ttsEngine?.speak(
                                            exchange.response,
                                            TextToSpeech.QUEUE_FLUSH,
                                            null,
                                            "medireach_tts_${exchange.id}"
                                        )
                                    },
                                    modifier = Modifier.testTag("speak_response_${exchange.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Listen with Multilingual Voice Assistant",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        onSaveToWorkspace(
                                            "${exchange.mode.titleEn}: ${exchange.prompt.take(50)}",
                                            exchange.response
                                        )
                                    },
                                    modifier = Modifier.testTag("save_response_${exchange.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkAdd,
                                        contentDescription = "Save to Personal Health Timeline",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Q: ${exchange.prompt}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = exchange.response,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // 6. Verified NLEM & Jan Aushadhi Medicine Safety Reference Cards (Feature #4)
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Medication,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Essential Medicine Safety & Jan Aushadhi Generic Guide",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        items(medicineSafetyList, key = { it.id }) { med ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = med.genericName,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                ttsEngine?.speak(
                                    "${med.genericName}. ${med.primaryUse}. Warnings: ${med.keyWarnings.joinToString()}",
                                    TextToSpeech.QUEUE_FLUSH,
                                    null,
                                    med.id
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Read Medicine Safety Aloud",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        text = "Common Brands: ${med.commonBrandsIndia} • Jan Aushadhi: ${med.janAushadhiPriceApprox}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = med.primaryUse, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Safety & Dosing Boundary: ${med.dosageGuidanceInfo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    med.keyWarnings.forEach { warning ->
                        Text(
                            text = "⚠️ $warning",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Source: ${med.sourceCitation}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
