package com.project.labs.nutrigrow.ui.screen.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.ChildrenModel
import com.project.labs.nutrigrow.data.model.EventModel
import com.project.labs.nutrigrow.data.model.UserModel
import com.project.labs.nutrigrow.data.repository.ChildRepository
import com.project.labs.nutrigrow.data.repository.EventRepository
import com.project.labs.nutrigrow.data.repository.UserRepository
import com.project.labs.nutrigrow.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository,
    private val childRepository: ChildRepository,
    private val eventRepository: EventRepository,
): ViewModel() {

    private val _children: MutableStateFlow<UiState<List<ChildrenModel>>> = MutableStateFlow(UiState.Loading)
    val children: StateFlow<UiState<List<ChildrenModel>>>
        get() = _children

    private val _event: MutableStateFlow<UiState<List<EventModel>>> = MutableStateFlow(UiState.Loading)
    val event: StateFlow<UiState<List<EventModel>>>
        get() = _event

    private val _user: MutableStateFlow<UiState<UserModel>> = MutableStateFlow(UiState.Loading)
    val user: StateFlow<UiState<UserModel>>
        get() = _user

    private val _parents: MutableStateFlow<UiState<List<UserModel>>> = MutableStateFlow(UiState.Loading)
    val parents: StateFlow<UiState<List<UserModel>>>
        get() = _parents

    private val _officer: MutableStateFlow<UiState<List<UserModel>>> = MutableStateFlow(UiState.Loading)
    val officers: StateFlow<UiState<List<UserModel>>>
        get() = _officer

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

    fun getIncomingEvent(date: String, region: String) {
        _event.value = UiState.Loading
        viewModelScope.launch {
            eventRepository.getIncomingEvent(date, region)
                .catch {
                    _event.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    try {
                        if (!data.success) {
                            if (data.message == "Unauthorized") {
                                _event.value = UiState.Unauthorized
                                return@collect
                            }
                            _event.value = UiState.Error(data.message)
                            return@collect
                        }
                        _event.value = UiState.Success(data.data)
                    } catch (e: Exception) {
                        _event.value = UiState.Error(e.message.toString())
                    }
                }
        }
    }

    fun getParentByRegion(region: String) {
        _parents.value = UiState.Loading
        viewModelScope.launch {
            userRepository.getParentByRegion(region)
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

    fun getOfficers() {
        _officer.value = UiState.Loading
        viewModelScope.launch {
            userRepository.getAllOfficer()
                .catch {
                    _officer.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    try {
                        if (!data.success) {
                            if (data.message == "Unauthorized") {
                                _officer.value = UiState.Unauthorized
                                return@collect
                            }
                            _officer.value = UiState.Error(data.message)
                            return@collect
                        }
                        _officer.value = UiState.Success(data.data)
                    } catch (e: Exception) {
                        _officer.value = UiState.Error(e.message.toString())
                    }
                }
        }
    }
}