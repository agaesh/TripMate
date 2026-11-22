package com.example.tripmate.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScrollableFilterLayout(
    filterCategories: List<String>,
    selectedFilter: String?,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    // Parameter ini membolehkan anda memasukkan apa-apa komponen lain di bawah LazyRow secara dinamik
    content: @Composable ColumnScope.() -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        // Bahagian Filter (Horizontal Scroll)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(filterCategories) { filter ->
                FloatingFilterPill(
                    label = filter,
                    isSelected = (filter == selectedFilter),
                    onClick = { onFilterSelected(filter) },
                )
            }
        }

        //the content is used to put other element wrapped by this
        content()
    }
}