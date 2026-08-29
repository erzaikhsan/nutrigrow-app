package com.project.labs.nutrigrow.ui.screen.vaccine.update

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
import com.project.labs.nutrigrow.data.model.VaccineModel
import com.project.labs.nutrigrow.ui.component.respond.ErrorMessage
import com.project.labs.nutrigrow.ui.component.respond.LoadingIndicator
import com.project.labs.nutrigrow.ui.component.snackbar.CustomSnackBar
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.state.UiState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateVaccineScreen(
    id: String,
    childId: String,
    redirectToHome: () -> Unit,
    viewModel: UpdateVaccineViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    modifier: Modifier = Modifier,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    var showDeleteDialog by remember { mutableStateOf(false) }

    val checkAuth by viewModel.isAuthenticated
    val newVaccine: UiState<VaccineModel> by viewModel.newVaccine
    val delete: UiState<VaccineModel> by viewModel.delete

    LaunchedEffect(key1 = childId) {
        viewModel.checkAuthentication()
        viewModel.getChildProfile(childId)
        viewModel.getVaccineById(id)
    }

    var place by remember { mutableStateOf("") }
    var vaccine_name by remember { mutableStateOf("") }
    var expandVaccine by remember { mutableStateOf(false) }
    var selectedVaccine by remember { mutableStateOf("") }
    val listVaccine = listOf("HB-0", "BCG", "Polio I", "Polio II", "Polio III", "Polio IV", "DPT-HB-Hib I", "DPT-HB-Hib II", "DPT-HB-Hib III", "DPT-HB-Hib Lanjutan", "Campak", "Campak Lanjutan", "MR", "IPV", "Lainnya")

    val snackState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var date by remember { mutableStateOf("") }

    val context = LocalContext.current

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

    var submit by remember { mutableStateOf("Perbarui") }
    var deleteButton by remember { mutableStateOf("Hapus") }

    fun reformatDate(inputDate: String): String {
        val outputFormatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS XXX", Locale.ENGLISH)

        val localDate = LocalDate.parse(inputDate, inputFormatter)
        val zonedDateTime = localDate.atTime(17, 0).atZone(ZoneOffset.UTC)
        return zonedDateTime.format(outputFormatter)
    }

    DisposableEffect(key1 = newVaccine, key2 = delete){
        when (newVaccine) {
            is UiState.Loading -> {
                submit = "Loading..."
                coroutineScope.launch {
                    kotlinx.coroutines.delay(500)
                    submit = "Perbarui"
                }
            }

            is UiState.Success -> {
                coroutineScope.launch {
                    snackState.showSnackbar("Berhasil Update Data Imunisasi")
                    redirectToHome()
                }

            }

            is UiState.Error -> {
                coroutineScope.launch {
                    snackState.showSnackbar((newVaccine as UiState.Error).errorMessage)
                    viewModel.resetNewVaccineState()
                }
                submit = "Perbarui"
            }
            else -> {}
        }

        when (delete) {
            is UiState.Loading -> {
                deleteButton = "Loading..."
                coroutineScope.launch {
                    kotlinx.coroutines.delay(500)
                    deleteButton = "Hapus"
                }
            }

            is UiState.Success -> {
                coroutineScope.launch {
                    snackState.showSnackbar("Berhasil Menghapus Data Imunisasi")
                    redirectToHome()
                }

            }

            is UiState.Error -> {
                coroutineScope.launch {
                    snackState.showSnackbar("Gagal Menghapus Data Imunisasi\n${(delete as UiState.Error).errorMessage}")
                    viewModel.resetDeleteState()
                }
                deleteButton = "Hapus"
            }
            else -> {}
        }

        onDispose { }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        viewModel.vaccine.collectAsState(initial = UiState.Loading).value.let { vaccineRespond ->
            when(vaccineRespond){
                is UiState.Loading -> {
                    LoadingIndicator()
                }

                is UiState.Success -> {
                    DisposableEffect(key1 = checkAuth){
                        if (date == "") date = reformatDate(vaccineRespond.data.date)
                        if (place == "") place = vaccineRespond.data.place
                        if (selectedVaccine == "" && vaccineRespond.data.vaccine_name in listVaccine){
                            selectedVaccine = vaccineRespond.data.vaccine_name
                        } else {
                            selectedVaccine = "Lainnya"
                            vaccine_name = vaccineRespond.data.vaccine_name
                        }

                        onDispose { }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(state = rememberScrollState())
                    ) {
                        viewModel.child.collectAsState(initial = UiState.Loading).value.let { childRespond ->
                            when (childRespond) {
                                is UiState.Loading -> {
                                    LoadingIndicator()
                                }

                                is UiState.Success -> {
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Image(
                                        painter = painterResource(id = if (childRespond.data.gender == "M") R.drawable.boy else R.drawable.girl,),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(100.dp)
                                            .align(Alignment.CenterHorizontally)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = childRespond.data.full_name,
                                        fontSize = 18.sp,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.W600,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(15.dp))
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 25.dp, end = 25.dp, top = 5.dp, bottom = 15.dp),
                                    ) {
                                        Text(
                                            text = "Tanggal Imunisasi",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.W500,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 5.dp)
                                        )
                                        OutlinedTextField(
                                            value = date,
                                            onValueChange = { date = it },
                                            keyboardOptions = KeyboardOptions.Default.copy(
                                                keyboardType = KeyboardType.Text,
                                                imeAction = ImeAction.Next
                                            ),
                                            enabled = false,
                                            trailingIcon = { Icon(painterResource(id = R.drawable.baseline_date_range_24), contentDescription = "Tanggal lahir", modifier = Modifier.size(22.dp)) },
                                            shape = RoundedCornerShape(10.dp),
                                            placeholder = { Text("Masukan Tanggal Imunisasi", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                            colors = TextFieldDefaults.colors(
                                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                focusedIndicatorColor = Color(0xFF9DA1A6),
                                                unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                                disabledIndicatorColor = Color(0xFF9DA1A6),
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { mDatePickerDialog.show() }
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Fasilitas Kesehatan",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.W500,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 5.dp)
                                        )
                                        OutlinedTextField(
                                            value = place,
                                            onValueChange = { place = it },
                                            keyboardOptions = KeyboardOptions.Default.copy(
                                                keyboardType = KeyboardType.Text,
                                                imeAction = ImeAction.Next
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            placeholder = { Text("Masukan Fasilitas Kesehatan", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                            colors = TextFieldDefaults.colors(
                                                focusedIndicatorColor = Color(0xFF9DA1A6),
                                                unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                                disabledIndicatorColor = Color(0xFF9DA1A6),
                                            ),
                                            minLines = 1,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Nama Vaksin",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.W500,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 5.dp)
                                        )
                                        ExposedDropdownMenuBox(
                                            expanded = expandVaccine,
                                            onExpandedChange = { expandVaccine = !expandVaccine },
                                        ) {
                                            OutlinedTextField(
                                                value = selectedVaccine,
                                                onValueChange = { selectedVaccine = it },
                                                readOnly = true,
                                                placeholder = { Text("Masukan Nama Vaksin", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                                trailingIcon = {
                                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandVaccine)
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
                                                expanded = expandVaccine,
                                                onDismissRequest = { expandVaccine = false },
                                                modifier = Modifier.background(MaterialTheme.colorScheme.background),
                                            ) {
                                                listVaccine.forEach { region ->
                                                    DropdownMenuItem(
                                                        text = { Text(region) },
                                                        onClick = {
                                                            selectedVaccine = region
                                                            expandVaccine = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                        if (selectedVaccine == "Lainnya"){
                                            Spacer(modifier = Modifier.height(5.dp))
                                            OutlinedTextField(
                                                value = vaccine_name,
                                                onValueChange = { vaccine_name = it },
                                                keyboardOptions = KeyboardOptions.Default.copy(
                                                    keyboardType = KeyboardType.Text,
                                                    imeAction = ImeAction.Done
                                                ),
                                                shape = RoundedCornerShape(10.dp),
                                                placeholder = { Text("Masukan Nama Vaksin", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                                colors = TextFieldDefaults.colors(
                                                    focusedIndicatorColor = Color(0xFF9DA1A6),
                                                    unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                                    disabledIndicatorColor = Color(0xFF9DA1A6),
                                                ),
                                                minLines = 1,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(35.dp))
                                        Button(
                                            onClick = {
                                                if (submit != "Loading...") viewModel.updateVaccine(id, childId, date, if (selectedVaccine == "Lainnya") vaccine_name else selectedVaccine, place)
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(45.dp)
                                        ) {
                                            Text(
                                                submit,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(15.dp))
                                        Button(
                                            onClick = {
                                                showDeleteDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color.Red,
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(45.dp)
                                        ) {
                                            Text(
                                                deleteButton,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                            )
                                        }
                                    }
                                }
                                is UiState.Error -> {
                                    ErrorMessage(message = childRespond.errorMessage)
                                }
                                else -> {}
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    ErrorMessage(message = vaccineRespond.errorMessage)
                }
                else -> { }
            }
        }

        SnackbarHost(
            modifier = Modifier.align(Alignment.TopCenter),
            hostState = snackState
        ) { snackbarData: SnackbarData ->
            CustomSnackBar(
                drawableRes = if (snackbarData.visuals.message.startsWith("Berhasil Update")) R.drawable.baseline_check_circle_outline_24 else R.drawable.baseline_error_outline_24,
                message = snackbarData.visuals.message,
                containerColor = if (snackbarData.visuals.message.startsWith("Berhasil Update")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                containerColor = Color.White,
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(text = "Konfirmasi Hapus") },
                text = { Text("Apakah Anda yakin ingin menghapus data?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            if (deleteButton != "Loading...") viewModel.deleteVaccine(id)
                        }
                    ) {
                        Text("Ya")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog = false }
                    ) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}