package com.project.labs.nutrigrow.ui.screen.parent.list

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.labs.nutrigrow.activity.parent.ParentActivity
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.UserModel
import com.project.labs.nutrigrow.ui.component.card.UserCard
import com.project.labs.nutrigrow.ui.component.input.SearchCard
import com.project.labs.nutrigrow.ui.component.pagination.PaginationBar
import com.project.labs.nutrigrow.ui.component.pagination.rememberPagination
import com.project.labs.nutrigrow.ui.component.respond.EmptyState
import com.project.labs.nutrigrow.ui.component.respond.ErrorMessage
import com.project.labs.nutrigrow.ui.component.respond.LoadingIndicator
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.state.UiState
import com.project.labs.nutrigrow.ui.theme.BrandGreen
import com.project.labs.nutrigrow.ui.theme.BrandGreenSoft
import kotlinx.coroutines.delay

private const val SEARCH_DEBOUNCE_MS = 400L

@Composable
fun ParentScreen(
    redirectToWelcome: (String) -> Unit,
    viewModel: ParentViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity

    val checkAuth by viewModel.isAuthenticated
    var keyword by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(key1 = Unit) {
        viewModel.checkAuthentication()
    }

    LaunchedEffect(keyword) {
        if (keyword.isNotBlank()) delay(SEARCH_DEBOUNCE_MS)
        when (checkAuth) {
            is UiState.Success -> {
                viewModel.getParentByName(keyword)
            }
            else -> { }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BrandGreenSoft)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            viewModel.user.collectAsState(initial = UiState.Loading).value.let { users ->
                when (users) {
                    is UiState.Loading -> {
                        LoadingIndicator()
                        LaunchedEffect(Unit) {
                            viewModel.getUserProfile()
                        }
                    }
                    is UiState.Success -> {
                        Spacer(modifier = Modifier.height(10.dp))
                        SearchCard(
                            value = keyword,
                            onValueChange = { keyword = it },
                            placeholder = "Cari Nama Orang Tua",
                        )
                        ElevatedCard(
                            shape = RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
                            ),
                            colors = CardDefaults.cardColors(Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp)
                        ) {
                            Text(
                                text = "Orang Tua Balita",
                                fontSize = 19.sp,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 25.dp, end = 25.dp, top = 8.dp, bottom = 8.dp)
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 15.dp, end = 15.dp),
                            color = BrandGreen,
                            thickness = 3.dp
                        )

                        val searchResult = viewModel.search.collectAsState(initial = UiState.Loading).value
                        val allParents = viewModel.parents.collectAsState(initial = UiState.Loading).value
                        val shown = if (searchResult is UiState.Success) searchResult else allParents

                        when (shown) {
                            is UiState.Loading -> {
                                LoadingIndicator()
                                LaunchedEffect(Unit) {
                                    viewModel.getAllParent()
                                }
                            }
                            is UiState.Success -> {
                                ParentList(
                                    parents = shown.data,
                                    keyword = keyword,
                                    listState = listState,
                                    onOpen = { parent ->
                                        activity.startActivity(
                                            Intent(context, ParentActivity::class.java).apply {
                                                putExtra("id", parent.user_id)
                                                (checkAuth as? UiState.Success<AuthModel>)?.let {
                                                    putExtra("role", it.data.role)
                                                }
                                            }
                                        )
                                    },
                                )
                            }
                            is UiState.Error -> {
                                ErrorMessage(message = shown.errorMessage)
                            }
                            else -> { }
                        }
                    }

                    is UiState.Error -> {
                        ErrorMessage(message = users.errorMessage)
                    }

                    is UiState.Unauthorized -> {
                        redirectToWelcome("Sesi Telah Berakhir\nSilahkan Masuk Kembali")
                    }

                    else -> { }
                }
            }
        }
    }
}

@Composable
private fun ParentList(
    parents: List<UserModel>,
    keyword: String,
    listState: LazyListState,
    onOpen: (UserModel) -> Unit,
) {
    val pagination = rememberPagination(items = parents, listState = listState)

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 15.dp, end = 15.dp)
            .background(BrandGreenSoft),
    ) {
        item { Spacer(modifier = Modifier.height(1.dp)) }

        if (pagination.visibleItems.isEmpty()) {
            item {
                EmptyState(
                    title = if (keyword.isBlank()) "Belum ada data" else "Tidak ditemukan",
                    description = if (keyword.isBlank()) {
                        "Belum ada orang tua yang terdaftar."
                    } else {
                        "Tidak ada orang tua dengan nama \"$keyword\"."
                    },
                )
            }
        } else {
            items(pagination.visibleItems.size) { index ->
                val parent = pagination.visibleItems[index]
                UserCard(
                    full_name = parent.full_name,
                    gender = parent.gender,
                    phone_number = parent.phone_number,
                    address = parent.address,
                    onClick = { onOpen(parent) }
                )
            }
            item { PaginationBar(pagination = pagination, itemLabel = "orang tua") }
        }

        item { Spacer(modifier = Modifier.height(10.dp)) }
    }
}
