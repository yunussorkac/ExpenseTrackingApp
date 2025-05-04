package com.app.expensetracking.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.expensetracking.domain.usecase.auth.LoginUseCase
import com.app.expensetracking.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    fun login(email: String, password: String,onResult: (Result<User>) -> Unit) {
        viewModelScope.launch {
            val result = loginUseCase(email, password)
            onResult(result)
        }
    }
}