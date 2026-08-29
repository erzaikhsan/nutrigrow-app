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
import com.project.labs.nutrigrow.ui.state.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
                .toUiState()
                .collect { _event.value = it }
        }
    }

    fun deleteEvent(id: String) {
        _delete.value = UiState.Loading
        viewModelScope.launch {
            eventRepository.deleteEvent(id)
                .toUiState()
                .collect { _delete.value = it }
        }
    }

    fun resetDeleteState() {
        _delete.value = UiState.Loading
    }
}