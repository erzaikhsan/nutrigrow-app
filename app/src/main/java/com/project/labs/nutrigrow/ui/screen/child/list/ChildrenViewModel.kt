package com.project.labs.nutrigrow.ui.screen.child.list

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.ChildrenModel
import com.project.labs.nutrigrow.data.model.GraduateModel
import com.project.labs.nutrigrow.data.model.UserModel
import com.project.labs.nutrigrow.data.repository.ChildRepository
import com.project.labs.nutrigrow.data.repository.UserRepository
import com.project.labs.nutrigrow.ui.state.UiState
import com.project.labs.nutrigrow.ui.state.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChildrenViewModel(
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

    private val _graduate: MutableState<UiState<GraduateModel>> = mutableStateOf(UiState.Unauthorized)
    val graduate: MutableState<UiState<GraduateModel>>
        get() = _graduate

    fun graduateChildren() {
        _graduate.value = UiState.Loading
        viewModelScope.launch {
            childRepository.graduateChildren()
                .toUiState()
                .collect { _graduate.value = it }
        }
    }

    fun resetGraduateState() {
        _graduate.value = UiState.Unauthorized
    }

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

    fun getChildrenByRegion( region: String ) {
        _children.value = UiState.Loading
        viewModelScope.launch {
            childRepository.getChildrenByRegion(region)
                .toUiState()
                .collect { _children.value = it }
        }
    }

    fun getAllChildren() {
        _children.value = UiState.Loading
        viewModelScope.launch {
            childRepository.getAllChildren()
                .toUiState()
                .collect { _children.value = it }
        }
    }

    fun getChildrenByNameAndRegion( name: String, region: String ) {
        _search.value = UiState.Loading
        viewModelScope.launch {
            childRepository.getChildrenByNameAndRegion(name, region)
                .toUiState()
                .collect { _search.value = it }
        }
    }

    fun getChildrenByName( name: String) {
        _search.value = UiState.Loading
        viewModelScope.launch {
            childRepository.getChildrenByName(name)
                .toUiState()
                .collect { _search.value = it }
        }
    }

    fun getChildrenByParent( id: String ) {
        _search.value = UiState.Loading
        viewModelScope.launch {
            childRepository.getChildrenByParent(id)
                .toUiState()
                .collect { _search.value = it }
        }
    }
}