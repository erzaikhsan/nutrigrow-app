package com.project.labs.nutrigrow.ui.screen.report

import android.app.DatePickerDialog
import android.content.Intent
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.util.Calendar
import java.util.Date

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
                                    if ((checkAuth as UiState.Success<AuthModel>).data.role == "Officer") {
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
                                                .fillMaxWidth(),
                                            color = Color(0xFF00BF63),
                                            thickness = 3.dp
                                        )
                                        Spacer(modifier = Modifier.height(15.dp))
                                    } else {
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
                                        ){
                                            Text(
                                                text = "Data Posyandu Jipang",
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
                                                .fillMaxWidth(),
                                            color = Color(0xFF00BF63),
                                            thickness = 3.dp
                                        )
                                        Spacer(modifier = Modifier.height(15.dp))
                                    }
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
                                            }
                                            Row (
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color.White)
                                                    .padding(20.dp)
                                                    .clickable {
                                                        viewModel.getChildrenReport(context)
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
                                                        viewModel.getParentReport(context)
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
            }
        }

        SnackbarHost(
            modifier = Modifier.align(Alignment.TopCenter),
            hostState = snackState
        ) { snackbarData: SnackbarData ->
            CustomSnackBar(
                drawableRes = if (snackbarData.visuals.message.startsWith("Gagal")) R.drawable.baseline_error_outline_24 else R.drawable.baseline_check_circle_outline_24,
                message = snackbarData.visuals.message,
                containerColor = if (snackbarData.visuals.message.startsWith("Gagal")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            )
        }
    }
}