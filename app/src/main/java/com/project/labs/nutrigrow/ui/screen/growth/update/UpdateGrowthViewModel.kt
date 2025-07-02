package com.project.labs.nutrigrow.ui.screen.growth.update

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

class UpdateGrowthViewModel (
    private val userRepository: UserRepository,
    private val childRepository: ChildRepository,
    private val growthRepository: GrowthRepository
): ViewModel() {
    private val _child: MutableStateFlow<UiState<ChildrenModel>> = MutableStateFlow(UiState.Loading)
    val child: StateFlow<UiState<ChildrenModel>>
        get() = _child

    private val _growth: MutableStateFlow<UiState<GrowthModel>> = MutableStateFlow(UiState.Loading)
    val growth: StateFlow<UiState<GrowthModel>>
        get() = _growth

    private val _newGrowth: MutableState<UiState<GrowthModel>> = mutableStateOf(UiState.Unauthorized)
    val newGrowth: MutableState<UiState<GrowthModel>>
        get() = _newGrowth

    private val _delete: MutableState<UiState<GrowthModel>> = mutableStateOf(UiState.Unauthorized)
    val delete: MutableState<UiState<GrowthModel>>
        get() = _delete

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

    fun getGrowthById(id: String) {
        _growth.value = UiState.Loading
        viewModelScope.launch {
            growthRepository.getGrowthById(id)
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

    fun updateGrowth( id: String, children_id: String, date: String, weight: String, height: String, head_circum: String, arm_circum: String, note: String) {
        if ( children_id.isEmpty() || date.isEmpty() || weight.isEmpty() || height.isEmpty() || head_circum.isEmpty() || arm_circum.isEmpty()) {
            _newGrowth.value = UiState.Error("Pastikan Semua Data\nDiisi Dengan Benar")
            return
        }
        _newGrowth.value = UiState.Loading
        viewModelScope.launch {
            growthRepository.updateGrowth( id,  children_id, reformatDate(date), weight.toDouble(), height.toDouble(), head_circum.toDouble(), arm_circum.toDouble(), note.ifEmpty { "Tidak ada catatan" }).catch {
                _newGrowth.value = UiState.Error(it.message.toString())
            }.collect { data ->
                try {
                    if (!data.success) {
                        if (data.message == "Not found") {
                            _newGrowth.value = UiState.Error("Data Penimbangan Bulan Ini Sudah Ada.")
                            return@collect
                        }
                        if (data.message == "Bad request") {
                            _newGrowth.value = UiState.Error("Masukan Tidak Sah.\nMohon Periksa Masukan Anda.\nPastikan Anda Memasukan Data Penimbangan Dengan Benar.")
                            return@collect
                        }
                        _newGrowth.value = UiState.Error(data.message)
                        return@collect
                    }
                    _newGrowth.value = UiState.Success(data.data)
                } catch (e: Exception) {
                    _newGrowth.value = UiState.Error(e.message.toString())
                }
            }
        }
    }

    fun deleteGrowth(id: String) {
        _delete.value = UiState.Loading
        viewModelScope.launch {
            growthRepository.deleteGrowth(id)
                .catch {
                    _delete.value = UiState.Error(it.message.toString())
                }
                .collect { data ->
                    try {
                        if (!data.success) {
                            if (data.message == "Unauthorized") {
                                _delete.value = UiState.Unauthorized
                                return@collect
                            }
                            _delete.value = UiState.Error(data.message)
                            return@collect
                        }
                        _delete.value = UiState.Success(data.data)
                    } catch (e: Exception) {
                        _delete.value = UiState.Error(e.message.toString())
                    }
                }
        }
    }

    fun reformatDate(inputDate: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS XXX", Locale.ENGLISH)

        val localDate = LocalDate.parse(inputDate, inputFormatter)
        val zonedDateTime = localDate.atTime(17, 0).atZone(ZoneOffset.UTC)
        return zonedDateTime.format(outputFormatter)
    }
}