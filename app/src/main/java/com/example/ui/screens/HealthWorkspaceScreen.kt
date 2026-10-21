package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.RadioButtonUnchecked
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.data.model.HealthRecord
import com.example.data.model.HealthReminder
import com.example.ui.viewmodel.UiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HealthWorkspaceScreen(
    healthRecordsState: UiState<List<HealthRecord>>,
    remindersState: UiState<List<HealthReminder>>,
    prefilledFacility: String,
    onAddHealthRecord: (
        title: String,
        category: String,
        details: String,
        facilityOrDoctor: String,
        questionsForDoctor: String,
        severityOrPriority: String
    ) -> Unit,
    onToggleRecordCompleted: (HealthRecord) -> Unit,
    onDeleteHealthRecord: (String) -> Unit,
    onAddReminder: (
        title: String,
        dosageOrNote: String,
        scheduleTime: String,
        reminderType: String
    ) -> Unit,
    onToggleReminderTaken: (HealthReminder) -> Unit,
    onDeleteReminder: (String) -> Unit,
    onSeedSampleData: () -> Unit
) {
    var selectedSection by rememberSaveable { mutableIntStateOf(0) } // 0 = Timeline & Doctor Visit Prep, 1 = Smart Reminders

    // Record Form State
    var recordTitle by rememberSaveable { mutableStateOf("") }
    var recordCategory by rememberSaveable { mutableStateOf("VISIT_PREP") }
    var recordDetails by rememberSaveable { mutableStateOf("") }
    var recordFacility by rememberSaveable(prefilledFacility) { mutableStateOf(prefilledFacility) }
    var recordQuestions by rememberSaveable { mutableStateOf("") }
    var recordPriority by rememberSaveable { mutableStateOf("ROUTINE") }

    // Reminder Form State
    var reminderTitle by rememberSaveable { mutableStateOf("") }
    var reminderNote by rememberSaveable { mutableStateOf("") }
    var reminderTime by rememberSaveable { mutableStateOf("08:00 AM Daily") }
    var reminderType by rememberSaveable { mutableStateOf("MEDICINE") }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedSection) {
            Tab(
                selected = selectedSection == 0,
                onClick = { selectedSection = 0 },
                text = { Text("Timeline & Visit Prep") },
                icon = { Icon(Icons.Default.EventNote, contentDescription = null) },
                modifier = Modifier.testTag("workspace_tab_timeline")
            )
            Tab(
                selected = selectedSection == 1,
                onClick = { selectedSection = 1 },
                text = { Text("Smart Reminders") },
                icon = { Icon(Icons.Default.AlarmOn, contentDescription = null) },
                modifier = Modifier.testTag("workspace_tab_reminders")
            )
        }

        if (selectedSection == 0) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("timeline_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "New Doctor Visit Prep / Health Timeline Entry",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf(
                                    "VISIT_PREP" to "Doctor Visit Prep",
                                    "TIMELINE" to "Health Timeline",
                                    "LAB_NOTE" to "Lab Note",
                                    "SYMPTOM_LOG" to "Symptom Log"
                                ).forEach { (key, label) ->
                                    FilterChip(
                                        selected = recordCategory == key,
                                        onClick = { recordCategory = key },
                                        label = { Text(label) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = recordTitle,
                                onValueChange = { recordTitle = it },
                                label = { Text("Visit Reason or Record Title *") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("record_title_input"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = recordFacility,
                                onValueChange = { recordFacility = it },
                                label = { Text("Hospital / PHC / Doctor Name") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("record_facility_input"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = recordDetails,
                                onValueChange = { recordDetails = it },
                                label = { Text("Symptom Timeline, Vitals or Report Summary *") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("record_details_input"),
                                minLines = 2
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = recordQuestions,
                                onValueChange = { recordQuestions = it },
                                label = { Text("Questions to Ask the Doctor (OPD Checklist)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("record_questions_input"),
                                minLines = 2
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("ROUTINE", "LOW", "MODERATE", "HIGH").forEach { prio ->
                                    FilterChip(
                                        selected = recordPriority == prio,
                                        onClick = { recordPriority = prio },
                                        label = { Text(prio) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    onAddHealthRecord(
                                        recordTitle,
                                        recordCategory,
                                        recordDetails,
                                        recordFacility,
                                        recordQuestions,
                                        recordPriority
                                    )
                                    recordTitle = ""
                                    recordDetails = ""
                                    recordQuestions = ""
                                },
                                enabled = recordTitle.isNotBlank() && recordDetails.isNotBlank(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("save_health_record_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save to Cloud Health Timeline")
                            }
                        }
                    }
                }

                when (healthRecordsState) {
                    is UiState.Loading -> {
                        item {
                            CircularProgressIndicator(modifier = Modifier.padding(24.dp))
                        }
                    }
                    is UiState.Error -> {
                        item {
                            Text(
                                text = healthRecordsState.message,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    is UiState.Success -> {
                        val records = healthRecordsState.data
                        if (records.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "Your Personal Health Timeline is empty.",
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Text(
                                            text = "Prepare questions before your next PHC/Hospital OPD visit or load a sample template.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        OutlinedButton(onClick = onSeedSampleData) {
                                            Text("Load Sample Visit Prep & Reminder")
                                        }
                                    }
                                }
                            }
                        } else {
                            items(records, key = { it.id }) { record ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("health_record_card_${record.id}"),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                color = MaterialTheme.colorScheme.primaryContainer,
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(
                                                    text = "${record.category} • ${record.severityOrPriority}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                            Row {
                                                IconButton(onClick = { onToggleRecordCompleted(record) }) {
                                                    Icon(
                                                        imageVector = if (record.completed) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                                        contentDescription = "Mark Visit Completed",
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                                IconButton(onClick = { onDeleteHealthRecord(record.id) }) {
                                                    Icon(
                                                        imageVector = Icons.Default.DeleteOutline,
                                                        contentDescription = "Delete Record",
                                                        tint = MaterialTheme.colorScheme.error
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = record.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            textDecoration = if (record.completed) TextDecoration.LineThrough else TextDecoration.None
                                        )
                                        if (record.facilityOrDoctor.isNotBlank()) {
                                            Text(
                                                text = "Facility / Doctor: ${record.facilityOrDoctor}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = record.details, style = MaterialTheme.typography.bodyMedium)
                                        if (record.questionsForDoctor.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Surface(
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = "Questions for Doctor:\n${record.questionsForDoctor}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    modifier = Modifier.padding(10.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Section 1: Smart Health Reminders
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("reminders_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Add Smart Health Reminder",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("MEDICINE", "VACCINE", "APPOINTMENT", "CHECKUP").forEach { type ->
                                    FilterChip(
                                        selected = reminderType == type,
                                        onClick = { reminderType = type },
                                        label = { Text(type) }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = reminderTitle,
                                onValueChange = { reminderTitle = it },
                                label = { Text("Medicine / Vaccine / Checkup Name *") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reminder_title_input"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = reminderNote,
                                onValueChange = { reminderNote = it },
                                label = { Text("Dosage or Instructions (e.g., After breakfast)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reminder_note_input"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = reminderTime,
                                onValueChange = { reminderTime = it },
                                label = { Text("Schedule Time (e.g., 08:00 AM Daily) *") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reminder_time_input"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    onAddReminder(reminderTitle, reminderNote, reminderTime, reminderType)
                                    reminderTitle = ""
                                    reminderNote = ""
                                },
                                enabled = reminderTitle.isNotBlank() && reminderTime.isNotBlank(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("save_reminder_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save Smart Reminder")
                            }
                        }
                    }
                }

                when (remindersState) {
                    is UiState.Loading -> {
                        item { CircularProgressIndicator(modifier = Modifier.padding(24.dp)) }
                    }
                    is UiState.Error -> {
                        item { Text(remindersState.message, color = MaterialTheme.colorScheme.error) }
                    }
                    is UiState.Success -> {
                        items(remindersState.data, key = { it.id }) { reminder ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reminder_card_${reminder.id}"),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${reminder.reminderType} • ${reminder.scheduleTime}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = reminder.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            textDecoration = if (reminder.takenToday) TextDecoration.LineThrough else TextDecoration.None
                                        )
                                        if (reminder.dosageOrNote.isNotBlank()) {
                                            Text(
                                                text = reminder.dosageOrNote,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Row {
                                        IconButton(onClick = { onToggleReminderTaken(reminder) }) {
                                            Icon(
                                                imageVector = if (reminder.takenToday) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                                contentDescription = "Mark Dose Taken Today",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        IconButton(onClick = { onDeleteReminder(reminder.id) }) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Delete Reminder",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
