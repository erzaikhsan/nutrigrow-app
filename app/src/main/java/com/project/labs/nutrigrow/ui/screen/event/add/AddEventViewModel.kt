package com.project.labs.nutrigrow.ui.screen.event.add

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.EventModel
import com.project.labs.nutrigrow.data.model.UserModel
import com.project.labs.nutrigrow.data.repository.EventRepository
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

class AddEventViewModel (
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
): ViewModel() {
    private val _event: MutableState<UiState<EventModel>> = mutableStateOf(UiState.Unauthorized)
    val event: MutableState<UiState<EventModel>>
        get() = _event

    private val _user: MutableStateFlow<UiState<UserModel>> = MutableStateFlow(UiState.Loading)
    val user: StateFlow<UiState<UserModel>>
        get() = _user

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

    fun addEvent( title: String, date: String, start_time: String, end_time: String, place: String, description: String, region: String) {
        if ( title.isEmpty() || date.isEmpty() || start_time.isEmpty() || end_time.isEmpty() || place.isEmpty() || description.isEmpty()) {
            _event.value = UiState.Error("Pastikan Semua Data Kegiatan\nDiisi Dengan Benar")
            return
        }
        _event.value = UiState.Loading
        viewModelScope.launch {
            eventRepository.addEvent( title, reformatDate(date), start_time, end_time, place, description, region = if (region == "Desa") "Village" else region).catch {
                _event.value = UiState.Error(it.message.toString())
            }.collect { data ->
                try {
                    if (!data.success) {
                        if (data.message == "Already exists") {
                            _event.value = UiState.Error("Kegiatan sudah terdaftar")
                            return@collect
                        }
                        if (data.message == "Bad request") {
                            _event.value = UiState.Error("Masukan Tidak Sah.\nMohon Periksa Masukan Anda.\nPastikan Anda Memasukan Data Kegiatan Dengan Benar.")
                            return@collect
                        }
                        _event.value = UiState.Error(data.message)
                        return@collect
                    }
                    _event.value = UiState.Success(data.data)
                } catch (e: Exception) {
                    _event.value = UiState.Error(e.message.toString())
                }
            }
        }
    }

    fun resetEventState() {
        _event.value = UiState.Loading
    }

    fun reformatDate(inputDate: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS XXX", Locale.ENGLISH)

        val localDate = LocalDate.parse(inputDate, inputFormatter)
        val zonedDateTime = localDate.atTime(17, 0).atZone(ZoneOffset.UTC)
        return zonedDateTime.format(outputFormatter)
    }
}