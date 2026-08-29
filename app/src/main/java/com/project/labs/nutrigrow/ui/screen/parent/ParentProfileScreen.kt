package com.project.labs.nutrigrow.ui.screen.parent

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.shadow
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.project.labs.nutrigrow.R
import com.project.labs.nutrigrow.activity.child.ChildActivity
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.UserModel
import com.project.labs.nutrigrow.ui.component.card.ChildCard
import com.project.labs.nutrigrow.ui.component.respond.ErrorMessage
import com.project.labs.nutrigrow.ui.component.respond.LoadingIndicator
import com.project.labs.nutrigrow.ui.component.respond.OriginalLoading
import com.project.labs.nutrigrow.ui.component.snackbar.CustomSnackBar
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.state.UiState
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ParentProfileScreen(
    id: String,
    redirectToHome: () -> Unit,
    viewModel: ParentProfileViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val snackState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showLogoutDialog by remember { mutableStateOf(false) }

    var showDeleteOfferDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val deletedState by viewModel.deleted

    LaunchedEffect(deletedState) {
        if (deletedState is UiState.Success) {
            (context as? Activity)?.finish()
        }
    }


    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.getParentAccount(id)
        }
    }

    val checkAuth by viewModel.isAuthenticated
    val isActive: UiState<UserModel> by viewModel.isActive

    LaunchedEffect(key1 = Unit) {
        viewModel.checkAuthentication()
    }

    fun formatDate(inputDate: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS XXX", Locale.ENGLISH)
        val outputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id", "ID"))
        val zonedDateTime = ZonedDateTime.parse(inputDate, inputFormatter)
        return zonedDateTime.format(outputFormatter)
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
                viewModel.getParentAccount(id)
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE0FFD2))
    ) {
        viewModel.parent.collectAsState(initial = UiState.Loading).value.let { parents ->
            when (parents) {
                is UiState.Loading -> {
                    LoadingIndicator()
                    LaunchedEffect(Unit) {
                        viewModel.getParentAccount(id)
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
                                        text = parents.data.full_name,
                                        fontSize = 18.sp,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = if (parents.data.gender == "M") "Ayah Balita" else "Ibu Balita",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                                AsyncImage(
                                    model = if (parents.data.gender == "M") R.drawable.man else R.drawable.woman,
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
                                        text = "Profile Orang Tua",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    when (checkAuth) {
                                        is UiState.Success -> {
                                            if ((checkAuth as UiState.Success<AuthModel>).data.role == "Admin"){
                                                if (parents.data.is_active) {
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
                                            text = if (parents.data.is_active) "Aktif" else "Tidak Aktif",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.W500,
                                            color = if (parents.data.is_active) Color.Green else Color.Red
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
                                            text = parents.data.full_name,
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
                                            text = if (parents.data.gender == "M") "Laki-laki" else "Perempuan",
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
                                            text = formatDate(parents.data.date_of_birth),
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
                                            text = parents.data.phone_number,
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
                                            text = parents.data.region,
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
                                            text = parents.data.address,
                                            maxLines = 3,
                                            textAlign = TextAlign.End,
                                            fontSize = 15.sp,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
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
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            start = 10.dp,
                                            end = 10.dp,
                                        )
                                ) {
                                    Text(
                                        text = "Profile Balita",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                    thickness = 1.dp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                viewModel.children.collectAsState(initial = UiState.Loading).value.let { child ->
                                    when (child) {
                                        is UiState.Loading -> {
                                            OriginalLoading()
                                            LaunchedEffect(Unit) {
                                                viewModel.getChildrenByParent(id)
                                            }
                                        }
                                        is UiState.Success -> {
                                            Spacer(modifier = Modifier.height(3.dp))
                                            if (child.data.isNotEmpty()) {
                                                child.data.forEach { childItem ->
                                                    ChildCard(
                                                        full_name = childItem.full_name,
                                                        gender = childItem.gender,
                                                        date_of_birth = childItem.date_of_birth,
                                                        color = "Green",
                                                        onClick = {
                                                            activityLauncher.launch(
                                                                Intent(context, ChildActivity::class.java).apply {
                                                                    putExtra("id", childItem.children_id)
                                                                    when(checkAuth){
                                                                        is UiState.Success -> {
                                                                            putExtra("role", ((checkAuth as UiState.Success<AuthModel>).data.role))
                                                                        }
                                                                        else -> {}
                                                                    }
                                                                }
                                                            )
                                                        }
                                                    )
                                                    Spacer(modifier = Modifier.height(10.dp))
                                                }
                                            } else {
                                                ElevatedCard(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(shape = CardDefaults.shape)
                                                        .shadow(
                                                            elevation = 0.dp,
                                                            spotColor = Color.Transparent,
                                                            shape = CardDefaults.shape
                                                        )
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(90.dp)
                                                            .background(Color.White)
                                                            .padding(
                                                                start = 10.dp,
                                                                end = 10.dp,
                                                                bottom = 7.dp,
                                                                top = 7.dp
                                                            )
                                                    ){
                                                        Text(
                                                            text = "Tidak Ada Data Balita.",
                                                            fontSize = 17.sp,
                                                            style = MaterialTheme.typography.titleMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            textAlign = TextAlign.Center,
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        is UiState.Error -> {
                                            ErrorMessage(message = child.errorMessage)
                                        }
                                        is UiState.Unauthorized -> {
                                            redirectToHome()
                                        }

                                        else -> {}
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    if (showDeleteOfferDialog) {
                        AlertDialog(
                            containerColor = Color.White,
                            onDismissRequest = { showDeleteOfferDialog = false },
                            title = { Text(text = "Hapus Akun Orang Tua") },
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
                                        viewModel.deleteParent(id)
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
                        if (parents.data.is_active) {
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
                    ErrorMessage(message = parents.errorMessage)
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
                drawableRes = if (snackbarData.visuals.message.startsWith("Gagal")) R.drawable.baseline_error_outline_24 else R.drawable.baseline_check_circle_outline_24,
                message = snackbarData.visuals.message,
                containerColor = if (snackbarData.visuals.message.startsWith("Gagal")) MaterialTheme.colorScheme.error else Color(0xFF49ACFC),
            )
        }
    }
}