package com.project.labs.nutrigrow.ui.screen.profile

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
import kotlinx.coroutines.launch

class ProfileViewModel (
    private val userRepository: UserRepository,
): ViewModel() {
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

    private val _reminderEnabled: MutableState<Boolean> = mutableStateOf(true)
    val reminderEnabled: MutableState<Boolean>
        get() = _reminderEnabled

    fun loadReminderSetting() {
        viewModelScope.launch {
            _reminderEnabled.value = userRepository.isReminderEnabled()
        }
    }

    fun setReminderEnabled(enabled: Boolean) {
        _reminderEnabled.value = enabled
        viewModelScope.launch {
            userRepository.setReminderEnabled(enabled)
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logOut()
        }
    }
}