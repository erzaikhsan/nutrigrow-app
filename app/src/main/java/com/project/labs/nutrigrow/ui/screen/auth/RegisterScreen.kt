package com.project.labs.nutrigrow.ui.screen.auth

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.labs.nutrigrow.R
import com.project.labs.nutrigrow.data.model.UserModel
import com.project.labs.nutrigrow.ui.component.snackbar.CustomSnackBar
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.state.UiState
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavHostController = rememberNavController(),
    redirectToLogin: () -> Unit = {},
    viewModel: AuthViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
) {

    var full_name by remember { mutableStateOf("") }
    var expandGender by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf("") }
    val listGender = listOf("Laki-Laki", "Perempuan")

    var expandRegion by remember { mutableStateOf(false) }
    var selectedRegion by remember { mutableStateOf("") }
    val listRegion = listOf("RW1","RW2","RW3","RW4","RW5")

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

    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    val user: UiState<UserModel> by viewModel.user
    var register by remember { mutableStateOf("Registrasi") }

    DisposableEffect(key1 = user){
        when (user) {
            is UiState.Loading -> {
                register = "Loading..."
            }
            is UiState.Error -> {
                register = "Registrasi"
                coroutineScope.launch {
                    snackState.showSnackbar("Registrasi Gagal!\n${(user as UiState.Error).errorMessage}")
                }
            }
            is UiState.Success -> {
                coroutineScope.launch {
                    snackState.showSnackbar("Registrasi Berhasil\nSilahkan Masuk dengan Email dan Password yang telah didaftarkan")
                    redirectToLogin()
                }
            }
            else -> {}
        }
        onDispose {  }
    }

    Scaffold( ) { paddingValue ->
        Box(
            modifier = Modifier
                .padding(paddingValue)
                .fillMaxSize()
                .background(Color(0xFFE0FFD2))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Image(
                    painter = painterResource(id = if (selectedGender == "Perempuan") R.drawable.woman else R.drawable.man,),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(25.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 25.dp, end = 25.dp, top = 5.dp, bottom = 15.dp),
                ) {
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
                            text = "Data lengkap Anda",
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 10.dp,
                                    end = 10.dp,
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

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Nama Lengkap",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W500,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp)
                    )
                    Box() {
                        OutlinedTextField(
                            value = full_name,
                            onValueChange = { full_name = it },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            trailingIcon = { Icon(painterResource(id = R.drawable.baseline_person_24), contentDescription = "Nama", modifier = Modifier.size(22.dp)) },
                            shape = RoundedCornerShape(10.dp),
                            placeholder = { Text("Masukan Nama Lengkap", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = Color(0xFF9DA1A6),
                                unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                disabledIndicatorColor = Color(0xFF9DA1A6),
                            ),
                            maxLines = 1,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Jenis Kelamin",
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
                        text = "Tanggal lahir",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W500,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp)
                    )
                    Box() {
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
                        text = "Nomor Telepon",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W500,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp)
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Next
                            ),
                            trailingIcon = { Icon(painterResource(id = R.drawable.baseline_phone_24), contentDescription = "Phone Number", modifier = Modifier.size(22.dp)) },
                            shape = RoundedCornerShape(10.dp),
                            placeholder = { Text("Masukan Nomor Telepon", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
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
                        text = "Alamat",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W500,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp)
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            trailingIcon = { Icon(painterResource(id = R.drawable.baseline_location_pin_24), contentDescription = "Address", modifier = Modifier.size(22.dp)) },
                            shape = RoundedCornerShape(10.dp),
                            placeholder = { Text("Masukan Alamat", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = Color(0xFF9DA1A6),
                                unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                disabledIndicatorColor = Color(0xFF9DA1A6),
                            ),
                            maxLines = 5,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Posyandu",
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

                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            if (register != "Loading...") viewModel.register(
                                full_name = full_name,
                                gender =  selectedGender,
                                date_of_birth = date_of_birth,
                                phone_number = phone,
                                address = address,
                                region = selectedRegion
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
                            register,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
            SnackbarHost(
                modifier = Modifier.align(Alignment.TopCenter),
                hostState = snackState
            ) { snackbarData: SnackbarData ->
                CustomSnackBar(
                    drawableRes = if (snackbarData.visuals.message.startsWith("Registrasi Gagal")) R.drawable.baseline_error_outline_24 else R.drawable.baseline_check_circle_outline_24,
                    message = snackbarData.visuals.message,
                    containerColor = if (snackbarData.visuals.message.startsWith("Registrasi Gagal")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}