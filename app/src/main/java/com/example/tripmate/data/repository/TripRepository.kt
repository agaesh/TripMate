package com.example.tripmate.data.repository

import com.example.tripmate.data.local.TripDao
import com.example.tripmate.data.model.Trip
import com.example.tripmate.data.model.TripActivityEntity
import kotlinx.coroutines.flow.Flow

class TripRepository(private val tripDao: TripDao) {

    val allTrips: Flow<List<Trip>> = tripDao.getAllTrips()

    fun getAllTrip(): Flow<List<Trip>> {
        return tripDao.getAllTrips()
    }
    fun getTripById(tripId: String): Flow<Trip?> = tripDao.getTripById(tripId)

    fun getActivitiesForTrip(tripId: String): Flow<List<TripActivityEntity>> =
        tripDao.getActivitiesForTrip(tripId)

    fun getTripTotalCost(tripId: String): Flow<Double?> = tripDao.getTripTotalCost(tripId)

    suspend fun saveTrip(trip: Trip) {
        tripDao.insertTrip(trip)
    }

    suspend fun deleteTrip(trip: Trip) {
        tripDao.deleteTrip(trip)
    }

    suspend fun deleteTripById(tripId: String) {
        tripDao.deleteTripById(tripId)
    }

    suspend fun saveActivity(activity: TripActivityEntity) {
        tripDao.insertActivity(activity)
    }

    suspend fun deleteActivity(activity: TripActivityEntity) {
        tripDao.deleteActivity(activity)
    }
}
