package com.project.labs.nutrigrow.ui.screen.child

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.ChildrenModel
import com.project.labs.nutrigrow.data.model.GrowthModel
import com.project.labs.nutrigrow.data.model.VaccineModel
import com.project.labs.nutrigrow.data.repository.ChildRepository
import com.project.labs.nutrigrow.data.repository.GrowthRepository
import com.project.labs.nutrigrow.data.repository.UserRepository
import com.project.labs.nutrigrow.data.repository.VaccineRepository
import com.project.labs.nutrigrow.ui.state.UiState
import com.project.labs.nutrigrow.ui.state.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChildProfileViewModel (
    private val userRepository: UserRepository,
    private val childRepository: ChildRepository,
    private val growthRepository: GrowthRepository,
    private val vaccineRepository: VaccineRepository
): ViewModel() {
    private val _child: MutableStateFlow<UiState<ChildrenModel>> = MutableStateFlow(UiState.Loading)
    val child: StateFlow<UiState<ChildrenModel>>
        get() = _child

    private val _growth: MutableStateFlow<UiState<GrowthModel>> = MutableStateFlow(UiState.Loading)
    val growth: StateFlow<UiState<GrowthModel>>
        get() = _growth

    private val _vaccine: MutableStateFlow<UiState<List<VaccineModel>>> = MutableStateFlow(UiState.Loading)
    val vaccine: StateFlow<UiState<List<VaccineModel>>>
        get() = _vaccine

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

    fun getLastGrowth(id: String) {
        _growth.value = UiState.Loading
        viewModelScope.launch {
            growthRepository.getlastGrowth(id)
                .toUiState()
                .collect { _growth.value = it }
        }
    }

    fun getVaccineByChildId(id: String) {
        _vaccine.value = UiState.Loading
        viewModelScope.launch {
            vaccineRepository.getVaccineByChildId(id)
                .toUiState()
                .collect { _vaccine.value = it }
        }
    }
}