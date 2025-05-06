package com.app.expensetracking.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.expensetracking.domain.usecase.expense.GetMonthlyCategoryTotalsUseCase
import com.app.expensetracking.domain.usecase.expense.GetMonthlyDailyExpensesUseCase
import com.app.expensetracking.model.ExpenseCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartViewModel @Inject constructor(
    private val getMonthlyCategoryTotalsUseCase: GetMonthlyCategoryTotalsUseCase,
    private val getMonthlyDailyExpensesUseCase: GetMonthlyDailyExpensesUseCase
) : ViewModel() {

    private val _categoryTotals = MutableStateFlow<Map<ExpenseCategory, Double>>(emptyMap())
    val categoryTotals: StateFlow<Map<ExpenseCategory, Double>> = _categoryTotals.asStateFlow()

    private val _dailyExpenses = MutableStateFlow<Map<String, Double>>(emptyMap())
    val dailyExpenses: StateFlow<Map<String, Double>> = _dailyExpenses.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()



    init {
        loadChartData()
    }

    fun loadChartData() {
        _isLoading.value = true

        viewModelScope.launch {
            getMonthlyCategoryTotalsUseCase()
                .catch { e ->
                    _isLoading.value = false
                }
                .collect { totals ->
                    _categoryTotals.value = totals
                    _isLoading.value = false
                }
        }

        viewModelScope.launch {
            getMonthlyDailyExpensesUseCase()
                .catch { e ->
                    _isLoading.value = false
                }
                .collect { dailyData ->
                    _dailyExpenses.value = dailyData
                    _isLoading.value = false
                }
        }
    }


}