package com.app.expensetracking.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.expensetracking.domain.usecase.expense.DeleteExpenseUseCase
import com.app.expensetracking.domain.usecase.expense.GetExpensesUseCase
import com.app.expensetracking.domain.usecase.expense.SearchExpenseUseCase
import com.app.expensetracking.domain.model.Expense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListScreenViewModel @Inject constructor(
    private val getExpensesFlowUseCase: GetExpensesUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val searchExpensesUseCase: SearchExpenseUseCase

) : ViewModel() {

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery


    fun getExpenses() {
        viewModelScope.launch {
            getExpensesFlowUseCase()
                .collect { expenseList ->
                    _expenses.value = expenseList
                }
        }
    }

    fun deleteExpense(expense: Expense, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = deleteExpenseUseCase(expense)
            onResult(result)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query

        viewModelScope.launch {
            if (query.isBlank()) {
                getExpenses()
            } else {
                searchExpensesUseCase(query).collect { result ->
                    _expenses.value = result
                }
            }
        }
    }

}