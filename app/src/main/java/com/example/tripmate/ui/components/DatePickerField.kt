package com.example.tripmate.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    selectedDateMillis: Long,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Date",
    dateFormat: String = "EEE, MMM dd, yyyy",
) {
    var showDialog by remember { mutableStateOf(false) }

    val dateFormatObj = remember(dateFormat) { SimpleDateFormat(dateFormat, Locale.getDefault()) }
    val formattedDate = dateFormatObj.format(Date(selectedDateMillis))

    Box(
        modifier = modifier,
    ) {
        CustomTextField(
            value = formattedDate,
            onValueChange = {},
            label = label,
            leadingIcon = Icons.Outlined.DateRange,
            singleLine = true,
            readOnly = true,
            enabled = true,
            modifier = Modifier.fillMaxWidth(),
        )

        // Transparent overlay to catch all click taps reliably
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showDialog = true },
        )
    }

    if (showDialog) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)

        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                        showDialog = false
                    },
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
