package com.example.tripmate.data

import com.example.tripmate.ui.screens.ActivityCategory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TripActivity(
    val id: String = java.util.UUID.randomUUID().toString(),
    val trip: String = "Paris",
    val title: String,
    val category: ActivityCategory,
    val dateMillis: Long,
    val hour: Int,
    val minute: Int,
    val location: String,
    val description: String = "",
    val status: String = "Upcoming"
) {
    val formattedDate: String
        get() {
            val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            return sdf.format(Date(dateMillis))
        }

    val formattedTime: String
        get() {
            val h = if (hour % 12 == 0) 12 else hour % 12
            val m = String.format(Locale.getDefault(), "%02d", minute)
            val amPm = if (hour < 12) "AM" else "PM"
            return "$h:$m $amPm"
        }
}

// Initial Sample Activities
fun getSampleActivities(): List<TripActivity> {
    return listOf(
        TripActivity(
            title = "Visit Eiffel Tower",
            category = ActivityCategory.SIGHTSEEING,
            trip = "Paris",
            location = "Paris, France",
            dateMillis = System.currentTimeMillis() + 86400000L * 2,
            hour = 10,
            minute = 0
        ),
        TripActivity(
            title = "Louvre Museum Tour",
            category = ActivityCategory.SIGHTSEEING,
            trip = "Paris",
            location = "Paris, France",
            dateMillis = System.currentTimeMillis() + 86400000L * 3,
            hour = 11,
            minute = 0
        ),
        TripActivity(
            title = "Flight to Tokyo",
            category = ActivityCategory.TRANSPORT,
            trip = "Tokyo",
            location = "Kuala Lumpur Airport",
            dateMillis = System.currentTimeMillis() + 86400000L * 7,
            hour = 8,
            minute = 0
        ),
        TripActivity(
            title = "Hotel Check-in",
            category = ActivityCategory.HOTEL,
            trip = "Tokyo",
            location = "Tokyo, Japan",
            dateMillis = System.currentTimeMillis() + 86400000L * 7,
            hour = 15,
            minute = 0
        )
    )
}