package com.example.tripmate.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey
    val id: String,
    val title: String,
    val fromPlace: String,
    val toPlace: String,
    val days: Int,
    val duration: String,
    val imageUrl: String,
    val startDateMillis: Long,
    val endDateMillis: Long
)
