package com.example.tripmate.data.repository

import com.example.tripmate.data.local.TripActivityDao
import com.example.tripmate.data.model.TripActivityEntity
import kotlinx.coroutines.flow.Flow

class TripActivityRepository(private val tripActivityDao: TripActivityDao) {

    val allActivities: Flow<List<TripActivityEntity>> = tripActivityDao.getAllActivities()

    fun getActivitiesForTrip(tripId: String): Flow<List<TripActivityEntity>> =
        tripActivityDao.getActivitiesForTrip(tripId)

    fun getTripTotalCost(tripId: String): Flow<Double?> =
        tripActivityDao.getTripTotalCost(tripId)

    suspend fun saveActivity(activity: TripActivityEntity) {
        tripActivityDao.insertActivity(activity)
    }

    suspend fun saveActivities(activities: List<TripActivityEntity>) {
        tripActivityDao.insertActivities(activities)
    }

    suspend fun deleteActivity(activity: TripActivityEntity) {
        tripActivityDao.deleteActivity(activity)
    }

    suspend fun deleteActivityById(activityId: String) {
        tripActivityDao.deleteActivityById(activityId)
    }
}
