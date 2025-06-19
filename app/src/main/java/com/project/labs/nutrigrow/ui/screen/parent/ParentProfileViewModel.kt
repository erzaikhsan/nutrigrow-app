package com.project.labs.nutrigrow.ui.screen.parent

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.labs.nutrigrow.data.model.AccountModel
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.ChildrenModel
import com.project.labs.nutrigrow.data.repository.ChildRepository
import com.project.labs.nutrigrow.data.repository.UserRepository
import com.project.labs.nutrigrow.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ParentProfileViewModel (
    private val userRepository: UserRepository,
    private val childRepository: ChildRepository,
): ViewModel() {
    private val _children: MutableStateFlow<UiState<List<ChildrenModel>>> = MutableStateFlow(UiState.Loading)
    val children: StateFlow<UiState<List<ChildrenModel>>>
        get() = _children

    private val _parent: MutableStateFlow<UiState<AccountModel>> = MutableStateFlow(UiState.Loading)
    val parent: StateFlow<UiState<AccountModel>>
        get() = _parent

    private val _isActive: MutableState<UiState<AccountModel>> = mutableStateOf(UiState.Loading)
    val isActive: MutableState<UiState<AccountModel>>
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
                .catch {
                    _parent.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    try {
                        if (!data.success) {
                            if (data.message == "Unauthorized") {
                                _parent.value = UiState.Unauthorized
                                return@collect
                            }
                            _parent.value = UiState.Error(data.message)
                            return@collect
                        }
                        _parent.value = UiState.Success(data.data)
                    } catch (e: Exception) {
                        _parent.value = UiState.Error(e.message.toString())
                    }
                }
        }
    }

    fun deactivateAccount(id: String) {
        _parent.value = UiState.Loading
        viewModelScope.launch {
            userRepository.deactivateAccount(id)
                .catch {
                    _isActive.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    try {
                        if (!data.success) {
                            if (data.message == "Unauthorized") {
                                _isActive.value = UiState.Unauthorized
                                return@collect
                            }
                            _isActive.value = UiState.Error(data.message)
                            return@collect
                        }
                        _isActive.value = UiState.Success(data.data)
                    } catch (e: Exception) {
                        _isActive.value = UiState.Error(e.message.toString())
                    }
                }
        }
    }

    fun activateAccount(id: String) {
        _parent.value = UiState.Loading
        viewModelScope.launch {
            userRepository.activateAccount(id)
                .catch {
                    _isActive.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    try {
                        if (!data.success) {
                            if (data.message == "Unauthorized") {
                                _isActive.value = UiState.Unauthorized
                                return@collect
                            }
                            _isActive.value = UiState.Error(data.message)
                            return@collect
                        }
                        _isActive.value = UiState.Success(data.data)
                    } catch (e: Exception) {
                        _isActive.value = UiState.Error(e.message.toString())
                    }
                }
        }
    }

    fun getChildrenByParent(id: String) {
        _children.value = UiState.Loading
        viewModelScope.launch {
            childRepository.getChildrenByParent(id)
                .catch {
                    _children.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    try {
                        if (!data.success) {
                            if (data.message == "Unauthorized") {
                                _children.value = UiState.Unauthorized
                                return@collect
                            }
                            _children.value = UiState.Error(data.message)
                            return@collect
                        }
                        _children.value = UiState.Success(data.data)
                    } catch (e: Exception) {
                        _children.value = UiState.Error(e.message.toString())
                    }
                }
        }
    }
}