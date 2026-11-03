package com.example.tripmate.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tripmate.ui.theme.TripMateTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    var counter by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("TripMate", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to TripMate!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your journey starts here.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Trips planned: $counter",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { counter++ }) {
                Text("Add New Trip")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TripMateTheme {
        HomeScreen()
    }
}
