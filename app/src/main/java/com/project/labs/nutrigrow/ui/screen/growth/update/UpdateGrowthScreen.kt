package com.project.labs.nutrigrow.ui.screen.growth.update

import android.app.DatePickerDialog
import android.widget.DatePicker
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.project.labs.nutrigrow.data.model.GrowthModel
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
fun UpdateGrowthScreen(
    id: String,
    childId: String,
    redirectToHome: () -> Unit,
    viewModel: UpdateGrowthViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    modifier: Modifier = Modifier,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    var showDeleteDialog by remember { mutableStateOf(false) }

    val checkAuth by viewModel.isAuthenticated
    val newGrowth: UiState<GrowthModel> by viewModel.newGrowth
    val delete: UiState<GrowthModel> by viewModel.delete

    LaunchedEffect(key1 = checkAuth) {
        viewModel.checkAuthentication()
        viewModel.getChildProfile(childId)
        viewModel.getGrowthById(id)
    }

    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var head_circum by remember { mutableStateOf("") }
    var arm_circum by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

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

    DisposableEffect(key1 = newGrowth, key2 = delete){
        when (newGrowth) {
            is UiState.Loading -> {
                submit = "Loading..."
                coroutineScope.launch {
                    kotlinx.coroutines.delay(500)
                    submit = "Perbarui"
                }
            }

            is UiState.Success -> {
                coroutineScope.launch {
                    snackState.showSnackbar("Berhasil Update Data Pertumbuhan")
                    redirectToHome()
                }

            }

            is UiState.Error -> {
                coroutineScope.launch {
                    snackState.showSnackbar((newGrowth as UiState.Error).errorMessage)
                    viewModel.resetNewGrowthState()
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
                    snackState.showSnackbar("Berhasil Menghapus Data Pertumbuhan")
                    redirectToHome()
                }

            }

            is UiState.Error -> {
                coroutineScope.launch {
                    snackState.showSnackbar("Gagal Menghapus Data Pertumbuhan\n${(delete as UiState.Error).errorMessage}")
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
        viewModel.growth.collectAsState(initial = UiState.Loading).value.let { growthRespond ->
            when(growthRespond){
                is UiState.Loading -> {
                    LoadingIndicator()
                }

                is UiState.Success -> {
                    DisposableEffect(key1 = checkAuth){
                        if (date == "") date = reformatDate(growthRespond.data.date)
                        if (weight == "") weight = growthRespond.data.weight.toString()
                        if (height == "") height = growthRespond.data.height.toString()
                        if (head_circum == "") head_circum = growthRespond.data.head_circum.toString()
                        if (arm_circum == "") arm_circum = growthRespond.data.arm_circum.toString()
                        if (note == "") note = growthRespond.data.note

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
                                            text = "Tanggal Penimbangan",
                                            fontSize = 16.sp,
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
                                            trailingIcon = { Icon(painterResource(id = R.drawable.baseline_date_range_24), contentDescription = "Date of Birth", modifier = Modifier.size(22.dp)) },
                                            shape = RoundedCornerShape(10.dp),
                                            placeholder = { Text("Masukan Tanggal Penimbangan", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                            colors = TextFieldDefaults.textFieldColors(
                                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                focusedIndicatorColor = Color(0xFF9DA1A6),
                                                unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                                disabledIndicatorColor = Color(0xFF9DA1A6),
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { if (growthRespond.data.age != 0) { mDatePickerDialog.show() } }
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Berat Badan (Kg)",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.W500,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 5.dp)
                                        )
                                        OutlinedTextField(
                                            value = weight,
                                            onValueChange = { weight = it },
                                            keyboardOptions = KeyboardOptions.Default.copy(
                                                keyboardType = KeyboardType.Number,
                                                imeAction = ImeAction.Next
                                            ),
                                            enabled = growthRespond.data.age != 0,
                                            shape = RoundedCornerShape(10.dp),
                                            placeholder = { Text("Masukan Berat Badan", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                            colors = TextFieldDefaults.textFieldColors(
                                                focusedIndicatorColor = Color(0xFF9DA1A6),
                                                unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                                disabledIndicatorColor = Color(0xFF9DA1A6),
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Tinggi Badan (Cm)",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.W500,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 5.dp)
                                        )
                                        OutlinedTextField(
                                            value = height,
                                            onValueChange = { height = it },
                                            keyboardOptions = KeyboardOptions.Default.copy(
                                                keyboardType = KeyboardType.Number,
                                                imeAction = ImeAction.Next
                                            ),
                                            enabled = growthRespond.data.age != 0,
                                            shape = RoundedCornerShape(10.dp),
                                            placeholder = { Text("Masukan Tinggi Badan", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                            colors = TextFieldDefaults.textFieldColors(
                                                focusedIndicatorColor = Color(0xFF9DA1A6),
                                                unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                                disabledIndicatorColor = Color(0xFF9DA1A6),
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Lingkar Kepala (Cm)",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.W500,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 5.dp)
                                        )
                                        OutlinedTextField(
                                            value = head_circum,
                                            onValueChange = { head_circum = it },
                                            keyboardOptions = KeyboardOptions.Default.copy(
                                                keyboardType = KeyboardType.Number,
                                                imeAction = ImeAction.Next
                                            ),
                                            enabled = growthRespond.data.age != 0,
                                            shape = RoundedCornerShape(10.dp),
                                            placeholder = { Text("Masukan Lingkar Kepala", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                            colors = TextFieldDefaults.textFieldColors(
                                                focusedIndicatorColor = Color(0xFF9DA1A6),
                                                unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                                disabledIndicatorColor = Color(0xFF9DA1A6),
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Lingkar Lengan (Cm)",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.W500,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 5.dp)
                                        )
                                        OutlinedTextField(
                                            value = arm_circum,
                                            onValueChange = { arm_circum = it },
                                            keyboardOptions = KeyboardOptions.Default.copy(
                                                keyboardType = KeyboardType.Number,
                                                imeAction = ImeAction.Next
                                            ),
                                            enabled = growthRespond.data.age != 0,
                                            shape = RoundedCornerShape(10.dp),
                                            placeholder = { Text("Masukan Lingkar Lengan", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                            colors = TextFieldDefaults.textFieldColors(
                                                focusedIndicatorColor = Color(0xFF9DA1A6),
                                                unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                                disabledIndicatorColor = Color(0xFF9DA1A6),
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "Catatan Penimbangan",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.W500,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 5.dp)
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            OutlinedTextField(
                                                value = note,
                                                onValueChange = { note = it },
                                                keyboardOptions = KeyboardOptions.Default.copy(
                                                    keyboardType = KeyboardType.Text,
                                                    imeAction = ImeAction.Done
                                                ),
                                                enabled = growthRespond.data.age != 0,
                                                shape = RoundedCornerShape(10.dp),
                                                placeholder = { Text("Masukan Catatan Penimbangan", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                                colors = TextFieldDefaults.textFieldColors(
                                                    focusedIndicatorColor = Color(0xFF9DA1A6),
                                                    unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                                    disabledIndicatorColor = Color(0xFF9DA1A6),
                                                ),
                                                minLines = 3,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                            )
                                        }

                                        if (growthRespond.data.age != 0) {
                                            Spacer(modifier = Modifier.height(20.dp))
                                            Button(
                                                onClick = {
                                                    if (submit != "Loading...") viewModel.updateGrowth(id, childId, date, weight, height, head_circum, arm_circum, note)
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
                                            Spacer(modifier = Modifier.height(5.dp))
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
                                        Spacer(modifier = Modifier.height(20.dp))
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
                    ErrorMessage(message = growthRespond.errorMessage)
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
                            if (deleteButton != "Loading...") viewModel.deleteGrowth(id)
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