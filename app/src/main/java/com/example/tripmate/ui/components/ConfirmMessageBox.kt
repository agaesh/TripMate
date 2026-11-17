package com.example.tripmate.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tripmate.ui.theme.TripMateTheme

@Composable
fun ConfirmMessageBox(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    confirmButtonText: String = "OK",
    confirmButtonColor: Color = MaterialTheme.colorScheme.primary,
    dismissButtonText: String? = null,
    onDismiss: (() -> Unit)? = null,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        icon = icon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = confirmButtonColor,
                )
            }
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
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = confirmButtonColor),
            ) {
                Text(
                    text = confirmButtonText,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        },
        dismissButton = if (dismissButtonText != null) {
            {
                TextButton(
                    onClick = {
                        onDismiss?.invoke() ?: onDismissRequest()
                    },
                ) {
                    Text(
                        text = dismissButtonText,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        } else null,
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
    )
}

@Preview(showBackground = true)
@Composable
fun ConfirmMessageBoxPreview() {
    TripMateTheme {
        ConfirmMessageBox(
            title = "Successfully Saved!",
            message = "Activity saved successfully. Would you like to add another activity?",
            confirmButtonText = "Add Another",
            dismissButtonText = "Back to Home",
            onConfirm = {},
            onDismissRequest = {},
        )
    }
}
