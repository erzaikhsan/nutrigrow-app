package com.project.labs.nutrigrow.ui.screen.mpasi

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import com.project.labs.nutrigrow.ui.component.pagination.PaginationBar
import com.project.labs.nutrigrow.ui.component.pagination.rememberPagination
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.labs.nutrigrow.activity.mpasi.DetailMpasiActivity
import com.project.labs.nutrigrow.ui.component.card.MpasiCard
import com.project.labs.nutrigrow.ui.component.respond.ErrorMessage
import com.project.labs.nutrigrow.ui.component.respond.LoadingIndicator
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.state.UiState

@Composable
fun MpasiScreen(
    redirectToHome: () -> Unit,
    viewModel: MpasiViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity

    val checkAuth by viewModel.isAuthenticated

    var selectedTab by remember { mutableStateOf("68Bulan") }

    LaunchedEffect(key1 = Unit) {
        viewModel.checkAuthentication()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE0FFD2))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 5.dp,
                    )
            ) {
                item { Spacer(modifier = Modifier.width(5.dp)) }
                item {
                    ElevatedCard(
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        ),
                        colors = CardDefaults.cardColors( if (selectedTab == "68Bulan") Color(0xFF00BF63) else Color.White),
                        modifier = Modifier
                            .height(if (selectedTab == "68Bulan") 45.dp else 35.dp)
                            .clickable { selectedTab = "68Bulan" }
                    ){
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(horizontal = 15.dp, vertical = 8.dp)
                                .fillMaxHeight()
                        ) {
                            Text(
                                text = "6-8 Bulan",
                                color = if (selectedTab == "68Bulan") Color.White else Color.Black,
                                fontSize = 15.sp,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }
                item {
                    ElevatedCard(
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        ),
                        colors = CardDefaults.cardColors( if (selectedTab == "911Bulan") Color(0xFF00BF63) else Color.White),
                        modifier = Modifier
                            .height(if (selectedTab == "911Bulan") 45.dp else 35.dp)
                            .clickable { selectedTab = "911Bulan" }
                    ){
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(horizontal = 15.dp, vertical = 8.dp)
                                .fillMaxHeight()
                        ) {
                            Text(
                                text = "9-11 Bulan",
                                color = if (selectedTab == "911Bulan") Color.White else Color.Black,
                                fontSize = 15.sp,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }
                item {
                    ElevatedCard(
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        ),
                        colors = CardDefaults.cardColors( if (selectedTab == "1223Bulan") Color(0xFF00BF63) else Color.White),
                        modifier = Modifier
                            .height(if (selectedTab == "1223Bulan") 45.dp else 35.dp)
                            .clickable { selectedTab = "1223Bulan" }
                    ){
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(horizontal = 15.dp, vertical = 8.dp)
                                .fillMaxHeight()
                        ) {
                            Text(
                                text = "12-23 Bulan",
                                color = if (selectedTab == "1223Bulan") Color.White else Color.Black,
                                fontSize = 15.sp,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }
                item {
                    ElevatedCard(
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        ),
                        colors = CardDefaults.cardColors( if (selectedTab == "25Tahun") Color(0xFF00BF63) else Color.White),
                        modifier = Modifier
                            .height(if (selectedTab == "25Tahun") 45.dp else 35.dp)
                            .clickable { selectedTab = "25Tahun" }
                    ){
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(horizontal = 15.dp, vertical = 8.dp)
                                .fillMaxHeight()
                        ) {
                            Text(
                                text = "2-5 Tahun",
                                color = if (selectedTab == "25Tahun") Color.White else Color.Black,
                                fontSize = 15.sp,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }
                item { Spacer(modifier = Modifier.width(5.dp)) }
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 15.dp,
                        end = 15.dp,
                    ),
                color = Color(0xFF00BF63),
                thickness = 3.dp
            )
            when (selectedTab) {
                "68Bulan" -> {
                    viewModel.mpasi.collectAsState(initial = UiState.Loading).value.let { mpasiRespond ->
                        when (mpasiRespond) {
                            is UiState.Loading -> {
                                LoadingIndicator()
                                LaunchedEffect(Unit) {
                                    viewModel.get68Bulan()
                                }
                            }
                            is UiState.Success -> {
                                val listState = rememberLazyListState()
                                val pagination = rememberPagination(items = mpasiRespond.data, listState = listState)
                                LazyColumn(
                                    state = listState,
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 15.dp)
                                        .background(Color(0xFFE0FFD2)),
                                ) {
                                    item {
                                        Spacer(modifier = Modifier.height(1.dp))
                                    }
                                    items(pagination.visibleItems.size) { mpasiItem ->
                                        MpasiCard(
                                            image = pagination.visibleItems[mpasiItem].image,
                                            title = pagination.visibleItems[mpasiItem].title,
                                            description = pagination.visibleItems[mpasiItem].description,
                                            onClick = {
                                                activity.startActivity(
                                                    Intent(context, DetailMpasiActivity::class.java).apply {
                                                        putExtra("id", pagination.absoluteIndex(mpasiItem))
                                                        putExtra("group", "68Bulan")
                                                    }
                                                )
                                            }
                                        )
                                    }
                                    item { PaginationBar(pagination = pagination, itemLabel = "resep") }
                                    item { Spacer(modifier = Modifier.height(10.dp)) }
                                }
                            }

                            is UiState.Error -> {
                                ErrorMessage(message = mpasiRespond.errorMessage)
                            }

                            is UiState.Unauthorized -> {
                                redirectToHome()
                            }

                            else -> {}
                        }
                    }
                }
                "911Bulan" -> {
                    viewModel.mpasi.collectAsState(initial = UiState.Loading).value.let { mpasiRespond ->
                        when (mpasiRespond) {
                            is UiState.Loading -> {
                                LoadingIndicator()
                                LaunchedEffect(Unit) {
                                    viewModel.get911Bulan()
                                }
                            }
                            is UiState.Success -> {
                                val listState = rememberLazyListState()
                                val pagination = rememberPagination(items = mpasiRespond.data, listState = listState)
                                LazyColumn(
                                    state = listState,
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 15.dp)
                                        .background(Color(0xFFE0FFD2)),
                                ) {
                                    item {
                                        Spacer(modifier = Modifier.height(1.dp))
                                    }
                                    items(pagination.visibleItems.size) { mpasiItem ->
                                        MpasiCard(
                                            image = pagination.visibleItems[mpasiItem].image,
                                            title = pagination.visibleItems[mpasiItem].title,
                                            description = pagination.visibleItems[mpasiItem].description,
                                            onClick = {
                                                activity.startActivity(
                                                    Intent(context, DetailMpasiActivity::class.java).apply {
                                                        putExtra("id", pagination.absoluteIndex(mpasiItem))
                                                        putExtra("group", "911Bulan")
                                                    }
                                                )
                                            }
                                        )
                                    }
                                    item { PaginationBar(pagination = pagination, itemLabel = "resep") }
                                    item { Spacer(modifier = Modifier.height(10.dp)) }
                                }
                            }

                            is UiState.Error -> {
                                ErrorMessage(message = mpasiRespond.errorMessage)
                            }

                            is UiState.Unauthorized -> {
                                redirectToHome()
                            }

                            else -> {}
                        }
                    }
                }
                "1223Bulan" -> {
                    viewModel.mpasi.collectAsState(initial = UiState.Loading).value.let { mpasiRespond ->
                        when (mpasiRespond) {
                            is UiState.Loading -> {
                                LoadingIndicator()
                                LaunchedEffect(Unit) {
                                    viewModel.get1223Bulan()
                                }
                            }
                            is UiState.Success -> {
                                val listState = rememberLazyListState()
                                val pagination = rememberPagination(items = mpasiRespond.data, listState = listState)
                                LazyColumn(
                                    state = listState,
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 15.dp)
                                        .background(Color(0xFFE0FFD2)),
                                ) {
                                    item {
                                        Spacer(modifier = Modifier.height(1.dp))
                                    }
                                    items(pagination.visibleItems.size) { mpasiItem ->
                                        MpasiCard(
                                            image = pagination.visibleItems[mpasiItem].image,
                                            title = pagination.visibleItems[mpasiItem].title,
                                            description = pagination.visibleItems[mpasiItem].description,
                                            onClick = {
                                                activity.startActivity(
                                                    Intent(context, DetailMpasiActivity::class.java).apply {
                                                        putExtra("id", pagination.absoluteIndex(mpasiItem))
                                                        putExtra("group", "1223Bulan")
                                                    }
                                                )
                                            }
                                        )
                                    }
                                    item { PaginationBar(pagination = pagination, itemLabel = "resep") }
                                    item { Spacer(modifier = Modifier.height(10.dp)) }
                                }
                            }

                            is UiState.Error -> {
                                ErrorMessage(message = mpasiRespond.errorMessage)
                            }

                            is UiState.Unauthorized -> {
                                redirectToHome()
                            }

                            else -> {}
                        }
                    }
                }
                "25Tahun" -> {
                    viewModel.mpasi.collectAsState(initial = UiState.Loading).value.let { mpasiRespond ->
                        when (mpasiRespond) {
                            is UiState.Loading -> {
                                LoadingIndicator()
                                LaunchedEffect(Unit) {
                                    viewModel.get25Tahun()
                                }
                            }
                            is UiState.Success -> {
                                val listState = rememberLazyListState()
                                val pagination = rememberPagination(items = mpasiRespond.data, listState = listState)
                                LazyColumn(
                                    state = listState,
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 15.dp)
                                        .background(Color(0xFFE0FFD2)),
                                ) {
                                    item {
                                        Spacer(modifier = Modifier.height(1.dp))
                                    }
                                    items(pagination.visibleItems.size) { mpasiItem ->
                                        MpasiCard(
                                            image = pagination.visibleItems[mpasiItem].image,
                                            title = pagination.visibleItems[mpasiItem].title,
                                            description = pagination.visibleItems[mpasiItem].description,
                                            onClick = {
                                                activity.startActivity(
                                                    Intent(context, DetailMpasiActivity::class.java).apply {
                                                        putExtra("id", pagination.absoluteIndex(mpasiItem))
                                                        putExtra("group", "25Tahun")
                                                    }
                                                )
                                            }
                                        )
                                    }
                                    item { PaginationBar(pagination = pagination, itemLabel = "resep") }
                                    item { Spacer(modifier = Modifier.height(10.dp)) }
                                }
                            }

                            is UiState.Error -> {
                                ErrorMessage(message = mpasiRespond.errorMessage)
                            }

                            is UiState.Unauthorized -> {
                                redirectToHome()
                            }

                            else -> {}
                        }
                    }
                }
            }

        }
    }
}