package com.project.labs.nutrigrow.ui.screen.parent

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.labs.nutrigrow.data.model.UserModel
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.ChildrenModel
import com.project.labs.nutrigrow.data.repository.ChildRepository
import com.project.labs.nutrigrow.data.repository.UserRepository
import com.project.labs.nutrigrow.ui.state.UiState
import com.project.labs.nutrigrow.ui.state.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ParentProfileViewModel (
    private val userRepository: UserRepository,
    private val childRepository: ChildRepository,
): ViewModel() {
    private val _children: MutableStateFlow<UiState<List<ChildrenModel>>> = MutableStateFlow(UiState.Loading)
    val children: StateFlow<UiState<List<ChildrenModel>>>
        get() = _children

    private val _parent: MutableStateFlow<UiState<UserModel>> = MutableStateFlow(UiState.Loading)
    val parent: StateFlow<UiState<UserModel>>
        get() = _parent

    private val _isActive: MutableState<UiState<UserModel>> = mutableStateOf(UiState.Loading)
    val isActive: MutableState<UiState<UserModel>>
        get() = _isActive

    private val _isAuthenticated: MutableState<UiState<AuthModel>> = mutableStateOf(UiState.Loading)
    val isAuthenticated: MutableState<UiState<AuthModel>>
        get() = _isAuthenticated

    fun checkAuthentication() {
        viewModelScope.launch {
            _isAuthenticated.value = UiState.Success(userRepository.getVerified())
        }
    }

    fun getParentAccount(id: String) {
        _parent.value = UiState.Loading
        viewModelScope.launch {
            userRepository.getParentAccount(id)
                .toUiState()
                .collect { _parent.value = it }
        }
    }

    private val _deleted: MutableState<UiState<String>> = mutableStateOf(UiState.Unauthorized)
    val deleted: MutableState<UiState<String>>
        get() = _deleted

    fun deleteParent(id: String) {
        _deleted.value = UiState.Loading
        viewModelScope.launch {
            userRepository.deleteParent(id)
                .toUiState()
                .collect { _deleted.value = it }
        }
    }

    fun deactivateAccount(id: String) {
        _parent.value = UiState.Loading
        viewModelScope.launch {
            userRepository.deactivateAccount(id)
                .toUiState()
                .collect { _isActive.value = it }
        }
    }

    fun activateAccount(id: String) {
        _parent.value = UiState.Loading
        viewModelScope.launch {
            userRepository.activateAccount(id)
                .toUiState()
                .collect { _isActive.value = it }
        }
    }

    fun getChildrenByParent(id: String) {
        _children.value = UiState.Loading
        viewModelScope.launch {
            childRepository.getChildrenByParent(id)
                .toUiState()
                .collect { _children.value = it }
        }
    }
}