package com.project.labs.nutrigrow.ui.state

import com.project.labs.nutrigrow.data.remote.response.TemplateResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

fun <T : Any> Flow<TemplateResponse<T>>.toUiState(): Flow<UiState<T>> =
    map { response ->
        when {
            response.success -> UiState.Success(response.data)
            response.message == "Unauthorized" -> UiState.Unauthorized
            else -> UiState.Error(response.message)
        }
    }.catch { cause ->
        emit(UiState.Error(cause.message.toString()))
    }
