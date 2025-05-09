package com.app.expensetracking.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.expensetracking.data.local.BooleanDataStore
import com.app.expensetracking.data.local.StringDataStore
import com.app.expensetracking.domain.usecase.auth.GetUserUseCase
import com.app.expensetracking.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val getUserUseCase: GetUserUseCase,
    private val booleanDataStore: BooleanDataStore,
    private val stringDataStore: StringDataStore
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> get() = _isDarkMode.asStateFlow()

    private val _selectedCurrency = MutableStateFlow("")
    val selectedCurrency: StateFlow<String> = _selectedCurrency.asStateFlow()

    init {
        isDarkMode()
    }


    fun loadCurrency() {
        viewModelScope.launch {
            stringDataStore.getString("currency", "").collect {
                _selectedCurrency.value = it
            }
        }
    }

    fun setCurrency(currency: String) {
        viewModelScope.launch {
            stringDataStore.saveString("currency", currency)
            _selectedCurrency.value = currency
        }
    }

    fun isDarkMode() {
        viewModelScope.launch {
            booleanDataStore.getBoolean("isDarkMode", false).collect {
                _isDarkMode.value = it
            }
        }
    }

    fun setDarkMode(isDarkMode : Boolean){
        viewModelScope.launch {
            booleanDataStore.saveBoolean("isDarkMode",isDarkMode)
        }
    }

    fun getUser() {
        viewModelScope.launch {
            getUserUseCase()
                .collect { result ->
                    if (result.isSuccess) {
                        _user.value = result.getOrNull()
                    } else {
                        _user.value = null
                    }
                }
        }
    }


    fun logout() {
        viewModelScope.launch {
            auth.signOut()
        }
    }
}