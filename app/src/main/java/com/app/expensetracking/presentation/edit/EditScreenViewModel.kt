package com.app.expensetracking.presentation.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.expensetracking.domain.usecase.expense.EditExpenseUseCase
import com.app.expensetracking.domain.usecase.expense.GetExpenseByIdUseCase
import com.app.expensetracking.domain.model.Expense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditScreenViewModel @Inject constructor(
    private val getExpenseByIdUseCase: GetExpenseByIdUseCase,
    private val editExpenseUseCase: EditExpenseUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _expense = MutableStateFlow<Expense?>(null)
    val expense: StateFlow<Expense?> = _expense.asStateFlow()

    fun loadExpense(expenseId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            getExpenseByIdUseCase(expenseId).fold(
                onSuccess = { expense ->
                    _expense.value = expense
                    _isLoading.value = false
                },
                onFailure = { error ->
                    _isLoading.value = false
                }
            )
        }
    }


    fun editExpense(expense: Expense, onComplete: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = editExpenseUseCase(expense)
            onComplete(result)
        }
    }
}