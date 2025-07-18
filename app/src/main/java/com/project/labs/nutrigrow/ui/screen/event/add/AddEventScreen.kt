package com.project.labs.nutrigrow.ui.screen.event.add

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.labs.nutrigrow.R
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.EventModel
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
fun AddEventScreen(
    redirectToHome: () -> Unit,
    viewModel: AddEventViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    modifier: Modifier = Modifier,
) {

    val checkAuth by viewModel.isAuthenticated

    LaunchedEffect(key1 = checkAuth) {
        viewModel.checkAuthentication()
    }

    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("00:00") }
    var endTime by remember { mutableStateOf("00:00") }
    var place by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var expandRegion by remember { mutableStateOf(false) }
    var selectedRegion by remember { mutableStateOf("") }
    val listRegion = listOf("Desa","RW1","RW2","RW3","RW4","RW5")

    val snackState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
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

    fun showTimePicker(isStartTime: Boolean) {
        val hour = mCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = mCalendar.get(Calendar.MINUTE)

        TimePickerDialog(
            context,
            { _, selectedHour: Int, selectedMinute: Int ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                if (isStartTime) startTime = formattedTime else endTime = formattedTime
            },
            hour,
            minute,
            true
        ).show()
    }

    val event: UiState<EventModel> by viewModel.event

    var submit by remember { mutableStateOf("Tambahkan") }

    DisposableEffect(key1 = event){
        when (event) {
            is UiState.Loading -> {
                submit = "Loading..."
                coroutineScope.launch {
                    kotlinx.coroutines.delay(500)
                    submit = "Tambahkan"
                }
            }
            is UiState.Error -> {
                submit = "Tambahkan"
                coroutineScope.launch {
                    snackState.showSnackbar((event as UiState.Error).errorMessage)
                    viewModel.resetEventState()
                }
            }
            is UiState.Success -> {
                coroutineScope.launch {
                    snackState.showSnackbar("Berhasil Menambahkan Kegiatan")
                    redirectToHome()
                }
            }
            else -> {}
        }
        onDispose {  }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(15.dp))
            viewModel.user.collectAsState(initial = UiState.Loading).value.let { user ->
                when (user) {
                    is UiState.Loading -> {
                        LoadingIndicator()
                        when (checkAuth) {
                            is UiState.Success -> {
                                viewModel.getUserProfile((checkAuth as UiState.Success<AuthModel>).data.role)
                            }
                            else -> {}
                        }
                    }
                    is UiState.Success -> {
                        Image(
                            painter = painterResource(R.drawable.posyandu,),
                            contentDescription = null,
                            modifier = Modifier
                                .size(200.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 25.dp, end = 25.dp, top = 5.dp, bottom = 15.dp),
                        ) {
                            Text(
                                text = "Judul Kegiatan",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W500,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 5.dp)
                            )
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                shape = RoundedCornerShape(10.dp),
                                placeholder = { Text("Masukan Judul Kegiatan", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
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
                                text = "Tanggal Kegiatan",
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
                                trailingIcon = { Icon(painterResource(id = R.drawable.baseline_date_range_24), contentDescription = "Date", modifier = Modifier.size(22.dp)) },
                                shape = RoundedCornerShape(10.dp),
                                placeholder = { Text("Masukan Tanggal Kegiatan", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                colors = TextFieldDefaults.textFieldColors(
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
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "Waktu :",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.W500,
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    OutlinedTextField(
                                        value = startTime,
                                        onValueChange = { startTime = it },
                                        enabled = false,
                                        shape = RoundedCornerShape(10.dp),
                                        placeholder = { Text("Waktu Mulai") },
                                        colors = TextFieldDefaults.textFieldColors(
                                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            focusedIndicatorColor = Color(0xFF9DA1A6),
                                            unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                            disabledIndicatorColor = Color(0xFF9DA1A6),
                                        ),
                                        modifier = Modifier
                                            .widthIn(max = 75.dp)
                                            .clickable { showTimePicker(true) }
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = " - ",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.W500,
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    OutlinedTextField(
                                        value = endTime,
                                        onValueChange = { endTime = it },
                                        enabled = false,
                                        shape = RoundedCornerShape(10.dp),
                                        placeholder = { Text("Waktu Selesai") },
                                        colors = TextFieldDefaults.textFieldColors(
                                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            focusedIndicatorColor = Color(0xFF9DA1A6),
                                            unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                            disabledIndicatorColor = Color(0xFF9DA1A6),
                                        ),
                                        modifier = Modifier
                                            .widthIn(max = 75.dp)
                                            .clickable { showTimePicker(false) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Wilayah Posyandu",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W500,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 5.dp)
                            )
                            ExposedDropdownMenuBox(
                                expanded = expandRegion,
                                onExpandedChange = { expandRegion = !expandRegion },
                            ) {
                                OutlinedTextField(
                                    value = selectedRegion,
                                    onValueChange = { selectedRegion = it },
                                    readOnly = true,
                                    placeholder = { Text("Masukan Wilayah Posyandu", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandRegion)
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
                                    expanded = expandRegion,
                                    onDismissRequest = { expandRegion = false },
                                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                                ) {
                                    listRegion.forEach { region ->
                                        DropdownMenuItem(
                                            text = { Text(region) },
                                            onClick = {
                                                selectedRegion = region
                                                expandRegion = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Tempat Kegiatan",
                                fontSize = 16.sp,
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
                                trailingIcon = { Icon(painterResource(id = R.drawable.baseline_location_pin_24), contentDescription = "Place", modifier = Modifier.size(22.dp)) },
                                placeholder = { Text("Masukan Tempat Kegiatan", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
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
                                text = "Deskripsi Kegiatan",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W500,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 5.dp)
                            )
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Done
                                ),
                                shape = RoundedCornerShape(10.dp),
                                placeholder = { Text("Masukan Deskripsi Kegiatan", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                colors = TextFieldDefaults.textFieldColors(
                                    focusedIndicatorColor = Color(0xFF9DA1A6),
                                    unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                    disabledIndicatorColor = Color(0xFF9DA1A6),
                                ),
                                minLines = 3,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = {
                                    viewModel.addEvent(
                                        title = title,
                                        date = date,
                                        start_time = startTime,
                                        end_time = endTime,
                                        place = place,
                                        description = description,
                                        region = selectedRegion,
                                    )
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
                        }
                    }
                    is UiState.Error -> {
                        ErrorMessage(message = user.errorMessage)
                    }
                    else -> {}
                }
            }
        }
        SnackbarHost(
            modifier = Modifier.align(Alignment.TopCenter),
            hostState = snackState
        ) { snackbarData: SnackbarData ->
            CustomSnackBar(
                drawableRes = if (snackbarData.visuals.message.startsWith("Berhasil Menambahkan")) R.drawable.baseline_check_circle_outline_24 else R.drawable.baseline_error_outline_24,
                message = snackbarData.visuals.message,
                containerColor = if (snackbarData.visuals.message.startsWith("Berhasil Menambahkan")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            )
        }
    }
}