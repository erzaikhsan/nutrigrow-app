package com.project.labs.nutrigrow.ui.screen.home

import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.project.labs.nutrigrow.R
import com.project.labs.nutrigrow.activity.child.ChildrenActivity
import com.project.labs.nutrigrow.activity.officer.OfficerActivity
import com.project.labs.nutrigrow.activity.parent.AllParentActivity
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.UserModel
import com.project.labs.nutrigrow.ui.component.card.OfficerCard
import com.project.labs.nutrigrow.ui.component.input.SearchCard
import com.project.labs.nutrigrow.ui.component.pagination.PaginationBar
import com.project.labs.nutrigrow.ui.component.pagination.rememberPagination
import com.project.labs.nutrigrow.ui.component.respond.ErrorMessage
import com.project.labs.nutrigrow.ui.component.respond.OriginalLoading
import com.project.labs.nutrigrow.ui.state.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAdminContent(
    user: UserModel,
    auth: AuthModel,
    viewModel: HomeViewModel,
    context: Context,
    activityLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    keyword: String,
    onKeywordChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    //Homepage Admin
    (if (keyword.isBlank()) viewModel.officers else viewModel.search)
        .collectAsState(initial = UiState.Loading).value.let { officer ->
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
        ) {
            item{
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 7.dp
                    ),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 12.dp,
                        bottomEnd = 12.dp
                    ),
                    colors = CardDefaults.cardColors(Color(0xFF00BF63)),
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        ElevatedCard(
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 7.dp
                            ),
                            colors = CardDefaults.cardColors( Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(shape = CardDefaults.shape)
                                .shadow(
                                    elevation = 0.dp,
                                    spotColor = Color.Transparent,
                                    shape = CardDefaults.shape
                                )
                                .padding(
                                    bottom = 10.dp,
                                    top = 8.dp,
                                    start = 15.dp,
                                    end = 15.dp
                                )
                        ){
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(15.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.nutrigrow_nobg),
                                    contentDescription = "Logo NutriGrow",
                                    modifier = Modifier
                                        .size(55.dp)
                                )
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .weight(1f)
                                ) {
                                    Text(
                                        text = "Hallo, ${user.full_name}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Welcome to NutriGrow",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                                AsyncImage(
                                    model = if (user.gender == "M") R.drawable.man else R.drawable.woman,
                                    contentDescription = "Foto profil",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(45.dp)
                                        .width(45.dp)
                                        .clip(CircleShape)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                        Row(
                            horizontalArrangement = Arrangement.Absolute.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 15.dp,
                                    end = 15.dp,
                                )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(80.dp)
                            ) {
                                ElevatedCard(
                                    modifier = Modifier
                                        .width(45.dp)
                                        .height(45.dp)
                                        .clip(shape = CardDefaults.shape)
                                        .shadow(
                                            elevation = 0.dp,
                                            spotColor = Color.Transparent,
                                            shape = CardDefaults.shape
                                        )
                                        .clickable {
                                            activityLauncher.launch(
                                                Intent(
                                                    context,
                                                    AllParentActivity::class.java
                                                )
                                            )
                                        }
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.White)
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.parent),
                                            contentDescription = "Orang tua",
                                            modifier = Modifier
                                                .size(40.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = "Orang Tua",
                                    fontSize = 13.sp,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .width(80.dp)
                            ) {
                                ElevatedCard(
                                    modifier = Modifier
                                        .width(45.dp)
                                        .height(45.dp)
                                        .clip(shape = CardDefaults.shape)
                                        .shadow(
                                            elevation = 0.dp,
                                            spotColor = Color.Transparent,
                                            shape = CardDefaults.shape
                                        )
                                        .clickable {
                                            activityLauncher.launch(
                                                Intent(
                                                    context,
                                                    ChildrenActivity::class.java
                                                )
                                            )
                                        }
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.White)
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.children),
                                            contentDescription = "Balita",
                                            modifier = Modifier
                                                .size(40.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = "Balita",
                                    fontSize = 13.sp,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
            when (officer) {
                is UiState.Loading -> {
                    item {
                        OriginalLoading()
                        LaunchedEffect(Unit) {
                            viewModel.getOfficers()
                        }
                    }
                }
                is UiState.Success -> {
                    item {
                        Spacer(modifier = Modifier.height(15.dp))
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
                                text = "Kader Posyandu Jipang",
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
                    }

                    item {
                        SearchCard(
                            value = keyword,
                            onValueChange = onKeywordChange,
                            placeholder = "Cari Nama Kader",
                        )
                    }

                    if (officer.data.isEmpty()){
                        item { Spacer(modifier = Modifier.height(10.dp))
                            ElevatedCard(
                                colors = CardDefaults.cardColors(Color.White),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .padding(horizontal = 15.dp)
                            ){
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(vertical = 10.dp)
                                ) {
                                    Text(
                                        text = "Tidak Ada Data Kader",
                                        fontSize = 17.sp,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            } }
                    } else {
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            val pagination = rememberPagination(items = officer.data)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 15.dp,
                                        end = 15.dp,
                                        bottom = 5.dp
                                    )
                            ) {
                                pagination.visibleItems.forEach { officerItem ->
                                    OfficerCard(
                                        full_name = officerItem.full_name,
                                        gender = officerItem.gender,
                                        phone_number = officerItem.phone_number,
                                        address = officerItem.address,
                                        region = officerItem.region,
                                        onClick = {
                                            activityLauncher.launch(
                                                Intent(context, OfficerActivity::class.java).apply {
                                                    putExtra("id", officerItem.user_id)
                                                    putExtra("role", auth.role)
                                                }
                                            )
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                                PaginationBar(pagination = pagination, itemLabel = "kader")
                            }
                        }
                        item { 
                            Spacer(modifier = Modifier.height(70.dp))
                        }
                    }
                }
                is UiState.Error -> {
                    item { ErrorMessage(message = officer.errorMessage) }
                }
                else -> {}
            }
        }
    }
}
