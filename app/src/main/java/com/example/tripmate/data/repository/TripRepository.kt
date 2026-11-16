package com.example.tripmate.data.repository

import com.example.tripmate.data.local.TripDao
import com.example.tripmate.data.model.Trip
import kotlinx.coroutines.flow.Flow

class TripRepository(private val tripDao: TripDao) {

    val allTrips: Flow<List<Trip>> = tripDao.getAllTrips()

    fun getTripById(tripId: String): Flow<Trip?> = tripDao.getTripById(tripId)

    suspend fun getTripCount(): Int = tripDao.getTripCount()

    suspend fun saveTrip(trip: Trip) {
        tripDao.insertTrip(trip)
    }

    suspend fun deleteTrip(trip: Trip) {
        tripDao.deleteTrip(trip)
    }

    suspend fun deleteTripById(tripId: String) {
        tripDao.deleteTripById(tripId)
    }
}
