package com.example.tripmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.tripmate.ui.screens.HomeScreen
import com.example.tripmate.ui.theme.TripMateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripMateTheme {
                HomeScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}