package com.project.labs.nutrigrow.ui.screen.validation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.ZScoreCheckModel
import com.project.labs.nutrigrow.data.model.ZScoreSample
import com.project.labs.nutrigrow.data.repository.GrowthRepository
import com.project.labs.nutrigrow.data.repository.UserRepository
import com.project.labs.nutrigrow.ui.state.UiState
import com.project.labs.nutrigrow.ui.state.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ValidationViewModel(
    private val userRepository: UserRepository,
    private val growthRepository: GrowthRepository,
) : ViewModel() {

    private val _isAuthenticated: MutableState<UiState<AuthModel>> = mutableStateOf(UiState.Loading)
    val isAuthenticated: MutableState<UiState<AuthModel>>
        get() = _isAuthenticated

    private val _samples: MutableStateFlow<UiState<List<ZScoreCheckModel>>> =
        MutableStateFlow(UiState.Loading)
    val samples: StateFlow<UiState<List<ZScoreCheckModel>>>
        get() = _samples

    private val _manual: MutableStateFlow<UiState<ZScoreCheckModel>?> = MutableStateFlow(null)
    val manual: StateFlow<UiState<ZScoreCheckModel>?>
        get() = _manual

    private val _fieldErrors: MutableState<Map<String, String>> = mutableStateOf(emptyMap())
    val fieldErrors: MutableState<Map<String, String>>
        get() = _fieldErrors

    private val _sampleSet: MutableState<ValidationSampleSet?> = mutableStateOf(null)
    val sampleSet: MutableState<ValidationSampleSet?>
        get() = _sampleSet

    private val _importError: MutableState<String?> = mutableStateOf(null)
    val importError: MutableState<String?>
        get() = _importError

    private val _form: MutableState<CalculatorForm> = mutableStateOf(CalculatorForm())
    val form: MutableState<CalculatorForm>
        get() = _form

    fun checkAuthentication() {
        viewModelScope.launch {
            _isAuthenticated.value = UiState.Success(userRepository.getVerified())
        }
    }

    fun clearFieldError(field: String) {
        if (_fieldErrors.value.containsKey(field)) {
            _fieldErrors.value = _fieldErrors.value - field
        }
    }

    fun setFieldErrors(errors: Map<String, String>) {
        _fieldErrors.value = errors
    }

    fun runSamples(samples: List<ZScoreSample>) {
        _samples.value = UiState.Loading
        viewModelScope.launch {
            growthRepository.checkZScoreBatch(samples)
                .toUiState()
                .collect { _samples.value = it }
        }
    }

    fun calculate(sample: ZScoreSample) {
        _manual.value = UiState.Loading
        viewModelScope.launch {
            growthRepository.checkZScoreBatch(listOf(sample))
                .toUiState()
                .collect { state ->
                    _manual.value = when (state) {
                        is UiState.Success -> state.data.firstOrNull()
                            ?.let { UiState.Success(it) }
                            ?: UiState.Error("Hasil perhitungan tidak diterima")
                        is UiState.Error -> UiState.Error(state.errorMessage)
                        is UiState.Unauthorized -> UiState.Unauthorized
                        else -> UiState.Loading
                    }
                }
        }
    }

    fun updateForm(form: CalculatorForm) {
        _form.value = form
    }

    fun applySampleSet(set: ValidationSampleSet) {
        _sampleSet.value = set
        _importError.value = null
        resetSamples()
    }

    fun failImport(message: String) {
        _importError.value = message
    }

    fun clearSampleSet() {
        _sampleSet.value = null
        _importError.value = null
        resetSamples()
    }

    fun resetSamples() {
        _samples.value = UiState.Loading
    }

    fun resetManual() {
        _manual.value = null
    }
}
