package com.example.tripmate.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tripmate.ui.components.CustomTextField
import com.example.tripmate.ui.components.DateTimePickerRow
import com.example.tripmate.ui.components.TripHeaderCoverCard
import com.example.tripmate.ui.theme.TripMateTheme

enum class ActivityCategory(val label: String, val emoji: String) {
    TRANSPORT("Transport", "✈️"),
    HOTEL("Accommodation", "🏨"),
    SIGHTSEEING("Sightseeing", "🎟️"),
    DINING("Dining", "🍕"),
    OTHER("Other", "📝")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityScreen(
    tripId: String = "",
    tripName: String = "Paris Summer Trip 2026",
    tripInfo: String = "7 Days • France",
    activityId: String? = null, // null = Add Mode, non-null = Edit Mode
    initialTitle: String = "",
    initialCategory: ActivityCategory = ActivityCategory.SIGHTSEEING,
    initialDateMillis: Long? = null,
    initialHour: Int = 10,
    initialMinute: Int = 0,
    initialLocation: String = "",
    initialDescription: String = "",
    onNavigateBack: () -> Unit = {},
    onSaveActivity: (
        title: String,
        category: ActivityCategory,
        dateMillis: Long,
        hour: Int,
        minute: Int,
        location: String,
        description: String
    ) -> Unit = { _, _, _, _, _, _, _ -> }
) {
    val isEditMode = activityId != null

    var title by remember { mutableStateOf(initialTitle) }
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    // Date & Time Selection State
    var selectedDateMillis by remember { mutableStateOf(initialDateMillis ?: System.currentTimeMillis()) }
    var selectedHour by remember { mutableStateOf(initialHour) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }

    var location by remember { mutableStateOf(initialLocation) }
    var description by remember { mutableStateOf(initialDescription) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Activity" else "Add Activity",
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
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Trip Header Cover Card
            TripHeaderCoverCard(
                tripName = tripName,
                tripInfo = tripInfo,
                isEditMode = isEditMode
            )

            // Activity Title
            CustomTextField(
                value = title,
                onValueChange = { title = it },
                label = "Activity Title *",
                placeholder = "e.g., Flight to Paris, Louvre Museum Tour",
                leadingIcon = Icons.Outlined.Edit,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Category Selection (Dropdown Combobox)
            ExposedDropdownMenuBox(
                expanded = categoryDropdownExpanded,
                onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                CustomTextField(
                    value = "${selectedCategory.emoji}  ${selectedCategory.label}",
                    onValueChange = {},
                    label = "Category",
                    leadingIcon = Icons.Outlined.Info,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded)
                    },
                    readOnly = true,
                    singleLine = true,
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false }
                ) {
                    ActivityCategory.entries.forEach { category ->
                        DropdownMenuItem(
                            text = { Text("${category.emoji}  ${category.label}") },
                            onClick = {
                                selectedCategory = category
                                categoryDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Reusable Date & Time Picker Component
            DateTimePickerRow(
                selectedDateMillis = selectedDateMillis,
                onDateSelected = { selectedDateMillis = it },
                selectedHour = selectedHour,
                selectedMinute = selectedMinute,
                onTimeSelected = { hour, minute ->
                    selectedHour = hour
                    selectedMinute = minute
                }
            )

            // Location Input
            CustomTextField(
                value = location,
                onValueChange = { location = it },
                label = "Location / Venue",
                placeholder = "e.g., Rue de Rivoli, 75001 Paris",
                leadingIcon = Icons.Outlined.LocationOn,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Description / Notes Input
            CustomTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description & Notes",
                placeholder = "Enter booking numbers, notes, or itinerary details...",
                leadingIcon = Icons.Outlined.Info,
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Save / Update Button
            Button(
                onClick = {
                    onSaveActivity(
                        title,
                        selectedCategory,
                        selectedDateMillis,
                        selectedHour,
                        selectedMinute,
                        location,
                        description
                    )
                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1976D2),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF1976D2).copy(alpha = 0.4f),
                    disabledContentColor = Color.White.copy(alpha = 0.7f)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 3.dp,
                    pressedElevation = 6.dp,
                    disabledElevation = 0.dp
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isEditMode) Icons.Outlined.Edit else Icons.Outlined.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEditMode) "Update Activity" else "Save Activity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddActivityScreenPreview() {
    TripMateTheme {
        AddActivityScreen(
            tripId = "sample_trip",
            tripName = "Paris Summer Vacation",
            tripInfo = "7 Days • France",
            activityId = null
        )
    }
}
