package com.project.labs.nutrigrow.ui.screen.growth

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.ChildrenModel
import com.project.labs.nutrigrow.data.model.UserModel
import com.project.labs.nutrigrow.data.repository.ChildRepository
import com.project.labs.nutrigrow.data.repository.UserRepository
import com.project.labs.nutrigrow.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class GrowthViewModel(
    private val userRepository: UserRepository,
    private val childRepository: ChildRepository
): ViewModel() {

    private val _children: MutableStateFlow<UiState<List<ChildrenModel>>> = MutableStateFlow(UiState.Loading)
    val children: StateFlow<UiState<List<ChildrenModel>>>
        get() = _children

    private val _search: MutableStateFlow<UiState<List<ChildrenModel>>> = MutableStateFlow(UiState.Loading)
    val search: StateFlow<UiState<List<ChildrenModel>>>
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

    fun getChildrenByParent( id: String ) {
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

    fun getChildrenByRegion( region: String ) {
        _children.value = UiState.Loading
        viewModelScope.launch {
            childRepository.getChildrenByRegion(region)
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

    fun getAllChildren() {
        _children.value = UiState.Loading
        viewModelScope.launch {
            childRepository.getAllChildren()
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

    fun getChildrenByNameAndRegion( name: String, region: String ) {
        _search.value = UiState.Loading
        viewModelScope.launch {
            childRepository.getChildrenByNameAndRegion(name, region)
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

    fun getChildrenByName( name: String) {
        _search.value = UiState.Loading
        viewModelScope.launch {
            childRepository.getChildrenByName(name)
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