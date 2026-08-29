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
import com.project.labs.nutrigrow.ui.state.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _search: MutableStateFlow<UiState<List<UserModel>>> = MutableStateFlow(UiState.Loading)
    val search: StateFlow<UiState<List<UserModel>>>
        get() = _search

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

    fun getChildrenByParent(id: String) {
        _children.value = UiState.Loading
        viewModelScope.launch {
            childRepository.getChildrenByParent(id)
                .toUiState()
                .collect { _children.value = it }
        }
    }

    fun getIncomingEvent(date: String, region: String) {
        _event.value = UiState.Loading
        viewModelScope.launch {
            eventRepository.getIncomingEvent(date, region)
                .toUiState()
                .collect { _event.value = it }
        }
    }

    fun getParentByRegion(region: String) {
        _parents.value = UiState.Loading
        viewModelScope.launch {
            userRepository.getParentByRegion(region)
                .toUiState()
                .collect { _parents.value = it }
        }
    }

    fun getOfficers() {
        _officer.value = UiState.Loading
        viewModelScope.launch {
            userRepository.getAllOfficer()
                .toUiState()
                .collect { _officer.value = it }
        }
    }

    fun getOfficerByName(name: String) {
        _search.value = UiState.Loading
        viewModelScope.launch {
            userRepository.getOfficerByName(name)
                .toUiState()
                .collect { _search.value = it }
        }
    }

    fun getParentByNameAndRegion( name: String, region: String) {
        _search.value = UiState.Loading
        viewModelScope.launch {
            userRepository.getParentByNameAndRegion(name, region)
                .toUiState()
                .collect { _search.value = it }
        }
    }
}