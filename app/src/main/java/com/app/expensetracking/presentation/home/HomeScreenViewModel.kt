package com.app.expensetracking.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.expensetracking.domain.usecase.expense.DeleteExpenseUseCase
import com.app.expensetracking.domain.usecase.expense.GetDailyExpenseTotalUseCase
import com.app.expensetracking.domain.usecase.expense.GetMonthlyCategoryTotalsUseCase
import com.app.expensetracking.domain.usecase.expense.GetMonthlyExpenseTotalUseCase
import com.app.expensetracking.domain.usecase.expense.GetRecentExpensesUseCase
import com.app.expensetracking.domain.usecase.expense.GetWeeklyExpenseTotalUseCase
import com.app.expensetracking.model.Expense
import com.app.expensetracking.model.ExpenseCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getDailyExpenseTotalUseCase: GetDailyExpenseTotalUseCase,
    private val getWeeklyExpenseTotalUseCase: GetWeeklyExpenseTotalUseCase,
    private val getMonthlyExpenseTotalUseCase: GetMonthlyExpenseTotalUseCase,
    private val getMonthlyCategoryTotalsUseCase: GetMonthlyCategoryTotalsUseCase,
    private val getRecentExpensesUseCase: GetRecentExpensesUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase
) : ViewModel() {


    private val _dailyTotal = MutableStateFlow(0.0)
    val dailyTotal: StateFlow<Double> = _dailyTotal.asStateFlow()

    private val _weeklyTotal = MutableStateFlow(0.0)
    val weeklyTotal: StateFlow<Double> = _weeklyTotal.asStateFlow()

    private val _monthlyTotal = MutableStateFlow(0.0)
    val monthlyTotal: StateFlow<Double> = _monthlyTotal.asStateFlow()

    private val _categoryTotals = MutableStateFlow<Map<ExpenseCategory, Double>>(emptyMap())
    val categoryTotals: StateFlow<Map<ExpenseCategory, Double>> = _categoryTotals.asStateFlow()

    private val _recentExpenses = MutableStateFlow<List<Expense>>(emptyList())
    val recentExpenses: StateFlow<List<Expense>> = _recentExpenses.asStateFlow()



    fun loadInitialData() {
        viewModelScope.launch {
            launch {
                getDailyExpenseTotalUseCase().collect {
                    _dailyTotal.value = it
                }
            }
            launch {
                getWeeklyExpenseTotalUseCase().collect {
                    _weeklyTotal.value = it
                }
            }
            launch {
                getMonthlyExpenseTotalUseCase().collect {
                    _monthlyTotal.value = it
                }
            }
            launch {
                getMonthlyCategoryTotalsUseCase().collect {
                    _categoryTotals.value = it
                }
            }
            launch {
                getRecentExpensesUseCase().collect {
                    _recentExpenses.value = it
                }
            }
        }
    }

    fun deleteExpense(expense: Expense, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = deleteExpenseUseCase(expense)
            onResult(result)
        }
    }

}