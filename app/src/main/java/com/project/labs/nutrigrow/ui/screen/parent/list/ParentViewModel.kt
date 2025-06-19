package com.project.labs.nutrigrow.ui.screen.parent.list

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


class ParentViewModel(
    private val userRepository: UserRepository,
): ViewModel() {

    private val _parents: MutableStateFlow<UiState<List<UserModel>>> = MutableStateFlow(UiState.Loading)
    val parents: StateFlow<UiState<List<UserModel>>>
        get() = _parents

    private val _search: MutableStateFlow<UiState<List<UserModel>>> = MutableStateFlow(UiState.Loading)
    val search: StateFlow<UiState<List<UserModel>>>
        get() = _search

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

    fun getUserProfile() {
        _user.value = UiState.Loading
        viewModelScope.launch {
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

    fun getAllParent() {
        _parents.value = UiState.Loading
        viewModelScope.launch {
            userRepository.getAllParent()
                .catch {
                    _parents.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    try {
                        if (!data.success) {
                            if (data.message == "Unauthorized") {
                                _parents.value = UiState.Unauthorized
                                return@collect
                            }
                            _parents.value = UiState.Error(data.message)
                            return@collect
                        }
                        _parents.value = UiState.Success(data.data)
                    } catch (e: Exception) {
                        _parents.value = UiState.Error(e.message.toString())
                    }
                }
        }
    }

    fun getParentByName( name: String) {
        _search.value = UiState.Loading
        viewModelScope.launch {
            userRepository.getParentByName(name)
                .catch {
                    _search.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    try {
                        if (!data.success) {
                            if (data.message == "Unauthorized") {
                                _search.value = UiState.Unauthorized
                                return@collect
                            }
                            _search.value = UiState.Error(data.message)
                            return@collect
                        }
                        _search.value = UiState.Success(data.data)
                    } catch (e: Exception) {
                        _search.value = UiState.Error(e.message.toString())
                    }
                }
        }
    }
}