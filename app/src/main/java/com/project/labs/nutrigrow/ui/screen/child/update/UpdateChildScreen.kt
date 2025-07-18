package com.project.labs.nutrigrow.ui.screen.child.update

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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.labs.nutrigrow.R
import com.project.labs.nutrigrow.data.model.ChildrenModel
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
fun UpdateChildScreen(
    id: String,
    redirectToHome: () -> Unit,
    viewModel: UpdateChildViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    modifier: Modifier = Modifier,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val checkAuth by viewModel.isAuthenticated
    val newChild: UiState<ChildrenModel> by viewModel.newChild

    LaunchedEffect(key1 = checkAuth) {
        viewModel.checkAuthentication()
        viewModel.getChildProfile(id)
    }

    var full_name by remember { mutableStateOf("") }
    var father by remember { mutableStateOf("") }
    var mother by remember { mutableStateOf("") }
    var order_of_child by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var place_of_birth by remember { mutableStateOf("") }
    var birth_weight by remember { mutableStateOf("") }
    var birth_height by remember { mutableStateOf("") }
    var birth_head_circum by remember { mutableStateOf("") }

    var expandGender by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf("") }
    val listGender = listOf("Laki-Laki", "Perempuan")


    val snackState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var date_of_birth by remember { mutableStateOf("") }

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
            date_of_birth = "$mDayOfMonth/${mMonth+1}/$mYear"
        }, mYear, mMonth, mDay
    )

    var submit by remember { mutableStateOf("Perbarui") }

    fun reformatDate(inputDate: String): String {
        val outputFormatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS XXX", Locale.ENGLISH)

        val localDate = LocalDate.parse(inputDate, inputFormatter)
        val zonedDateTime = localDate.atTime(17, 0).atZone(ZoneOffset.UTC)
        return zonedDateTime.format(outputFormatter)
    }

    DisposableEffect(key1 = newChild){
        when (newChild) {
            is UiState.Loading -> {
                submit = "Loading..."
                coroutineScope.launch {
                    kotlinx.coroutines.delay(500)
                    submit = "Perbarui"
                }
            }

            is UiState.Success -> {
                coroutineScope.launch {
                    snackState.showSnackbar("Berhasil Update Data Balita\n${(newChild as UiState.Success).data.full_name}")
                    redirectToHome()
                }
            }

            is UiState.Error -> {
                coroutineScope.launch {
                    snackState.showSnackbar((newChild as UiState.Error).errorMessage)
                    viewModel.resetNewChild()
                }
                submit = "Perbarui"
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
        viewModel.child.collectAsState(initial = UiState.Loading).value.let { respond ->
            when (respond) {
                is UiState.Loading -> {
                    LoadingIndicator()
                }

                is UiState.Success -> {
                    DisposableEffect(key1 = checkAuth){
                        if (full_name == "") full_name = respond.data.full_name
                        if (region == "") region = respond.data.region
                        if (selectedGender == "") selectedGender = if (respond.data.gender == "M") "Laki-Laki" else "Perempuan"
                        if (place_of_birth == "") place_of_birth = respond.data.place_of_birth
                        if (date_of_birth == "") date_of_birth = reformatDate(respond.data.date_of_birth)
                        if (father == "") father = respond.data.father
                        if (mother == "") mother = respond.data.mother
                        if (order_of_child == "") order_of_child = respond.data.order_of_child.toString()
                        if (birth_weight == "") birth_weight = respond.data.birth_weight.toString()
                        if (birth_height == "") birth_height = respond.data.birth_height.toString()
                        if (birth_head_circum == "") birth_head_circum = respond.data.birth_head_circum.toString()

                        onDispose { }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(state = rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Image(
                            painter = painterResource(id = if (selectedGender == "Laki-Laki") R.drawable.boy else R.drawable.girl,),
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(35.dp))
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 25.dp, end = 25.dp, top = 5.dp, bottom = 15.dp),
                        ) {
                            Text(
                                text = "Nama Anak",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W500,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 5.dp)
                            )
                            Box {
                                OutlinedTextField(
                                    value = full_name,
                                    onValueChange = { full_name = it },
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Next
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    placeholder = { Text("Masukan Nama Anak", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    colors = TextFieldDefaults.textFieldColors(
                                        focusedIndicatorColor = Color(0xFF9DA1A6),
                                        unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                        disabledIndicatorColor = Color(0xFF9DA1A6),
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Nama Ayah",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W500,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 5.dp)
                            )
                            Box {
                                OutlinedTextField(
                                    value = father,
                                    onValueChange = { father = it },
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Next
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    placeholder = { Text("Masukan Nama Ayah", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    colors = TextFieldDefaults.textFieldColors(
                                        focusedIndicatorColor = Color(0xFF9DA1A6),
                                        unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                        disabledIndicatorColor = Color(0xFF9DA1A6),
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Nama Ibu",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W500,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 5.dp)
                            )
                            Box {
                                OutlinedTextField(
                                    value = mother,
                                    onValueChange = { mother = it },
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Next
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    placeholder = { Text("Masukan Nama Ibu", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    colors = TextFieldDefaults.textFieldColors(
                                        focusedIndicatorColor = Color(0xFF9DA1A6),
                                        unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                        disabledIndicatorColor = Color(0xFF9DA1A6),
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Anak Ke-",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W500,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 5.dp)
                            )
                            Box {
                                OutlinedTextField(
                                    value = order_of_child,
                                    onValueChange = { order_of_child = it },
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Next
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    placeholder = { Text("Masukan Anak Ke", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    colors = TextFieldDefaults.textFieldColors(
                                        focusedIndicatorColor = Color(0xFF9DA1A6),
                                        unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                        disabledIndicatorColor = Color(0xFF9DA1A6),
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Jenis Kelamin Anak",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W500,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 5.dp)
                            )
                            ExposedDropdownMenuBox(
                                expanded = expandGender,
                                onExpandedChange = { expandGender = !expandGender },
                            ) {
                                OutlinedTextField(
                                    value = selectedGender,
                                    onValueChange = { selectedGender = it },
                                    readOnly = true,
                                    placeholder = { Text("Masukan Jenis Kelamin", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandGender)
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
                                    expanded = expandGender,
                                    onDismissRequest = { expandGender = false },
                                    modifier = Modifier.background(MaterialTheme.colorScheme.background),
                                ) {
                                    listGender.forEach { gender ->
                                        DropdownMenuItem(
                                            text = { Text(gender) },
                                            onClick = {
                                                selectedGender = gender
                                                expandGender = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Tempat Lahir Anak",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W500,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 5.dp)
                            )
                            Box {
                                OutlinedTextField(
                                    value = place_of_birth,
                                    onValueChange = { place_of_birth = it },
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Next
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    placeholder = { Text("Masukan Tempat Lahir", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    colors = TextFieldDefaults.textFieldColors(
                                        focusedIndicatorColor = Color(0xFF9DA1A6),
                                        unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                        disabledIndicatorColor = Color(0xFF9DA1A6),
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Tanggal Lahir Anak",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W500,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 5.dp)
                            )
                            Box {
                                OutlinedTextField(
                                    value = date_of_birth,
                                    onValueChange = { date_of_birth = it },
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Next
                                    ),
                                    enabled = false,
                                    trailingIcon = { Icon(painterResource(id = R.drawable.baseline_date_range_24), contentDescription = "Date of Birth", modifier = Modifier.size(22.dp)) },
                                    shape = RoundedCornerShape(10.dp),
                                    placeholder = { Text("Masukan Tanggal Lahir", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
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
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Berat Badan Lahir (Kg)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W500,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 5.dp)
                            )
                            Box {
                                OutlinedTextField(
                                    value = birth_weight,
                                    onValueChange = { birth_weight = it },
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Next
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    placeholder = { Text("Masukan Berat Badan Saat Lahir", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    colors = TextFieldDefaults.textFieldColors(
                                        focusedIndicatorColor = Color(0xFF9DA1A6),
                                        unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                        disabledIndicatorColor = Color(0xFF9DA1A6),
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Tinggi Badan Lahir (Cm)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W500,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 5.dp)
                            )
                            Box {
                                OutlinedTextField(
                                    value = birth_height,
                                    onValueChange = { birth_height = it },
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Next
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    placeholder = { Text("Masukan Tinggi Badan Saat Lahir", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    colors = TextFieldDefaults.textFieldColors(
                                        focusedIndicatorColor = Color(0xFF9DA1A6),
                                        unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                        disabledIndicatorColor = Color(0xFF9DA1A6),
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Lingkar Kepala Lahir (Cm)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.W500,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 5.dp)
                            )
                            Box {
                                OutlinedTextField(
                                    value = birth_head_circum,
                                    onValueChange = { birth_head_circum = it },
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    placeholder = { Text("Masukan Lingkar Kepala Saat Lahir", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    colors = TextFieldDefaults.textFieldColors(
                                        focusedIndicatorColor = Color(0xFF9DA1A6),
                                        unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                        disabledIndicatorColor = Color(0xFF9DA1A6),
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = {
                                    if (submit != "Loading...") viewModel.updateChildren(id = id, full_name = full_name, gender = selectedGender, place_of_birth = place_of_birth, date_of_birth = date_of_birth, father = father, mother = mother, order_of_child = order_of_child, region = region, birth_weight = birth_weight, birth_height = birth_height, birth_head_circum = birth_head_circum)
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
                }
                is UiState.Error -> {
                    ErrorMessage(message = respond.errorMessage)
                }
                else -> {}
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
    }
}