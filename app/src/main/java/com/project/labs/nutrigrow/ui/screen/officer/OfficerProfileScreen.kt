package com.project.labs.nutrigrow.ui.screen.officer

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import android.app.Activity
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
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
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.UserModel
import com.project.labs.nutrigrow.ui.component.respond.ErrorMessage
import com.project.labs.nutrigrow.ui.component.respond.LoadingIndicator
import com.project.labs.nutrigrow.ui.component.snackbar.CustomSnackBar
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.state.UiState
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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

    var showDeleteOfferDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val deletedState by viewModel.deleted

    LaunchedEffect(deletedState) {
        if (deletedState is UiState.Success) {
            (context as? Activity)?.finish()
        }
    }


    val monthNames = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )
    var selectedMonthIndex by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var expandedMonth by remember { mutableStateOf(false) }

    val yearOptions = (2015..2025).toList().reversed()
    var selectedYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var expandedYear by remember { mutableStateOf(false) }

    val checkAuth by viewModel.isAuthenticated
    val pdfDownloadState by viewModel.pdfDownloadState.collectAsState()
    val isActive: UiState<UserModel> by viewModel.isActive

    val snackState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        viewModel.checkAuthentication()
    }

    LaunchedEffect(pdfDownloadState) {
        when (val state = pdfDownloadState) {
            is UiState.Success -> {
                coroutineScope.launch {
                    snackState.showSnackbar("Berhasil Ekspor Data")
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
                viewModel.resetPdfDownloadState()
            }

            is UiState.Error -> {
                coroutineScope.launch {
                    snackState.showSnackbar(state.errorMessage)
                }
                viewModel.resetPdfDownloadState()
            }

            else -> {}
        }
    }

    DisposableEffect(key1 = isActive){
        when (isActive) {
            is UiState.Success -> {
                coroutineScope.launch {
                    snackState.showSnackbar("Berhasil ${
                        if ((isActive as UiState.Success<UserModel>).data.is_active) {
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
                    LaunchedEffect(Unit) {
                        viewModel.getOfficerAccount(id)
                    }
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
                                    contentDescription = "Foto profil",
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
                                                if (officers.data.is_active) {
                                                    Icon(
                                                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_group_remove_24),
                                                        contentDescription = "Nonaktifkan",
                                                        tint = Color.Red,
                                                        modifier = Modifier
                                                            .size(23.dp)
                                                            .clickable { showLogoutDialog = true },
                                                    )
                                                } else {
                                                    Icon(
                                                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_how_to_reg_24),
                                                        contentDescription = "Aktif",
                                                        tint = Color.Green,
                                                        modifier = Modifier
                                                            .size(23.dp)
                                                            .clickable { showLogoutDialog = true },
                                                    )
                                                }
                                                Icon(
                                                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_delete_forever_24),
                                                    contentDescription = "Hapus Akun",
                                                    tint = Color.Red,
                                                    modifier = Modifier
                                                        .size(23.dp)
                                                        .clickable { showDeleteOfferDialog = true },
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
                                            text = "Status Akun :",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.W500,
                                        )
                                        Text(
                                            text = if (officers.data.is_active) "Aktif" else "Tidak Aktif",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.W500,
                                            color = if (officers.data.is_active) Color.Green else Color.Red
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ExposedDropdownMenuBox(
                                expanded = expandedMonth,
                                onExpandedChange = { expandedMonth = !expandedMonth },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = monthNames[selectedMonthIndex],
                                    onValueChange = {},
                                    readOnly = true,
                                    placeholder = { Text("Pilih Bulan Penimbangan", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMonth)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedIndicatorColor = Color(0xFF9DA1A6),
                                        unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                        disabledIndicatorColor = Color(0xFF9DA1A6),
                                    ),
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedMonth,
                                    onDismissRequest = { expandedMonth = false }
                                ) {
                                    monthNames.forEachIndexed { index, month ->
                                        DropdownMenuItem(
                                            text = { Text(month) },
                                            onClick = {
                                                selectedMonthIndex = index
                                                expandedMonth = false
                                            }
                                        )
                                    }
                                }
                            }

                            ExposedDropdownMenuBox(
                                expanded = expandedYear,
                                onExpandedChange = { expandedYear = !expandedYear },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = selectedYear.toString(),
                                    onValueChange = {},
                                    readOnly = true,
                                    placeholder = { Text("Pilih Tahun Penimbangan", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMonth)
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedIndicatorColor = Color(0xFF9DA1A6),
                                        unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                        disabledIndicatorColor = Color(0xFF9DA1A6),
                                    ),
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedYear,
                                    onDismissRequest = { expandedYear = false }
                                ) {
                                    yearOptions.forEach { year ->
                                        DropdownMenuItem(
                                            text = { Text(year.toString()) },
                                            onClick = {
                                                selectedYear = year
                                                expandedYear = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(7.dp))
                        HorizontalDivider(
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
                                                month = selectedMonthIndex + 1,
                                                year = selectedYear
                                            )
                                        }
                                ){
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Balita",
                                    )
                                    Spacer(modifier = Modifier.width(20.dp))
                                    Column {
                                        Text(
                                            text = "Laporan Penimbangan",
                                            style = MaterialTheme.typography.titleMedium,
                                        )
                                        Text(
                                            text = "Laporan penimbangan balita",
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "",
                                    )
                                }
                                HorizontalDivider(
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
                                                month = selectedMonthIndex + 1,
                                                year = selectedYear,
                                                context
                                            )
                                        }
                                ){
                                    Icon(
                                        imageVector = Icons.Default.Face,
                                        contentDescription = "Balita",
                                    )
                                    Spacer(modifier = Modifier.width(20.dp))
                                    Column {
                                        Text(
                                            text = "Data Hasil Penimbangan",
                                            style = MaterialTheme.typography.titleMedium,
                                        )
                                        Text(
                                            text = "Data hasil penimbangan terbaru",
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "",
                                    )
                                }
                                HorizontalDivider(
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
                    if (showDeleteOfferDialog) {
                        AlertDialog(
                            containerColor = Color.White,
                            onDismissRequest = { showDeleteOfferDialog = false },
                            title = { Text(text = "Hapus Akun Kader") },
                            text = { Text("Menghapus akun bersifat permanen dan tidak dapat dibatalkan. Bila hanya ingin menghentikan aksesnya untuk sementara, nonaktifkan saja akunnya.") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showDeleteOfferDialog = false
                                        viewModel.deactivateAccount(id)
                                    }
                                ) {
                                    Text("Nonaktifkan Saja")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        showDeleteOfferDialog = false
                                        showDeleteConfirmDialog = true
                                    }
                                ) {
                                    Text("Hapus Permanen", color = Color.Red)
                                }
                            }
                        )
                    }
                    if (showDeleteConfirmDialog) {
                        AlertDialog(
                            containerColor = Color.White,
                            onDismissRequest = { showDeleteConfirmDialog = false },
                            title = { Text(text = "Konfirmasi Hapus Permanen") },
                            text = { Text("Akun akan dinonaktifkan sekaligus dihapus dari sistem. Tindakan ini tidak dapat dibatalkan.") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showDeleteConfirmDialog = false
                                        viewModel.deleteOfficer(id)
                                    }
                                ) {
                                    Text("Ya, Hapus", color = Color.Red)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                                    Text("Batal")
                                }
                            }
                        )
                    }
                    if (showLogoutDialog) {
                        if (officers.data.is_active) {
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

                else -> {}
            }
        }

        SnackbarHost(
            modifier = Modifier.align(Alignment.TopCenter),
            hostState = snackState
        ) { snackbarData: SnackbarData ->
            CustomSnackBar(
                drawableRes = if (snackbarData.visuals.message.startsWith("Berhasil")) R.drawable.baseline_check_circle_outline_24 else R.drawable.baseline_error_outline_24,
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