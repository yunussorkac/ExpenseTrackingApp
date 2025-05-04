package com.app.expensetracking.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.expensetracking.domain.usecase.auth.RegisterUseCase
import com.app.expensetracking.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterScreenViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    fun register(email: String, password: String, onResult: (Result<User>) -> Unit) {
        viewModelScope.launch {
            val result = registerUseCase(email, password)
            onResult(result)
        }
    }
}