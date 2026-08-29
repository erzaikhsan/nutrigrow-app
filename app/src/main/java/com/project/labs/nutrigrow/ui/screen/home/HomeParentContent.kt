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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.project.labs.nutrigrow.R
import com.project.labs.nutrigrow.activity.article.ArticleActivity
import com.project.labs.nutrigrow.activity.child.AddChildActivity
import com.project.labs.nutrigrow.activity.child.ChildActivity
import com.project.labs.nutrigrow.activity.event.DetailEventActivity
import com.project.labs.nutrigrow.activity.growth.GrowthActivity
import com.project.labs.nutrigrow.activity.mpasi.MpasiActivity
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.UserModel
import com.project.labs.nutrigrow.ui.component.card.ChildCard
import com.project.labs.nutrigrow.ui.component.card.EventCard
import com.project.labs.nutrigrow.ui.component.respond.ErrorMessage
import com.project.labs.nutrigrow.ui.component.respond.OriginalLoading
import com.project.labs.nutrigrow.ui.state.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeParentContent(
    user: UserModel,
    auth: AuthModel,
    viewModel: HomeViewModel,
    context: Context,
    activityLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    date: String,
    modifier: Modifier = Modifier,
) {
    //Homepage Parent
    viewModel.event.collectAsState(initial = UiState.Loading).value.let { event ->
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
                        Text(
                            text = "Profil Balita",
                            fontSize = 18.sp,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(
                                start = 25.dp,
                                end = 25.dp,
                            )
                        )
                        Spacer(modifier = Modifier.height(7.dp))
                        viewModel.children.collectAsState(initial = UiState.Loading).value.let { child ->
                            when (child) {
                                is UiState.Loading -> {
                                    OriginalLoading()
                                    LaunchedEffect(Unit) {
                                        viewModel.getChildrenByParent(auth.id)
                                    }
                                }
                                is UiState.Success -> {
                                    if (child.data.isEmpty()) {
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(
                                                    start = 25.dp,
                                                    end = 25.dp,
                                                )
                                        ) {
                                            ElevatedCard(
                                                colors = CardDefaults.cardColors(Color.White),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(80.dp)
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
                                                                AddChildActivity::class.java
                                                            )
                                                        )
                                                    }
                                            ) {
                                                Column(
                                                    verticalArrangement = Arrangement.Center,
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(
                                                            Color.White
                                                        )
                                                ) {
                                                    Icon(
                                                        imageVector = ImageVector.vectorResource(id = R.drawable.round_add_circle_24),
                                                        contentDescription = "Tambah balita",
                                                        modifier = Modifier
                                                            .size(33.dp)
                                                            .padding(
                                                                bottom = 8.dp
                                                            ),
                                                        tint = Color.Gray
                                                    )
                                                    Text(
                                                        text = "Tambah Data Balita",
                                                        textAlign = TextAlign.Center,
                                                        fontSize = 15.sp,
                                                        style = MaterialTheme.typography.titleMedium,
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        LazyRow(
                                            horizontalArrangement = Arrangement.spacedBy(15.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        ) {
                                            item { Spacer(modifier = Modifier.width(1.dp)) }
                                            items(child.data.size) { children ->
                                                ChildCard(
                                                    full_name = child.data[children].full_name,
                                                    gender = child.data[children].gender,
                                                    date_of_birth = child.data[children].date_of_birth,
                                                    color = "White",
                                                    onClick = {
                                                        activityLauncher.launch(
                                                            Intent(context, ChildActivity::class.java).apply {
                                                                putExtra("id", child.data[children].children_id)
                                                                putExtra("role", auth.role)
                                                            }
                                                        )
                                                    },
                                                )
                                            }
                                            item {
                                                ElevatedCard(
                                                    modifier = Modifier
                                                        .width(90.dp)
                                                        .height(90.dp)
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
                                                                    AddChildActivity::class.java
                                                                )
                                                            )
                                                        }
                                                ) {
                                                    Column(
                                                        verticalArrangement = Arrangement.Center,
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .background(
                                                                Color.White
                                                            )
                                                    ) {
                                                        Icon(
                                                            imageVector = ImageVector.vectorResource(id = R.drawable.round_add_circle_24),
                                                            contentDescription = "Tambah balita",
                                                            modifier = Modifier
                                                                .size(30.dp)
                                                                .padding(
                                                                    bottom = 5.dp
                                                                ),
                                                            tint = Color.Gray
                                                        )
                                                        Text(
                                                            text = "Tambah\nBalita",
                                                            textAlign = TextAlign.Center,
                                                            fontSize = 12.sp,
                                                            style = MaterialTheme.typography.titleMedium,
                                                        )
                                                    }
                                                }
                                            }
                                            item { Spacer(modifier = Modifier.width(1.dp)) }
                                        }
                                    }
                                }
                                is UiState.Error -> {
                                    ErrorMessage(message = child.errorMessage)
                                }
                                else -> {}
                            }
                        }
                        Spacer(modifier = Modifier.height(25.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
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
                                                    GrowthActivity::class.java
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
                                            painter = painterResource(id = R.drawable.growth),
                                            contentDescription = "Pertumbuhan",
                                            modifier = Modifier
                                                .size(40.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = "Pertumbuhan",
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
                                                    MpasiActivity::class.java
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
                                            painter = painterResource(id = R.drawable.recipe),
                                            contentDescription = "MPASI",
                                            modifier = Modifier
                                                .size(40.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = "MPASI",
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
                                                    ArticleActivity::class.java
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
                                            painter = painterResource(id = R.drawable.article),
                                            contentDescription = "Artikel",
                                            modifier = Modifier
                                                .size(40.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = "Artikel",
                                    fontSize = 13.sp,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
            when (event) {
                is UiState.Loading -> {
                    item {
                        OriginalLoading()
                        LaunchedEffect(Unit) {
                            viewModel.getIncomingEvent(date, user.region)
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
                                text = "Kegiatan Posyandu",
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

                    if (event.data.isEmpty()){
                        item {
                            Spacer(modifier = Modifier.height(7.dp))
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
                                        text = "Tidak Ada Kegiatan\nYang Akan Berlangsung.",
                                        fontSize = 17.sp,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(7.dp))
                        }
                    } else {
                        item {
                            Spacer(modifier = Modifier.height(7.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = 15.dp,
                                        end = 15.dp,
                                        bottom = 5.dp
                                    )
                            ) {
                                Spacer(modifier = Modifier.height(1.dp))
                                event.data.forEach { eventItem ->
                                    EventCard(
                                        title = eventItem.title,
                                        date = eventItem.date,
                                        start_time = eventItem.start_time,
                                        end_time = eventItem.end_time,
                                        place = eventItem.place,
                                        onClick = {
                                            activityLauncher.launch(
                                                Intent(context, DetailEventActivity::class.java).apply {
                                                    putExtra("id", eventItem.id)
                                                }
                                            )
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                Spacer(modifier = Modifier.height(1.dp))
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    item { ErrorMessage(message = event.errorMessage) }
                }
                else -> {}
            }
        }
    }
}
