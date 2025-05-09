package com.app.expensetracking.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.expensetracking.data.local.StringDataStore
import com.app.expensetracking.domain.usecase.auth.LoginUseCase
import com.app.expensetracking.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val stringDataStore: StringDataStore
) : ViewModel() {

    private val _selectedCurrency = MutableStateFlow("")
    val selectedCurrency: StateFlow<String> = _selectedCurrency.asStateFlow()


    fun login(email: String, password: String,onResult: (Result<User>) -> Unit) {
        viewModelScope.launch {
            val result = loginUseCase(email, password)
            onResult(result)
        }
    }

    fun setCurrency(currency: String) {
        viewModelScope.launch {
            stringDataStore.saveString("currency", currency)
            _selectedCurrency.value = currency
        }
    }

}