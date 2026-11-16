package com.example.tripmate.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tripmate.data.model.Trip
import com.example.tripmate.data.model.TripActivityEntity
import com.example.tripmate.data.repository.TripRepository
import com.example.tripmate.ui.screens.ActivityCategory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class TripViewModel(private val repository: TripRepository) : ViewModel() {

    val rawTrips: StateFlow<List<Trip>> = repository.allTrips.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _pageLimit = MutableStateFlow(5)
    val pageLimit: StateFlow<Int> = _pageLimit.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    val filteredTrips: StateFlow<List<Trip>> = combine(
        rawTrips,
        _searchQuery,
        _selectedFilter
    ) { trips, query, filter ->
        trips.filter { trip ->
            val matchesQuery = query.isBlank() ||
                trip.title.contains(query, ignoreCase = true) ||
                trip.toPlace.contains(query, ignoreCase = true) ||
                trip.fromPlace.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                "Europe" -> trip.toPlace.contains("France", true) || trip.toPlace.contains("Italy", true) || trip.toPlace.contains("Switzerland", true) || trip.toPlace.contains("UK", true) || trip.toPlace.contains("Spain", true) || trip.toPlace.contains("Netherlands", true) || trip.toPlace.contains("Greece", true) || trip.toPlace.contains("Czech Republic", true) || trip.toPlace.contains("Germany", true)
                "Asia" -> trip.toPlace.contains("Japan", true) || trip.toPlace.contains("Indonesia", true) || trip.toPlace.contains("South Korea", true) || trip.toPlace.contains("UAE", true) || trip.toPlace.contains("Singapore", true)
                "Americas" -> trip.toPlace.contains("USA", true) || trip.toPlace.contains("Canada", true) || trip.toPlace.contains("Brazil", true)
                "Oceania" -> trip.toPlace.contains("Australia", true) || trip.toPlace.contains("Zealand", true)
                "Short Getaways" -> trip.days <= 5
                else -> true // "All"
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val pagedTrips: StateFlow<List<Trip>> = combine(
        filteredTrips,
        _pageLimit
    ) { trips, limit ->
        trips.take(limit)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val hasMore: StateFlow<Boolean> = combine(
        filteredTrips,
        _pageLimit
    ) { trips, limit ->
        trips.size > limit
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        _pageLimit.value = 5
    }

    fun setSelectedFilter(filter: String) {
        _selectedFilter.value = filter
        _pageLimit.value = 5
    }

    fun loadMoreTrips() {
        if (_isLoadingMore.value || !hasMore.value) return
        viewModelScope.launch {
            _isLoadingMore.value = true
            delay(600)
            _pageLimit.value += 5
            _isLoadingMore.value = false
        }
    }

    private val _selectedTripId = MutableStateFlow("paris_2026")
    val selectedTripId: StateFlow<String> = _selectedTripId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedTrip: StateFlow<Trip?> = _selectedTripId.flatMapLatest { tripId ->
        repository.getTripById(tripId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

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

    fun saveTrip(trip: Trip) {
        viewModelScope.launch {
            repository.saveTrip(trip)
        }
    }

    fun deleteTrip(trip: Trip) {
        viewModelScope.launch {
            repository.deleteTrip(trip)
        }
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

class TripViewModelFactory(private val repository: TripRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TripViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
