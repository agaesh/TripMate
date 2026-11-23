package com.example.tripmate.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tripmate.data.model.TripActivityEntity
import com.example.tripmate.ui.theme.TripMateTheme
import com.example.tripmate.ui.viewmodels.TripActivityViewModel
import com.example.tripmate.ui.viewmodels.TripViewModel
import java.util.Calendar
import java.util.Locale

// ── Theme tokens (same values used by HomeScreen / FeatureMediaCard) ──
private val AccentPurple = Color(0xFF7B1FA2)
private val CardBorder = Color(0xFFE2E8F0)
private val TextPrimary = Color(0xFF1A202C)
private val TextSecondary = Color(0xFF4A5568)
private val DangerRed = Color(0xFFE53935)
private val WarningOrange = Color(0xFFF57C00)
private val SuccessGreen = Color(0xFF2E7D32)

private fun budgetCategoryColor(category: ActivityCategory): Color = when (category) {
    ActivityCategory.TRANSPORT -> Color(0xFF1976D2)
    ActivityCategory.HOTEL -> Color(0xFF7B1FA2)
    ActivityCategory.SIGHTSEEING -> Color(0xFFF57C00)
    ActivityCategory.DINING -> Color(0xFF2E7D32)
    ActivityCategory.OTHER -> Color(0xFF607D8B)
}

private fun money(value: Double): String = String.format(Locale.US, "\$%,.2f", value)

private fun moneyShort(value: Double): String = String.format(Locale.US, "\$%,.0f", value)

/** One spending entry used by the Budget & Stats screen. */
data class BudgetExpense(
    val title: String,
    val category: ActivityCategory,
    val amount: Double,
    val day: Int,
)

// ── Mock data (replace with real activities from the database later) ──
private val mockBudgetExpenses = listOf(
    BudgetExpense("Eurostar Train to Paris", ActivityCategory.TRANSPORT, 180.00, 1),
    BudgetExpense("Hotel Le Marais (6 nights)", ActivityCategory.HOTEL, 360.00, 1),
    BudgetExpense("Metro & Navigo Pass", ActivityCategory.TRANSPORT, 42.00, 2),
    BudgetExpense("Eiffel Tower Summit", ActivityCategory.SIGHTSEEING, 35.50, 2),
    BudgetExpense("French Fine Dining", ActivityCategory.DINING, 120.00, 2),
    BudgetExpense("Louvre Museum Tour", ActivityCategory.SIGHTSEEING, 68.00, 3),
    BudgetExpense("Seine Dinner Cruise", ActivityCategory.DINING, 95.00, 4),
    BudgetExpense("Versailles Day Trip", ActivityCategory.SIGHTSEEING, 80.00, 5),
    BudgetExpense("Souvenirs & Gifts", ActivityCategory.OTHER, 48.00, 5),
    BudgetExpense("Cafe & Street Food", ActivityCategory.DINING, 56.00, 6),
    BudgetExpense("Eurostar Return", ActivityCategory.TRANSPORT, 180.00, 7),
)

private const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L

