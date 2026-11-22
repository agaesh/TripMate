package com.example.tripmate

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.tripmate.ui.screens.AddActivityScreen
import com.example.tripmate.ui.screens.AddEditTripScreen
import com.example.tripmate.ui.screens.BudgetStatScreen
import com.example.tripmate.ui.screens.HomeScreen
import com.example.tripmate.ui.screens.ItineraryScreen
import com.example.tripmate.ui.theme.TripMateTheme
import com.example.tripmate.ui.viewmodels.TripActivityViewModel
import com.example.tripmate.ui.viewmodels.TripActivityViewModelFactory
import com.example.tripmate.ui.viewmodels.TripViewModel
import com.example.tripmate.ui.viewmodels.TripViewModelFactory

sealed interface Screen {
    data object Home : Screen
    data object Itinerary : Screen
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

        val database = TripDatabase.getDatabase(this)
        val tripRepository = TripRepository(database.tripDao())
        val activityRepository = TripActivityRepository(database.tripActivityDao())
        val tripFactory = TripViewModelFactory(tripRepository, activityRepository)
        val activityFactory = TripActivityViewModelFactory(activityRepository)

        setContent {


            TripMateTheme {
                        HomeScreen(
                            modifier = Modifier.fillMaxSize(),
                            },
                            }
                    }
                            onNavigateBack = {
                        )
                    }
                        AddActivityScreen(
                            onNavigateBack = {
                            },
                                        title = title,
                                        category = category,
                                        dateMillis = dateMillis,
                                        hour = hour,
                                        minute = minute,
                                        location = location,
                                        description = description,
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
