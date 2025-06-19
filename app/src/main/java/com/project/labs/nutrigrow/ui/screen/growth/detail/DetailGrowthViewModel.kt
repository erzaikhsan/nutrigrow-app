package com.project.labs.nutrigrow.ui.screen.growth.detail

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
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
                .catch {
                    _child.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    try {
                        if (!data.success) {
                            if (data.message == "Unauthorized") {
                                _child.value = UiState.Unauthorized
                                return@collect
                            }
                            _child.value = UiState.Error(data.message)
                            return@collect
                        }
                        _child.value = UiState.Success(data.data)
                    } catch (e: Exception) {
                        _child.value = UiState.Error(e.message.toString())
                    }
                }
        }
    }

    fun getGrowthByChildId(id: String) {
        _growth.value = UiState.Loading
        viewModelScope.launch {
            growthRepository.getGrowthByChildId(id)
                .catch {
                    _growth.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    try {
                        if (!data.success) {
                            if (data.message == "Unauthorized") {
                                _growth.value = UiState.Unauthorized
                                return@collect
                            }
                            _growth.value = UiState.Error(data.message)
                            return@collect
                        }
                        _growth.value = UiState.Success(data.data)
                    } catch (e: Exception) {
                        _growth.value = UiState.Error(e.message.toString())
                    }
                }
        }
    }
}