package com.app.expensetracking.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.expensetracking.domain.usecase.currency.CurrencyUseCase
import com.app.expensetracking.domain.usecase.expense.DeleteExpenseUseCase
import com.app.expensetracking.domain.usecase.expense.GetExpensesUseCase
import com.app.expensetracking.domain.model.Expense
import com.app.expensetracking.domain.model.ExpenseCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val currencyUseCase : CurrencyUseCase
) : ViewModel() {


    private val _recentExpenses = MutableStateFlow<List<Expense>>(emptyList())
    val recentExpenses: StateFlow<List<Expense>> = _recentExpenses.asStateFlow()

    private val _dailyTotalConverted = MutableStateFlow<Double?>(null)
    val dailyTotalConverted: StateFlow<Double?> = _dailyTotalConverted.asStateFlow()

    private val _weeklyTotalConverted = MutableStateFlow<Double?>(null)
    val weeklyTotalConverted: StateFlow<Double?> = _weeklyTotalConverted.asStateFlow()

    private val _monthlyTotalConverted = MutableStateFlow<Double?>(null)
    val monthlyTotalConverted: StateFlow<Double?> = _monthlyTotalConverted.asStateFlow()

    private val _categoryTotalsConverted = MutableStateFlow<Map<ExpenseCategory, Double>>(emptyMap())
    val categoryTotalsConverted: StateFlow<Map<ExpenseCategory, Double>> = _categoryTotalsConverted.asStateFlow()

    fun getRecentExpenses() {
        viewModelScope.launch {
            getExpensesUseCase().collect { expenses ->
                _recentExpenses.value = expenses.takeLast(5)
            }
        }
    }

    fun loadConvertedCategoryTotals(targetCurrency: String) {
        viewModelScope.launch {

            getExpensesUseCase().collect { expenses ->
                val categoryTotalsWithSourceCurrency = mutableMapOf<ExpenseCategory, Pair<Double, String>>()

                expenses.forEach { expense ->
                    val currentTotal = categoryTotalsWithSourceCurrency[expense.category]?.first ?: 0.0
                    val currentCurrency = categoryTotalsWithSourceCurrency[expense.category]?.second ?: expense.currency

                    categoryTotalsWithSourceCurrency[expense.category] = Pair(currentTotal + expense.amount, currentCurrency)
                }

                val convertedCategoryTotals = mutableMapOf<ExpenseCategory, Double>()
                categoryTotalsWithSourceCurrency.forEach { (category, data) ->
                    val (amount, sourceCurrency) = data
                    val convertedAmount = convertAmount(amount, sourceCurrency, targetCurrency)
                    convertedCategoryTotals[category] = convertedAmount ?: 0.0
                }

                _categoryTotalsConverted.value = convertedCategoryTotals
            }
        }
    }


    fun loadConvertedTotals(targetCurrency: String) {
        viewModelScope.launch {


            getExpensesUseCase().collect { expenses ->
                val dailyTotal = calculateTotalForExpenses(expenses, targetCurrency, "daily")
                val weeklyTotal = calculateTotalForExpenses(expenses, targetCurrency, "weekly")
                val monthlyTotal = calculateTotalForExpenses(expenses, targetCurrency, "monthly")

                _dailyTotalConverted.value = dailyTotal
                _weeklyTotalConverted.value = weeklyTotal
                _monthlyTotalConverted.value = monthlyTotal
            }
        }
    }

    private suspend fun calculateTotalForExpenses(
        expenses: List<Expense>,
        targetCurrency: String,
        period: String
    ): Double? {
        val filteredExpenses = filterExpensesByPeriod(expenses, period)
        var total = 0.0
        for (expense in filteredExpenses) {
            val convertedAmount = convertAmount(expense.amount, expense.currency, targetCurrency)
            total += convertedAmount ?: 0.0
        }
        return total
    }

    private fun filterExpensesByPeriod(expenses: List<Expense>, period: String): List<Expense> {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = now

        return when (period) {
            "daily" -> {
                val startOfDay = calendar.apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                expenses.filter { it.date >= startOfDay }
            }
            "weekly" -> {
                val startOfWeek = calendar.apply {
                    set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                expenses.filter { it.date >= startOfWeek }
            }
            "monthly" -> {
                val startOfMonth = calendar.apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                expenses.filter { it.date >= startOfMonth }
            }
            else -> expenses
        }
    }

    fun deleteExpense(expense: Expense, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = deleteExpenseUseCase(expense)
            onResult(result)
        }
    }

    private suspend fun convertAmount(amount: Double, fromCurrency: String, toCurrency: String): Double? {
        if (fromCurrency.equals(toCurrency, ignoreCase = true)) {
            return amount
        }

        val response = currencyUseCase.getSingleRateFlow(fromCurrency.lowercase(), toCurrency.lowercase())
        val rate = response.firstOrNull()

        if (rate == null) {
            return null
        }

        val convertedAmount = amount * rate
        return convertedAmount
    }


}