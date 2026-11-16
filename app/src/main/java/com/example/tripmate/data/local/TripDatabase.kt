package com.example.tripmate.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.tripmate.data.model.Trip
import com.example.tripmate.data.model.TripActivityEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Trip::class, TripActivityEntity::class],
    version = 3,
    exportSchema = false
)
abstract class TripDatabase : RoomDatabase() {

    abstract fun tripDao(): TripDao

    companion object {
        @Volatile
        private var INSTANCE: TripDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): TripDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TripDatabase::class.java,
                    "tripmate_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(TripDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class TripDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateInitialSampleData(database.tripDao())
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateInitialSampleData(database.tripDao())
                    }
                }
            }

            suspend fun populateInitialSampleData(dao: TripDao) {
                val sampleTrips = listOf(
                    Trip(
                        id = "paris_2026",
                        title = "Paris Summer Vacation",
                        fromPlace = "London, UK",
                        toPlace = "Paris, France",
                        days = 7,
                        duration = "7 Days / 6 Nights",
                        imageUrl = "https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&w=600&q=80",
                        startDateMillis = System.currentTimeMillis(),
                        endDateMillis = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L)
                    ),
                    Trip(
                        id = "tokyo_2026",
                        title = "Tokyo Adventure",
                        fromPlace = "San Francisco, USA",
                        toPlace = "Tokyo, Japan",
                        days = 10,
                        duration = "10 Days / 9 Nights",
                        imageUrl = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?auto=format&fit=crop&w=600&q=80",
                        startDateMillis = System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000L),
                        endDateMillis = System.currentTimeMillis() + (40 * 24 * 60 * 60 * 1000L)
                    ),
                    Trip(
                        id = "bali_2026",
                        title = "Bali Island Getaway",
                        fromPlace = "Sydney, Australia",
                        toPlace = "Bali, Indonesia",
                        days = 5,
                        duration = "5 Days / 4 Nights",
                        imageUrl = "https://images.unsplash.com/photo-1537996194471-e657df975ab4?auto=format&fit=crop&w=600&q=80",
                        startDateMillis = System.currentTimeMillis() + (60 * 24 * 60 * 60 * 1000L),
                        endDateMillis = System.currentTimeMillis() + (65 * 24 * 60 * 60 * 1000L)
                    ),
                    Trip(
                        id = "rome_2026",
                        title = "Historic Rome Heritage",
                        fromPlace = "Berlin, Germany",
                        toPlace = "Rome, Italy",
                        days = 6,
                        duration = "6 Days / 5 Nights",
                        imageUrl = "https://images.unsplash.com/photo-1552832230-c0197dd311b5?auto=format&fit=crop&w=600&q=80",
                        startDateMillis = System.currentTimeMillis() + (90 * 24 * 60 * 60 * 1000L),
                        endDateMillis = System.currentTimeMillis() + (96 * 24 * 60 * 60 * 1000L)
                    ),
                    Trip(
                        id = "swiss_2026",
                        title = "Swiss Alps Expedition",
                        fromPlace = "Zurich, Switzerland",
                        toPlace = "Zermatt, Switzerland",
                        days = 8,
                        duration = "8 Days / 7 Nights",
                        imageUrl = "https://images.unsplash.com/photo-1530122037265-a5f1f91d3b99?auto=format&fit=crop&w=600&q=80",
                        startDateMillis = System.currentTimeMillis() + (120 * 24 * 60 * 60 * 1000L),
                        endDateMillis = System.currentTimeMillis() + (128 * 24 * 60 * 60 * 1000L)
                    ),
                    Trip(
                        id = "ny_2026",
                        title = "New York Skyline Experience",
                        fromPlace = "Toronto, Canada",
                        toPlace = "New York, USA",
                        days = 4,
                        duration = "4 Days / 3 Nights",
                        imageUrl = "https://images.unsplash.com/photo-1496442226666-8d4d0e62e6e9?auto=format&fit=crop&w=600&q=80",
                        startDateMillis = System.currentTimeMillis() + (150 * 24 * 60 * 60 * 1000L),
                        endDateMillis = System.currentTimeMillis() + (154 * 24 * 60 * 60 * 1000L)
                    ),
                    Trip(
                        id = "london_2026",
                        title = "London Cultural Tour",
                        fromPlace = "Dublin, Ireland",
                        toPlace = "London, UK",
                        days = 5,
                        duration = "5 Days / 4 Nights",
                        imageUrl = "https://images.unsplash.com/photo-1513635269975-59663e0ac1ad?auto=format&fit=crop&w=600&q=80",
                        startDateMillis = System.currentTimeMillis() + (180 * 24 * 60 * 60 * 1000L),
                        endDateMillis = System.currentTimeMillis() + (185 * 24 * 60 * 60 * 1000L)
                    ),
                    Trip(
                        id = "sydney_2026",
                        title = "Sydney Coastal Break",
                        fromPlace = "Auckland, New Zealand",
                        toPlace = "Sydney, Australia",
                        days = 6,
                        duration = "6 Days / 5 Nights",
                        imageUrl = "https://images.unsplash.com/photo-1506973035872-a4ec16b8e8d9?auto=format&fit=crop&w=600&q=80",
                        startDateMillis = System.currentTimeMillis() + (210 * 24 * 60 * 60 * 1000L),
                        endDateMillis = System.currentTimeMillis() + (216 * 24 * 60 * 60 * 1000L)
                    ),
                    Trip(
                        id = "barcelona_2026",
                        title = "Barcelona Tapas & Architecture",
                        fromPlace = "Madrid, Spain",
                        toPlace = "Barcelona, Spain",
                        days = 5,
                        duration = "5 Days / 4 Nights",
                        imageUrl = "https://images.unsplash.com/photo-1539037116277-4db20889f2d4?auto=format&fit=crop&w=600&q=80",
                        startDateMillis = System.currentTimeMillis() + (240 * 24 * 60 * 60 * 1000L),
                        endDateMillis = System.currentTimeMillis() + (245 * 24 * 60 * 60 * 1000L)
                    ),
                    Trip(
                        id = "kyoto_2026",
                        title = "Kyoto Ancient Shrines",
                        fromPlace = "Osaka, Japan",
                        toPlace = "Kyoto, Japan",
                        days = 4,
                        duration = "4 Days / 3 Nights",
                        imageUrl = "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&w=600&q=80",
                        startDateMillis = System.currentTimeMillis() + (270 * 24 * 60 * 60 * 1000L),
                        endDateMillis = System.currentTimeMillis() + (274 * 24 * 60 * 60 * 1000L)
                    ),
                    Trip(
                        id = "amsterdam_2026",
                        title = "Amsterdam Canals & Tulips",
                        fromPlace = "Brussels, Belgium",
                        toPlace = "Amsterdam, Netherlands",
                        days = 5,
                        duration = "5 Days / 4 Nights",
                        imageUrl = "https://images.unsplash.com/photo-1512470876302-972faa2aa9a4?auto=format&fit=crop&w=600&q=80",
                        startDateMillis = System.currentTimeMillis() + (300 * 24 * 60 * 60 * 1000L),
                        endDateMillis = System.currentTimeMillis() + (305 * 24 * 60 * 60 * 1000L)
                    ),
                    Trip(
                        id = "santorini_2026",
                        title = "Santorini Sunset & Beaches",
                        fromPlace = "Athens, Greece",
                        toPlace = "Santorini, Greece",
                        days = 6,
                        duration = "6 Days / 5 Nights",
                        imageUrl = "https://images.unsplash.com/photo-1570077188670-e3a8d69ac5ff?auto=format&fit=crop&w=600&q=80",
                        startDateMillis = System.currentTimeMillis() + (330 * 24 * 60 * 60 * 1000L),
                        endDateMillis = System.currentTimeMillis() + (336 * 24 * 60 * 60 * 1000L)
                    ),
                    Trip(
                        id = "prague_2026",
                        title = "Prague Castle & Old Town",
                        fromPlace = "Vienna, Austria",
                        toPlace = "Prague, Czech Republic",
                        days = 4,
                        duration = "4 Days / 3 Nights",
                        imageUrl = "https://images.unsplash.com/photo-1541849546-216549ae216d?auto=format&fit=crop&w=600&q=80",
                        startDateMillis = System.currentTimeMillis() + (360 * 24 * 60 * 60 * 1000L),
                        endDateMillis = System.currentTimeMillis() + (364 * 24 * 60 * 60 * 1000L)
                    ),
                    Trip(
                        id = "seoul_2026",
                        title = "Seoul K-Culture & Food Tour",
                        fromPlace = "Tokyo, Japan",
                        toPlace = "Seoul, South Korea",
                        days = 6,
                        duration = "6 Days / 5 Nights",
                        imageUrl = "https://images.unsplash.com/photo-1538485399081-7191377e8241?auto=format&fit=crop&w=600&q=80",
                        startDateMillis = System.currentTimeMillis() + (390 * 24 * 60 * 60 * 1000L),
                        endDateMillis = System.currentTimeMillis() + (396 * 24 * 60 * 60 * 1000L)
                    ),
                    Trip(
                        id = "singapore_2026",
                        title = "Singapore Garden City Escape",
                        fromPlace = "Kuala Lumpur, Malaysia",
                        toPlace = "Singapore, Singapore",
                        days = 4,
                        duration = "4 Days / 3 Nights",
                        imageUrl = "https://images.unsplash.com/photo-1525625293386-3f8f99389edd?auto=format&fit=crop&w=600&q=80",
                        startDateMillis = System.currentTimeMillis() + (420 * 24 * 60 * 60 * 1000L),
                        endDateMillis = System.currentTimeMillis() + (424 * 24 * 60 * 60 * 1000L)
                    ),
                    Trip(
                        id = "rio_2026",
                        title = "Rio de Janeiro Coastal Carnival",
                        fromPlace = "Sao Paulo, Brazil",
                        toPlace = "Rio de Janeiro, Brazil",
                        days = 8,
                        duration = "8 Days / 7 Nights",
                        imageUrl = "https://images.unsplash.com/photo-1483729558449-99ef09a8c325?auto=format&fit=crop&w=600&q=80",
                        startDateMillis = System.currentTimeMillis() + (450 * 24 * 60 * 60 * 1000L),
                        endDateMillis = System.currentTimeMillis() + (458 * 24 * 60 * 60 * 1000L)
                    ),
                    Trip(
                        id = "vancouver_2026",
                        title = "Vancouver Mountains & Ocean",
                        fromPlace = "Seattle, USA",
                        toPlace = "Vancouver, Canada",
                        days = 5,
                        duration = "5 Days / 4 Nights",
                        imageUrl = "https://images.unsplash.com/photo-1559511260-66a654ae982a?auto=format&fit=crop&w=600&q=80",
                        startDateMillis = System.currentTimeMillis() + (480 * 24 * 60 * 60 * 1000L),
                        endDateMillis = System.currentTimeMillis() + (485 * 24 * 60 * 60 * 1000L)
                    ),
                    Trip(
                        id = "queenstown_2026",
                        title = "Queenstown Alpine Adventure",
                        fromPlace = "Melbourne, Australia",
                        toPlace = "Queenstown, New Zealand",
                        days = 7,
                        duration = "7 Days / 6 Nights",
                        imageUrl = "https://images.unsplash.com/photo-1589802829985-817e51171b92?auto=format&fit=crop&w=600&q=80",
                        startDateMillis = System.currentTimeMillis() + (510 * 24 * 60 * 60 * 1000L),
                        endDateMillis = System.currentTimeMillis() + (517 * 24 * 60 * 60 * 1000L)
                    ),
                    Trip(
                        id = "dubai_2026",
                        title = "Dubai Desert & Skyscrapers",
                        fromPlace = "Doha, Qatar",
                        toPlace = "Dubai, UAE",
                        days = 5,
                        duration = "5 Days / 4 Nights",
                        imageUrl = "https://images.unsplash.com/photo-1512453979798-5ea266f8880c?auto=format&fit=crop&w=600&q=80",
                        startDateMillis = System.currentTimeMillis() + (540 * 24 * 60 * 60 * 1000L),
                        endDateMillis = System.currentTimeMillis() + (545 * 24 * 60 * 60 * 1000L)
                    ),
                    Trip(
                        id = "cairo_2026",
                        title = "Cairo Pyramids & Nile Cruise",
                        fromPlace = "Riyadh, Saudi Arabia",
                        toPlace = "Cairo, Egypt",
                        days = 7,
                        duration = "7 Days / 6 Nights",
                        imageUrl = "https://images.unsplash.com/photo-1503177119275-0aa32b3a9368?auto=format&fit=crop&w=600&q=80",
                        startDateMillis = System.currentTimeMillis() + (570 * 24 * 60 * 60 * 1000L),
                        endDateMillis = System.currentTimeMillis() + (577 * 24 * 60 * 60 * 1000L)
                    )
                )

                val sampleActivities = listOf(
                    TripActivityEntity(
                        id = "act_paris_1",
                        tripId = "paris_2026",
                        title = "Eurostar Train to Paris",
                        category = "TRANSPORT",
                        cost = 180.00,
                        dateMillis = System.currentTimeMillis(),
                        hour = 8,
                        minute = 30,
                        location = "St Pancras Station, London",
                        description = "Standard Premier seat ticket"
                    ),
                    TripActivityEntity(
                        id = "act_paris_2",
                        tripId = "paris_2026",
                        title = "Visit Eiffel Tower Summit",
                        category = "SIGHTSEEING",
                        cost = 35.50,
                        dateMillis = System.currentTimeMillis() + (24 * 60 * 60 * 1000L),
                        hour = 14,
                        minute = 0,
                        location = "Champ de Mars, Paris",
                        description = "Summit elevator tickets booked"
                    ),
                    TripActivityEntity(
                        id = "act_paris_3",
                        tripId = "paris_2026",
                        title = "French Fine Dining Le Marais",
                        category = "DINING",
                        cost = 120.00,
                        dateMillis = System.currentTimeMillis() + (24 * 60 * 60 * 1000L),
                        hour = 19,
                        minute = 30,
                        location = "Le Marais District, Paris",
                        description = "French cuisine reservation for 2"
                    )
                )

                dao.insertTrips(sampleTrips)
                dao.insertActivities(sampleActivities)
            }
        }
    }
}
