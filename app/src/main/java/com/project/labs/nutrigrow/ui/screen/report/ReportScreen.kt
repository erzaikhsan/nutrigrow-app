@file:Suppress("IMPLICIT_CAST_TO_ANY")

package com.project.labs.nutrigrow.ui.screen.report

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.labs.nutrigrow.R
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.ui.component.respond.ErrorMessage
import com.project.labs.nutrigrow.ui.component.respond.LoadingIndicator
import com.project.labs.nutrigrow.ui.component.snackbar.CustomSnackBar
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.state.UiState
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    redirectToHome: () -> Unit,
    viewModel: ReportViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

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

    val snackState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = checkAuth) {
        viewModel.checkAuthentication()
    }

    val downloadKey = if (pdfDownloadState is UiState.Success) {
        (pdfDownloadState as UiState.Success<File>).data.absolutePath
    } else pdfDownloadState

    LaunchedEffect(downloadKey) {
        when (val state = pdfDownloadState) {
            is UiState.Success -> {
                coroutineScope.launch {
                    snackState.showSnackbar("Berhasil Ekspor Data")
                }

                val file = state.data
                Toast.makeText(context, "PDF disimpan di: ${file.absolutePath}", Toast.LENGTH_LONG).show()

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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE0FFD2))
    ){
        viewModel.user.collectAsState(initial = UiState.Loading).value.let { user ->
            when (user) {
                is UiState.Loading -> {
                    LoadingIndicator(modifier = Modifier)
                    viewModel.getUserProfile()
                }

                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxSize()
                    ) {
                        item {
                            when (checkAuth) {
                                is UiState.Success -> {

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(),
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
                                                colors = TextFieldDefaults.textFieldColors(
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
                                                colors = TextFieldDefaults.textFieldColors(
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
                                    Spacer(modifier = Modifier.height(15.dp))
                                    Divider(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        color = Color(0xFF00BF63),
                                        thickness = 3.dp
                                    )
                                    Spacer(modifier = Modifier.height(15.dp))
                                    ElevatedCard(
                                        elevation = CardDefaults.cardElevation(
                                            defaultElevation = 6.dp
                                        ),

                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Column {
                                            if ((checkAuth as UiState.Success<AuthModel>).data.role == "Officer") {
                                                Row (
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(Color.White)
                                                        .padding(20.dp)
                                                        .clickable {
                                                            viewModel.getMonthlyReport(
                                                                context = context,
                                                                region = user.data.region,
                                                                month = selectedMonthIndex + 1,
                                                                year = selectedYear
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
                                                Divider(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(Color.White)
                                                        .padding(horizontal = 15.dp),
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                                    thickness = 1.dp
                                                )
                                            }
                                            Row (
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color.White)
                                                    .padding(20.dp)
                                                    .clickable {
                                                        when (checkAuth) {
                                                            is UiState.Success -> {
                                                                when((checkAuth as UiState.Success<AuthModel>).data.role) {
                                                                    "Admin" -> {
                                                                        viewModel.getChildrenReport(month = selectedMonthIndex + 1, year = selectedYear, context)
                                                                    }
                                                                    "Officer" -> {
                                                                        viewModel.getRegionChildrenReport(region = (checkAuth as UiState.Success<AuthModel>).data.region, month = selectedMonthIndex + 1, year = selectedYear, context)
                                                                    }
                                                                    else -> {}
                                                                }
                                                            }
                                                            else -> {}
                                                        }

                                                    }
                                            ){
                                                Icon(
                                                    imageVector = Icons.Default.Face,
                                                    contentDescription = "Toddler",
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
                                                        when (checkAuth) {
                                                            is UiState.Success -> {
                                                                when((checkAuth as UiState.Success<AuthModel>).data.role) {
                                                                    "Admin" -> {
                                                                        viewModel.getParentReport(context)
                                                                    }
                                                                    "Officer" -> {
                                                                        viewModel.getRegionParentReport(region = (checkAuth as UiState.Success<AuthModel>).data.region,context)
                                                                    }
                                                                    else -> {}
                                                                }
                                                            }
                                                            else -> {}
                                                        }
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
                                }
                                else -> {}
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    ErrorMessage(message = user.errorMessage, modifier = Modifier)
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
                containerColor = if (snackbarData.visuals.message.startsWith("Berhasil")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            )
        }
    }
}