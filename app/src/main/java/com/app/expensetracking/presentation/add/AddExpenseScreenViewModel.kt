package com.app.expensetracking.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.expensetracking.domain.usecase.expense.AddExpenseUseCase
import com.app.expensetracking.domain.model.Expense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddExpenseScreenViewModel @Inject constructor(
    private val addExpenseUseCase: AddExpenseUseCase
) : ViewModel() {



    fun addExpense(expense: Expense, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = addExpenseUseCase(expense)
            onResult(result)
        }
    }
}