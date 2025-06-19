package com.project.labs.nutrigrow.ui.screen.growth.detail

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.project.labs.nutrigrow.R
import com.project.labs.nutrigrow.activity.growth.AddGrowthActivity
import com.project.labs.nutrigrow.activity.growth.UpdateGrowthActivity
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.ui.component.graph.bbtb.WflhChart
import com.project.labs.nutrigrow.ui.component.graph.bbu.WfaChart02
import com.project.labs.nutrigrow.ui.component.graph.bbu.WfaChart25
import com.project.labs.nutrigrow.ui.component.graph.tbu.GrowthChart02
import com.project.labs.nutrigrow.ui.component.graph.tbu.GrowthChart25
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
fun DetailGrowthScreen(
    id: String,
    redirectToHome: () -> Unit,
    viewModel: DetailGrowthViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity

    val checkAuth by viewModel.isAuthenticated
    var selectedTab by remember { mutableStateOf("Grafik") }

    LaunchedEffect(key1 = checkAuth) {
        viewModel.checkAuthentication()
    }

    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.getChildProfile(id)
            viewModel.getGrowthByChildId(id)
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
                    val dob = LocalDate.parse(child.data.date_of_birth.substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    val currentDate = LocalDate.now(ZoneId.systemDefault())
                    val period = Period.between(dob, currentDate)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        ElevatedCard(
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
                                    bottom = 7.dp,
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
                                    .padding(
                                        start = 15.dp,
                                        end = 15.dp,
                                        bottom = 10.dp,
                                        top = 10.dp
                                    )
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.nutrigrow_nobg),
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 15.dp,
                                    end = 15.dp,
                                    top = 5.dp,
                                    bottom = 5.dp
                                )
                        ) {
                            ElevatedCard(
                                colors = CardDefaults.cardColors( if (selectedTab == "Data") Color.White else Color(0xFF00BF63)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .padding(
                                        end = 5.dp
                                    )
                                    .clickable { selectedTab = "Grafik" }
                            ){
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxSize()
                                ) {
                                    Text(
                                        text = "Grafik",
                                        color = if (selectedTab == "Data") Color.Black else Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.W600
                                    )
                                }
                            }
                            ElevatedCard(
                                colors = CardDefaults.cardColors( if (selectedTab == "Grafik") Color.White else Color(0xFF00BF63)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .padding(
                                        start = 5.dp,
                                    )
                                    .clickable { selectedTab = "Data" }
                            ){
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxSize()
                                ) {
                                    Text(
                                        text = "Data",
                                        color = if (selectedTab == "Grafik") Color.Black else Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.W600
                                    )
                                }
                            }
                        }

                        when (selectedTab) {
                            "Grafik" -> {
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
                                            top = 7.dp,
                                            start = 10.dp,
                                            end = 10.dp
                                        )
                                ){
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                top = 20.dp,
                                            )
                                    ) {
                                        Text(
                                            text = "Grafik Pertumbuhan",
                                            fontSize = 20.sp,
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(
                                                    start = 30.dp,
                                                    end = 30.dp,
                                                )
                                        )
                                        if (period.years < 2){
                                            Text(
                                                text = "(Umur 0 sampai 2 tahun)",
                                                textAlign = TextAlign.Center,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.W600,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(start = 20.dp, end = 20.dp)
                                            )
                                        } else {
                                            Text(
                                                text = "(Umur 2 sampai 5 tahun)",
                                                textAlign = TextAlign.Center,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.W600,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(start = 20.dp, end = 20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(5.dp))
                                        Divider(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 20.dp, end = 20.dp)
                                                .background(Color.White),
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                            thickness = 1.dp
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        viewModel.growth.collectAsState(initial = UiState.Loading).value.let { grew ->
                                            when (grew) {
                                                is UiState.Loading -> {
                                                    OriginalLoading()
                                                    viewModel.getGrowthByChildId(id)
                                                }
                                                is UiState.Success -> {

                                                    //Tinggi Badan Per Umur
                                                    Text(
                                                        text = "Berdasarkan Panjang Badan atau Tinggi Badan Menurut Umur (PB/U atau TB/U)",
                                                        textAlign = TextAlign.Center,
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.W500,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(start = 20.dp, end = 20.dp)
                                                    )
                                                    Spacer(modifier = Modifier.height(5.dp))
                                                    val growthData = grew.data
                                                    val tbuData = growthData.map { growthEntry ->
                                                        Pair(growthEntry.age, growthEntry.height.toInt())
                                                    }

                                                    if (period.years < 2){
                                                        GrowthChart02(data = tbuData, child.data.gender)
                                                    } else {
                                                        GrowthChart25(data = tbuData, child.data.gender)
                                                    }

                                                    //Berat Badan Per Umur
                                                    Divider(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(start = 20.dp, end = 20.dp)
                                                            .background(Color.White),
                                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                                        thickness = 1.dp
                                                    )
                                                    Spacer(modifier = Modifier.height(10.dp))
                                                    Text(
                                                        text = "Berdasarkan Berat Badan Menurut Umur (BB/U)",
                                                        textAlign = TextAlign.Center,
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.W500,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(start = 20.dp, end = 20.dp)
                                                    )
                                                    Spacer(modifier = Modifier.height(5.dp))
                                                    val bbuData = growthData.map { growthEntry ->
                                                        Pair(growthEntry.age, growthEntry.weight.toFloat())
                                                    }

                                                    if (period.years < 2){
                                                        WfaChart02(data = bbuData, child.data.gender)
                                                    } else {
                                                        WfaChart25(data = bbuData, child.data.gender)
                                                    }

                                                    //Berat Badan Per Tinggi Badan
                                                    Divider(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(start = 20.dp, end = 20.dp)
                                                            .background(Color.White),
                                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                                        thickness = 1.dp
                                                    )
                                                    Spacer(modifier = Modifier.height(10.dp))
                                                    Text(
                                                        text = "Berdasarkan Berat Badan Menurut Tinggi Badan (BB/TB)",
                                                        textAlign = TextAlign.Center,
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.W500,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(start = 20.dp, end = 20.dp)
                                                    )
                                                    Spacer(modifier = Modifier.height(5.dp))
                                                    val bbtb = growthData.map { growthEntry ->
                                                        Pair(growthEntry.height.toInt(), growthEntry.weight.toFloat())
                                                    }

                                                    WflhChart(data = bbtb, gender = child.data.gender )
                                                }
                                                is UiState.Error -> {
                                                    ErrorMessage(message = grew.errorMessage)
                                                }

                                                else -> {  }
                                            }
                                        }
                                    }
                                }
                            }

                            "Data" -> {
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
                                                text = "Data Penimbangan",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
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
                                        viewModel.growth.collectAsState(initial = UiState.Loading).value.let { growths ->
                                            when (growths) {
                                                is UiState.Loading -> {
                                                    OriginalLoading()
                                                }
                                                is UiState.Success -> {
                                                    if (growths.data.isNotEmpty()) {
                                                        Column(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(top = 10.dp)
                                                        ) {
                                                            growths.data.forEach { growthItem ->
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
                                                                                    if ((checkAuth as UiState.Success<AuthModel>).data.role == "Officer") {
                                                                                        activityLauncher.launch(
                                                                                            Intent(
                                                                                                context,
                                                                                                UpdateGrowthActivity::class.java
                                                                                            ).apply {
                                                                                                putExtra(
                                                                                                    "id",
                                                                                                    growthItem.id
                                                                                                )
                                                                                                putExtra(
                                                                                                    "childId",
                                                                                                    id
                                                                                                )
                                                                                            }
                                                                                        )
                                                                                    }
                                                                                }

                                                                                else -> {}
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
                                                                            text = "Tanggal :",
                                                                            fontSize = 17.sp,
                                                                            color = Color.White,
                                                                            style = MaterialTheme.typography.titleMedium,
                                                                            fontWeight = FontWeight.Bold,
                                                                            textAlign = TextAlign.Center,
                                                                        )
                                                                        Text(
                                                                            text = formatDate(growthItem.date),
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
                                                                        Row(
                                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                                            modifier = Modifier
                                                                                .fillMaxWidth()
                                                                        ) {
                                                                            Text(
                                                                                text = "Umur Anak :",
                                                                                fontSize = 15.sp,
                                                                                fontWeight = FontWeight.W500,
                                                                            )
                                                                            Text(
                                                                                text = "${growthItem.age} Bulan",
                                                                                fontSize = 15.sp,
                                                                            )
                                                                        }
                                                                        Spacer(modifier = Modifier.height(3.dp))
                                                                        Row(
                                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                                            modifier = Modifier
                                                                                .fillMaxWidth()
                                                                        ) {
                                                                            Text(
                                                                                text = "Tinggi Badan :",
                                                                                fontSize = 15.sp,
                                                                                fontWeight = FontWeight.W500,
                                                                            )
                                                                            Text(
                                                                                text = "${growthItem.height} Cm",
                                                                                fontSize = 15.sp,
                                                                            )
                                                                        }
                                                                        Spacer(modifier = Modifier.height(3.dp))
                                                                        Row(
                                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                                            modifier = Modifier
                                                                                .fillMaxWidth()
                                                                        ) {
                                                                            Text(
                                                                                text = "Berat Badan :",
                                                                                fontSize = 15.sp,
                                                                                fontWeight = FontWeight.W500,
                                                                            )
                                                                            Text(
                                                                                text = "${growthItem.weight} Kg",
                                                                                fontSize = 15.sp,
                                                                            )
                                                                        }
                                                                        Spacer(modifier = Modifier.height(3.dp))
                                                                        Row(
                                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                                            modifier = Modifier
                                                                                .fillMaxWidth()
                                                                        ) {
                                                                            Text(
                                                                                text = "Lingkar Kepala :",
                                                                                fontSize = 15.sp,
                                                                                fontWeight = FontWeight.W500,
                                                                            )
                                                                            Text(
                                                                                text = "${growthItem.head_circum} Cm",
                                                                                fontSize = 15.sp,
                                                                            )
                                                                        }
                                                                        Spacer(modifier = Modifier.height(3.dp))
                                                                        Row(
                                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                                            modifier = Modifier
                                                                                .fillMaxWidth()
                                                                        ) {
                                                                            Text(
                                                                                text = "Lingkar Lengan :",
                                                                                fontSize = 15.sp,
                                                                                fontWeight = FontWeight.W500,
                                                                            )
                                                                            Text(
                                                                                text = "${growthItem.arm_circum} Cm",
                                                                                fontSize = 15.sp,
                                                                            )
                                                                        }
                                                                        Spacer(modifier = Modifier.height(5.dp))
                                                                        Row(
                                                                            modifier = Modifier
                                                                                .fillMaxWidth()
                                                                        ) {
                                                                            Text(
                                                                                text = "Catatan Posyandu :",
                                                                                fontSize = 15.sp,
                                                                                fontWeight = FontWeight.W500,
                                                                            )
                                                                        }
                                                                        Spacer(modifier = Modifier.height(3.dp))
                                                                        ElevatedCard(
                                                                            colors = CardDefaults.cardColors(Color.White),
                                                                            modifier = Modifier
                                                                                .fillMaxWidth()
                                                                                .heightIn(min = 50.dp)
                                                                        ){
                                                                            Text(
                                                                                text = growthItem.note,
                                                                                fontSize = 15.sp,
                                                                                minLines = 3,
                                                                                textAlign = TextAlign.Center,
                                                                                modifier = Modifier
                                                                                    .fillMaxWidth()
                                                                                    .padding( horizontal = 7.dp, vertical = 10.dp)
                                                                            )
                                                                        }
                                                                    }
                                                                }
                                                                Spacer(modifier = Modifier.height(10.dp))
                                                            }
                                                        }
                                                    } else {
                                                        ElevatedCard(
                                                            colors = CardDefaults.cardColors(Color(0xFFE0FFD2)),
                                                            modifier = Modifier
                                                                .fillMaxWidth()
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
                                                                    text = "Tidak Ada Data\nPenimbangan Balita",
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
                                                                    text = "Silahkan masukan data penimbangan anak secara rutin setiap bulannya pada Posyandu Balita di Desa Jipang.",
                                                                    fontSize = 15.sp,
                                                                    textAlign = TextAlign.Center,
                                                                    fontWeight = FontWeight.W500,
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                                is UiState.Error -> {
                                                    ErrorMessage(message = growths.errorMessage)
                                                }

                                                else -> {  }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }
                is UiState.Error -> {
                    ErrorMessage(message = child.errorMessage)
                }
                is UiState.Unauthorized -> {
                    redirectToHome()
                }
            }
        }
        when (checkAuth) {
            is UiState.Success -> {
                if ((checkAuth as UiState.Success<AuthModel>).data.role == "Officer"){
                    FloatingActionButton(
                        shape = CircleShape,
                        onClick = {
                            activityLauncher.launch(
                                Intent(context, AddGrowthActivity::class.java).apply {
                                    putExtra("id", id)
                                }
                            )
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(10.dp)
                    ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        ){
                            Text(
                                text = "Tambah Data Penimbangan",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.W600,
                                modifier = Modifier.padding(end = 5.dp)
                            )
                        }
                    }
                }
            }
            else -> { }
        }
    }
}
