package com.project.labs.nutrigrow.ui.screen.article

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.labs.nutrigrow.data.model.ArticleModel
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.dummy.DummyData
import com.project.labs.nutrigrow.data.repository.UserRepository
import com.project.labs.nutrigrow.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ArticleViewModel(
    private val userRepository: UserRepository,
): ViewModel() {

    private val _article: MutableStateFlow<UiState<List<ArticleModel>>> = MutableStateFlow(UiState.Loading)
    val article: StateFlow<UiState<List<ArticleModel>>>
        get() = _article

    private val _isAuthenticated: MutableState<UiState<AuthModel>> = mutableStateOf(UiState.Loading)
    val isAuthenticated: MutableState<UiState<AuthModel>>
        get() = _isAuthenticated

    fun checkAuthentication() {
        viewModelScope.launch {
            _isAuthenticated.value = UiState.Success(userRepository.getVerified())
        }
    }

    fun getArticle() {
        _article.value = UiState.Success(DummyData.artikelDummy)
    }
}