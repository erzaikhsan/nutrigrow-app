package com.project.labs.nutrigrow.ui.component.pagination

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.labs.nutrigrow.ui.theme.SurfaceCard
import com.project.labs.nutrigrow.ui.theme.TextSecondary
import kotlinx.coroutines.launch

const val DEFAULT_PAGE_SIZE = 10

data class Pagination<T>(
    val visibleItems: List<T>,
    val page: Int,
    val pageCount: Int,
    val pageSize: Int,
    val totalItems: Int,
    val onPageChange: (Int) -> Unit,
) {
    fun absoluteIndex(visibleIndex: Int): Int = page * pageSize + visibleIndex
}

@Composable
fun <T> rememberPagination(
    items: List<T>,
    listState: LazyListState? = null,
    pageSize: Int = DEFAULT_PAGE_SIZE,
): Pagination<T> {
    var page by rememberSaveable { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    val pageCount = if (items.isEmpty()) 1 else (items.size + pageSize - 1) / pageSize
    val current = page.coerceIn(0, pageCount - 1)

    return Pagination(
        visibleItems = items.drop(current * pageSize).take(pageSize),
        page = current,
        pageCount = pageCount,
        pageSize = pageSize,
        totalItems = items.size,
        onPageChange = { target ->
            page = target.coerceIn(0, pageCount - 1)
            listState?.let { scope.launch { it.animateScrollToItem(0) } }
        },
    )
}

@Composable
fun PaginationBar(
    pagination: Pagination<*>,
    modifier: Modifier = Modifier,
    itemLabel: String = "data",
) {
    if (pagination.pageCount <= 1) return

    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            IconButton(
                onClick = { pagination.onPageChange(pagination.page - 1) },
                enabled = pagination.page > 0,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Halaman sebelumnya",
                )
            }
            Text(
                text = "Halaman ${pagination.page + 1} dari ${pagination.pageCount}  ·  ${pagination.totalItems} $itemLabel",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
            IconButton(
                onClick = { pagination.onPageChange(pagination.page + 1) },
                enabled = pagination.page < pagination.pageCount - 1,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Halaman berikutnya",
                )
            }
        }
    }
}
