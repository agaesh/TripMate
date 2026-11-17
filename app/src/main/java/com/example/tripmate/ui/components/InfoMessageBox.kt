package com.example.tripmate.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tripmate.ui.theme.TripMateTheme

@Composable
fun InfoMessageBox(
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Notice",
    buttonText: String = "Okay",
    icon: ImageVector = Icons.Outlined.Info,
    buttonColor: Color = Color(0xFF1976D2),
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = buttonColor,
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            ) {
                Text(
                    text = buttonText,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
    )
}

@Preview(showBackground = true)
@Composable
fun InfoMessageBoxPreview() {
    TripMateTheme {
        InfoMessageBox(
            title = "Schedule Conflict",
            message = "Existing activity schedule on this time.",
            onDismiss = {},
        )
    }
}
