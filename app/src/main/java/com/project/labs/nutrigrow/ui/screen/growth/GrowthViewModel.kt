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
import com.project.labs.nutrigrow.ui.state.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
                    .toUiState()
                    .collect { _user.value = it }
            } else {
                userRepository.getOfficerProfile()
                    .toUiState()
                    .collect { _user.value = it }
            }
        }
    }

    fun getChildrenByParent( id: String ) {
        _children.value = UiState.Loading
        viewModelScope.launch {
            childRepository.getChildrenByParent(id)
                .toUiState()
                .collect { _children.value = it }
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
}