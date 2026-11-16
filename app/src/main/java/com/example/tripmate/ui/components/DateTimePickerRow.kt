package com.example.tripmate.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DateTimePickerRow(
    selectedDateMillis: Long,
    onDateSelected: (Long) -> Unit,
    selectedHour: Int,
    selectedMinute: Int,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Date & Time"
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DatePickerField(
                selectedDateMillis = selectedDateMillis,
                onDateSelected = onDateSelected,
                modifier = Modifier.weight(1f)
            )

            TimePickerField(
                selectedHour = selectedHour,
                selectedMinute = selectedMinute,
                onTimeSelected = onTimeSelected,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
