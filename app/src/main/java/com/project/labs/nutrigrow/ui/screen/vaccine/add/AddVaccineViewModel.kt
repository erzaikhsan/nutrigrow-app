package com.project.labs.nutrigrow.ui.screen.vaccine.add

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.ChildrenModel
import com.project.labs.nutrigrow.data.model.VaccineModel
import com.project.labs.nutrigrow.data.repository.ChildRepository
import com.project.labs.nutrigrow.data.repository.UserRepository
import com.project.labs.nutrigrow.data.repository.VaccineRepository
import com.project.labs.nutrigrow.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

class AddVaccineViewModel (
    private val userRepository: UserRepository,
    private val childRepository: ChildRepository,
    private val vaccineRepository: VaccineRepository
): ViewModel() {
    private val _child: MutableStateFlow<UiState<ChildrenModel>> = MutableStateFlow(UiState.Loading)
    val child: StateFlow<UiState<ChildrenModel>>
        get() = _child

    private val _vaccine: MutableState<UiState<VaccineModel>> = mutableStateOf(UiState.Unauthorized)
    val vaccine: MutableState<UiState<VaccineModel>>
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

    fun addVaccine(  children_id: String, date: String, vaccine_name: String) {
        if ( children_id.isEmpty() || date.isEmpty() || vaccine_name.isEmpty()) {
            _vaccine.value = UiState.Error("Pastikan Semua Data\nDiisi Dengan Benar")
            return
        }
        _vaccine.value = UiState.Loading
        viewModelScope.launch {
            vaccineRepository.addVaccine( children_id, reformatDate(date), vaccine_name).catch {
                _vaccine.value = UiState.Error(it.message.toString())
            }.collect { data ->
                try {
                    if (!data.success) {
                        if (data.message == "Already exists") {
                            _vaccine.value = UiState.Error("Data Vaksin  Sudah Ada.")
                            return@collect
                        }
                        if (data.message == "Bad request") {
                            _vaccine.value = UiState.Error("Masukan Tidak Sah.\nMohon Periksa Masukan Anda.\nPastikan Anda Memasukan Data Vaksin Dengan Benar.")
                            return@collect
                        }
                        _vaccine.value = UiState.Error(data.message)
                        return@collect
                    }
                    _vaccine.value = UiState.Success(data.data)
                } catch (e: Exception) {
                    _vaccine.value = UiState.Error(e.message.toString())
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