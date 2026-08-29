package com.project.labs.nutrigrow.ui.screen.officer

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.UserModel
import com.project.labs.nutrigrow.data.repository.ReportRepository
import com.project.labs.nutrigrow.data.repository.UserRepository
import com.project.labs.nutrigrow.ui.state.UiState
import com.project.labs.nutrigrow.ui.state.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

class OfficerProfileViewModel (
    private val userRepository: UserRepository,
    private val reportRepository: ReportRepository,
): ViewModel() {
    private val _officer: MutableStateFlow<UiState<UserModel>> = MutableStateFlow(UiState.Loading)
    val officer: StateFlow<UiState<UserModel>>
        get() = _officer

    private val _isActive: MutableState<UiState<UserModel>> = mutableStateOf(UiState.Loading)
    val isActive: MutableState<UiState<UserModel>>
        get() = _isActive

    private val _pdfDownloadState = MutableStateFlow<UiState<File>>(UiState.Loading)
    val pdfDownloadState: StateFlow<UiState<File>> = _pdfDownloadState

    private val _isAuthenticated: MutableState<UiState<AuthModel>> = mutableStateOf(UiState.Loading)
    val isAuthenticated: MutableState<UiState<AuthModel>>
        get() = _isAuthenticated

    fun checkAuthentication() {
        viewModelScope.launch {
            _isAuthenticated.value = UiState.Success(userRepository.getVerified())
        }
    }

    fun getOfficerAccount(id: String) {
        _officer.value = UiState.Loading
        viewModelScope.launch {
            userRepository.getOfficerAccount(id)
                .toUiState()
                .collect { _officer.value = it }
        }
    }

    fun getRegionChildrenReport(region : String, month: Number, year: Number, context: Context) {
        _officer.value = UiState.Loading
        if ( region.isEmpty() || month == 0 || year == 0) {
            _pdfDownloadState.value = UiState.Error("Mohon Pilih Tanggal Penimbangan")
            return
        }
        viewModelScope.launch {
            viewModelScope.launch {
                try {
                    val file = reportRepository.getRegionChildrenReport(region = region, month = month, year = year, context)
                    if (file != null) {
                        _pdfDownloadState.value = UiState.Success(file)
                    } else {
                        _pdfDownloadState.value = UiState.Error("Tidak ada data Balita.")
                    }
                } catch (e: Exception) {
                    _pdfDownloadState.value = UiState.Error("Error: ${e.message}")
                }
            }
        }
    }

    fun getRegionParentReport(region : String, context: Context) {
        _officer.value = UiState.Loading
        viewModelScope.launch {
            viewModelScope.launch {
                try {
                    val file = reportRepository.getRegionParentReport(region = region, context)
                    if (file != null) {
                        _pdfDownloadState.value = UiState.Success(file)
                    } else {
                        _pdfDownloadState.value = UiState.Error("Tidak ada data Orang Tua Balita.")
                    }
                } catch (e: Exception) {
                    _pdfDownloadState.value = UiState.Error("Error: ${e.message}")
                }
            }
        }
    }

    fun getMonthlyReport(region: String, month: Number, year: Number, context: Context) {
        _officer.value = UiState.Loading
        if ( region.isEmpty() || month == 0 || year == 0) {
            _pdfDownloadState.value = UiState.Error("Mohon Pilih Bulan Penimbangan")
            return
        }
        viewModelScope.launch {
            viewModelScope.launch {
                try {
                    val file = reportRepository.getMonthlyReport(region = region, month = month, year = year, context = context)
                    if (file != null) {
                        _pdfDownloadState.value = UiState.Success(file)
                    } else {
                        _pdfDownloadState.value = UiState.Error("Tidak ada data Laporan Penimbangan.")
                    }
                } catch (e: Exception) {
                    _pdfDownloadState.value = UiState.Error("Error: ${e.message}")
                }
            }
        }
    }

    private val _deleted: MutableState<UiState<String>> = mutableStateOf(UiState.Unauthorized)
    val deleted: MutableState<UiState<String>>
        get() = _deleted

    fun deleteOfficer(id: String) {
        _deleted.value = UiState.Loading
        viewModelScope.launch {
            userRepository.deleteOfficer(id)
                .toUiState()
                .collect { _deleted.value = it }
        }
    }

    fun deactivateAccount(id: String) {
        _officer.value = UiState.Loading
        viewModelScope.launch {
            userRepository.deactivateAccount(id)
                .toUiState()
                .collect { _isActive.value = it }
        }
    }

    fun activateAccount(id: String) {
        _officer.value = UiState.Loading
        viewModelScope.launch {
            userRepository.activateAccount(id)
                .toUiState()
                .collect { _isActive.value = it }
        }
    }

    fun resetPdfDownloadState() {
        _pdfDownloadState.value = UiState.Loading
    }

    fun reformatDate(inputDate: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS XXX", Locale.ENGLISH)

        val localDate = LocalDate.parse(inputDate, inputFormatter)
        val zonedDateTime = localDate.atTime(17, 0).atZone(ZoneOffset.UTC)
        return zonedDateTime.format(outputFormatter)
    }
}