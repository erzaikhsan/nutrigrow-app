package com.project.labs.nutrigrow.ui.screen.event.detail

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

class DetailEventViewModel(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
): ViewModel() {

    private val _event: MutableStateFlow<UiState<EventModel>> = MutableStateFlow(UiState.Loading)
    val event: StateFlow<UiState<EventModel>>
        get() = _event

    private val _delete: MutableState<UiState<EventModel>> = mutableStateOf(UiState.Unauthorized)
    val delete: MutableState<UiState<EventModel>>
        get() = _delete

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

    fun deleteEvent(id: String) {
        _delete.value = UiState.Loading
        viewModelScope.launch {
            eventRepository.deleteEvent(id)
                .catch {
                    _delete.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    try {
                        if (!data.success) {
                            if (data.message == "Unauthorized") {
                                _delete.value = UiState.Unauthorized
                                return@collect
                            }
                            _delete.value = UiState.Error(data.message)
                            return@collect
                        }
                        _delete.value = UiState.Success(data.data)
                    } catch (e: Exception) {
                        _delete.value = UiState.Error(e.message.toString())
                    }
                }
        }
    }

    fun resetDeleteState() {
        _delete.value = UiState.Loading
    }
}