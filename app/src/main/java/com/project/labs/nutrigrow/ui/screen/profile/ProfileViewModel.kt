package com.project.labs.nutrigrow.ui.screen.profile

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.UserModel
import com.project.labs.nutrigrow.data.repository.UserRepository
import com.project.labs.nutrigrow.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
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
                    .catch {
                        _user.value = UiState.Error(it.message.toString())
                    }.collect { data ->
                        try {
                            if (!data.success) {
                                if (data.message == "Unauthorized") {
                                    _user.value = UiState.Unauthorized
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
            } else {
                userRepository.getOfficerProfile()
                    .catch {
                        _user.value = UiState.Error(it.message.toString())
                    }.collect { data ->
                        try {
                            if (!data.success) {
                                if (data.message == "Unauthorized") {
                                    _user.value = UiState.Unauthorized
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
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logOut()
        }
    }
}