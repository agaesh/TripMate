package com.example.tripmate.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.tripmate.data.model.TripActivityEntity
import com.example.tripmate.ui.components.FeatureMediaCard
import com.example.tripmate.ui.components.OverlayBadge
import com.example.tripmate.ui.components.ScrollableFilterLayout
import com.example.tripmate.ui.theme.TripMateTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ── Theme tokens (same values used by HomeScreen / FeatureMediaCard) ──
private val AccentPurple = Color(0xFF7B1FA2)
private val CardBorder = Color(0xFFE2E8F0)
private val TextPrimary = Color(0xFF1A202C)
private val TextSecondary = Color(0xFF4A5568)
private val DeleteRed = Color(0xFFE53935)

private fun detailCategoryColor(category: ActivityCategory): Color = when (category) {
    ActivityCategory.TRANSPORT -> Color(0xFF1976D2)
    ActivityCategory.HOTEL -> Color(0xFF7B1FA2)
    ActivityCategory.SIGHTSEEING -> Color(0xFFF57C00)
    ActivityCategory.DINING -> Color(0xFF2E7D32)
    ActivityCategory.OTHER -> Color(0xFF607D8B)
}

private fun TripActivityEntity.toCategory(): ActivityCategory =
    runCatching { ActivityCategory.valueOf(category) }.getOrDefault(ActivityCategory.OTHER)

private fun formatMoney(value: Double): String = String.format(Locale.US, "\$%,.2f", value)

private fun formatTime(hour: Int, minute: Int): String = String.format(Locale.US, "%02d:%02d", hour, minute)

