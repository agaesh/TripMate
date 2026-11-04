package com.example.tripmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.tripmate.ui.screens.AddActivityScreen
import com.example.tripmate.ui.screens.HomeScreen
import com.example.tripmate.ui.theme.TripMateTheme

sealed interface Screen {
    data object Home : Screen
    data class AddEditActivity(
        val tripId: String = "default_trip",
        val tripName: String = "Paris Summer Vacation",
        val tripInfo: String = "7 Days • France",
        val activityId: String? = null
    ) : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

            TripMateTheme {
                when (val screen = currentScreen) {
                    is Screen.Home -> {
                        HomeScreen(
                            modifier = Modifier.fillMaxSize(),
                            onNavigateToAddActivity = {
                                currentScreen = Screen.AddEditActivity()
                            }
                        )
                    }
                    is Screen.AddEditActivity -> {
                        AddActivityScreen(
                            tripId = screen.tripId,
                            tripName = screen.tripName,
                            tripInfo = screen.tripInfo,
                            activityId = screen.activityId,
                            onNavigateBack = {
                                currentScreen = Screen.Home
                            },
                            onSaveActivity = { _, _, _, _, _, _, _ ->
                                currentScreen = Screen.Home
                            }
                        )
                    }
                }
            }
        }
    }
}
