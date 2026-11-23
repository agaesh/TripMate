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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tripmate.data.model.TripActivityEntity
import com.example.tripmate.ui.components.ConfirmMessageBox
import com.example.tripmate.ui.components.CustomTextField
import com.example.tripmate.ui.components.DateTimePickerRow
import com.example.tripmate.ui.components.InfoMessageBox
import com.example.tripmate.ui.components.TripHeaderCoverCard
import com.example.tripmate.ui.theme.TripMateTheme
import com.example.tripmate.util.ValidationUtils

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
    activityId: String? = null,
    initialTitle: String = "",
    initialCategory: ActivityCategory = ActivityCategory.SIGHTSEEING,
    initialCost: Double = 0.0,
    initialDateMillis: Long? = null,
    initialHour: Int = 10,
    initialMinute: Int = 0,
    initialLocation: String = "",
    initialDescription: String = "",
    existingActivities: List<TripActivityEntity> = emptyList(),
    showSuccessDialog: Boolean = true,
    onNavigateBack: () -> Unit = {},
    onSaveActivity: (
        title: String,
        category: ActivityCategory,
        cost: Double,
        dateMillis: Long,
        hour: Int,
        minute: Int,
        location: String,
        description: String
    ) -> Unit = { _, _, _, _, _, _, _, _ -> }
) {
    val isEditMode = activityId != null

    var isInitialized by remember(activityId) { mutableStateOf(false) }

    var showSuccessAlert by remember { mutableStateOf(false) }
    var validationErrorMessage by remember { mutableStateOf<String?>(null) }

    var title by remember(activityId) { mutableStateOf(initialTitle) }
    var selectedCategory by remember(activityId) { mutableStateOf(initialCategory) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var costText by remember(activityId) { mutableStateOf(if (initialCost > 0) initialCost.toString() else "") }

    var selectedDateMillis by remember(activityId) { mutableStateOf(initialDateMillis ?: System.currentTimeMillis()) }
    var selectedHour by remember(activityId) { mutableStateOf(initialHour) }
    var selectedMinute by remember(activityId) { mutableStateOf(initialMinute) }

    var location by remember(activityId) { mutableStateOf(initialLocation) }
    var description by remember(activityId) { mutableStateOf(initialDescription) }

    LaunchedEffect(activityId) {
        if (!isInitialized) {
            title = initialTitle
            selectedCategory = initialCategory
            costText = if (initialCost > 0) initialCost.toString() else ""
            selectedDateMillis = initialDateMillis ?: System.currentTimeMillis()
            selectedHour = initialHour
            selectedMinute = initialMinute
            location = initialLocation
            description = initialDescription
            isInitialized = true
        }
    }

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
            TripHeaderCoverCard(
                tripName = tripName,
                tripInfo = tripInfo,
                isEditMode = isEditMode
            )

            CustomTextField(
                value = title,
                onValueChange = { title = it },
                label = "Activity Title *",
                placeholder = "e.g., Flight to Paris, Louvre Museum Tour",
                leadingIcon = Icons.Outlined.Edit,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

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

            CustomTextField(
                value = costText,
                onValueChange = { input ->
                    costText = input.filter { char -> char.isDigit() || char == '.' }
                },
                label = "Cost Allocation ($)",
                placeholder = "e.g., 150.00",
                leadingIcon = Icons.Outlined.Info,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

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

            CustomTextField(
                value = location,
                onValueChange = { location = it },
                label = "Location / Venue",
                placeholder = "e.g., Rue de Rivoli, 75001 Paris",
                leadingIcon = Icons.Outlined.LocationOn,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

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

            Button(
                onClick = {
                    val trimmedTitle = title.trim()
                    if (trimmedTitle.isBlank()) {
                        validationErrorMessage = "Activity Title is required and cannot be blank."
                        return@Button
                    }

                    val hasOverlap = ValidationUtils.hasActivityTimeOverlap(
                        existingActivities = existingActivities,
                        currentActivityId = activityId,
                        dateMillis = selectedDateMillis,
                        hour = selectedHour,
                        minute = selectedMinute
                    )

                    if (hasOverlap) {
                        validationErrorMessage = "Schedule Conflict: An activity is already scheduled for this exact date and time. Please adjust the time."
                        return@Button
                    }

                    val parsedCost = costText.toDoubleOrNull() ?: 0.0
                    onSaveActivity(
                        trimmedTitle,
                        selectedCategory,
                        parsedCost,
                        selectedDateMillis,
                        selectedHour,
                        selectedMinute,
                        location.trim(),
                        description.trim()
                    )
                    showSuccessAlert = showSuccessDialog
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
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

        // Custom Validation / Overlap Error Modal using InfoMessageBox
        validationErrorMessage?.let { message ->
            InfoMessageBox(
                title = "Cannot Save Activity",
                message = message,
                buttonText = "OK",
                buttonColor = Color(0xFFD32F2F),
                onDismiss = { validationErrorMessage = null },
            )
        }

        if (showSuccessAlert) {
            ConfirmMessageBox(
                title = if (isEditMode) "Successfully Updated!" else "Successfully Saved!",
                message = if (isEditMode) {
                    "Activity details have been updated."
                } else {
                    "Activity saved! You can add another activity or return to itinerary."
                },
                confirmButtonText = if (isEditMode) "Back to Itinerary" else "Add Another",
                confirmButtonColor = Color(0xFF1976D2),
                dismissButtonText = if (!isEditMode) "Back to Itinerary" else null,
                onConfirm = {
                    showSuccessAlert = false
                    if (isEditMode) {
                        onNavigateBack()
                    } else {
                        title = ""
                        costText = ""
                        location = ""
                        description = ""
                        selectedCategory = ActivityCategory.SIGHTSEEING
                    }
                },
                onDismiss = {
                    showSuccessAlert = false
                    onNavigateBack()
                },
                onDismissRequest = {
                    showSuccessAlert = false
                },
            )
        }
    }
}
