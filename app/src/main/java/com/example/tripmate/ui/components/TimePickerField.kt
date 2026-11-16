package com.example.tripmate.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerField(
    selectedHour: Int,
    selectedMinute: Int,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Time",
    is24Hour: Boolean = false
) {
    var showDialog by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = selectedHour,
        initialMinute = selectedMinute,
        is24Hour = is24Hour
    )

    val formattedTime = remember(selectedHour, selectedMinute, is24Hour) {
        if (is24Hour) {
            String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
        } else {
            val period = if (selectedHour >= 12) "PM" else "AM"
            val hour12 = when (val h = selectedHour % 12) {
                0 -> 12
                else -> h
            }
            String.format(Locale.getDefault(), "%02d:%02d %s", hour12, selectedMinute, period)
        }
    }

    Surface(
        modifier = modifier.clickable { showDialog = true }
    ) {
        CustomTextField(
            value = formattedTime,
            onValueChange = {},
            label = label,
            leadingIcon = Icons.Outlined.Info,
            singleLine = true,
            readOnly = true,
            enabled = true
        )
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(state = timePickerState)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                        TextButton(onClick = {
                            onTimeSelected(timePickerState.hour, timePickerState.minute)
                            showDialog = false
                        }) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}
