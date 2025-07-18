package com.project.labs.nutrigrow.ui.screen.parent.list

import android.app.Activity
import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.labs.nutrigrow.R
import com.project.labs.nutrigrow.activity.parent.ParentActivity
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.ui.component.card.UserCard
import com.project.labs.nutrigrow.ui.component.respond.ErrorMessage
import com.project.labs.nutrigrow.ui.component.respond.LoadingIndicator
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.state.UiState

@OptIn(ExperimentalMaterial3Api::class)
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

    LaunchedEffect(key1 = checkAuth) {
        viewModel.checkAuthentication()
    }

    LaunchedEffect(keyword) {
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
            .background(Color(0xFFE0FFD2))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            viewModel.user.collectAsState(initial = UiState.Loading).value.let { users ->
                when (users) {
                    is UiState.Loading -> {
                        LoadingIndicator()
                        viewModel.getUserProfile()
                    }
                    is UiState.Success -> {
                        Spacer(modifier = Modifier.height(10.dp))
                        ElevatedCard(
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            ),
                            colors = CardDefaults.elevatedCardColors(
                                Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 15.dp, end = 15.dp, top = 10.dp, bottom = 15.dp)
                        ){
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Box (
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 8.dp)
                                ) {
                                    TextField(
                                        value = keyword,
                                        onValueChange = { keyword = it },
                                        shape = RoundedCornerShape(7.dp),
                                        placeholder = { Text("Cari Nama Orang Tua", style = MaterialTheme.typography.titleMedium) },
                                        colors = TextFieldDefaults.textFieldColors(
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            disabledIndicatorColor = Color.Transparent,
                                        ),
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            keyboardType = KeyboardType.Text,
                                            imeAction = ImeAction.Search
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(55.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { }
                                ) {
                                    Icon(painterResource(id = R.drawable.round_search_24), contentDescription = "Search")
                                }
                            }
                        }
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
                        ){
                            Text(
                                text = "Orang Tua Balita",
                                fontSize = 19.sp,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 25.dp,
                                        end = 25.dp,
                                        top = 8.dp,
                                        bottom = 8.dp
                                    )
                            )
                        }
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 15.dp,
                                    end = 15.dp,
                                ),
                            color = Color(0xFF00BF63),
                            thickness = 3.dp
                        )
                        viewModel.search.collectAsState(initial = UiState.Loading).value.let { search ->
                            when (search) {
                                is UiState.Loading -> {
                                    viewModel.parents.collectAsState(initial = UiState.Loading).value.let { parent ->
                                        when (parent) {
                                            is UiState.Loading -> {
                                                LoadingIndicator()
                                                viewModel.getAllParent()
                                            }
                                            is UiState.Success -> {
                                                LazyColumn(
                                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(start = 15.dp, end = 15.dp)
                                                        .background(Color(0xFFE0FFD2)),
                                                ) {
                                                    if (parent.data.isNotEmpty()) {
                                                        item { Spacer(modifier = Modifier.height(1.dp)) }
                                                        items(parent.data.size) { parentItem ->
                                                            UserCard(
                                                                full_name = parent.data[parentItem].full_name,
                                                                gender = parent.data[parentItem].gender,
                                                                phone_number = parent.data[parentItem].phone_number,
                                                                address = parent.data[parentItem].address,
                                                                onClick = {
                                                                    activity.startActivity(
                                                                        Intent(context, ParentActivity::class.java).apply {
                                                                            putExtra("id", parent.data[parentItem].user_id)
                                                                            when(checkAuth){
                                                                                is UiState.Success -> {
                                                                                    putExtra("role", ((checkAuth as UiState.Success<AuthModel>).data.role))
                                                                                }
                                                                                else -> {}
                                                                            }
                                                                        }
                                                                    )
                                                                }
                                                            )
                                                        }
                                                        item { Spacer(modifier = Modifier.height(10.dp)) }
                                                    } else {
                                                        item { Spacer(modifier = Modifier.height(1.dp)) }
                                                        item {
                                                            ElevatedCard(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .clip(shape = CardDefaults.shape)
                                                                    .shadow(
                                                                        elevation = 0.dp,
                                                                        spotColor = Color.Transparent,
                                                                        shape = CardDefaults.shape
                                                                    )
                                                            ) {
                                                                Row(
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    horizontalArrangement = Arrangement.Center,
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                        .height(90.dp)
                                                                        .background(Color.White)
                                                                        .padding(start = 10.dp, end = 10.dp, bottom = 7.dp, top = 7.dp)
                                                                ){
                                                                    Text(
                                                                        text = "Tidak Ada Data.",
                                                                        fontSize = 17.sp,
                                                                        style = MaterialTheme.typography.titleMedium,
                                                                        fontWeight = FontWeight.Bold,
                                                                        textAlign = TextAlign.Center,
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            is UiState.Error -> {
                                                ErrorMessage(message = parent.errorMessage)
                                            }

                                            else -> { }
                                        }
                                    }
                                }

                                is UiState.Success -> {
                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(start = 15.dp, end = 15.dp)
                                            .background(Color(0xFFE0FFD2)),
                                    ){
                                        if (search.data.isNotEmpty()) {
                                            item { Spacer(modifier = Modifier.height(1.dp)) }
                                            items(search.data.size) { parentItem ->
                                                UserCard(
                                                    full_name = search.data[parentItem].full_name,
                                                    gender = search.data[parentItem].gender,
                                                    phone_number = search.data[parentItem].phone_number,
                                                    address = search.data[parentItem].address,
                                                    onClick = {
                                                        activity.startActivity(
                                                            Intent(context, ParentActivity::class.java).apply {
                                                                putExtra("id", search.data[parentItem].user_id)
                                                                when(checkAuth){
                                                                    is UiState.Success -> {
                                                                        putExtra("role", ((checkAuth as UiState.Success<AuthModel>).data.role))
                                                                    }
                                                                    else -> {}
                                                                }
                                                            }
                                                        )
                                                    }
                                                )
                                            }
                                            item { Spacer(modifier = Modifier.height(10.dp)) }
                                        } else {
                                            item { Spacer(modifier = Modifier.height(1.dp)) }
                                            item {
                                                ElevatedCard(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(shape = CardDefaults.shape)
                                                        .shadow(
                                                            elevation = 0.dp,
                                                            spotColor = Color.Transparent,
                                                            shape = CardDefaults.shape
                                                        )
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(90.dp)
                                                            .background(Color.White)
                                                            .padding(start = 10.dp, end = 10.dp, bottom = 7.dp, top = 7.dp)
                                                    ){
                                                        Text(
                                                            text = "Tidak Ada Data.",
                                                            fontSize = 17.sp,
                                                            style = MaterialTheme.typography.titleMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            textAlign = TextAlign.Center,
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                is UiState.Error -> {
                                    ErrorMessage(message = search.errorMessage)
                                }

                                else -> { }
                            }
                        }

                    }

                    is UiState.Error -> {
                        ErrorMessage(message = users.errorMessage)
                    }

                    is UiState.Unauthorized -> {
                        redirectToWelcome("Sesi Telah Berakhir\nSilahkan Masuk Kembali")
                    }

                    else -> {}
                }
            }
        }
    }
}