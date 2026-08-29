package com.project.labs.nutrigrow.ui.screen.validation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.state.UiState
import com.project.labs.nutrigrow.ui.theme.BrandGreen
import com.project.labs.nutrigrow.ui.theme.BrandGreenSoft
import com.project.labs.nutrigrow.ui.theme.SurfaceCard
import com.project.labs.nutrigrow.ui.theme.TextSecondary

private const val TAB_CALCULATOR = 0
private const val TAB_SAMPLES = 1

@Composable
fun ValidationScreen(
    redirectToHome: (String) -> Unit,
    viewModel: ValidationViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(TAB_CALCULATOR) }
    val sampleState by viewModel.samples.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkAuthentication()
    }

    if (sampleState is UiState.Unauthorized) {
        redirectToHome("Sesi Telah Berakhir\nSilahkan Masuk Kembali")
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrandGreenSoft)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceCard,
            contentColor = BrandGreen,
        ) {
            ValidationTab(
                title = "Hitung Satu Data",
                selected = selectedTab == TAB_CALCULATOR,
                onClick = { selectedTab = TAB_CALCULATOR },
            )
            ValidationTab(
                title = "Uji Berkas",
                selected = selectedTab == TAB_SAMPLES,
                onClick = { selectedTab = TAB_SAMPLES },
            )
        }

        when (selectedTab) {
            TAB_CALCULATOR -> ValidationCalculatorTab(viewModel = viewModel)
            else -> ValidationSampleTab(viewModel = viewModel)
        }
    }
}

@Composable
private fun ValidationTab(title: String, selected: Boolean, onClick: () -> Unit) {
    Tab(
        selected = selected,
        onClick = onClick,
        selectedContentColor = BrandGreen,
        unselectedContentColor = TextSecondary,
        text = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            )
        },
    )
}
