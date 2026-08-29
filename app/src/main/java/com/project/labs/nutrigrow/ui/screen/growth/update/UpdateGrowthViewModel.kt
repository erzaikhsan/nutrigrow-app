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
import com.project.labs.nutrigrow.ui.state.toUiState
import com.project.labs.nutrigrow.utils.ARM_RANGE
import com.project.labs.nutrigrow.utils.FIELD_ARM
import com.project.labs.nutrigrow.utils.FIELD_DATE
import com.project.labs.nutrigrow.utils.FIELD_HEAD
import com.project.labs.nutrigrow.utils.FIELD_HEIGHT
import com.project.labs.nutrigrow.utils.FIELD_WEIGHT
import com.project.labs.nutrigrow.utils.HEAD_RANGE
import com.project.labs.nutrigrow.utils.HEIGHT_RANGE
import com.project.labs.nutrigrow.utils.WEIGHT_RANGE
import com.project.labs.nutrigrow.utils.parseMeasurement
import com.project.labs.nutrigrow.utils.validateMeasurement
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
                .toUiState()
                .collect { _child.value = it }
        }
    }

    fun getGrowthById(id: String) {
        _growth.value = UiState.Loading
        viewModelScope.launch {
            growthRepository.getGrowthById(id)
                .toUiState()
                .collect { _growth.value = it }
        }
    }

    private val _fieldErrors: MutableState<Map<String, String>> = mutableStateOf(emptyMap())
    val fieldErrors: MutableState<Map<String, String>>
        get() = _fieldErrors

    fun clearFieldError(field: String) {
        if (_fieldErrors.value.containsKey(field)) {
            _fieldErrors.value = _fieldErrors.value - field
        }
    }

    fun updateGrowth( id: String, children_id: String, date: String, weight: String, height: String, head_circum: String, arm_circum: String, note: String) {
        if (children_id.isEmpty()) {
            _newGrowth.value = UiState.Error("Data balita tidak terbaca.\nBuka ulang halaman ini.")
            return
        }

        val errors = mutableMapOf<String, String>()

        val weighedOn = runCatching {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("d/M/yyyy"))
        }.getOrNull()

        when {
            date.isBlank() -> errors[FIELD_DATE] = "Tanggal penimbangan belum dipilih."
            weighedOn == null -> errors[FIELD_DATE] = "Tanggal penimbangan tidak terbaca."
            weighedOn.isAfter(LocalDate.now()) -> errors[FIELD_DATE] = "Tanggal penimbangan tidak boleh melewati hari ini."
        }

        validateMeasurement(weight, WEIGHT_RANGE)?.let { errors[FIELD_WEIGHT] = it }
        validateMeasurement(height, HEIGHT_RANGE)?.let { errors[FIELD_HEIGHT] = it }
        validateMeasurement(head_circum, HEAD_RANGE)?.let { errors[FIELD_HEAD] = it }
        validateMeasurement(arm_circum, ARM_RANGE)?.let { errors[FIELD_ARM] = it }

        _fieldErrors.value = errors

        if (errors.isNotEmpty()) {
            _newGrowth.value = UiState.Error(
                if (errors.size == 1) "Ada 1 isian yang perlu diperbaiki."
                else "Ada ${errors.size} isian yang perlu diperbaiki."
            )
            return
        }

        val weightValue = parseMeasurement(weight) ?: return
        val heightValue = parseMeasurement(height) ?: return
        val headValue = parseMeasurement(head_circum) ?: return
        val armValue = parseMeasurement(arm_circum) ?: return

        _newGrowth.value = UiState.Loading
        viewModelScope.launch {
            growthRepository.updateGrowth( id,  children_id, reformatDate(date), weightValue, heightValue, headValue, armValue, note.ifEmpty { "Tidak ada catatan" }).catch {
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
                .toUiState()
                .collect { _delete.value = it }
        }
    }

    fun resetNewGrowthState() {
        _newGrowth.value = UiState.Loading
    }

    fun resetDeleteState() {
        _delete.value = UiState.Loading
    }

    fun reformatDate(inputDate: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS XXX", Locale.ENGLISH)

        val localDate = LocalDate.parse(inputDate, inputFormatter)
        val zonedDateTime = localDate.atTime(17, 0).atZone(ZoneOffset.UTC)
        return zonedDateTime.format(outputFormatter)
    }
}