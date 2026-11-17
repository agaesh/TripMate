package com.example.tripmate.util

import com.example.tripmate.data.model.TripActivityEntity
import java.util.Calendar

object ValidationUtils {

    /**
     * Checks if a new or updated activity time (dateMillis, hour, minute) overlaps with any existing activity for the trip.
     * Ignore self if currentActivityId is non-null.
     */
    fun hasActivityTimeOverlap(
        existingActivities: List<TripActivityEntity>,
        currentActivityId: String?,
        dateMillis: Long,
        hour: Int,
        minute: Int,
    ): Boolean {
        val targetCal = Calendar.getInstance().apply {
            timeInMillis = dateMillis
        }

        return existingActivities.any { activity ->
            // Skip self when updating an existing activity
            if (currentActivityId != null && activity.id == currentActivityId) {
                return@any false
            }

            val existingCal = Calendar.getInstance().apply {
                timeInMillis = activity.dateMillis
            }

            val isSameDay = targetCal.get(Calendar.YEAR) == existingCal.get(Calendar.YEAR) &&
                targetCal.get(Calendar.DAY_OF_YEAR) == existingCal.get(Calendar.DAY_OF_YEAR)

            val isSameTime = activity.hour == hour && activity.minute == minute

            isSameDay && isSameTime
        }
    }

    /**
     * Checks if trip dates are valid (endDate >= startDate).
     */
    fun isValidTripDates(startDateMillis: Long, endDateMillis: Long): Boolean {
        val startCal = Calendar.getInstance().apply {
            timeInMillis = startDateMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endCal = Calendar.getInstance().apply {
            timeInMillis = endDateMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return !endCal.before(startCal)
    }

    /**
     * Validates required trip fields.
     */
    fun isValidTripInput(
        title: String,
        fromPlace: String,
        toPlace: String,
        startDateMillis: Long,
        endDateMillis: Long,
    ): Boolean {
        return title.isNotBlank() &&
            fromPlace.isNotBlank() &&
            toPlace.isNotBlank() &&
            isValidTripDates(startDateMillis, endDateMillis)
    }
}
