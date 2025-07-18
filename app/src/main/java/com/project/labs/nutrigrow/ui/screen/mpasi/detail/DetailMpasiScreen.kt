package com.project.labs.nutrigrow.ui.screen.mpasi.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.project.labs.nutrigrow.ui.component.respond.ErrorMessage
import com.project.labs.nutrigrow.ui.component.respond.LoadingIndicator
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.state.UiState
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DetailMpasiScreen(
    id: Int,
    group: String,
    redirectToHome: () -> Unit,
    viewModel: DetailMpasiViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    modifier: Modifier = Modifier,
) {
    val checkAuth by viewModel.isAuthenticated

    LaunchedEffect(key1 = checkAuth) {
        viewModel.checkAuthentication()
    }

    fun formatDate(inputDate: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS XXX", Locale.ENGLISH)
        val outputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id", "ID"))
        val zonedDateTime = ZonedDateTime.parse(inputDate, inputFormatter)
        return zonedDateTime.format(outputFormatter)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        viewModel.mpasi.collectAsState(initial = UiState.Loading).value.let { mpasiRespond ->
            when (mpasiRespond) {
                is UiState.Loading -> {
                    LoadingIndicator()
                    viewModel.getMpasiById(id, group)
                }
                is UiState.Success -> {
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        item {
                            AsyncImage(
                                model = mpasiRespond.data.image,
                                contentDescription = "Mpasi Image",
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        item {
                            ElevatedCard(
                                colors = CardDefaults.cardColors(Color(0xFFE0FFD2)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(shape = CardDefaults.shape)
                                    .shadow(
                                        elevation = 0.dp,
                                        spotColor = Color.Transparent,
                                        shape = CardDefaults.shape
                                    )
                                    .padding(
                                        start = 10.dp,
                                        end = 10.dp,
                                        bottom = 20.dp
                                    )
                            ){
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF00BF63))
                                        .padding(
                                            horizontal = 20.dp,
                                            vertical = 10.dp
                                        )
                                ) {
                                    Text(
                                        text = mpasiRespond.data.title.uppercase(Locale.getDefault()),
                                        style = MaterialTheme.typography.titleLarge,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                }
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            horizontal = 15.dp,
                                            vertical = 10.dp
                                        )
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ){
                                        Text(
                                            text = mpasiRespond.data.writer,
                                            maxLines = 1,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                        Text(
                                            text = formatDate(mpasiRespond.data.date),
                                            maxLines = 1,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Divider(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        thickness = 1.dp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = mpasiRespond.data.description,
                                        textAlign = TextAlign.Justify,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
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