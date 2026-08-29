package com.project.labs.nutrigrow.ui.screen.child

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.labs.nutrigrow.ui.component.respond.ErrorMessage
import com.project.labs.nutrigrow.ui.component.respond.LoadingIndicator
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.state.UiState
import com.project.labs.nutrigrow.ui.theme.BrandGreenSoft
import com.project.labs.nutrigrow.ui.theme.Spacing

@Composable
fun ChildProfileScreen(
    id: String,
    redirectToHome: () -> Unit,
    viewModel: ChildProfileViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val checkAuth by viewModel.isAuthenticated

    LaunchedEffect(key1 = Unit) {
        viewModel.checkAuthentication()
    }

    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.getChildProfile(id)
            viewModel.getLastGrowth(id)
            viewModel.getVaccineByChildId(id)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BrandGreenSoft)
    ) {
        viewModel.child.collectAsState(initial = UiState.Loading).value.let { child ->
            when (child) {
                is UiState.Loading -> {
                    LoadingIndicator()
                    LaunchedEffect(Unit) {
                        viewModel.getChildProfile(id)
                    }
                }

                is UiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        ChildProfileHeader(
                            child = child,
                            id = id,
                        )
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        ChildIdentityCard(
                            child = child,
                            checkAuth = checkAuth,
                            id = id,
                            context = context,
                            activityLauncher = activityLauncher,
                        )
                        ChildGrowthSection(
                            child = child,
                            id = id,
                            context = context,
                            activityLauncher = activityLauncher,
                            viewModel = viewModel,
                        )
                        ChildVaccineSection(
                            child = child,
                            checkAuth = checkAuth,
                            id = id,
                            context = context,
                            activityLauncher = activityLauncher,
                            viewModel = viewModel,
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                    }
                }

                is UiState.Error -> {
                    ErrorMessage(message = child.errorMessage)
                }

                is UiState.Unauthorized -> {
                    redirectToHome()
                }

                else -> {
                    redirectToHome()
                }
            }
        }
    }
}
