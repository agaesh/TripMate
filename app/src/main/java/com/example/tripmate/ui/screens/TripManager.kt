package com.example.tripmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tripmate.data.model.Trip
import com.example.tripmate.ui.components.ConfirmMessageBox
import com.example.tripmate.ui.components.FeatureMediaCard
import com.example.tripmate.ui.components.ScrollableFilterLayout
import com.example.tripmate.ui.components.TripMateNavigationDrawer
import com.example.tripmate.ui.theme.TripMateTheme
import kotlinx.coroutines.launch

val tripFilterCategories = listOf("All", "Europe", "Asia", "Americas", "Oceania", "Short Getaways")

private val AccentPurple = Color(0xFF7B1FA2)
private val AccentPurpleDark = Color(0xFF6A1B9A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripManager(
    modifier: Modifier = Modifier,
    trips: List<Trip> = emptyList(),
    budgetsByTripId: Map<String, Double> = emptyMap(),
    searchQuery: String = "",
    selectedFilter: String = "All",
    isLoading: Boolean = false,
    isLoadingMore: Boolean = false,
    hasMore: Boolean = true,
    onSearchQueryChange: (String) -> Unit = {},
    onFilterSelected: (String) -> Unit = {},
    onSelectTrip: (Trip) -> Unit = {},
    onEditTrip: (Trip) -> Unit = {},
    onDeleteTrip: (Trip) -> Unit = {},
    onLoadMore: () -> Unit = {},
    onAddTripClick: () -> Unit = {},
    onNavigateToDashboard: () -> Unit = {},
) {
    var tripToDelete by remember { mutableStateOf<Trip?>(null) }
    val listState = rememberLazyListState()
    var selectedCategory by remember { mutableStateOf(selectedFilter) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            TripMateNavigationDrawer(
                selectedRoute = "trip_manager",
                onNavigateToDashboard = {
                    scope.launch { drawerState.close() }
                    onNavigateToDashboard()
                },
                onNavigateToTripManager = { scope.launch { drawerState.close() } },
                onCloseDrawer = { scope.launch { drawerState.close() } },
            )
        },
    ) {
        Scaffold(
            containerColor = Color(0xFFF8F9FA),
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open navigation drawer",
                                tint = AccentPurple
                            )
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = AccentPurple,
                                modifier = Modifier
                                    .size(28.dp)
                                    .padding(end = 6.dp),
                            )
                            Text("TripMate", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AccentPurple.copy(alpha = 0.12f),
                            ) {
                                Text(
                                    text = "ADMIN PANEL",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentPurpleDark,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddTripClick,
                    shape = CircleShape,
                    containerColor = AccentPurple,
                    contentColor = Color.White,
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Trip",
                    )
                }
            },
            modifier = modifier,
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                // ── Stagnant Header (Search + Filter pills + Category text) ──
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("Search trips by title or destination...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = null,
                                tint = Color.Gray,
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    ScrollableFilterLayout(
                        filterCategories = tripFilterCategories,
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selected ->
                            selectedCategory = selected
                            onFilterSelected(selected)
                        },
                    ) {
                        Text(
                            text = "Category: $selectedCategory",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ── Section Title ─────────────────────────────────────
                Text(
                    text = "Trips Manager (${trips.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ── Scrollable Card Container ─────────────────────────
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    when {
                        isLoading && trips.isEmpty() -> item(key = "initial_loading") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 64.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(color = AccentPurple)
                            }
                        }

                        trips.isEmpty() -> item(key = "empty") {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 48.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = AccentPurple,
                                    modifier = Modifier.size(64.dp),
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No Trips Found",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentPurple,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Try adjusting your search query or filter pills.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }

                        else -> {
                            items(trips, key = { it.id }) { trip ->
                                FeatureMediaCard(
                                    trip = trip,
                                    budget = budgetsByTripId[trip.id] ?: 0.0,
                                    onClick = { onSelectTrip(trip) },
                                    onEditClick = { onEditTrip(trip) },
                                    onDeleteClick = { tripToDelete = trip },
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                )
                            }

                            // ── Manual Click to Load More ─────────────────────
                            if (hasMore) {
                                item(key = "load_more") {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp, horizontal = 16.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        if (isLoadingMore) {
                                            CircularProgressIndicator(
                                                color = AccentPurple,
                                                modifier = Modifier.size(28.dp),
                                                strokeWidth = 3.dp,
                                            )
                                        } else {
                                            Button(
                                                onClick = onLoadMore,
                                                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                                                shape = RoundedCornerShape(16.dp),
                                                modifier = Modifier.fillMaxWidth(0.6f),
                                            ) {
                                                Text(
                                                    text = "Load More Trips",
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                item(key = "end_of_list") {
                                    Text(
                                        text = "You've reached the end",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ── Delete confirmation ────────────────────
    tripToDelete?.let { trip ->
        ConfirmMessageBox(
            title = "Delete Trip",
            message = "Are you sure you want to delete '${trip.title}' and all its activities?",
            confirmButtonText = "Delete",
            confirmButtonColor = Color.Red,
            dismissButtonText = "Cancel",
            onConfirm = {
                onDeleteTrip(trip)
                tripToDelete = null
            },
            onDismiss = {
                tripToDelete = null
            },
            onDismissRequest = {
                tripToDelete = null
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TripManagerPreview() {
    TripMateTheme {
        TripManager(
            trips = listOf(
                Trip(
                    id = "paris_2026",
                    title = "Paris Summer Vacation",
                    fromPlace = "London, UK",
                    toPlace = "Paris, France",
                    days = 7,
                    duration = "7 Days / 6 Nights",
                    imageUrl = "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&w=600&q=80",
                    startDateMillis = System.currentTimeMillis(),
                    endDateMillis = System.currentTimeMillis(),
                ),
                Trip(
                    id = "tokyo_2026",
                    title = "Tokyo Adventure",
                    fromPlace = "San Francisco, USA",
                    toPlace = "Tokyo, Japan",
                    days = 10,
                    duration = "10 Days / 9 Nights",
                    imageUrl = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?auto=format&fit=crop&w=600&q=80",
                    startDateMillis = System.currentTimeMillis(),
                    endDateMillis = System.currentTimeMillis(),
                ),
            ),
        )
    }
}
