package com.project.labs.nutrigrow.ui.screen.growth.add

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
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGrowthScreen(
    id: String,
    redirectToHome: () -> Unit,
    viewModel: AddGrowthViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    modifier: Modifier = Modifier,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val checkAuth by viewModel.isAuthenticated
    val growth: UiState<GrowthModel> by viewModel.growth

    LaunchedEffect(key1 = checkAuth) {
        viewModel.checkAuthentication()
        viewModel.getChildProfile(id)
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

    var submit by remember { mutableStateOf("Tambahkan") }

    DisposableEffect(key1 = growth){
        when (growth) {
            is UiState.Loading -> {
                submit = "Loading..."
                coroutineScope.launch {
                    kotlinx.coroutines.delay(500)
                    submit = "Tambahkan"
                }
            }

            is UiState.Success -> {
                coroutineScope.launch {
                    snackState.showSnackbar("Berhasil Menambahkan Data Pertumbuhan")
                    redirectToHome()
                }
            }

            is UiState.Error -> {
                coroutineScope.launch {
                    snackState.showSnackbar((growth as UiState.Error).errorMessage)
                    viewModel.resetGrowthState()
                }
                submit = "Tambahkan"
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
        ) {
            viewModel.child.collectAsState(initial = UiState.Loading).value.let { respond ->
                when (respond) {
                    is UiState.Loading -> {
                        LoadingIndicator()
                    }

                    is UiState.Success -> {
                        Spacer(modifier = Modifier.height(20.dp))
                        Image(
                            painter = painterResource(id = if (respond.data.gender == "M") R.drawable.boy else R.drawable.girl,),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = respond.data.full_name,
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
                                    .clickable { mDatePickerDialog.show() }
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

                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = {
                                    if (submit != "Loading...") viewModel.addGrowth(id, date, weight, height, head_circum, arm_circum, note)
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
                        ErrorMessage(message = respond.errorMessage)
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