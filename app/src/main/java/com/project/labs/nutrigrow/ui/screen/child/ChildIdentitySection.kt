package com.project.labs.nutrigrow.ui.screen.child

import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.project.labs.nutrigrow.R
import com.project.labs.nutrigrow.activity.child.UpdateChildActivity
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.ChildrenModel
import com.project.labs.nutrigrow.ui.state.UiState


@Composable
fun ChildProfileHeader(
    child: UiState.Success<ChildrenModel>,
    id: String,
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 7.dp
        ),
        shape = RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp,
            bottomStart = 12.dp,
            bottomEnd = 12.dp
        ),
        colors = CardDefaults.cardColors(Color(0xFF00BF63)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 10.dp, end = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 15.dp,
                    end = 15.dp,
                    bottom = 10.dp,
                    top = 10.dp
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.nutrigrow_negative_nobg),
                contentDescription = "Logo NutriGrow",
                modifier = Modifier
                    .size(55.dp)
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = child.data.full_name,
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = calculateAge(child.data.date_of_birth),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            AsyncImage(
                model = if (child.data.gender == "M") R.drawable.boy else R.drawable.girl,
                contentDescription = "Foto profil",
                modifier = Modifier
                    .padding(4.dp)
                    .size(50.dp)
                    .width(45.dp)
            )
        }
    }
}

@Composable
fun ChildIdentityCard(
    child: UiState.Success<ChildrenModel>,
    checkAuth: UiState<AuthModel>,
    id: String,
    context: Context,
    activityLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
) {
    ElevatedCard(
        colors = CardDefaults.cardColors(Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = CardDefaults.shape)
            .shadow(
                elevation = 0.dp,
                spotColor = Color.Transparent,
                shape = CardDefaults.shape
            )
            .padding(
                bottom = 5.dp,
                top = 5.dp,
                start = 10.dp,
                end = 10.dp
            )
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 20.dp,
                    bottom = 15.dp,
                    start = 20.dp,
                    end = 20.dp
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 10.dp,
                        end = 10.dp,
                    )
            ) {
                Text(
                    text = "Profil Balita",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                when (checkAuth) {
                    is UiState.Success -> {
                        if ((checkAuth as UiState.Success<AuthModel>).data.role == "Parent" || (checkAuth as UiState.Success<AuthModel>).data.role == "Officer"){
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_edit_square_24),
                                contentDescription = "Ubah profil balita",
                                modifier = Modifier
                                    .size(23.dp)
                                    .clickable {
                                        activityLauncher.launch(
                                            Intent(
                                                context,
                                                UpdateChildActivity::class.java
                                            ).apply {
                                                putExtra("id", child.data.children_id)
                                            }
                                        )
                                    },
                            )
                        }
                    }
                    else -> { }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp)
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Nama Lengkap :",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W500,
                    )
                    Text(
                        text = child.data.full_name,
                        fontSize = 15.sp,
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Nama Ayah :",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W500,
                    )
                    Text(
                        text = child.data.father,
                        fontSize = 15.sp,
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Nama Ibu :",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W500,
                    )
                    Text(
                        text = child.data.mother,
                        fontSize = 15.sp,
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Jenis Kelamin :",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W500,
                    )
                    Text(
                        text = if (child.data.gender == "M") "Laki-laki" else "Perempuan",
                        fontSize = 15.sp,
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Anak Ke :",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W500,
                    )
                    Text(
                        text = child.data.order_of_child.toString(),
                        fontSize = 15.sp,
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Tempat lahir :",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W500,
                    )
                    Text(
                        text = child.data.place_of_birth,
                        fontSize = 15.sp,
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Tanggal lahir :",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W500,
                    )
                    Text(
                        text = formatDate(child.data.date_of_birth),
                        fontSize = 15.sp,
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Berat Badan saat lahir :",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W500,
                    )
                    Text(
                        text = "${child.data.birth_weight} kg",
                        fontSize = 15.sp,
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Tinggi Badan saat lahir :",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W500,
                    )
                    Text(
                        text = "${child.data.birth_height} Cm",
                        fontSize = 15.sp,
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Lingkar Kepala saat lahir :",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W500,
                    )
                    Text(
                        text = "${child.data.birth_head_circum} Cm",
                        fontSize = 15.sp,
                    )
                }
            }
        }
    }
}
