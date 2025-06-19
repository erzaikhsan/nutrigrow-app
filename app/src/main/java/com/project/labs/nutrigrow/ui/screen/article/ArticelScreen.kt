package com.project.labs.nutrigrow.ui.screen.article

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.labs.nutrigrow.activity.article.DetailArticleActivity
import com.project.labs.nutrigrow.ui.component.card.ArticleCard
import com.project.labs.nutrigrow.ui.component.respond.ErrorMessage
import com.project.labs.nutrigrow.ui.component.respond.LoadingIndicator
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.state.UiState

@Composable
fun ArticelScreen(
    redirectToHome: () -> Unit,
    viewModel: ArticleViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity

    val checkAuth by viewModel.isAuthenticated

    LaunchedEffect(key1 = checkAuth) {
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
            viewModel.article.collectAsState(initial = UiState.Loading).value.let { articleRespond ->
                when (articleRespond) {
                    is UiState.Loading -> {
                        LoadingIndicator()
                        viewModel.getArticle()
                    }
                    is UiState.Success -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 15.dp)
                                .background(Color(0xFFE0FFD2)),
                        ) {
                            item {
                                Spacer(modifier = Modifier.height(1.dp))
                            }
                            items(articleRespond.data.size) { articleItem ->
                                ArticleCard(
                                    image = articleRespond.data[articleItem].image,
                                    title = articleRespond.data[articleItem].title,
                                    description = articleRespond.data[articleItem].description,
                                    onClick = {
                                        activity.startActivity(
                                            Intent(context, DetailArticleActivity::class.java).apply {
                                                putExtra("id", articleItem)
                                            }
                                        )
                                    }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(10.dp)) }
                        }
                    }

                    is UiState.Error -> {
                        ErrorMessage(message = articleRespond.errorMessage)
                    }

                    is UiState.Unauthorized -> {
                        redirectToHome()
                    }
                }
            }
        }
    }
}