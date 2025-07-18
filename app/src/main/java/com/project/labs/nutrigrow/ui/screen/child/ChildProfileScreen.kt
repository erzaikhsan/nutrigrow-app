package com.project.labs.nutrigrow.ui.screen.child

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.project.labs.nutrigrow.R
import com.project.labs.nutrigrow.activity.child.UpdateChildActivity
import com.project.labs.nutrigrow.activity.growth.DetailGrowthActivity
import com.project.labs.nutrigrow.activity.vaccine.AddVaccineActivity
import com.project.labs.nutrigrow.activity.vaccine.UpdateVaccineActivity
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.ui.component.respond.ErrorMessage
import com.project.labs.nutrigrow.ui.component.respond.LoadingIndicator
import com.project.labs.nutrigrow.ui.component.respond.OriginalLoading
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.state.UiState
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ChildProfileScreen(
    id: String,
    redirectToHome: () -> Unit,
    viewModel: ChildProfileViewModel = viewModel(
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

    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.getChildProfile(id)
            viewModel.getLastGrowth(id)
            viewModel.getVaccineByChildId(id)
        }
    }

    fun calculateAge(dateOfBirth: String): String {
        val dob = LocalDate.parse(dateOfBirth.substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val currentDate = LocalDate.now(ZoneId.systemDefault())

        val period = Period.between(dob, currentDate)

        return "${period.years} tahun ${period.months} bulan"
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
            .background(Color(0xFFE0FFD2))
    ) {
        viewModel.child.collectAsState(initial = UiState.Loading).value.let { child ->
            when (child) {
                is UiState.Loading -> {
                    LoadingIndicator()
                    viewModel.getChildProfile(id)
                }
                is UiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
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
                                    contentDescription = "NutriGrow Logo",
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
                                    contentDescription = "Profile Image",
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(50.dp)
                                        .width(45.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
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
                                                    contentDescription = "Edit Child Profile",
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
                                    Divider(
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
                                        text = "Pertumbuhan Balita",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "See All",
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clickable {
                                                activityLauncher.launch(
                                                    Intent(context, DetailGrowthActivity::class.java).apply {
                                                        putExtra("id", child.data.children_id)
                                                    }
                                                )
                                            },
                                        tint = Color.Gray
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                    thickness = 1.dp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                viewModel.growth.collectAsState(initial = UiState.Loading).value.let { growth ->
                                    when (growth) {
                                        is UiState.Loading -> {
                                            OriginalLoading()
                                            viewModel.getLastGrowth(id)
                                        }
                                        is UiState.Success -> {
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Row(
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(
                                                        start = 10.dp,
                                                        end = 10.dp,
                                                    )
                                            ) {
                                                Text(
                                                    text = "Terakhir diperbarui :",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.W500,
                                                )
                                                Text(
                                                    text = formatDate(growth.data.date),
                                                    fontSize = 14.sp,
                                                    fontStyle = FontStyle.Italic,
                                                )
                                            }
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 10.dp, bottom = 10.dp)
                                            ) {

                                                ElevatedCard(
                                                    shape = RoundedCornerShape(
                                                        topStart = 12.dp,
                                                        topEnd = 12.dp,
                                                        bottomStart = 0.dp,
                                                        bottomEnd = 0.dp
                                                    ),
                                                    colors = CardDefaults.cardColors(Color(0xFFE0FFD2)),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .shadow(
                                                            elevation = 0.dp,
                                                            spotColor = Color.Transparent,
                                                            shape = CardDefaults.shape
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
                                                            text = "Catatan Posyandu :",
                                                            fontSize = 17.sp,
                                                            color = Color.White,
                                                            style = MaterialTheme.typography.titleMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            textAlign = TextAlign.Center,
                                                        )
                                                    }
                                                    Column(
                                                        verticalArrangement = Arrangement.Center,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(
                                                                top = 15.dp,
                                                                bottom = 15.dp,
                                                                start = 10.dp,
                                                                end = 10.dp
                                                            )
                                                    ) {
                                                        ElevatedCard(
                                                            colors = CardDefaults.cardColors(Color.White),
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .heightIn(min = 50.dp)
                                                        ){
                                                            Text(
                                                                text = growth.data.note,
                                                                fontSize = 15.sp,
                                                                textAlign = TextAlign.Center,
                                                                minLines = 3,
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .padding( horizontal = 7.dp, vertical = 10.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                                ElevatedCard(
                                                    shape = RoundedCornerShape(
                                                        topStart = 0.dp,
                                                        topEnd = 0.dp,
                                                        bottomStart = 0.dp,
                                                        bottomEnd = 0.dp
                                                    ),
                                                    colors = CardDefaults.cardColors(Color(0xFFE0FFD2)),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .shadow(
                                                            elevation = 0.dp,
                                                            spotColor = Color.Transparent,
                                                            shape = CardDefaults.shape
                                                        )
                                                ){
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .background(Color(0xFF00BF63))
                                                            .padding(
                                                                horizontal = 20.dp,
                                                                vertical = 10.dp
                                                            )
                                                    ) {
                                                        Text(
                                                            text = "Lingkar Kepala :",
                                                            fontSize = 17.sp,
                                                            color = Color.White,
                                                            style = MaterialTheme.typography.titleMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            textAlign = TextAlign.Center,
                                                        )
                                                        Text(
                                                            text = "${growth.data.head_circum} Cm",
                                                            color = Color.White,
                                                            fontSize = 16.sp,
                                                            fontWeight = FontWeight.W600,
                                                        )
                                                    }
                                                }
                                                Divider(
                                                    modifier = Modifier
                                                        .fillMaxWidth(),
                                                    color = Color(0xFFE0FFD2),
                                                    thickness = 3.dp
                                                )
                                                ElevatedCard(
                                                    shape = RoundedCornerShape(
                                                        topStart = 0.dp,
                                                        topEnd = 0.dp,
                                                        bottomStart = 12.dp,
                                                        bottomEnd = 12.dp
                                                    ),
                                                    colors = CardDefaults.cardColors(Color(0xFFE0FFD2)),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .shadow(
                                                            elevation = 0.dp,
                                                            spotColor = Color.Transparent,
                                                            shape = CardDefaults.shape
                                                        )
                                                ){
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .background(Color(0xFF00BF63))
                                                            .padding(
                                                                horizontal = 20.dp,
                                                                vertical = 10.dp
                                                            )
                                                    ) {
                                                        Text(
                                                            text = "Lingkar Lengan :",
                                                            fontSize = 17.sp,
                                                            color = Color.White,
                                                            style = MaterialTheme.typography.titleMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            textAlign = TextAlign.Center,
                                                        )
                                                        Text(
                                                            text = "${growth.data.arm_circum} Cm",
                                                            color = Color.White,
                                                            fontSize = 16.sp,
                                                            fontWeight = FontWeight.W600,
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(10.dp))
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
                                                ){
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .background(Color(0xFF00BF63))
                                                            .padding(
                                                                horizontal = 20.dp,
                                                                vertical = 10.dp
                                                            )
                                                    ) {
                                                        Text(
                                                            text = "Berat Badan :",
                                                            fontSize = 17.sp,
                                                            color = Color.White,
                                                            style = MaterialTheme.typography.titleMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            textAlign = TextAlign.Center,
                                                        )
                                                        Text(
                                                            text = "${growth.data.weight} kg",
                                                            color = Color.White,
                                                            fontSize = 16.sp,
                                                            fontWeight = FontWeight.W600,
                                                        )
                                                    }
                                                    Column(
                                                        verticalArrangement = Arrangement.Center,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(
                                                                horizontal = 20.dp,
                                                                vertical = 10.dp
                                                            )
                                                    ) {
                                                        Spacer(modifier = Modifier.height(10.dp))
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                        ) {
                                                            Text(
                                                                text = "Interpretasi :",
                                                                fontSize = 15.sp,
                                                                fontWeight = FontWeight.W600,
                                                            )
                                                            ElevatedCard(
                                                                colors = if (growth.data.wfa_status == "Normal") {
                                                                    CardDefaults.cardColors(Color(0xFF00BF63))
                                                                } else if (growth.data.wfa_status == "Underweight" || growth.data.wfa_status == "Risk of Overweight") {
                                                                    CardDefaults.cardColors(Color(0xFFFF9800))
                                                                } else if (growth.data.wfa_status == "Severely Underweight" || growth.data.wfa_status == "Overweight") {
                                                                    CardDefaults.cardColors(Color(0xFFF44336))
                                                                } else { CardDefaults.cardColors(Color(0xFF929492)) },
                                                                modifier = Modifier
                                                                    .clip(shape = CardDefaults.shape)
                                                                    .shadow(
                                                                        elevation = 0.dp,
                                                                        spotColor = Color.Transparent,
                                                                        shape = CardDefaults.shape
                                                                    )
                                                                    .shadow(
                                                                        elevation = 0.dp,
                                                                        spotColor = Color.Transparent,
                                                                        shape = CardDefaults.shape
                                                                    )
                                                            ){
                                                                Text(
                                                                    text = growth.data.wfa_status,
                                                                    fontSize = 15.sp,
                                                                    color = Color.White,
                                                                    modifier = Modifier.padding( vertical = 2.dp, horizontal = 8.dp)
                                                                )
                                                            }
                                                        }
                                                        Spacer(modifier = Modifier.height(10.dp))
                                                        Text(
                                                            text = when (growth.data.wfa_status) {
                                                                "Normal" -> "Balita dengan status gizi ${growth.data.wfa_status} perlu mempertahankan pola makan seimbang dengan variasi makanan yang beragam. Berikan makanan dari semua kelompok gizi: karbohidrat, protein, lemak, vitamin, dan mineral dalam proporsi yang tepat. Jadwal makan teratur 3 kali makan utama dan 2 kali camilan sehat. Dorong konsumsi sayuran dan buah-buahan berwarna-warni, protein dari sumber hewani dan nabati, serta karbohidrat kompleks. Batasi makanan tinggi gula, garam, dan lemak jenuh. Pastikan kebutuhan air putih terpenuhi dan aktivitas fisik yang cukup untuk mendukung pertumbuhan optimal dan mencegah kelebihan berat badan."
                                                                "Underweight" -> "Balita dengan status gizi ${growth.data.wfa_status} (Kurus) membutuhkan peningkatan asupan kalori dan protein untuk mencapai berat badan ideal. Berikan makanan bergizi tinggi dengan porsi yang disesuaikan kemampuan makan anak. Fokus pada protein berkualitas tinggi seperti telur, ikan, ayam, dan kacang-kacangan. Kombinasikan dengan karbohidrat kompleks dan lemak sehat. Berikan camilan bergizi di antara waktu makan utama, seperti buah-buahan, yogurt, atau biskuit yang diperkaya. Pastikan asupan vitamin dan mineral cukup melalui sayuran berwarna dan buah-buahan. Pantau perkembangan berat badan secara rutin dan konsultasikan dengan ahli gizi jika diperlukan."
                                                                "Severely Underweight" -> "Balita dengan status gizi ${growth.data.wfa_status} (Sangat Kurus) memerlukan intervensi nutrisi intensif dan segera. Kebutuhan kalori harian perlu ditingkatkan secara bertahap dengan fokus pada makanan padat energi dan protein tinggi. Berikan makanan dalam porsi kecil namun sering, sekitar 6-8 kali sehari. Prioritaskan protein hewani seperti telur, ikan, daging, dan susu, serta karbohidrat kompleks seperti nasi, kentang, dan ubi. Tambahkan lemak sehat dari alpukat, minyak zaitun, atau kacang-kacangan. Hindari makanan tinggi serat yang dapat membuat anak cepat kenyang. Konsultasi dengan tenaga kesehatan sangat diperlukan untuk evaluasi penyebab dan rencana pemulihan yang tepat."
                                                                "Overweight and Obese" -> "Balita dengan status gizi Overweight (Berlebih) memerlukan pengaturan pola makan yang lebih ketat namun tetap memenuhi kebutuhan nutrisi untuk pertumbuhan. Kurangi asupan kalori dengan membatasi makanan tinggi lemak jenuh, gula, dan makanan olahan. Perbanyak konsumsi sayuran, buah-buahan, dan protein tanpa lemak. Berikan porsi makan yang lebih kecil namun tetap mengandung nutrisi lengkap. Batasi minuman manis dan camilan tinggi kalori. Tingkatkan aktivitas fisik sesuai usia anak seperti bermain aktif. Hindari diet ketat yang dapat mengganggu pertumbuhan, dan konsultasikan dengan dokter atau ahli gizi untuk rencana penurunan berat badan yang aman."
                                                                else -> "Data tidak lengkap"
                                                            },
                                                            textAlign = TextAlign.Justify,
                                                            fontSize = 15.sp,
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(10.dp))
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
                                                ){
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .background(Color(0xFF00BF63))
                                                            .padding(
                                                                horizontal = 20.dp,
                                                                vertical = 10.dp
                                                            )
                                                    ) {
                                                        Text(
                                                            text = "Tinggi Badan :",
                                                            fontSize = 17.sp,
                                                            color = Color.White,
                                                            style = MaterialTheme.typography.titleMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            textAlign = TextAlign.Center,
                                                        )
                                                        Text(
                                                            text = "${growth.data.height} Cm",
                                                            color = Color.White,
                                                            fontSize = 16.sp,
                                                            fontWeight = FontWeight.W600,
                                                        )
                                                    }
                                                    Column(
                                                        verticalArrangement = Arrangement.Center,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(
                                                                horizontal = 20.dp,
                                                                vertical = 10.dp
                                                            )
                                                    ) {
                                                        Spacer(modifier = Modifier.height(10.dp))
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                        ) {
                                                            Text(
                                                                text = "Interpretasi :",
                                                                fontSize = 15.sp,
                                                                fontWeight = FontWeight.W600,
                                                            )
                                                            ElevatedCard(
                                                                colors = if (growth.data.hfa_status == "Normal"  || growth.data.hfa_status == "Tall") {
                                                                    CardDefaults.cardColors(Color(0xFF00BF63))
                                                                } else if (growth.data.hfa_status == "Stunted" || growth.data.hfa_status == "Abnormal") {
                                                                    CardDefaults.cardColors(Color(0xFFFF9800))
                                                                } else if (growth.data.hfa_status == "Severely Stunted") {
                                                                    CardDefaults.cardColors(Color(0xFFF44336))
                                                                } else { CardDefaults.cardColors(Color(0xFF929492)) },
                                                                modifier = Modifier
                                                                    .clip(shape = CardDefaults.shape)
                                                                    .shadow(
                                                                        elevation = 0.dp,
                                                                        spotColor = Color.Transparent,
                                                                        shape = CardDefaults.shape
                                                                    )
                                                            ){
                                                                Text(
                                                                    text = growth.data.hfa_status,
                                                                    fontSize = 15.sp,
                                                                    color = Color.White,
                                                                    modifier = Modifier.padding( vertical = 2.dp, horizontal = 8.dp)
                                                                )
                                                            }
                                                        }
                                                        Spacer(modifier = Modifier.height(10.dp))
                                                        Text(
                                                            text = when (growth.data.hfa_status) {
                                                                "Normal" -> "Balita dengan status gizi Normal perlu mempertahankan asupan nutrisi seimbang untuk mendukung pertumbuhan linier yang optimal. Berikan variasi makanan yang mengandung protein, kalsium, vitamin D, dan mikronutrien lainnya. Pastikan asupan susu atau produk susu yang cukup untuk mendukung pertumbuhan tulang. Berikan sayuran hijau, buah-buahan, dan sumber protein yang beragam. Jaga pola makan teratur dan hindari kebiasaan makan yang dapat mengganggu penyerapan nutrisi. Aktivitas fisik yang cukup juga penting untuk merangsang pertumbuhan tulang. Lakukan pemantauan pertumbuhan secara berkala untuk memastikan pertumbuhan tetap dalam jalur yang baik."
                                                                "Stunted" -> "Balita dengan status gizi Stunting (Pendek) membutuhkan asupan nutrisi yang optimal untuk memaksimalkan potensi pertumbuhan tinggi badan. Prioritaskan makanan sumber protein hewani yang mengandung asam amino lengkap seperti telur, ikan, ayam, dan susu. Kombinasikan dengan protein nabati dari kacang-kacangan dan biji-bijian. Pastikan asupan mikronutrien penting seperti zinc, zat besi, kalsium, dan vitamin A melalui daging, hati, sayuran hijau, dan buah-buahan berwarna orange. Berikan makanan yang mudah dicerna dan diserap tubuh. Hindari faktor yang dapat menghambat penyerapan nutrisi seperti infeksi berulang. Konsultasi dengan tenaga kesehatan untuk memastikan tidak ada masalah kesehatan yang mendasari."
                                                                "Severely Stunted" -> "Balita dengan status gizi Severely Stunting (Sangat Pendek) memerlukan intervensi nutrisi yang intensif dan berkelanjutan untuk mengoptimalkan pertumbuhan linier yang tersisa. Berikan makanan tinggi protein berkualitas seperti telur, ikan, daging, dan produk susu untuk mendukung pertumbuhan tulang dan otot. Pastikan asupan kalsium, fosfor, vitamin D, dan zinc yang cukup melalui susu, keju, sayuran hijau, dan kacang-kacangan. Berikan makanan padat energi dalam porsi kecil namun sering. Hindari makanan yang dapat menghambat penyerapan nutrisi. Suplementasi vitamin dan mineral mungkin diperlukan sesuai anjuran dokter. Konsultasi dengan ahli gizi dan dokter anak secara rutin untuk evaluasi dan modifikasi rencana nutrisi sangat penting."
                                                                else -> "Data tidak lengkap"
                                                            },
                                                            textAlign = TextAlign.Justify,
                                                            fontSize = 15.sp,
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(10.dp))
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
                                                ){
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .background(Color(0xFF00BF63))
                                                            .padding(
                                                                horizontal = 20.dp,
                                                                vertical = 10.dp
                                                            )
                                                    ) {
                                                        Text(
                                                            text = "Tinggi Badan Badan Berdasarkan Berat Badan Anak",
                                                            fontSize = 17.sp,
                                                            color = Color.White,
                                                            style = MaterialTheme.typography.titleMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            textAlign = TextAlign.Center,
                                                        )
                                                    }
                                                    Column(
                                                        verticalArrangement = Arrangement.Center,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(
                                                                horizontal = 20.dp,
                                                                vertical = 10.dp
                                                            )
                                                    ) {
                                                        Spacer(modifier = Modifier.height(10.dp))
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                        ) {
                                                            Text(
                                                                text = "Interpretasi :",
                                                                fontSize = 15.sp,
                                                                fontWeight = FontWeight.W600,
                                                            )
                                                            ElevatedCard(
                                                                colors = if (growth.data.wfh_status == "Normal" ) {
                                                                    CardDefaults.cardColors(Color(0xFF00BF63))
                                                                } else if (growth.data.wfh_status == "Risk of Wasted" || growth.data.wfh_status == "Risk of Overweight") {
                                                                    CardDefaults.cardColors(Color(0xFFFFEB3B))
                                                                } else if (growth.data.wfh_status == "Wasted" || growth.data.wfh_status == "Overweight") {
                                                                    CardDefaults.cardColors(Color(0xFFFF9800))
                                                                } else if (growth.data.wfh_status == "Severely Wasted" || growth.data.wfh_status == "Obese") {
                                                                    CardDefaults.cardColors(Color(0xFFF44336))
                                                                } else { CardDefaults.cardColors(Color(0xFF929492)) },
                                                                modifier = Modifier
                                                                    .clip(shape = CardDefaults.shape)
                                                                    .shadow(
                                                                        elevation = 0.dp,
                                                                        spotColor = Color.Transparent,
                                                                        shape = CardDefaults.shape
                                                                    )
                                                            ){
                                                                Text(
                                                                    text = growth.data.wfh_status,
                                                                    fontSize = 15.sp,
                                                                    color = Color.White,
                                                                    modifier = Modifier.padding( vertical = 2.dp, horizontal = 8.dp)
                                                                )
                                                            }
                                                        }
                                                        Spacer(modifier = Modifier.height(10.dp))
                                                        Text(
                                                            text = when (growth.data.wfh_status) {
                                                                "Normal" -> "Balita dengan proporsi berat badan sesuai tinggi badan perlu mempertahankan keseimbangan nutrisi untuk menjaga komposisi tubuh yang optimal. Berikan makanan seimbang dari semua kelompok gizi dengan porsi yang tepat. Pastikan asupan protein cukup untuk mempertahankan massa otot, karbohidrat untuk energi, dan lemak sehat untuk perkembangan otak. Berikan variasi sayuran dan buah-buahan untuk memenuhi kebutuhan vitamin dan mineral. Jaga pola makan teratur dan hindari makanan berlebihan yang dapat menyebabkan kelebihan berat badan. Dorong aktivitas fisik yang sesuai usia untuk mempertahankan keseimbangan energi dan mendukung perkembangan motorik."
                                                                "Wasting" -> "Balita dengan status gizi ${growth.data.wfh_status} membutuhkan peningkatan asupan energi dan protein untuk memulihkan massa tubuh yang hilang. Berikan makanan padat energi seperti nasi dengan minyak, alpukat, kacang-kacangan, dan protein hewani. Fokus pada makanan yang mudah dicerna dan tinggi kalori. Berikan camilan bergizi tinggi di antara waktu makan seperti susu, biskuit yang diperkaya, atau smoothie buah dengan susu. Pastikan asupan vitamin dan mineral cukup untuk mendukung metabolisme dan pemulihan. Hindari makanan yang dapat mengganggu nafsu makan atau pencernaan. Pantau perkembangan berat badan secara rutin dan sesuaikan porsi makanan sesuai kemajuan."
                                                                "Severely Wasting" -> "Balita dengan status gizi ${growth.data.wfh_status} berada dalam kondisi gizi buruk akut yang memerlukan penanganan medis segera. Berikan makanan tinggi kalori dan protein dengan fokus pada pemulihan massa otot dan lemak tubuh. Makanan harus mudah dicerna seperti bubur yang diperkaya protein, susu, dan minyak. Berikan makanan dalam porsi kecil namun sangat sering, setiap 2-3 jam. Prioritaskan makanan terapeutik seperti RUTF (Ready-to-Use Therapeutic Food) jika tersedia. Hindari makanan tinggi serat yang dapat membuat kenyang tanpa memberikan kalori cukup. Pemantauan medis ketat diperlukan untuk mendeteksi dan mengatasi komplikasi. Konsultasi dengan dokter anak dan ahli gizi untuk rencana rehabilitasi nutrisi yang tepat."
                                                                "Overweight and Obese" -> "Balita dengan kelebihan berat badan relatif terhadap tinggi badannya memerlukan pengaturan pola makan untuk mencapai proporsi tubuh yang sehat. Kurangi asupan kalori dengan membatasi makanan tinggi lemak dan gula, namun tetap memenuhi kebutuhan nutrisi untuk pertumbuhan. Perbanyak konsumsi sayuran, buah-buahan, dan protein tanpa lemak. Berikan porsi makan yang lebih kecil dengan frekuensi yang cukup. Batasi minuman manis dan camilan tinggi kalori. Pilih camilan sehat seperti buah potong atau yogurt rendah lemak. Tingkatkan aktivitas fisik sesuai kemampuan anak. Konsultasi dengan ahli gizi untuk rencana penurunan berat badan yang aman tanpa mengganggu pertumbuhan."
                                                                else -> "Data tidak lengkap"
                                                            },
                                                            textAlign = TextAlign.Justify,
                                                            fontSize = 15.sp,
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        is UiState.Error -> {
                                            if (growth.errorMessage == "Not found") {
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
                                                        .padding(vertical = 10.dp)
                                                ){
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .background(Color(0xFFFF9800))
                                                            .padding(
                                                                horizontal = 20.dp,
                                                                vertical = 10.dp
                                                            )
                                                    ) {
                                                        Text(
                                                            text = "Tidak Ada Data\nPertumbuhan Balita",
                                                            fontSize = 17.sp,
                                                            color = Color.White,
                                                            style = MaterialTheme.typography.titleMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            textAlign = TextAlign.Center,
                                                        )
                                                    }
                                                    Column(
                                                        verticalArrangement = Arrangement.Center,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(
                                                                horizontal = 20.dp,
                                                                vertical = 10.dp
                                                            )
                                                    ) {
                                                        Text(
                                                            text = "Silahkan masukan data pertumbuhan anak secara rutin setiap bulannya pada Posyandu Balita di Desa Jipang.",
                                                            fontSize = 15.sp,
                                                            textAlign = TextAlign.Center,
                                                            fontWeight = FontWeight.W500,
                                                        )
                                                    }
                                                }
                                            } else {
                                                ErrorMessage(message = growth.errorMessage)
                                            }
                                        }

                                        else -> { }
                                    }
                                }
                            }
                        }
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
                                    bottom = 15.dp,
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
                                        text = "Imunisasi Balita",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    when (checkAuth) {
                                        is UiState.Success -> {
                                            if ((checkAuth as UiState.Success<AuthModel>).data.role == "Officer"){
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Add Vaccine",
                                                    modifier = Modifier
                                                        .size(28.dp)
                                                        .clickable {
                                                            activityLauncher.launch(
                                                                Intent(context, AddVaccineActivity::class.java).apply {
                                                                    putExtra("id", child.data.children_id)
                                                                }
                                                            )
                                                        },
                                                    tint = Color.Gray
                                                )
                                            }
                                        }
                                        else -> { }
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                    thickness = 1.dp
                                )
                                viewModel.vaccine.collectAsState(initial = UiState.Loading).value.let { vaccines ->
                                    when (vaccines) {
                                        is UiState.Loading -> {
                                            OriginalLoading()
                                            viewModel.getVaccineByChildId(id)
                                        }
                                        is UiState.Success -> {
                                            if (vaccines.data.isEmpty()) {
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
                                                        .padding(vertical = 10.dp)
                                                ){
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .background(Color(0xFFFF9800))
                                                            .padding(
                                                                horizontal = 20.dp,
                                                                vertical = 10.dp
                                                            )
                                                    ) {
                                                        Text(
                                                            text = "Balita belum mendapatkan Imunisasi",
                                                            fontSize = 17.sp,
                                                            color = Color.White,
                                                            style = MaterialTheme.typography.titleMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            textAlign = TextAlign.Center,
                                                        )
                                                    }
                                                    Column(
                                                        verticalArrangement = Arrangement.Center,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(
                                                                horizontal = 20.dp,
                                                                vertical = 10.dp
                                                            )
                                                    ) {
                                                        Text(
                                                            text = "Silahkan datang ke Posyandu Desa Jipang atau Puskesmas terdekat untuk mendapatkan informasi lebih lanjut mengenai imunisasi anak.",
                                                            fontSize = 15.sp,
                                                            textAlign = TextAlign.Center,
                                                            fontWeight = FontWeight.W500,
                                                        )
                                                    }
                                                }
                                            } else {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(top = 10.dp)
                                                ) {
                                                    vaccines.data.forEach { vaccineItem ->
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
                                                                .clickable {
                                                                    when (checkAuth) {
                                                                        is UiState.Success -> {
                                                                            if ((checkAuth as UiState.Success<AuthModel>).data.role == "Officer"){
                                                                                activityLauncher.launch(
                                                                                    Intent(context, UpdateVaccineActivity::class.java).apply {
                                                                                        putExtra("id", vaccineItem.id)
                                                                                        putExtra("childId", id)
                                                                                    }
                                                                                )
                                                                            }
                                                                        }
                                                                        else -> { }
                                                                    }
                                                                }
                                                        ){
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .background(Color(0xFF00BF63))
                                                                    .padding(
                                                                        horizontal = 20.dp,
                                                                        vertical = 10.dp
                                                                    )
                                                            ) {
                                                                Text(
                                                                    text = "Nama Vaksin :",
                                                                    fontSize = 17.sp,
                                                                    color = Color.White,
                                                                    style = MaterialTheme.typography.titleMedium,
                                                                    fontWeight = FontWeight.Bold,
                                                                    textAlign = TextAlign.Center,
                                                                )
                                                                Spacer(modifier = Modifier.width(10.dp))
                                                                Text(
                                                                    text = vaccineItem.vaccine_name,
                                                                    color = Color.White,
                                                                    textAlign = TextAlign.End,
                                                                    fontSize = 16.sp,
                                                                    fontWeight = FontWeight.W600,
                                                                )
                                                            }
                                                            Column(
                                                                verticalArrangement = Arrangement.Center,
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .padding(
                                                                        horizontal = 20.dp,
                                                                        vertical = 10.dp
                                                                    )
                                                            ) {
                                                                Row(
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                ) {
                                                                    Text(
                                                                        text = "Divaksin pada :",
                                                                        fontSize = 15.sp,
                                                                        fontWeight = FontWeight.W600,
                                                                    )
                                                                    Text(
                                                                        text = formatDate(vaccineItem.date),
                                                                        fontSize = 15.sp,
                                                                        modifier = Modifier.padding( vertical = 2.dp, horizontal = 8.dp)
                                                                    )
                                                                }
                                                                Row(
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                                    modifier = Modifier
                                                                        .fillMaxWidth()
                                                                ) {
                                                                    Text(
                                                                        text = "Faskes :",
                                                                        fontSize = 15.sp,
                                                                        fontWeight = FontWeight.W600,
                                                                    )
                                                                    Text(
                                                                        text = vaccineItem.place,
                                                                        fontSize = 15.sp,
                                                                        modifier = Modifier.padding( vertical = 2.dp, horizontal = 8.dp)
                                                                    )
                                                                }
                                                            }
                                                        }
                                                        Spacer(modifier = Modifier.height(10.dp))
                                                    }
                                                }
                                            }
                                        }
                                        is UiState.Error -> {
                                            ErrorMessage(message = vaccines.errorMessage)
                                        }

                                        else -> {  }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
                is UiState.Error -> {
                    ErrorMessage(message = child.errorMessage)
                }
                is UiState.Unauthorized -> {
                    redirectToHome()
                }

                else -> { redirectToHome() }
            }
        }
    }
}