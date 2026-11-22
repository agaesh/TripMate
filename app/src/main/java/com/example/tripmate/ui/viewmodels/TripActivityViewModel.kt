package com.example.tripmate.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tripmate.data.model.TripActivityEntity
import com.example.tripmate.data.repository.TripActivityRepository
import com.example.tripmate.ui.screens.ActivityCategory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class TripActivityViewModel(private val repository: TripActivityRepository) : ViewModel() {

    private val _selectedTripId = MutableStateFlow("paris_2026")
    val selectedTripId: StateFlow<String> = _selectedTripId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedTripActivities: StateFlow<List<TripActivityEntity>> = _selectedTripId.flatMapLatest { tripId ->
        repository.getActivitiesForTrip(tripId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSelectedTripId(tripId: String) {
        _selectedTripId.value = tripId
    }

    fun saveActivity(
        activityId: String?,
        tripId: String,
        title: String,
        category: ActivityCategory,
        cost: Double,
        dateMillis: Long,
        hour: Int,
        minute: Int,
        location: String,
        description: String
    ) {
        viewModelScope.launch {
            val entity = TripActivityEntity(
                id = activityId ?: UUID.randomUUID().toString(),
                tripId = tripId.ifBlank { "paris_2026" },
                title = title,
                category = category.name,
                cost = cost,
                dateMillis = dateMillis,
                hour = hour,
                minute = minute,
                location = location,
                description = description
            )
            repository.saveActivity(entity)
        }
    }

    fun deleteActivity(activity: TripActivityEntity) {
        viewModelScope.launch {
            repository.deleteActivity(activity)
        }
    }
}

class TripActivityViewModelFactory(private val repository: TripActivityRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TripActivityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
