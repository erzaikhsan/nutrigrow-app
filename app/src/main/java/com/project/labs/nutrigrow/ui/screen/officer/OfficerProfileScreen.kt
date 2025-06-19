package com.project.labs.nutrigrow.ui.screen.officer

import android.app.DatePickerDialog
import android.content.Intent
import android.widget.DatePicker
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.project.labs.nutrigrow.R
import com.project.labs.nutrigrow.data.model.AccountModel
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.ui.component.respond.ErrorMessage
import com.project.labs.nutrigrow.ui.component.respond.LoadingIndicator
import com.project.labs.nutrigrow.ui.component.snackbar.CustomSnackBar
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.state.UiState
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun OfficerProfileScreen(
    id: String,
    redirectToHome: () -> Unit,
    viewModel: OfficerProfileViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    var showLogoutDialog by remember { mutableStateOf(false) }

    var date by remember { mutableStateOf("") }

    val mYear: Int
    val mMonth: Int
    val mDay: Int

    val mCalendar = Calendar.getInstance()

    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()

    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            date = "$mDayOfMonth/${mMonth+1}/$mYear"
        }, mYear, mMonth, mDay
    )

    val checkAuth by viewModel.isAuthenticated
    val pdfDownloadState by viewModel.pdfDownloadState.collectAsState()
    val isActive: UiState<AccountModel> by viewModel.isActive

    val snackState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = checkAuth) {
        viewModel.checkAuthentication()
    }

    LaunchedEffect(pdfDownloadState) {
        when (val state = pdfDownloadState) {
            is UiState.Success -> {
                coroutineScope.launch {
                    snackState.showSnackbar("Berhasil Ekspor Data")
                    redirectToHome()
                }
                val file = state.data
                Toast.makeText(context, "PDF disimpan di: ${file.absolutePath}", Toast.LENGTH_LONG).show()

                // Buka file PDF
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Tidak ada aplikasi PDF", Toast.LENGTH_SHORT).show()
                }
            }

            is UiState.Error -> {
                if (state.errorMessage == "Mohon Pilih Tanggal Penimbangan") {
                    coroutineScope.launch {
                        snackState.showSnackbar("Gagal Ekspor Data\n${state.errorMessage}")
                    }
                } else {
                    coroutineScope.launch {
                        snackState.showSnackbar("Gagal Ekspor Data\n${state.errorMessage}")
                        redirectToHome()
                    }
                }
            }

            else -> {}
        }
    }

    DisposableEffect(key1 = isActive){
        when (isActive) {
            is UiState.Success -> {
                coroutineScope.launch {
                    snackState.showSnackbar("Berhasil ${
                        if ((isActive as UiState.Success<AccountModel>).data.isActive) {
                            "Mengaktifkan Akun"
                        } else {
                            "Menonaktifkan Akun"
                        }
                    }")
                }
                viewModel.getOfficerAccount(id)
            }

            is UiState.Error -> {
                coroutineScope.launch {
                    snackState.showSnackbar("Gagal Menonaktifkan Akun\n${(isActive as UiState.Error).errorMessage}")
                }
            }
            else -> {}
        }

        onDispose { }
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
        viewModel.officer.collectAsState(initial = UiState.Loading).value.let { officers ->
            when (officers) {
                is UiState.Loading -> {
                    LoadingIndicator()
                    viewModel.getOfficerAccount(id)
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
                                        text = officers.data.full_name,
                                        fontSize = 18.sp,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Petugas Kader",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                                AsyncImage(
                                    model = if (officers.data.gender == "M") R.drawable.man else R.drawable.woman,
                                    contentDescription = "Profile Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(50.dp)
                                        .width(45.dp)
                                        .clip(CircleShape)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(7.dp))
                        ElevatedCard(
                            colors = CardDefaults.cardColors(Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
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
                                        text = "Profil Kader",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    when (checkAuth) {
                                        is UiState.Success -> {
                                            if ((checkAuth as UiState.Success<AuthModel>).data.role == "Admin"){
                                                if (officers.data.isActive) {
                                                    Icon(
                                                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_group_remove_24),
                                                        contentDescription = "Deactivate",
                                                        tint = Color.Red,
                                                        modifier = Modifier
                                                            .size(23.dp)
                                                            .clickable { showLogoutDialog = true },
                                                    )
                                                } else {
                                                    Icon(
                                                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_how_to_reg_24),
                                                        contentDescription = "Active",
                                                        tint = Color.Green,
                                                        modifier = Modifier
                                                            .size(23.dp)
                                                            .clickable { showLogoutDialog = true },
                                                    )
                                                }

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
                                            text = "Status Akun :",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.W500,
                                        )
                                        Text(
                                            text = if (officers.data.isActive) "Aktif" else "Tidak Aktif",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.W500,
                                            color = if (officers.data.isActive) Color.Green else Color.Red
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(5.dp))
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
                                            text = officers.data.full_name,
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
                                            text = if (officers.data.gender == "M") "Laki-laki" else "Perempuan",
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
                                            text = formatDate(officers.data.date_of_birth),
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
                                            text = "No. Hp :",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.W500,
                                        )
                                        Text(
                                            text = officers.data.phone_number,
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
                                            text = "Wilayah :",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.W500,
                                        )
                                        Text(
                                            text = officers.data.region,
                                            fontSize = 15.sp,
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Alamat :",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.W500,
                                        )
                                        Spacer(modifier = Modifier.width(25.dp))
                                        Text(
                                            text = officers.data.address,
                                            maxLines = 3,
                                            textAlign = TextAlign.End,
                                            fontSize = 15.sp,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        ElevatedCard(
                            shape = RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
                            ),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                                .clickable { mDatePickerDialog.show() }
                        ) {
                            Column(
                                modifier = Modifier.padding(
                                    start = 20.dp,
                                    end = 20.dp,
                                    top = 15.dp,
                                    bottom = 15.dp
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_date_range_24),
                                        contentDescription = "Date of Birth",
                                        tint = Color.Black,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(15.dp))
                                    Text(
                                        text = date.ifBlank { "Pilih Tanggal Penimbangan" },
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            color = Color(0xFF00BF63),
                            thickness = 3.dp
                        )
                        Spacer(modifier = Modifier.height(7.dp))
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                        ) {
                            Column {
                                Row (
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White)
                                        .padding(20.dp)
                                        .clickable {
                                            viewModel.getMonthlyReport(
                                                context = context,
                                                region = officers.data.region,
                                                currentDate = date
                                            )
                                        }
                                ){
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Toddler",
                                    )
                                    Spacer(modifier = Modifier.width(20.dp))
                                    Column {
                                        Text(
                                            text = "Penimbangan Balita",
                                            style = MaterialTheme.typography.titleMedium,
                                        )
                                        Text(
                                            text = "Laporan data penimbangan balita",
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "",
                                    )
                                }
                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White)
                                        .padding(horizontal = 15.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                    thickness = 1.dp
                                )
                                Row (
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White)
                                        .padding(20.dp)
                                        .clickable {
                                            viewModel.getRegionChildrenReport(
                                                officers.data.region,
                                                context
                                            )
                                        }
                                ){
                                    Icon(
                                        imageVector = Icons.Default.Face,
                                        contentDescription = "Toddler",
                                    )
                                    Spacer(modifier = Modifier.width(20.dp))
                                    Column {
                                        Text(
                                            text = "Biodata Balita",
                                            style = MaterialTheme.typography.titleMedium,
                                        )
                                        Text(
                                            text = "Data lengkap balita Posyandu",
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "",
                                    )
                                }
                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White)
                                        .padding(horizontal = 15.dp),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                    thickness = 1.dp
                                )

                                Row (
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White)
                                        .padding(20.dp)
                                        .clickable {
                                            viewModel.getRegionParentReport(
                                                officers.data.region,
                                                context
                                            )
                                        }
                                ){
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Orang Tua",
                                    )
                                    Spacer(modifier = Modifier.width(20.dp))
                                    Column {
                                        Text(
                                            text = "Biodata Orang Tua Balita",
                                            style = MaterialTheme.typography.titleMedium,
                                        )
                                        Text(
                                            text = "Data lengkap orang tua balita",
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "",
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    if (showLogoutDialog) {
                        if (officers.data.isActive) {
                            AlertDialog(
                                containerColor = Color.White,
                                onDismissRequest = { showLogoutDialog = false },
                                title = { Text(text = "Konfirmasi Nonaktifkan Akun") },
                                text = { Text("Apakah Anda yakin menonaktifkan akun ini?") },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            showLogoutDialog = false
                                            viewModel.deactivateAccount(id)
                                        }
                                    ) {
                                        Text("Ya")
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = { showLogoutDialog = false }
                                    ) {
                                        Text("Batal")
                                    }
                                }
                            )
                        } else {
                            AlertDialog(
                                containerColor = Color.White,
                                onDismissRequest = { showLogoutDialog = false },
                                title = { Text(text = "Konfirmasi Aktivasi Akun") },
                                text = { Text("Apakah Anda yakin mengaktfkan akun ini?") },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            showLogoutDialog = false
                                            viewModel.activateAccount(id)
                                        }
                                    ) {
                                        Text("Ya")
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = { showLogoutDialog = false }
                                    ) {
                                        Text("Batal")
                                    }
                                }
                            )
                        }
                    }
                }
                is UiState.Error -> {
                    ErrorMessage(message = officers.errorMessage)
                }
                is UiState.Unauthorized -> {
                    redirectToHome()
                }
            }
        }

        SnackbarHost(
            modifier = Modifier.align(Alignment.TopCenter),
            hostState = snackState
        ) { snackbarData: SnackbarData ->
            CustomSnackBar(
                drawableRes = if (snackbarData.visuals.message.startsWith("Gagal")) R.drawable.baseline_error_outline_24 else R.drawable.baseline_check_circle_outline_24,
                message = snackbarData.visuals.message,
                containerColor = when (snackbarData.visuals.message) {
                    "Berhasil Ekspor Data" -> MaterialTheme.colorScheme.primary
                    "Berhasil Menonaktifkan Akun" -> Color(0xFF49ACFC)
                    "Berhasil Mengaktifkan Akun" -> Color(0xFF49ACFC)
                    else -> MaterialTheme.colorScheme.error
                },
            )
        }
    }
}