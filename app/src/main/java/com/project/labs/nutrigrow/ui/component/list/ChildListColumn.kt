package com.project.labs.nutrigrow.ui.component.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.labs.nutrigrow.data.model.ChildrenModel
import com.project.labs.nutrigrow.ui.component.card.ChildCard
import com.project.labs.nutrigrow.ui.component.pagination.PaginationBar
import com.project.labs.nutrigrow.ui.component.pagination.rememberPagination
import com.project.labs.nutrigrow.ui.component.respond.EmptyState
import com.project.labs.nutrigrow.ui.theme.BrandGreenSoft

@Composable
fun ChildListColumn(
    children: List<ChildrenModel>,
    keyword: String,
    listState: LazyListState,
    onOpen: (ChildrenModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagination = rememberPagination(items = children, listState = listState)

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(start = 15.dp, end = 15.dp)
            .background(BrandGreenSoft),
    ) {
        item { Spacer(modifier = Modifier.height(1.dp)) }

        if (pagination.visibleItems.isEmpty()) {
            item {
                EmptyState(
                    title = if (keyword.isBlank()) "Belum ada data balita" else "Balita tidak ditemukan",
                    description = if (keyword.isBlank()) {
                        "Belum ada balita yang terdaftar di daftar ini."
                    } else {
                        "Tidak ada balita dengan nama \"$keyword\"."
                    },
                )
            }
        } else {
            items(pagination.visibleItems.size) { index ->
                val child = pagination.visibleItems[index]
                ChildCard(
                    full_name = child.full_name,
                    gender = child.gender,
                    date_of_birth = child.date_of_birth,
                    color = "White",
                    onClick = { onOpen(child) }
                )
            }
            item { PaginationBar(pagination = pagination, itemLabel = "balita") }
        }

        item { Spacer(modifier = Modifier.height(10.dp)) }
    }
}
