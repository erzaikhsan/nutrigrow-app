package com.project.labs.nutrigrow.ui.screen.auth

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.UserModel
import com.project.labs.nutrigrow.data.model.VerifyModel
import com.project.labs.nutrigrow.data.repository.UserRepository
import com.project.labs.nutrigrow.ui.state.UiState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

class AuthViewModel (
    private val userRepository: UserRepository
): ViewModel() {
    private val _user: MutableState<UiState<UserModel>> = mutableStateOf(UiState.Unauthorized)
    val user: MutableState<UiState<UserModel>>
        get() = _user

    private val _account: MutableState<UiState<String>> = mutableStateOf(UiState.Unauthorized)
    val account: MutableState<UiState<String>>
        get() = _account

    private val _otpCode: MutableState<UiState<VerifyModel>> = mutableStateOf(UiState.Unauthorized)
    val otpCode: MutableState<UiState<VerifyModel>>
        get() = _otpCode

    private val _auth: MutableState<UiState<AuthModel>> = mutableStateOf(UiState.Unauthorized)
    val auth: MutableState<UiState<AuthModel>>
        get() = _auth

    fun login(email: String, password: String) {
        if(email.isEmpty() || password.isEmpty()){
            _auth.value = UiState.Error("Email dan Kata Sandi\nTidak Boleh Kosong")
            return
        }
        _auth.value = UiState.Loading
        viewModelScope.launch {
            userRepository.login(email = email, password = password)
                .catch {
                    _auth.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    try {
                        if (!data.success) {
                            if (data.message == "Bad request") {
                                _auth.value = UiState.Error("Email atau Password Tidak Sah.\nSilahkan Coba Lagi")
                                return@collect
                            }
                            if (data.message == "Unauthorized") {
                                _auth.value = UiState.Error("Email atau Kata Sandi Salah.\nSilahkan Coba Lagi")
                                return@collect
                            }
                            if (data.message == "Forbidden") {
                                _auth.value = UiState.Error("Akun Anda Dinonaktifkan.\nSilahkan Hubungi Admin")
                                return@collect
                            }
                            _auth.value = UiState.Error(data.message)
                            return@collect
                        }
                        _auth.value = UiState.Success(data.data)
                    } catch (e: Exception) {
                        _auth.value = UiState.Error(e.message.toString())
                    }
                }
        }
    }

    fun createAccount( email: String, password: String, confirm_password: String ) {
        if ( email.isEmpty() || password.isEmpty() || confirm_password.isEmpty()) {
            _account.value = UiState.Error("Semua Data Harus Diisi")
            return
        }
        if (password != confirm_password) {
            _account.value = UiState.Error("Kata Sandi dan Konfirmasi Kata Sandi\nHarus Sama")
            return
        }
        _account.value = UiState.Loading
        viewModelScope.launch {
            userRepository.createAccount(
                email = email,
                password = password,
            ).catch {
                _account.value = UiState.Error(it.message.toString())
            }.collect { data ->
                try {
                    if (!data.success) {
                        if (data.message == "Already exists") {
                            _account.value = UiState.Error("Email Sudah Digunakan,\nSilahkan Coba Dengan Email Lain")
                            return@collect
                        }
                        if (data.message == "Bad request") {
                            _account.value = UiState.Error("Masukan Tidak Sah.\nMohon Periksa Masukan Anda.\nPastikan Anda Menggunakan Email dan Kata Sandi Yang Benar,\nMinimal 8 Karakter.")
                            return@collect
                        }
                        _account.value = UiState.Error(data.message)
                        return@collect
                    }
                    _account.value = UiState.Success(data.message)
                } catch (e: Exception) {
                    _account.value = UiState.Error(e.message.toString())
                }
            }
        }
    }

    fun verifyOtp( otpCode: String ) {
        if ( otpCode.isEmpty()) {
            _otpCode.value = UiState.Error("Semua Data Harus Diisi")
            return
        }
        _otpCode.value = UiState.Loading
        viewModelScope.launch {
            userRepository.verifyOtp(
                otpCode = otpCode
            ).catch {
                _otpCode.value = UiState.Error(it.message.toString())
            }.collect { data ->
                try {
                    if (!data.success) {
                        if (data.message == "Already exists") {
                            _otpCode.value = UiState.Error("Silahkan Cek Kode OTP di Email Anda")
                            return@collect
                        }
                        if (data.message == "Bad request") {
                            _otpCode.value = UiState.Error("Masukan Tidak Sah.\nMohon Periksa Masukan Anda.\nPastikan Anda Menggunakan Email dan Kata Sandi Yang Benar,\nMinimal 8 Karakter.")
                            return@collect
                        }
                        _otpCode.value = UiState.Error(data.message)
                        return@collect
                    }
                    _otpCode.value = UiState.Success(data.data)
                } catch (e: Exception) {
                    _otpCode.value = UiState.Error(e.message.toString())
                }
            }
        }
    }

    fun register( full_name: String, gender: String, date_of_birth: String, phone_number: String, address: String, region: String) {
        if ( full_name.isEmpty() || gender.isEmpty() || date_of_birth.isEmpty() || phone_number.isEmpty() || address.isEmpty() || region.isEmpty()) {
            _user.value = UiState.Error("Semua Data Harus Diisi")
            return
        }
        _user.value = UiState.Loading
        viewModelScope.launch {
            userRepository.registerParent(
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
                            _user.value = UiState.Error("Masukan Tidak Sah.\nMohon Periksa Masukan Anda.\nPastikan Anda Menggunakan Email dan Kata Sandi Yang Benar,\nMinimal 8 Karakter.")
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