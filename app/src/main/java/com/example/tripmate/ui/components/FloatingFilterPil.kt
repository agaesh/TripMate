package com.example.tripmate.ui.components
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Define the custom AccentPurple color required by the pill design
val AccentPurple = Color(0xFF6366F1) // Indigo/Purple hex code example

@Composable
fun FloatingFilterPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Set the dynamic container and content colors based on selection state
    val containerColor = if (isSelected) AccentPurple else Color.White
    val contentColor = if (isSelected) Color.White else Color(0xFF334155)

    // 2. Apply a border only when the pill is not selected
    val border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFCBD5E1))

    // 3. Material 3 Surface which natively supports the onClick parameter
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        contentColor = contentColor,
        border = border,
        tonalElevation = if (isSelected) 4.dp else 0.dp,
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}
