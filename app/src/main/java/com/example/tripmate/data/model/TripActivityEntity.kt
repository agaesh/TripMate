package com.example.tripmate.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trip_activities")
data class TripActivityEntity(
    @PrimaryKey
    val id: String,
    val tripId: String,
    val title: String,
    val category: String,
    val cost: Double = 0.0,
    val dateMillis: Long,
    val hour: Int,
    val minute: Int,
    val location: String,
    val description: String
)
