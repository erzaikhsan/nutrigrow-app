package com.project.labs.nutrigrow.ui.screen.child.add

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
import com.project.labs.nutrigrow.utils.BIRTH_HEAD_RANGE
import com.project.labs.nutrigrow.utils.BIRTH_HEIGHT_RANGE
import com.project.labs.nutrigrow.utils.BIRTH_WEIGHT_RANGE
import com.project.labs.nutrigrow.utils.FIELD_BIRTH_HEAD
import com.project.labs.nutrigrow.utils.FIELD_BIRTH_HEIGHT
import com.project.labs.nutrigrow.utils.FIELD_BIRTH_WEIGHT
import com.project.labs.nutrigrow.utils.FIELD_DOB
import com.project.labs.nutrigrow.utils.FIELD_FATHER
import com.project.labs.nutrigrow.utils.FIELD_GENDER
import com.project.labs.nutrigrow.utils.FIELD_MOTHER
import com.project.labs.nutrigrow.utils.FIELD_NAME
import com.project.labs.nutrigrow.utils.FIELD_ORDER
import com.project.labs.nutrigrow.utils.FIELD_PLACE
import com.project.labs.nutrigrow.utils.FIELD_REGION
import com.project.labs.nutrigrow.utils.parseMeasurement
import com.project.labs.nutrigrow.utils.requireText
import com.project.labs.nutrigrow.utils.validateMeasurement
import com.project.labs.nutrigrow.utils.validateOrderOfChild
import com.project.labs.nutrigrow.ui.state.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

class AddChildViewModel (
    private val userRepository: UserRepository,
    private val childRepository: ChildRepository,
): ViewModel() {
    private val _child: MutableState<UiState<ChildrenModel>> = mutableStateOf(UiState.Unauthorized)
    val child: MutableState<UiState<ChildrenModel>>
        get() = _child

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

    private val _fieldErrors: MutableState<Map<String, String>> = mutableStateOf(emptyMap())
    val fieldErrors: MutableState<Map<String, String>>
        get() = _fieldErrors

    fun clearFieldError(field: String) {
        if (_fieldErrors.value.containsKey(field)) {
            _fieldErrors.value = _fieldErrors.value - field
        }
    }

    fun addChild( full_name: String, gender: String, place_of_birth: String, date_of_birth: String, father: String, mother: String, order_of_child: String, region: String, birth_weight: String, birth_height: String, birth_head_circum: String) {
        val errors = mutableMapOf<String, String>()

        requireText(full_name, "Nama lengkap")?.let { errors[FIELD_NAME] = it }
        requireText(gender, "Jenis kelamin")?.let { errors[FIELD_GENDER] = it }
        requireText(place_of_birth, "Tempat lahir")?.let { errors[FIELD_PLACE] = it }
        requireText(father, "Nama ayah")?.let { errors[FIELD_FATHER] = it }
        requireText(mother, "Nama ibu")?.let { errors[FIELD_MOTHER] = it }
        requireText(region, "Wilayah")?.let { errors[FIELD_REGION] = it }
        validateOrderOfChild(order_of_child)?.let { errors[FIELD_ORDER] = it }

        val bornOn = runCatching {
            LocalDate.parse(date_of_birth, DateTimeFormatter.ofPattern("d/M/yyyy"))
        }.getOrNull()

        when {
            date_of_birth.isBlank() -> errors[FIELD_DOB] = "Tanggal lahir belum dipilih."
            bornOn == null -> errors[FIELD_DOB] = "Tanggal lahir tidak terbaca."
            bornOn.isAfter(LocalDate.now()) -> errors[FIELD_DOB] = "Tanggal lahir tidak boleh melewati hari ini."
        }

        validateMeasurement(birth_weight, BIRTH_WEIGHT_RANGE)?.let { errors[FIELD_BIRTH_WEIGHT] = it }
        validateMeasurement(birth_height, BIRTH_HEIGHT_RANGE)?.let { errors[FIELD_BIRTH_HEIGHT] = it }
        validateMeasurement(birth_head_circum, BIRTH_HEAD_RANGE)?.let { errors[FIELD_BIRTH_HEAD] = it }

        _fieldErrors.value = errors

        if (errors.isNotEmpty()) {
            _child.value = UiState.Error(
                if (errors.size == 1) "Ada 1 isian yang perlu diperbaiki."
                else "Ada ${errors.size} isian yang perlu diperbaiki."
            )
            return
        }

        val orderValue = order_of_child.trim().toInt()
        val birthWeightValue = parseMeasurement(birth_weight) ?: return
        val birthHeightValue = parseMeasurement(birth_height) ?: return
        val birthHeadValue = parseMeasurement(birth_head_circum) ?: return

        _child.value = UiState.Loading
        viewModelScope.launch {
            childRepository.addChildren( full_name = full_name, gender = if (gender == "Laki-Laki") "M" else "F", place_of_birth = place_of_birth, date_of_birth = reformatDate(date_of_birth), father = father, mother = mother, order_of_child = orderValue, region = region, birth_weight = birthWeightValue, birth_height = birthHeightValue, birth_head_circum = birthHeadValue ).catch {
                _child.value = UiState.Error(it.message.toString())
            }.collect { data ->
                try {
                    if (!data.success) {
                        if (data.message == "Already exists") {
                            _child.value = UiState.Error("Anak anda sudah terdaftar")
                            return@collect
                        }
                        if (data.message == "Bad request") {
                            _child.value = UiState.Error("Masukan Tidak Sah.\nMohon Periksa Masukan Anda.\nPastikan Anda Memasukan Data Anak Dengan Benar.")
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

    fun resetChildState() {
        _child.value = UiState.Loading
    }

    fun reformatDate(inputDate: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS XXX", Locale.ENGLISH)

        val localDate = LocalDate.parse(inputDate, inputFormatter)
        val zonedDateTime = localDate.atTime(17, 0).atZone(ZoneOffset.UTC)
        return zonedDateTime.format(outputFormatter)
    }
}