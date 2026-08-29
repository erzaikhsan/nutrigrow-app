package com.project.labs.nutrigrow.ui.screen.growth.detail

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.ChildrenModel
import com.project.labs.nutrigrow.data.model.GrowthModel
import com.project.labs.nutrigrow.data.repository.ChildRepository
import com.project.labs.nutrigrow.data.repository.GrowthRepository
import com.project.labs.nutrigrow.data.repository.UserRepository
import com.project.labs.nutrigrow.ui.state.UiState
import com.project.labs.nutrigrow.ui.state.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailGrowthViewModel (
    private val userRepository: UserRepository,
    private val childRepository: ChildRepository,
    private val growthRepository: GrowthRepository,
): ViewModel() {
    private val _child: MutableStateFlow<UiState<ChildrenModel>> = MutableStateFlow(UiState.Loading)
    val child: StateFlow<UiState<ChildrenModel>>
        get() = _child

    private val _growth: MutableStateFlow<UiState<List<GrowthModel>>> = MutableStateFlow(UiState.Loading)
    val growth: StateFlow<UiState<List<GrowthModel>>>
        get() = _growth

    private val _currentGrowth: MutableState<UiState<List<GrowthModel>>> = mutableStateOf(UiState.Unauthorized)
    val currentGrowth: MutableState<UiState<List<GrowthModel>>>
        get() = _currentGrowth

    private val _isAuthenticated: MutableState<UiState<AuthModel>> = mutableStateOf(UiState.Loading)
    val isAuthenticated: MutableState<UiState<AuthModel>>
        get() = _isAuthenticated

    fun checkAuthentication() {
        viewModelScope.launch {
            _isAuthenticated.value = UiState.Success(userRepository.getVerified())
        }
    }

    fun getChildProfile(id: String) {
        _child.value = UiState.Loading
        viewModelScope.launch {
            childRepository.getChildProfile(id)
                .toUiState()
                .collect { _child.value = it }
        }
    }

    fun getGrowthByChildId(id: String) {
        _currentGrowth.value = UiState.Loading
        viewModelScope.launch {
            growthRepository.getGrowthByChildId(id)
                .toUiState()
                .collect { _currentGrowth.value = it }
        }
    }

    fun getGrowthByChildIdInYear(id: String, year: Number) {
        _growth.value = UiState.Loading
        viewModelScope.launch {
            growthRepository.getGrowthByChildIdInYear(id, year)
                .toUiState()
                .collect { _growth.value = it }
        }
    }
}