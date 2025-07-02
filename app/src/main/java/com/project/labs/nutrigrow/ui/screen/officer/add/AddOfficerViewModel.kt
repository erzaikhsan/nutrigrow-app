package com.project.labs.nutrigrow.ui.screen.officer.add

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.UserModel
import com.project.labs.nutrigrow.data.repository.UserRepository
import com.project.labs.nutrigrow.ui.state.UiState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

class AddOfficerViewModel (
    private val userRepository: UserRepository
): ViewModel() {
    private val _user: MutableState<UiState<UserModel>> = mutableStateOf(UiState.Unauthorized)
    val user: MutableState<UiState<UserModel>>
        get() = _user

    private val _isAuthenticated: MutableState<UiState<AuthModel>> = mutableStateOf(UiState.Loading)
    val isAuthenticated: MutableState<UiState<AuthModel>>
        get() = _isAuthenticated

    fun checkAuthentication() {
        viewModelScope.launch {
            _isAuthenticated.value = UiState.Success(userRepository.getVerified())
        }
    }

    fun register( email: String, password: String, confirm_password: String, full_name: String, gender: String, date_of_birth: String, phone_number: String, address: String, region: String) {
        if ( email.isEmpty() || password.isEmpty() || confirm_password.isEmpty() || full_name.isEmpty() || gender.isEmpty() || date_of_birth.isEmpty() || phone_number.isEmpty() || address.isEmpty() || region.isEmpty()) {
            _user.value = UiState.Error("Semua Data Harus Diisi")
            return
        }
        if (password != confirm_password) {
            _user.value = UiState.Error("Kata Sandi dan Konfirmasi Kata Sandi\nHarus Sama")
            return
        }
        _user.value = UiState.Loading
        viewModelScope.launch {
            userRepository.registerOfficer(
                email = email,
                password = password,
                full_name = full_name,
                gender = if (gender == "Laki-Laki") "M" else "F",
                date_of_birth = reformatDate(date_of_birth),
                phone_number = phone_number,
                address = address,
                region = region
            ).catch {
                _user.value = UiState.Error(it.message.toString())
            }.collect { data ->
                try {
                    if (!data.success) {
                        if (data.message == "Already exists") {
                            _user.value = UiState.Error("Email Sudah Digunakan,\nSilahkan Coba Dengan Email Lain")
                            return@collect
                        }
                        if (data.message == "Bad request") {
                            _user.value = UiState.Error("Masukan Tidak Sah.\nPastikan Anda Menggunakan Email dan Kata Sandi Yang Benar,\nMinimal 8 Karakter.")
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

    fun reformatDate(inputDate: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS XXX", Locale.ENGLISH)

        val localDate = LocalDate.parse(inputDate, inputFormatter)
        val zonedDateTime = localDate.atTime(17, 0).atZone(ZoneOffset.UTC)
        return zonedDateTime.format(outputFormatter)
    }
}