package com.project.labs.nutrigrow.ui.screen.event

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

class EventViewModel(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
): ViewModel() {

    private val _event: MutableStateFlow<UiState<List<EventModel>>> = MutableStateFlow(UiState.Loading)
    val event: StateFlow<UiState<List<EventModel>>>
        get() = _event

    private val _isAuthenticated: MutableState<UiState<AuthModel>> = mutableStateOf(UiState.Loading)
    val isAuthenticated: MutableState<UiState<AuthModel>>
        get() = _isAuthenticated

    fun checkAuthentication() {
        viewModelScope.launch {
            _isAuthenticated.value = UiState.Success(userRepository.getVerified())
        }
    }

    fun getAllEvent( ) {
        _event.value = UiState.Loading
        viewModelScope.launch {
            eventRepository.getAllEvent()
                .toUiState()
                .collect { _event.value = it }
        }
    }
}