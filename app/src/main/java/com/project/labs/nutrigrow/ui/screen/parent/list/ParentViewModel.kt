package com.project.labs.nutrigrow.ui.screen.parent.list

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
                .toUiState()
                .collect { _user.value = it }
        }
    }

    fun getAllParent() {
        _parents.value = UiState.Loading
        viewModelScope.launch {
            userRepository.getAllParent()
                .toUiState()
                .collect { _parents.value = it }
        }
    }

    fun getParentByName( name: String) {
        _search.value = UiState.Loading
        viewModelScope.launch {
            userRepository.getParentByName(name)
                .toUiState()
                .collect { _search.value = it }
        }
    }
}