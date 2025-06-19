package com.project.labs.nutrigrow.ui.screen.event.update

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.EventModel
import com.project.labs.nutrigrow.data.repository.EventRepository
import com.project.labs.nutrigrow.data.repository.UserRepository
import com.project.labs.nutrigrow.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

class UpdateEventViewModel (
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository
): ViewModel() {
    private val _event: MutableStateFlow<UiState<EventModel>> = MutableStateFlow(UiState.Loading)
    val event: StateFlow<UiState<EventModel>>
        get() = _event

    private val _newEvent: MutableState<UiState<EventModel>> = mutableStateOf(UiState.Unauthorized)
    val newEvent: MutableState<UiState<EventModel>>
        get() = _newEvent

    private val _isAuthenticated: MutableState<UiState<AuthModel>> = mutableStateOf(UiState.Loading)
    val isAuthenticated: MutableState<UiState<AuthModel>>
        get() = _isAuthenticated

    fun checkAuthentication() {
        viewModelScope.launch {
            _isAuthenticated.value = UiState.Success(userRepository.getVerified())
        }
    }

    fun getEventById(id: String) {
        _event.value = UiState.Loading
        viewModelScope.launch {
            eventRepository.getEventById(id)
                .catch {
                    _event.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    try {
                        if (!data.success) {
                            if (data.message == "Unauthorized") {
                                _event.value = UiState.Unauthorized
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

    fun updateEvent( id: String, title: String, date: String, start_time: String, end_time: String, place: String, description: String, region: String ) {
        if ( id.isEmpty() || title.isEmpty() || date.isEmpty() || start_time.isEmpty() || end_time.isEmpty() || place.isEmpty() || description.isEmpty() || region.isEmpty() ) {
            _newEvent.value = UiState.Error("Pastikan Semua Data\nDiisi Dengan Benar")
            return
        }
        _newEvent.value = UiState.Loading
        viewModelScope.launch {
            eventRepository.updateEvent( id, title, reformatDate(date), start_time, end_time, place, description, region = if (region == "Desa") "Village" else region ).catch {
                _newEvent.value = UiState.Error(it.message.toString())
            }.collect { data ->
                try {
                    if (!data.success) {
                        if (data.message == "Already exists") {
                            _newEvent.value = UiState.Error("Data Pertumbuhan Bulan Ini Sudah Ada.")
                            return@collect
                        }
                        if (data.message == "Bad request") {
                            _newEvent.value = UiState.Error("Masukan Tidak Sah.\nMohon Periksa Masukan Anda.\nPastikan Anda Memasukan Data Anak Dengan Benar.")
                            return@collect
                        }
                        _newEvent.value = UiState.Error(data.message)
                        return@collect
                    }
                    _newEvent.value = UiState.Success(data.data)
                } catch (e: Exception) {
                    _newEvent.value = UiState.Error(e.message.toString())
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