package com.project.labs.nutrigrow.ui.screen.child.update

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.ChildrenModel
import com.project.labs.nutrigrow.data.repository.ChildRepository
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

class UpdateChildViewModel (
    private val userRepository: UserRepository,
    private val childRepository: ChildRepository,
): ViewModel() {
    private val _child: MutableStateFlow<UiState<ChildrenModel>> = MutableStateFlow(UiState.Loading)
    val child: StateFlow<UiState<ChildrenModel>>
        get() = _child

    private val _newChild: MutableState<UiState<ChildrenModel>> = mutableStateOf(UiState.Unauthorized)
    val newChild: MutableState<UiState<ChildrenModel>>
        get() = _newChild

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

    fun updateChildren(id: String, full_name: String, gender: String, place_of_birth: String, date_of_birth: String, father : String, mother : String, order_of_child: String, region: String, birth_weight: String, birth_height: String, birth_head_circum: String) {
        if ( full_name.isEmpty() || gender.isEmpty() || date_of_birth.isEmpty() || region.isEmpty() || birth_weight.isEmpty() || birth_height.isEmpty() || birth_head_circum.isEmpty() || place_of_birth.isEmpty() || father.isEmpty() || mother.isEmpty() || order_of_child.isEmpty()) {
            _newChild.value = UiState.Error("Pastikan Semua Data Anak\nDiisi Dengan Benar")
            return
        }

        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
        val dob = LocalDate.parse(date_of_birth, formatter)

        if (dob.isAfter(today)) {
            _newChild.value = UiState.Error("Tanggal Lahir tidak boleh melebihi tanggal hari ini")
            return
        }

        _newChild.value = UiState.Loading
        viewModelScope.launch {
            childRepository.updateChildren( id = id, full_name = full_name, gender = if (gender == "Laki-Laki") "M" else "F", date_of_birth = reformatDate(date_of_birth), father = father, mother = mother, order_of_child = order_of_child.toInt(), region =  region, place_of_birth = place_of_birth, birth_weight = birth_weight.toDouble(), birth_height = birth_height.toDouble(), birth_head_circum = birth_head_circum.toDouble()).catch {
                _newChild.value = UiState.Error(it.message.toString())
            }.collect { data ->
                try {
                    if (!data.success) {
                        if (data.message == "Already exists") {
                            _newChild.value = UiState.Error("Anak anda sudah terdaftar")
                            return@collect
                        }
                        if (data.message == "Bad request") {
                            _newChild.value = UiState.Error("Masukan Tidak Sah.\nMohon Periksa Masukan Anda.\nPastikan Anda Memasukan Data Anak Dengan Benar.")
                            return@collect
                        }
                        _newChild.value = UiState.Error(data.message)
                        return@collect
                    }
                    _newChild.value = UiState.Success(data.data)
                } catch (e: Exception) {
                    _newChild.value = UiState.Error(e.message.toString())
                }
            }
        }
    }

    fun resetNewChild() {
        _newChild.value = UiState.Loading
    }

    fun reformatDate(inputDate: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS XXX", Locale.ENGLISH)

        val localDate = LocalDate.parse(inputDate, inputFormatter)
        val zonedDateTime = localDate.atTime(17, 0).atZone(ZoneOffset.UTC)
        return zonedDateTime.format(outputFormatter)
    }
}
