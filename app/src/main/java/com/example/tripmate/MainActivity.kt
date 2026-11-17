package com.example.tripmate

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tripmate.data.local.TripDatabase
import com.example.tripmate.data.model.Trip
import com.example.tripmate.data.repository.TripActivityRepository
import com.example.tripmate.data.repository.TripRepository
import com.example.tripmate.ui.screens.AddActivityScreen
import com.example.tripmate.ui.screens.AddEditTripScreen
import com.example.tripmate.ui.screens.HomeScreen
import com.example.tripmate.ui.theme.TripMateTheme
import com.example.tripmate.ui.viewmodels.TripActivityViewModel
import com.example.tripmate.ui.viewmodels.TripActivityViewModelFactory
import com.example.tripmate.ui.viewmodels.TripViewModel
import com.example.tripmate.ui.viewmodels.TripViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = TripDatabase.getDatabase(this)
        val tripRepository = TripRepository(database.tripDao())
        val activityRepository = TripActivityRepository(database.tripActivityDao())
        val tripFactory = TripViewModelFactory(tripRepository, activityRepository)
        val activityFactory = TripActivityViewModelFactory(activityRepository)

        setContent {
            val navController = rememberNavController()
            val tripViewModel: TripViewModel = viewModel(factory = tripFactory)
            val activityViewModel: TripActivityViewModel = viewModel(factory = activityFactory)

            val trips by tripViewModel.pagedTrips.collectAsState()
            val budgetsByTripId by tripViewModel.tripBudgets.collectAsState()
            val searchQuery by tripViewModel.searchQuery.collectAsState()
            val selectedFilter by tripViewModel.selectedFilter.collectAsState()
            val isLoadingMore by tripViewModel.isLoadingMore.collectAsState()
            val hasMore by tripViewModel.hasMore.collectAsState()

            TripMateTheme {
                NavHost(
                    navController = navController,
                    startDestination = "home",
                ) {
                    composable("home") {
                        HomeScreen(
                            modifier = Modifier.fillMaxSize(),
                            trips = trips,
                            budgetsByTripId = budgetsByTripId,
                            searchQuery = searchQuery,
                            selectedFilter = selectedFilter,
                            isLoadingMore = isLoadingMore,
                            hasMore = hasMore,
                            onSearchQueryChange = { query -> tripViewModel.setSearchQuery(query) },
                            onFilterSelected = { filter -> tripViewModel.setSelectedFilter(filter) },
                            onLoadMore = { tripViewModel.loadMoreTrips() },
                            onDeleteTrip = { trip -> tripViewModel.deleteTrip(trip) },
                            onAddTripClick = {
                                navController.navigate("add_edit_trip")
                            },
                            onEditTrip = { tripToEdit ->
                                navController.navigate("add_edit_trip?tripId=${tripToEdit.id}")
                            },
                            onSelectTrip = { selectedTrip ->
                                tripViewModel.setSelectedTripId(selectedTrip.id)
                                activityViewModel.setSelectedTripId(selectedTrip.id)
                                val encodedName = Uri.encode(selectedTrip.title)
                                val encodedInfo = Uri.encode("${selectedTrip.days} Days • ${selectedTrip.toPlace}")
                                navController.navigate("add_edit_activity?tripId=${selectedTrip.id}&tripName=$encodedName&tripInfo=$encodedInfo")
                            },
                        )
                    }

                    composable(
                        route = "add_edit_trip?tripId={tripId}",
                        arguments = listOf(
                            navArgument("tripId") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            },
                        ),
                    ) { backStackEntry ->
                        val tripId = backStackEntry.arguments?.getString("tripId")
                        val initialTrip by if (tripId != null) {
                            tripViewModel.getTripById(tripId).collectAsState(initial = null)
                        } else {
                            remember { mutableStateOf<Trip?>(null) }
                        }

                        AddEditTripScreen(
                            initialTrip = initialTrip,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onSaveTrip = { savedTrip ->
                                tripViewModel.saveTrip(savedTrip)
                                navController.popBackStack()
                            },
                        )
                    }

                    composable(
                        route = "add_edit_activity?tripId={tripId}&tripName={tripName}&tripInfo={tripInfo}&activityId={activityId}",
                        arguments = listOf(
                            navArgument("tripId") { type = NavType.StringType; defaultValue = "paris_2026" },
                            navArgument("tripName") { type = NavType.StringType; defaultValue = "Paris Summer Vacation" },
                            navArgument("tripInfo") { type = NavType.StringType; defaultValue = "7 Days • France" },
                            navArgument("activityId") { type = NavType.StringType; nullable = true; defaultValue = null },
                        ),
                    ) { backStackEntry ->
                        val tripId = backStackEntry.arguments?.getString("tripId") ?: "paris_2026"
                        val tripName = backStackEntry.arguments?.getString("tripName") ?: "Paris Summer Vacation"
                        val tripInfo = backStackEntry.arguments?.getString("tripInfo") ?: "7 Days • France"
                        val activityId = backStackEntry.arguments?.getString("activityId")

                        AddActivityScreen(
                            tripId = tripId,
                            tripName = tripName,
                            tripInfo = tripInfo,
                            activityId = activityId,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onSaveActivity = { title, category, cost, dateMillis, hour, minute, location, description ->
                                activityViewModel.saveActivity(
                                    activityId = activityId,
                                    tripId = tripId,
                                    title = title,
                                    category = category,
                                    cost = cost,
                                    dateMillis = dateMillis,
                                    hour = hour,
                                    minute = minute,
                                    location = location,
                                    description = description,
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}
