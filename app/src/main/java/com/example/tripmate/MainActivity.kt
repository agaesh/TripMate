package com.example.tripmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tripmate.data.local.TripDatabase
import com.example.tripmate.data.repository.TripActivityRepository
import com.example.tripmate.data.repository.TripRepository
import com.example.tripmate.ui.screens.AddActivityScreen
import com.example.tripmate.ui.screens.HomeScreen
import com.example.tripmate.ui.theme.TripMateTheme
import com.example.tripmate.ui.viewmodels.TripActivityViewModel
import com.example.tripmate.ui.viewmodels.TripActivityViewModelFactory
import com.example.tripmate.ui.viewmodels.TripViewModel
import com.example.tripmate.ui.viewmodels.TripViewModelFactory

sealed interface Screen {
    data object Home : Screen
    data class AddEditActivity(
        val tripId: String = "paris_2026",
        val tripName: String = "Paris Summer Vacation",
        val tripInfo: String = "7 Days • France",
        val activityId: String? = null,
    ) : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = TripDatabase.getDatabase(this)
        val tripRepository = TripRepository(database.tripDao())
        val activityRepository = TripActivityRepository(database.tripActivityDao())
        val tripFactory = TripViewModelFactory(tripRepository)
        val activityFactory = TripActivityViewModelFactory(activityRepository)

        setContent {
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
            val tripViewModel: TripViewModel = viewModel(factory = tripFactory)
            val activityViewModel: TripActivityViewModel = viewModel(factory = activityFactory)

            val trips by tripViewModel.pagedTrips.collectAsState()
            val searchQuery by tripViewModel.searchQuery.collectAsState()
            val selectedFilter by tripViewModel.selectedFilter.collectAsState()
            val isLoadingMore by tripViewModel.isLoadingMore.collectAsState()
            val hasMore by tripViewModel.hasMore.collectAsState()

            TripMateTheme {
                when (val screen = currentScreen) {
                    is Screen.Home -> {
                        HomeScreen(
                            modifier = Modifier.fillMaxSize(),
                            trips = trips,
                            searchQuery = searchQuery,
                            selectedFilter = selectedFilter,
                            isLoadingMore = isLoadingMore,
                            hasMore = hasMore,
                            onSearchQueryChange = { query -> tripViewModel.setSearchQuery(query) },
                            onFilterSelected = { filter -> tripViewModel.setSelectedFilter(filter) },
                            onLoadMore = { tripViewModel.loadMoreTrips() },
                            onDeleteTrip = { trip -> tripViewModel.deleteTrip(trip) },
                            onSelectTrip = { selectedTrip ->
                                tripViewModel.setSelectedTripId(selectedTrip.id)
                                activityViewModel.setSelectedTripId(selectedTrip.id)
                                currentScreen = Screen.AddEditActivity(
                                    tripId = selectedTrip.id,
                                    tripName = selectedTrip.title,
                                    tripInfo = "${selectedTrip.days} Days • ${selectedTrip.toPlace}",
                                )
                            },
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
                            onSaveActivity = { title, category, cost, dateMillis, hour, minute, location, description ->
                                activityViewModel.saveActivity(
                                    activityId = screen.activityId,
                                    tripId = screen.tripId,
                                    title = title,
                                    category = category,
                                    cost = cost,
                                    dateMillis = dateMillis,
                                    hour = hour,
                                    minute = minute,
                                    location = location,
                                    description = description,
                                )
                                currentScreen = Screen.Home
                            },
                        )
                    }
                }
            }
        }
    }
}