private fun startOfDay(millis: Long): Long = Calendar.getInstance().apply {
    timeInMillis = millis
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

/**
 * Turns the trip's saved activities (Room) into expenses for this screen.
 * Only activities with a cost are counted. "Day N" is counted from the earliest activity,
 * the same way ActivityDetailScreen numbers its days.
 */
private fun List<TripActivityEntity>.toBudgetExpenses(): List<BudgetExpense> {
    if (isEmpty()) return emptyList()
    val firstDay = startOfDay(minOf { it.dateMillis })
    return filter { it.cost > 0 }
        .sortedWith(compareBy({ it.dateMillis }, { it.hour }, { it.minute }))
        .map { activity ->
            val dayIndex = Math.round((startOfDay(activity.dateMillis) - firstDay).toDouble() / MILLIS_PER_DAY).toInt() + 1
            BudgetExpense(
                title = activity.title,
                category = runCatching { ActivityCategory.valueOf(activity.category) }
                    .getOrDefault(ActivityCategory.OTHER),
                amount = activity.cost,
                day = dayIndex
            )
        }
}

/**
 * Budget & Statistics screen linked to the Room database.
 *
 * Data flow:  Room -> Repository -> ViewModel -> this screen.
 *  - trip name / info / number of days come from [TripViewModel.selectedTrip]
 *  - expenses come from [TripActivityViewModel.selectedTripActivities] (each activity's cost)
 *
 * Both ViewModels are created once in MainActivity and passed in.
 * Note: the database has no "budget" column yet, so the budget limit is the [totalBudget] parameter.
 */
@Composable
fun BudgetStatScreen(
    tripViewModel: TripViewModel,
    activityViewModel: TripActivityViewModel,
    modifier: Modifier = Modifier,
    totalBudget: Double = 1500.0,
    onNavigateBack: () -> Unit = {},
) {
    val trip by tripViewModel.selectedTrip.collectAsState()
    val activities by activityViewModel.selectedTripActivities.collectAsState()

    val expenses = remember(activities) { activities.toBudgetExpenses() }
    val totalDays = maxOf(trip?.days ?: 0, expenses.maxOfOrNull { it.day } ?: 0, 1)

    BudgetStatContent(
        modifier = modifier,
        tripName = trip?.title ?: "Loading...",
        tripInfo = trip?.let { "${it.days} Days • ${it.toPlace}" } ?: "",
        totalBudget = totalBudget,
        totalDays = totalDays,
        expenses = expenses,
        onNavigateBack = onNavigateBack,
    )
}

/** Stateless UI. Only displays what it is given, so it is easy to preview (defaults = mock data). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BudgetStatContent(
    modifier: Modifier = Modifier,
    tripName: String = "Paris Summer Vacation",
    tripInfo: String = "7 Days • France",
    totalBudget: Double = 1500.0,
    totalDays: Int = 7,
    expenses: List<BudgetExpense> = mockBudgetExpenses,
    onNavigateBack: () -> Unit = {},
) {
    val totalSpent = expenses.sumOf { it.amount }
    val remaining = totalBudget - totalSpent
    val rawPercent = if (totalBudget > 0) totalSpent / totalBudget else 0.0
    val progress = rawPercent.coerceIn(0.0, 1.0).toFloat()
    val statusColor = when {
        rawPercent >= 0.9 -> DangerRed
        rawPercent >= 0.7 -> WarningOrange
        else -> SuccessGreen
    }

    val byCategory = ActivityCategory.entries
        .map { cat -> cat to expenses.filter { it.category == cat }.sumOf { it.amount } }
        .filter { it.second > 0 }
        .sortedByDescending { it.second }

    val dailyTotals = (1..totalDays).map { d -> expenses.filter { it.day == d }.sumOf { it.amount } }
    val maxDaily = dailyTotals.maxOrNull() ?: 0.0
    val avgPerDay = if (totalDays > 0) totalSpent / totalDays else 0.0
    val biggest = expenses.maxByOrNull { it.amount }
    val topExpenses = expenses.sortedByDescending { it.amount }.take(3)

    Scaffold(
        modifier = modifier,
        containerColor = Color(0xFFF8F9FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Budget & Stats",
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Trip title ──
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
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

            // ── Budget overview ──
            SectionCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Budget Overview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = statusColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "${(rawPercent * 100).toInt()}% used",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = money(totalSpent),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = AccentPurple
                    )
                    Text(
                        text = "spent of ${money(totalBudget)} budget",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                ProgressBar(progress = progress, color = statusColor)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (remaining >= 0) "Remaining" else "Over budget",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = money(kotlin.math.abs(remaining)),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (remaining >= 0) SuccessGreen else DangerRed
                    )
                }
            }

            // ── Quick stats (2 x 2) ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = Icons.Outlined.LocationOn,
                    label = "Expenses",
                    value = expenses.size.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Outlined.DateRange,
                    label = "Avg / Day",
                    value = money(avgPerDay),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    icon = Icons.Outlined.Info,
                    label = "Biggest Expense",
                    value = biggest?.let { money(it.amount) } ?: "-",
                    subtitle = biggest?.title,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    icon = Icons.Outlined.Check,
                    label = "Budget Left",
                    value = money(remaining.coerceAtLeast(0.0)),
                    modifier = Modifier.weight(1f)
                )
            }

            // ── Spending by category ──
            SectionCard {
                Text(
                    text = "Spending by Category",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                if (byCategory.isEmpty()) {
                    Text(
                        text = "No expenses yet. Add activities with a cost to see them here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                byCategory.forEach { (category, amount) ->
                    val share = if (totalSpent > 0) (amount / totalSpent) else 0.0
                    val color = budgetCategoryColor(category)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(color = color.copy(alpha = 0.12f), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = category.emoji, fontSize = 18.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = category.label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${money(amount)} • ${(share * 100).toInt()}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                            }
                            ProgressBar(progress = share.toFloat(), color = color, height = 8)
                        }
                    }
                }
            }

            // ── Daily spending bar chart ──
            SectionCard {
                Text(
                    text = "Daily Spending",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    dailyTotals.forEachIndexed { index, amount ->
                        val fraction = if (maxDaily > 0) (amount / maxDaily).toFloat() else 0f
                        val barHeight = if (amount > 0) (100f * fraction).coerceAtLeast(4f) else 0f
                        val isPeak = amount > 0 && amount == maxDaily

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            if (amount > 0) {
                                Text(
                                    text = moneyShort(amount),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Clip
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Box(
                                modifier = Modifier
                                    .width(22.dp)
                                    .height(barHeight.dp)
                                    .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                    .background(
                                        if (isPeak) AccentPurple else AccentPurple.copy(alpha = 0.35f)
                                    )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "D${index + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // ── Top expenses ──
            SectionCard {
                Text(
                    text = "Top Expenses",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                if (topExpenses.isEmpty()) {
                    Text(
                        text = "No expenses yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                topExpenses.forEachIndexed { index, expense ->
                    if (index > 0) HorizontalDivider(color = CardBorder)

                    val color = budgetCategoryColor(expense.category)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(color = color.copy(alpha = 0.12f), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = expense.category.emoji, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = expense.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Day ${expense.day} • ${expense.category.label}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = money(expense.amount),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AccentPurple
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/** White bordered card, same look as the cards on HomeScreen. */
@Composable
private fun SectionCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content
        )
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
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
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color = AccentPurple.copy(alpha = 0.12f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AccentPurple,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/** Simple rounded progress bar built from two Boxes (no extra dependency). */
@Composable
private fun ProgressBar(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    height: Int = 12,
) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .clip(shape)
            .background(color.copy(alpha = 0.15f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(shape)
                .background(color)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BudgetStatScreenPreview() {
    TripMateTheme {
        BudgetStatContent()
    }
}
