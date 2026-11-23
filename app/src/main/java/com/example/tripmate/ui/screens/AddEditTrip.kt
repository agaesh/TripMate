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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tripmate.data.model.Trip
import com.example.tripmate.ui.components.ConfirmMessageBox
import com.example.tripmate.ui.components.CustomTextField
import com.example.tripmate.ui.components.DatePickerField
import com.example.tripmate.ui.components.InfoMessageBox
import com.example.tripmate.ui.components.TripHeaderCoverCard
import com.example.tripmate.ui.theme.TripMateTheme
import com.example.tripmate.util.ValidationUtils
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTripScreen(
    initialTrip: Trip? = null,
    onNavigateBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToItinerary: (tripId: String) -> Unit = {},
    onSaveTrip: (Trip) -> Unit = {},
) {
    val isEditMode = initialTrip != null

    var title by remember(initialTrip) { mutableStateOf(initialTrip?.title ?: "") }
    var fromPlace by remember(initialTrip) { mutableStateOf(initialTrip?.fromPlace ?: "") }
    var toPlace by remember(initialTrip) { mutableStateOf(initialTrip?.toPlace ?: "") }
    var imageUrl by remember(initialTrip) { mutableStateOf(initialTrip?.imageUrl ?: "") }

    var startDateMillis by remember(initialTrip) {
        mutableLongStateOf(initialTrip?.startDateMillis ?: System.currentTimeMillis())
    }
    var endDateMillis by remember(initialTrip) {
        mutableLongStateOf(
            initialTrip?.endDateMillis ?: (System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L)),
        )
    }

    var validationErrorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var savedTrip by remember { mutableStateOf<Trip?>(null) }

    // Auto-calculate days & duration string
    val diffMillis = maxOf(0L, endDateMillis - startDateMillis)
    val calculatedDays = maxOf(1, (diffMillis / (24 * 60 * 60 * 1000L)).toInt() + 1)
    val durationText = "$calculatedDays Days / ${maxOf(0, calculatedDays - 1)} Nights"

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Trip" else "Add New Trip",
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Live Trip Header Cover Card Preview
            TripHeaderCoverCard(
                tripName = title.ifBlank { "New Trip Destination" },
                tripInfo = "$durationText • ${toPlace.ifBlank { "Destination" }}",
                isEditMode = isEditMode,
            )

            // Trip Title Field
            CustomTextField(
                value = title,
                onValueChange = { title = it },
                label = "Trip Title *",
                placeholder = "e.g., Paris Summer Vacation, Tokyo Adventure",
                leadingIcon = Icons.Outlined.Edit,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            // Origin / From Place
            CustomTextField(
                value = fromPlace,
                onValueChange = { fromPlace = it },
                label = "Departure City / Origin *",
                placeholder = "e.g., London, UK or San Francisco, USA",
                leadingIcon = Icons.Outlined.LocationOn,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            // Destination / To Place
            CustomTextField(
                value = toPlace,
                onValueChange = { toPlace = it },
                label = "Destination City / Country *",
                placeholder = "e.g., Paris, France or Tokyo, Japan",
                leadingIcon = Icons.Outlined.LocationOn,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            // Date Selection Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                DatePickerField(
                    selectedDateMillis = startDateMillis,
                    onDateSelected = { startDateMillis = it },
                    label = "Start Date",
                    modifier = Modifier.weight(1f),
                )
                DatePickerField(
                    selectedDateMillis = endDateMillis,
                    onDateSelected = { endDateMillis = it },
                    label = "End Date",
                    modifier = Modifier.weight(1f),
                )
            }

            // Cover Image URL Field
            CustomTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = "Cover Image URL (Optional)",
                placeholder = "https://images.unsplash.com/...",
                leadingIcon = Icons.Outlined.Info,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Save / Update Button
            Button(
                onClick = {
                    val trimmedTitle = title.trim()
                    val trimmedFrom = fromPlace.trim()
                    val trimmedTo = toPlace.trim()

                    if (trimmedTitle.isBlank() || trimmedFrom.isBlank() || trimmedTo.isBlank()) {
                        validationErrorMessage = "Please fill in all required fields: Trip Title, Origin, and Destination."
                        return@Button
                    }

                    if (!ValidationUtils.isValidTripDates(startDateMillis, endDateMillis)) {
                        validationErrorMessage = "Invalid Trip Dates: End Date cannot be earlier than Start Date."
                        return@Button
                    }

                    val tripObj = Trip(
                        id = initialTrip?.id ?: UUID.randomUUID().toString(),
                        title = trimmedTitle,
                        fromPlace = trimmedFrom,
                        toPlace = trimmedTo,
                        days = calculatedDays,
                        duration = durationText,
                        imageUrl = imageUrl.trim(),
                        startDateMillis = startDateMillis,
                        endDateMillis = endDateMillis,
                    )
                    onSaveTrip(tripObj)
                    savedTrip = tripObj
                    showSuccessDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7B1FA2),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF7B1FA2).copy(alpha = 0.4f),
                    disabledContentColor = Color.White.copy(alpha = 0.7f),
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 3.dp,
                    pressedElevation = 6.dp,
                    disabledElevation = 0.dp,
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = if (isEditMode) Icons.Outlined.Edit else Icons.Outlined.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEditMode) "Update Trip" else "Save Trip",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Custom Validation Error Modal using InfoMessageBox
        validationErrorMessage?.let { message ->
            InfoMessageBox(
                title = "Cannot Proceed",
                message = message,
                buttonText = "OK",
                buttonColor = Color(0xFFD32F2F),
                onDismiss = { validationErrorMessage = null },
            )
        }

        // Post-Save Choice ConfirmMessageBox
        if (showSuccessDialog) {
            ConfirmMessageBox(
                title = if (isEditMode) "Trip Successfully Updated!" else "Trip Successfully Saved!",
                message = if (isEditMode) {
                    "Your trip details have been updated."
                } else {
                    "Your new trip '${savedTrip?.title}' has been created!"
                },
                confirmButtonText = "View Trip Itinerary",
                confirmButtonColor = Color(0xFF7B1FA2),
                dismissButtonText = "Go to Trip List",
                onConfirm = {
                    showSuccessDialog = false
                    savedTrip?.let { onNavigateToItinerary(it.id) } ?: onNavigateToHome()
                },
                onDismiss = {
                    showSuccessDialog = false
                    onNavigateToHome()
                },
                onDismissRequest = {
                    showSuccessDialog = false
                    onNavigateToHome()
                },
            )
        }
    }
}
