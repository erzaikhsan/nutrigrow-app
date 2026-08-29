package com.project.labs.nutrigrow.ui.screen.profile.update

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.UserModel
import com.project.labs.nutrigrow.data.repository.UserRepository
import com.project.labs.nutrigrow.ui.state.UiState
import com.project.labs.nutrigrow.ui.state.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

class ProfileUpdateViewModel (
    private val userRepository: UserRepository,
): ViewModel() {
    private val _user: MutableStateFlow<UiState<UserModel>> = MutableStateFlow(UiState.Loading)
    val user: StateFlow<UiState<UserModel>>
        get() = _user

    private val _newUser: MutableState<UiState<UserModel>> = mutableStateOf(UiState.Unauthorized)
    val newUser: MutableState<UiState<UserModel>>
        get() = _newUser

    private val _isAuthenticated: MutableState<UiState<AuthModel>> = mutableStateOf(UiState.Loading)
    val isAuthenticated: MutableState<UiState<AuthModel>>
        get() = _isAuthenticated

    fun checkAuthentication() {
        viewModelScope.launch {
            _isAuthenticated.value = UiState.Success(userRepository.getVerified())
        }
    }

    fun getUserProfile(role: String) {
        _user.value = UiState.Loading
        viewModelScope.launch {
            if (role == "Parent"){
                userRepository.getParentProfile()
                    .toUiState()
                    .collect { _user.value = it }
            } else {
                userRepository.getOfficerProfile()
                    .toUiState()
                    .collect { _user.value = it }
            }
        }
    }

    fun updateProfile(role: String, full_name: String, gender: String, date_of_birth: String, phone_number: String, address: String, region: String) {
        if ( full_name.isEmpty() || gender.isEmpty() || date_of_birth.isEmpty() || region.isEmpty() || phone_number.isEmpty() || address.isEmpty()) {
            _newUser.value = UiState.Error("Pastikan Semua Data Anda\nDiisi Dengan Benar")
            return
        }

        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
        val dob = LocalDate.parse(date_of_birth, formatter)

        if (dob.isAfter(today)) {
            _newUser.value = UiState.Error("Tanggal Lahir tidak boleh melebihi tanggal hari ini")
            return
        }

        _newUser.value = UiState.Loading
        viewModelScope.launch {
            if (role == "Parent"){
                userRepository.updateParent( full_name = full_name, gender = if (gender == "Laki-Laki") "M" else "F", date_of_birth = reformatDate(date_of_birth), region =  region, phone_number = phone_number, address = address,).catch {
                    _newUser.value = UiState.Error(it.message.toString())
                }.collect { data ->
                    try {
                        if (!data.success) {
                            if (data.message == "Already exists") {
                                _newUser.value = UiState.Error("Data anda sudah terdaftar")
                                return@collect
                            }
                            if (data.message == "Bad request") {
                                _newUser.value = UiState.Error("Masukan Tidak Sah.\nMohon Periksa Masukan Anda.\nPastikan Anda Memasukan Data Dengan Benar.")
                                return@collect
                            }
                            _newUser.value = UiState.Error(data.message)
                            return@collect
                        }
                        _newUser.value = UiState.Success(data.data)
                    } catch (e: Exception) {
                        _newUser.value = UiState.Error(e.message.toString())
                    }
                }
            } else {
                userRepository.updateOfficer( full_name = full_name, gender = if (gender == "Laki-Laki") "M" else "F", date_of_birth = reformatDate(date_of_birth), region =  region, phone_number = phone_number, address = address,).catch {
                    _newUser.value = UiState.Error(it.message.toString())
                }.collect { data ->
                    try {
                        if (!data.success) {
                            if (data.message == "Already exists") {
                                _newUser.value = UiState.Error("Data anda sudah terdaftar")
                                return@collect
                            }
                            if (data.message == "Bad request") {
                                _newUser.value = UiState.Error("Masukan Tidak Sah.\nMohon Periksa Masukan Anda.\nPastikan Anda Memasukan Data Dengan Benar.")
                                return@collect
                            }
                            _newUser.value = UiState.Error(data.message)
                            return@collect
                        }
                        _newUser.value = UiState.Success(data.data)
                    } catch (e: Exception) {
                        _newUser.value = UiState.Error(e.message.toString())
                    }
                }
            }
        }
    }

    fun resetNewUser() {
        _newUser.value = UiState.Loading
    }

    fun reformatDate(inputDate: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS XXX", Locale.ENGLISH)

        val localDate = LocalDate.parse(inputDate, inputFormatter)
        val zonedDateTime = localDate.atTime(17, 0).atZone(ZoneOffset.UTC)
        return zonedDateTime.format(outputFormatter)
    }
}