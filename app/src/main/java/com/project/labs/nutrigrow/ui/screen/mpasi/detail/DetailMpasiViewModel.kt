package com.project.labs.nutrigrow.ui.screen.mpasi.detail

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.MpasiModel
import com.project.labs.nutrigrow.data.model.dummy.DummyData
import com.project.labs.nutrigrow.data.repository.UserRepository
import com.project.labs.nutrigrow.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailMpasiViewModel(
    private val userRepository: UserRepository,
): ViewModel() {

    private val _mpasi: MutableStateFlow<UiState<MpasiModel>> = MutableStateFlow(UiState.Loading)
    val mpasi: StateFlow<UiState<MpasiModel>>
        get() = _mpasi

    private val _isAuthenticated: MutableState<UiState<AuthModel>> = mutableStateOf(UiState.Loading)
    val isAuthenticated: MutableState<UiState<AuthModel>>
        get() = _isAuthenticated

    fun checkAuthentication() {
        viewModelScope.launch {
            _isAuthenticated.value = UiState.Success(userRepository.getVerified())
        }
    }

    fun getMpasiById(id: Int, group: String) {
        when(group){
            "68Bulan" -> {
                _mpasi.value = UiState.Success(DummyData.mpasi68Dummy[id])
            }
            "911Bulan" -> {
                _mpasi.value = UiState.Success(DummyData.mpasi911Dummy[id])
            }
            "1223Bulan" -> {
                _mpasi.value = UiState.Success(DummyData.mpasi1223Dummy[id])
            }
            "25Tahun" -> {
                _mpasi.value = UiState.Success(DummyData.mpasi25Dummy[id])
            }
            else -> { }
        }
    }
}