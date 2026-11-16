package com.example.tripmate.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tripmate.data.model.TripActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripActivityDao {

    @Query("SELECT * FROM trip_activities ORDER BY dateMillis ASC, hour ASC, minute ASC")
    fun getAllActivities(): Flow<List<TripActivityEntity>>

    @Query("SELECT * FROM trip_activities WHERE tripId = :tripId ORDER BY dateMillis ASC, hour ASC, minute ASC")
    fun getActivitiesForTrip(tripId: String): Flow<List<TripActivityEntity>>

    @Query("SELECT SUM(cost) FROM trip_activities WHERE tripId = :tripId")
    fun getTripTotalCost(tripId: String): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: TripActivityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<TripActivityEntity>)

    @Delete
    suspend fun deleteActivity(activity: TripActivityEntity)

    @Query("DELETE FROM trip_activities WHERE id = :activityId")
    suspend fun deleteActivityById(activityId: String)
}
