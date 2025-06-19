package com.project.labs.nutrigrow.ui.screen.history

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.CheckModel
import com.project.labs.nutrigrow.data.model.UserModel
import com.project.labs.nutrigrow.data.repository.CheckUpRepository
import com.project.labs.nutrigrow.data.repository.UserRepository
import com.project.labs.nutrigrow.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val userRepository: UserRepository,
    private val checkUpRepository: CheckUpRepository
): ViewModel() {

    private val _user: MutableStateFlow<UiState<UserModel>> = MutableStateFlow(UiState.Loading)
    val user: StateFlow<UiState<UserModel>>
        get() = _user

    private val _isAuthenticated: MutableState<UiState<AuthModel>> = mutableStateOf(UiState.Loading)
    val isAuthenticated: MutableState<UiState<AuthModel>>
        get() = _isAuthenticated

    private val _check: MutableStateFlow<UiState<List<CheckModel>>> = MutableStateFlow(UiState.Loading)
    val check: StateFlow<UiState<List<CheckModel>>>
        get() = _check

    fun checkAuthentication() {
        viewModelScope.launch {
            _isAuthenticated.value = UiState.Success(userRepository.getVerified())
        }
    }

    fun getUserParent() {
        _user.value = UiState.Loading
        viewModelScope.launch {
            userRepository.getParentProfile()
                .catch {
                    _user.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
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

    fun getCheckUpByParentId(parents_id: String) {
        _check.value = UiState.Loading
        viewModelScope.launch {
            checkUpRepository.getCheckUpByParentId(parents_id)
                .catch {
                    _check.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    try {
                        if (!data.success) {
                            if (data.message == "Bad Request") {
                                _check.value = UiState.Error("Anda Tidak Memiliki Riwayat Check Up.\nSilahkan Coba Lagi")
                                return@collect
                            }
                            _check.value = UiState.Error(data.message)
                            return@collect
                        }
                        _check.value = UiState.Success(data.data)
                    } catch (e: Exception) {
                        _check.value = UiState.Error(e.message.toString())
                    }
                }
        }
    }

}