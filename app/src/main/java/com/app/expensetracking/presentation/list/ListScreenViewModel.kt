package com.app.expensetracking.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.expensetracking.domain.usecase.expense.GetExpensesUseCase
import com.app.expensetracking.model.Expense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListScreenViewModel @Inject constructor(
    private val getExpensesFlowUseCase: GetExpensesUseCase
) : ViewModel() {

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses



    fun getExpenses() {
        viewModelScope.launch {
            getExpensesFlowUseCase()
                .collect { expenseList ->
                    _expenses.value = expenseList
                }
        }
    }
}