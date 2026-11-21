package com.example.tripmate.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tripmate.data.TripActivity
import com.example.tripmate.data.getSampleActivities
import com.example.tripmate.ui.theme.TripMateTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItineraryScreen(
    activities: List<TripActivity> = emptyList(),
    onNavigateBack: () -> Unit = {}
) {
    var selectedTrip by remember { mutableStateOf("All Trips") }
    var selectedDate by remember { mutableStateOf("All Dates") }

    val filteredActivities = activities.filter { activity ->
        (selectedTrip == "All Trips" || activity.trip == selectedTrip) &&
                (selectedDate == "All Dates" || activity.status == selectedDate)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Itinerary",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Your Travel Activities",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "View and organise your planned activities.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Trip Filter
            Text(
                text = "Filter by Trip",
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All Trips", "Paris", "Tokyo").forEach { trip ->
                    FilterChip(
                        selected = selectedTrip == trip,
                        onClick = { selectedTrip = trip },
                        label = { Text(trip) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status Filter
            Text(
                text = "Filter by Status",
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All Dates", "Upcoming", "Past").forEach { date ->
                    FilterChip(
                        selected = selectedDate == date,
                        onClick = { selectedDate = date },
                        label = { Text(date) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${filteredActivities.size} Activities Found",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredActivities.isEmpty()) {
                Text(
                    text = "No activities found. Try adding one with the + button!",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredActivities) { activity ->
                        ItineraryActivityCard(activity)
                    }
                }
            }
        }
    }
}

@Composable
private fun ItineraryActivityCard(
    activity: TripActivity
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = activity.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${activity.category.emoji} ${activity.category.label} • ${activity.trip}",
                color = MaterialTheme.colorScheme.primary
            )

            if (activity.location.isNotBlank()) {
                Row {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(activity.location)
                }
            }

            Row {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("${activity.formattedDate} • ${activity.formattedTime}")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItineraryScreenPreview() {
    TripMateTheme {
        ItineraryScreen(activities = getSampleActivities())
    }
}