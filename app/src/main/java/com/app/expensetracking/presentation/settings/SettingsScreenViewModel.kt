package com.app.expensetracking.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.expensetracking.domain.usecase.auth.GetUserUseCase
import com.app.expensetracking.model.User
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
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()


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