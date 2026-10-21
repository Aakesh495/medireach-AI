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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.HospitalFacility

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CareFinderScreen(
    hospitals: List<HospitalFacility>,
    onPrepareVisitForFacility: (facilityName: String) -> Unit
) {
    val context = LocalContext.current
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf("ALL") } // ALL, TERTIARY, RURAL_CHC_PHC, DESERT_ZONE, JANAUSHADHI

    val filteredHospitals = hospitals.filter { facility ->
        val matchesQuery = searchQuery.isBlank() ||
            facility.name.contains(searchQuery, ignoreCase = true) ||
            facility.stateAndDistrict.contains(searchQuery, ignoreCase = true) ||
            facility.specialties.any { it.contains(searchQuery, ignoreCase = true) }

        val matchesFilter = when (selectedFilter) {
            "TERTIARY" -> facility.facilityType.contains("AIIMS") || facility.facilityType.contains("District") || facility.facilityType.contains("Tertiary")
            "RURAL_CHC_PHC" -> facility.facilityType.contains("CHC") || facility.facilityType.contains("PHC")
            "DESERT_ZONE" -> facility.isUnderservedZone
            "JANAUSHADHI" -> facility.facilityType.contains("Jan Aushadhi")
            else -> true
        }
        matchesQuery && matchesFilter
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("care_finder_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header
        item {
            Column {
                Text(
                    text = "Care Finder, Capacity & Desert Detector",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Locate AIIMS, District Hospitals, Rural CHC/PHCs, Jan Aushadhi Pharmacies & check real-time bed/oxygen capacity.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 2. Search & Category Filter Chips (Features #6, #12, #16)
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                label = { Text("Search hospital, district, specialty or Jan Aushadhi...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("care_search_input"),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text("All Facilities (${hospitals.size})") },
                    modifier = Modifier.testTag("filter_all")
                )
                FilterChip(
                    selected = selectedFilter == "TERTIARY",
                    onClick = { selectedFilter = "TERTIARY" },
                    label = { Text("AIIMS & District HQ") },
                    modifier = Modifier.testTag("filter_tertiary")
                )
                FilterChip(
                    selected = selectedFilter == "RURAL_CHC_PHC",
                    onClick = { selectedFilter = "RURAL_CHC_PHC" },
                    label = { Text("Rural CHC / PHC") },
                    modifier = Modifier.testTag("filter_rural")
                )
                FilterChip(
                    selected = selectedFilter == "DESERT_ZONE",
                    onClick = { selectedFilter = "DESERT_ZONE" },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = { Text("Healthcare Desert Detector") },
                    modifier = Modifier.testTag("filter_desert")
                )
                FilterChip(
                    selected = selectedFilter == "JANAUSHADHI",
                    onClick = { selectedFilter = "JANAUSHADHI" },
                    label = { Text("Jan Aushadhi Generic") },
                    modifier = Modifier.testTag("filter_janaushadhi")
                )
            }
        }

        // 3. Healthcare Desert Detector Insights Banner (Feature #16)
        if (selectedFilter == "DESERT_ZONE") {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Healthcare Desert Detector — Rural Access Gap Protocol",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Blocks where the nearest ICU/FRU is >30 km away are flagged below. Recommended Action: Use eSanjeevani Tele-OPD at the local Sub-Centre/PHC for non-emergencies, or pre-alert 108 ALS Ambulance early for golden-hour transfers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }

        // 4. Facility & Smart Capacity Cards (Features #6, #12)
        items(filteredHospitals, key = { it.id }) { facility ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hospital_card_${facility.id}"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalHospital,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = facility.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "${facility.facilityType} • ${facility.stateAndDistrict} (${facility.distanceKm} km)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (facility.ayushmanEmpanelled) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "PM-JAY / BSKY",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }

                    if (facility.isUnderservedZone) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "⚠️ Underserved Rural Zone: Nearest ICU/Tertiary Referral is ${facility.nearestReferralKm} km away. Pre-book 108 or eSanjeevani.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Smart Hospital Capacity Dashboard Metrics
                    if (facility.generalBedsTotal > 0) {
                        val bedRatio = facility.generalBedsAvailable.toFloat() / facility.generalBedsTotal.toFloat()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "General Beds: ${facility.generalBedsAvailable}/${facility.generalBedsTotal} Open",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = "ICU Beds: ${facility.icuBedsAvailable}/${facility.icuBedsTotal} Open",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (facility.icuBedsAvailable > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { bedRatio.coerceIn(0.05f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "O2 Status: ${facility.oxygenStatus}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Est. OPD Wait: ~${facility.opdWaitMinutes} mins",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        facility.specialties.forEach { spec ->
                            AssistChip(
                                onClick = { onPrepareVisitForFacility(facility.name) },
                                label = { Text(spec, style = MaterialTheme.typography.bodySmall) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${facility.emergencyPhone}"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("call_facility_${facility.id}")
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Call (${facility.emergencyPhone})")
                        }
                        OutlinedButton(
                            onClick = {
                                val mapUri = Uri.parse("geo:0,0?q=${Uri.encode("${facility.name} ${facility.stateAndDistrict}")}")
                                val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                                context.startActivity(mapIntent)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("directions_facility_${facility.id}")
                        ) {
                            Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Directions")
                        }
                    }
                }
            }
        }
    }
}
