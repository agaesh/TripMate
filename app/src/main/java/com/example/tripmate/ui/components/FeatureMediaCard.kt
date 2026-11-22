package com.example.tripmate.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tripmate.data.model.Trip

private val AccentPurpleCard = Color(0xFF7B1FA2)

/**
 * A generic "media header" card format:
 *
 *  ┌───────────────────────────────┐
 *  │  media zone    [badge]        │   ← fixed-height rounded media + optional overlay badge
 *  ├───────────────────────────────┤
 *  │  content()           [trail]  │   ← weighted info column + optional trailing actions
 *  └───────────────────────────────┘
 *
 * Content-agnostic: works for trips, products, articles, events, restaurants, etc.
 * The caller supplies what goes inside the media zone, the badge, the body, and the actions.
 *
 * @param onClick           Card tap handler.
 * @param mediaContent      What renders inside the media zone (usually an image).
 * @param content           Title + meta rows for the body. Sits in a weighted column.
 * @param badgeContent      Optional pill overlaid at the media's top-end.
 * @param trailingContent   Optional actions rendered to the right of [content].
 * @param mediaHeight       Height of the media zone. Default 160.dp.
 * @param mediaCornerRadius Corner radius of the media zone. Default 18.dp.
 * @param cardCornerRadius  Corner radius of the card itself. Default 24.dp.
 */
@Composable
fun FeatureMediaCard(
    onClick: () -> Unit,
    mediaContent: @Composable BoxScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    badgeContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    mediaHeight: Dp = 160.dp,
    mediaCornerRadius: Dp = 18.dp,
    cardCornerRadius: Dp = 24.dp,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // ── Media zone ──────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(mediaHeight)
                    .clip(RoundedCornerShape(mediaCornerRadius))
                    .background(Color(0xFFEDF2F7))
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(mediaCornerRadius),
                    ),
            ) {
                mediaContent()

                if (badgeContent != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp),
                    ) {
                        badgeContent()
                    }
                }
            }

            // ── Info + actions row ──────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    content()
                }

                trailingContent?.invoke()
            }
        }
    }
}

/**
 * Convenient overload of [FeatureMediaCard] specifically styled for displaying a [Trip].
 */
@Composable
fun FeatureMediaCard(
    trip: Trip,
    budget: Double = 0.0,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FeatureMediaCard(
        onClick = onClick,
        modifier = modifier,
        mediaContent = {
            if (trip.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = trip.imageUrl,
                    contentDescription = trip.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        },
        badgeContent = {
            Box(modifier = Modifier.fillMaxSize()) {
                // Top-right badge using your existing OverlayBadge
                OverlayBadge(
                    text = "${trip.days} Days",
                    modifier = Modifier.align(Alignment.TopEnd)
                )

                // Bottom-right budget badge with a different M3 color
                Surface(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                ) {
                    Text(
                        text = "RM ${"%.2f".format(budget)}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }
        },
        content = {
            Text(
                text = trip.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A202C),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = AccentPurpleCard,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = trip.fromPlace,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4A5568),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = trip.toPlace,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = AccentPurpleCard,
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = trip.duration,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )
            }
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEditClick, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit Trip",
                        tint = AccentPurpleCard,
                    )
                }
                IconButton(onClick = onDeleteClick, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete Trip",
                        tint = Color(0xFFE53935),
                    )
                }
            }
        },
    )
}

/**
 * Standard pill overlay for [FeatureMediaCard]'s [badgeContent] slot.
 * Black 65% rounded pill with bold white text.
 */
@Composable
fun OverlayBadge(
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.Black.copy(alpha = 0.65f),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}
