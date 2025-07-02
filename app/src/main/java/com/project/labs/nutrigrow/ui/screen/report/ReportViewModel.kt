package com.project.labs.nutrigrow.ui.screen.report

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

class ReportViewModel (
    private val userRepository: UserRepository,
    private val reportRepository: ReportRepository,
): ViewModel() {
    private val _user: MutableStateFlow<UiState<UserModel>> = MutableStateFlow(UiState.Loading)
    val user: StateFlow<UiState<UserModel>>
        get() = _user

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

    fun getUserProfile() {
        _user.value = UiState.Loading
        viewModelScope.launch {
            userRepository.getOfficerProfile()
                .catch {
                    _user.value = UiState.Error(it.message.toString())
                }.collect { data ->
                    try {
                        if (!data.success) {
                            if (data.message == "Unauthorized") {
                                _user.value = UiState.Unauthorized
                                return@collect
                            }
                            _user.value = UiState.Error(data.message)
                            return@collect
                        }
                        _user.value = UiState.Success(data.data)
                    } catch (e: Exception) {
                        _user.value = UiState.Error(e.message.toString())
                    }
                }
        }
    }

    fun getChildrenReport( currentDate: String, context: Context) {
        _user.value = UiState.Loading
        if ( currentDate.isEmpty()) {
            _pdfDownloadState.value = UiState.Error("Mohon Pilih Tanggal Penimbangan")
            return
        }
        viewModelScope.launch {
            viewModelScope.launch {
                try {
                    val file = reportRepository.getChildrenReport( currentDate = reformatDate(currentDate), context)
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

    fun getParentReport(context: Context) {
        _user.value = UiState.Loading
        viewModelScope.launch {
            viewModelScope.launch {
                try {
                    val file = reportRepository.getParentReport(context)
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

    fun getMonthlyReport(region: String, currentDate: String, context: Context) {
        _user.value = UiState.Loading
        if ( region.isEmpty() || currentDate.isEmpty()) {
            _pdfDownloadState.value = UiState.Error("Mohon Pilih Tanggal Penimbangan")
            return
        }
        viewModelScope.launch {
            viewModelScope.launch {
                try {
                    val file = reportRepository.getMonthlyReport(region = region, currentDate = reformatDate(currentDate), context = context)
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

    fun reformatDate(inputDate: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS XXX", Locale.ENGLISH)

        val localDate = LocalDate.parse(inputDate, inputFormatter)
        val zonedDateTime = localDate.atTime(17, 0).atZone(ZoneOffset.UTC)
        return zonedDateTime.format(outputFormatter)
    }
}