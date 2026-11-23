package com.example.tripmate.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.tripmate.ui.components.TripMateNavigationDrawer
import com.example.tripmate.ui.theme.TripMateTheme
import kotlinx.coroutines.launch

private val DashboardPurple = Color(0xFF7B1FA2)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToTripManager: () -> Unit,
    onAddTripClick: () -> Unit, // retained for signature compatibility if needed
    modifier: Modifier = Modifier,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            TripMateNavigationDrawer(
                selectedRoute = "dashboard",
                onNavigateToDashboard = { scope.launch { drawerState.close() } },
                onNavigateToTripManager = {
                    scope.launch { drawerState.close() }
                    onNavigateToTripManager()
                },
                onCloseDrawer = { scope.launch { drawerState.close() } },
            )
        },
    ) {
        Scaffold(
            modifier = modifier,
            containerColor = Color(0xFFF8F9FA),
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open navigation drawer",
                                tint = DashboardPurple,
                            )
                        }
                    },
                    title = {
                        Text(text = "Dashboard", fontWeight = FontWeight.Bold)
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                )
            },
        ) { innerPadding ->
            // Blank canvas template ready for other developers
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Dashboard - Ready for implementation",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    TripMateTheme {
        DashboardScreen(
            onNavigateToTripManager = {},
            onAddTripClick = {},
        )
    }
}
