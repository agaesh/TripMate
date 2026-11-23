package com.example.tripmate.util

import com.example.tripmate.data.model.TripActivityEntity
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUtilsTest {

    @Test
    fun isValidTripDates_validRange_returnsTrue() {
        val start = System.currentTimeMillis()
        val end = start + (7 * 24 * 60 * 60 * 1000L)
        assertTrue(ValidationUtils.isValidTripDates(start, end))
    }

    @Test
    fun isValidTripDates_sameDay_returnsTrue() {
        val start = System.currentTimeMillis()
        assertTrue(ValidationUtils.isValidTripDates(start, start))
    }

    @Test
    fun isValidTripDates_endBeforeStart_returnsFalse() {
        val start = System.currentTimeMillis()
        val end = start - (24 * 60 * 60 * 1000L)
        assertFalse(ValidationUtils.isValidTripDates(start, end))
    }

    @Test
    fun isValidTripInput_completeInput_returnsTrue() {
        val start = System.currentTimeMillis()
        val end = start + (2 * 24 * 60 * 60 * 1000L)
        assertTrue(ValidationUtils.isValidTripInput("Paris Vacation", "London", "Paris", start, end))
    }

    @Test
    fun isValidTripInput_blankTitle_returnsFalse() {
        val start = System.currentTimeMillis()
        val end = start + (2 * 24 * 60 * 60 * 1000L)
        assertFalse(ValidationUtils.isValidTripInput("   ", "London", "Paris", start, end))
    }

    @Test
    fun isValidTripInput_blankFromPlace_returnsFalse() {
        val start = System.currentTimeMillis()
        val end = start + (2 * 24 * 60 * 60 * 1000L)
        assertFalse(ValidationUtils.isValidTripInput("Paris Vacation", "", "Paris", start, end))
    }

    @Test
    fun isValidTripInput_blankToPlace_returnsFalse() {
        val start = System.currentTimeMillis()
        val end = start + (2 * 24 * 60 * 60 * 1000L)
        assertFalse(ValidationUtils.isValidTripInput("Paris Vacation", "London", "", start, end))
    }

    @Test
    fun hasActivityTimeOverlap_noOverlapDifferentTime_returnsFalse() {
        val dateMillis = System.currentTimeMillis()
        val existing = listOf(
            TripActivityEntity(
                id = "act1", tripId = "t1", title = "Museum",
                category = "SIGHTSEEING", cost = 10.0, dateMillis = dateMillis,
                hour = 10, minute = 0, location = "", description = ""
            )
        )
        assertFalse(ValidationUtils.hasActivityTimeOverlap(existing, null, dateMillis, 11, 0))
    }

    @Test
    fun hasActivityTimeOverlap_overlapSameTime_returnsTrue() {
        val dateMillis = System.currentTimeMillis()
        val existing = listOf(
            TripActivityEntity(
                id = "act1", tripId = "t1", title = "Museum",
                category = "SIGHTSEEING", cost = 10.0, dateMillis = dateMillis,
                hour = 10, minute = 0, location = "", description = ""
            )
        )
        assertTrue(ValidationUtils.hasActivityTimeOverlap(existing, null, dateMillis, 10, 0))
    }

    @Test
    fun hasActivityTimeOverlap_editingSameActivity_returnsFalse() {
        val dateMillis = System.currentTimeMillis()
        val existing = listOf(
            TripActivityEntity(
                id = "act1", tripId = "t1", title = "Museum",
                category = "SIGHTSEEING", cost = 10.0, dateMillis = dateMillis,
                hour = 10, minute = 0, location = "", description = ""
            )
        )
        assertFalse(ValidationUtils.hasActivityTimeOverlap(existing, "act1", dateMillis, 10, 0))
    }
}
