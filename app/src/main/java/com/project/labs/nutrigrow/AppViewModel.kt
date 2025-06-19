package com.project.labs.nutrigrow

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.labs.nutrigrow.data.repository.UserRepository
import com.project.labs.nutrigrow.ui.state.UiState
import kotlinx.coroutines.launch

class AppViewModel (
    private val userRepository: UserRepository
): ViewModel() {
    private val _isAuthenticated: MutableState<UiState<Boolean>> = mutableStateOf(UiState.Loading)
    val isAuthenticated: MutableState<UiState<Boolean>>
        get() = _isAuthenticated

    private val _role: MutableState<UiState<String>> = mutableStateOf(UiState.Loading)
    val role: MutableState<UiState<String>>
        get() = _role

    fun checkAuthentication() {
        viewModelScope.launch {
            _isAuthenticated.value = UiState.Success(userRepository.getVerified().token.isNotEmpty())
        }
    }

    fun getRole() {
        viewModelScope.launch {
            _role.value = UiState.Success(userRepository.getVerified().role)
        }
    }

}