/**
 * Trip itinerary screen: shows every activity of the selected trip grouped by day.
 *
 * Navigation is NOT wired yet – everything is exposed through callbacks:
 *  - [onAddActivity]    → later: go to AddEditActivity in add mode
 *  - [onEditActivity]   → later: go to AddEditActivity in edit mode
 *  - [onDeleteActivity] → later: activityViewModel.deleteActivity(it)
 *  - [onViewBudget]     → later: go to BudgetStatScreen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailScreen(
    modifier: Modifier = Modifier,
    tripName: String = "Paris Summer Vacation",
    tripInfo: String = "7 Days • France",
    tripImageUrl: String = "",
    activities: List<TripActivityEntity> = emptyList(),
    onNavigateBack: () -> Unit = {},
    onAddActivity: () -> Unit = {},
    onEditActivity: (TripActivityEntity) -> Unit = {},
    onDeleteActivity: (TripActivityEntity) -> Unit = {},
    onViewBudget: () -> Unit = {},
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var activityToDelete by remember { mutableStateOf<TripActivityEntity?>(null) }

    val filterOptions = remember { listOf("All") + ActivityCategory.entries.map { it.label } }
    val dayFormat = remember { SimpleDateFormat("EEE, MMM dd", Locale.getDefault()) }

    val sortedActivities = remember(activities) {
        activities.sortedWith(compareBy({ it.dateMillis }, { it.hour }, { it.minute }))
    }
    val allDayKeys = remember(sortedActivities) {
        sortedActivities.map { dayFormat.format(Date(it.dateMillis)) }.distinct()
    }
    val filteredActivities = remember(sortedActivities, selectedFilter) {
        sortedActivities.filter { selectedFilter == "All" || it.toCategory().label == selectedFilter }
    }
    val groupedByDay = remember(filteredActivities) {
        filteredActivities.groupBy { dayFormat.format(Date(it.dateMillis)) }
    }
    val totalCost = remember(activities) { activities.sumOf { it.cost } }

    Scaffold(
        modifier = modifier,
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Trip Itinerary",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddActivity,
                containerColor = AccentPurple,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(imageVector = Icons.Outlined.Add, contentDescription = null) },
                text = { Text("Add Activity", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Trip header (reuses FeatureMediaCard + OverlayBadge) ──
            item(key = "trip_header") {
                FeatureMediaCard(
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 16.dp),
                    mediaHeight = 140.dp,
                    mediaContent = {
                        if (tripImageUrl.isNotBlank()) {
                            AsyncImage(
                                model = tripImageUrl,
                                contentDescription = tripName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    },
                    badgeContent = {
                        OverlayBadge(text = "${activities.size} Activities")
                    },
                    content = {
                        Text(
                            text = tripName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                tint = AccentPurple,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = tripInfo,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                        }
                    }
                )
            }

            // ── Summary row + link to Budget & Stats ──
            item(key = "summary") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SummaryStatCard(
                            label = "Total Cost",
                            value = formatMoney(totalCost),
                            modifier = Modifier.weight(1f)
                        )
                        SummaryStatCard(
                            label = "Days Planned",
                            value = allDayKeys.size.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Button(
                        onClick = onViewBudget,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "View Budget & Stats",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // ── Section title + category filter pills (reuses ScrollableFilterLayout) ──
            item(key = "section_title") {
                Text(
                    text = "Itinerary (${filteredActivities.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item(key = "filters") {
                ScrollableFilterLayout(
                    filterCategories = filterOptions,
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // ── Activities grouped by day ──
            if (filteredActivities.isEmpty()) {
                item(key = "empty") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = AccentPurple,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Activities Found",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = AccentPurple
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap \"Add Activity\" to plan your first stop.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                groupedByDay.forEach { (dayKey, dayActivities) ->
                    item(key = "day_$dayKey") {
                        DayHeader(
                            dayNumber = allDayKeys.indexOf(dayKey) + 1,
                            dateLabel = dayKey,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    items(dayActivities, key = { it.id }) { activity ->
                        ActivityItemCard(
                            activity = activity,
                            onClick = { onEditActivity(activity) },
                            onEdit = { onEditActivity(activity) },
                            onDelete = { activityToDelete = activity },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }

    // ── Delete confirmation (same style as HomeScreen) ──
    activityToDelete?.let { activity ->
        AlertDialog(
            onDismissRequest = { activityToDelete = null },
            title = { Text("Delete Activity", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete '${activity.title}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteActivity(activity)
                        activityToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { activityToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SummaryStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AccentPurple
            )
        }
    }
}

@Composable
private fun DayHeader(
    dayNumber: Int,
    dateLabel: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = AccentPurple.copy(alpha = 0.12f)
        ) {
            Text(
                text = "Day $dayNumber",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = AccentPurple,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = dateLabel,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )
    }
}

@Composable
private fun ActivityItemCard(
    activity: TripActivityEntity,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val category = activity.toCategory()
    val accent = detailCategoryColor(category)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Category emoji badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color = accent.copy(alpha = 0.12f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = category.emoji, fontSize = 22.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accent.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = category.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = accent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formatTime(activity.hour, activity.minute),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                if (activity.location.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = AccentPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = activity.location,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (activity.description.isNotBlank()) {
                    Text(
                        text = activity.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatMoney(activity.cost),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AccentPurple
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit Activity",
                            tint = AccentPurple
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete Activity",
                            tint = DeleteRed
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActivityDetailScreenPreview() {
    val now = System.currentTimeMillis()
    val day = 24 * 60 * 60 * 1000L
    TripMateTheme {
        ActivityDetailScreen(
            tripName = "Paris Summer Vacation",
            tripInfo = "7 Days • France",
            activities = listOf(
                TripActivityEntity(
                    id = "a1", tripId = "paris_2026", title = "Eurostar Train to Paris",
                    category = "TRANSPORT", cost = 180.0, dateMillis = now, hour = 8, minute = 30,
                    location = "St Pancras Station, London", description = "Standard Premier seat ticket"
                ),
                TripActivityEntity(
                    id = "a2", tripId = "paris_2026", title = "Visit Eiffel Tower Summit",
                    category = "SIGHTSEEING", cost = 35.5, dateMillis = now + day, hour = 14, minute = 0,
                    location = "Champ de Mars, Paris", description = "Summit elevator tickets booked"
                ),
                TripActivityEntity(
                    id = "a3", tripId = "paris_2026", title = "French Fine Dining Le Marais",
                    category = "DINING", cost = 120.0, dateMillis = now + day, hour = 19, minute = 30,
                    location = "Le Marais District, Paris", description = "French cuisine reservation for 2"
                )
            )
        )
    }
}
