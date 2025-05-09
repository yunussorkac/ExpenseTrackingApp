package com.app.expensetracking.presentation.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.expensetracking.domain.usecase.currency.CurrencyUseCase
import com.app.expensetracking.domain.usecase.expense.GetExpensesUseCase
import com.app.expensetracking.domain.model.Expense
import com.app.expensetracking.domain.model.ExpenseCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ChartScreenViewModel @Inject constructor(
    private val currencyUseCase: CurrencyUseCase,
    private val getExpensesUseCase: GetExpensesUseCase
) : ViewModel() {

    private val _convertedCategoryTotals = MutableStateFlow<Map<ExpenseCategory, Double>>(emptyMap())
    val convertedCategoryTotals: StateFlow<Map<ExpenseCategory, Double>> = _convertedCategoryTotals.asStateFlow()

    private val _convertedDailyExpenses = MutableStateFlow<Map<String, Double>>(emptyMap())
    val convertedDailyExpenses: StateFlow<Map<String, Double>> = _convertedDailyExpenses.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val currentCalendar = Calendar.getInstance()
    private val _selectedMonth = MutableStateFlow(currentCalendar.get(Calendar.MONTH))
    val selectedMonth: StateFlow<Int> = _selectedMonth.asStateFlow()

    private val _selectedYear = MutableStateFlow(currentCalendar.get(Calendar.YEAR))
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()


    fun nextMonth(targetCurrency: String) {
        _isLoading.value = true
        if (_selectedMonth.value == 11) {
            _selectedMonth.value = 0
            _selectedYear.value = _selectedYear.value + 1
        } else {
            _selectedMonth.value = _selectedMonth.value + 1
        }
        loadConvertedData(targetCurrency)
    }

    fun previousMonth(targetCurrency: String) {
        _isLoading.value = true
        if (_selectedMonth.value == 0) {
            _selectedMonth.value = 11
            _selectedYear.value = _selectedYear.value - 1
        } else {
            _selectedMonth.value = _selectedMonth.value - 1
        }
        loadConvertedData(targetCurrency)
    }

    private suspend fun calculateCategoryTotals(
        expenses: List<Expense>,
        targetCurrency: String,
        month: Int,
        year: Int
    ): Map<ExpenseCategory, Double> {
        val filteredExpenses = expenses.filter { expense ->
            val calendar = Calendar.getInstance().apply {
                timeInMillis = expense.date
            }
            calendar.get(Calendar.MONTH) == month && calendar.get(Calendar.YEAR) == year
        }

        val expensesByCategory = filteredExpenses.groupBy { it.category }

        val convertedCategoryTotals = mutableMapOf<ExpenseCategory, Double>()

        for ((category, categoryExpenses) in expensesByCategory) {
            var totalForCategory = 0.0

            for (expense in categoryExpenses) {
                val convertedAmount = convertAmount(expense.amount, expense.currency, targetCurrency) ?: 0.0
                totalForCategory += convertedAmount
            }

            convertedCategoryTotals[category] = totalForCategory
        }

        return convertedCategoryTotals
    }

    private suspend fun calculateDailyExpenses(
        expenses: List<Expense>,
        targetCurrency: String,
        month: Int,
        year: Int
    ): Map<String, Double> {
        val filteredExpenses = expenses.filter { expense ->
            val calendar = Calendar.getInstance().apply {
                timeInMillis = expense.date
            }
            calendar.get(Calendar.MONTH) == month && calendar.get(Calendar.YEAR) == year
        }

        val expensesByDay = mutableMapOf<String, MutableList<Expense>>()

        filteredExpenses.forEach { expense ->
            val calendar = Calendar.getInstance().apply {
                timeInMillis = expense.date
            }
            val day = calendar.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
            val dayExpenses = expensesByDay.getOrPut(day) { mutableListOf() }
            dayExpenses.add(expense)
        }

        val convertedDailyExpenses = mutableMapOf<String, Double>()

        for ((day, dayExpenses) in expensesByDay) {
            var totalForDay = 0.0

            for (expense in dayExpenses) {
                val convertedAmount = convertAmount(expense.amount, expense.currency, targetCurrency) ?: 0.0
                totalForDay += convertedAmount
            }

            convertedDailyExpenses[day] = totalForDay
        }

        return convertedDailyExpenses
    }


    fun loadConvertedData(targetCurrency: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                getExpensesUseCase().collect { expenses ->
                    println("Loaded ${expenses.size} expenses for processing")
                    println("Selected month: ${_selectedMonth.value + 1}, year: ${_selectedYear.value}")

                    val monthYearLog = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(
                        Calendar.getInstance().apply {
                            set(Calendar.YEAR, _selectedYear.value)
                            set(Calendar.MONTH, _selectedMonth.value)
                        }.time
                    )
                    println("Processing data for: $monthYearLog")

                    val convertedTotals = calculateCategoryTotals(
                        expenses,
                        targetCurrency,
                        _selectedMonth.value,
                        _selectedYear.value
                    )
                    _convertedCategoryTotals.value = convertedTotals
                    println("Category totals calculated: ${convertedTotals.size} categories")
                    println("Total amount across all categories: ${convertedTotals.values.sum()} $targetCurrency")

                    val dailyExpenses = calculateDailyExpenses(
                        expenses,
                        targetCurrency,
                        _selectedMonth.value,
                        _selectedYear.value
                    )
                    _convertedDailyExpenses.value = dailyExpenses
                    println("Daily expenses calculated: ${dailyExpenses.size} days")
                    println("Total daily amount: ${dailyExpenses.values.sum()} $targetCurrency")

                    _isLoading.value = false
                }
            } catch(e: Exception) {
                e.printStackTrace()
                _isLoading.value = false
            }
        }
    }

    private suspend fun convertAmount(amount: Double, fromCurrency: String, toCurrency: String): Double? {
        if (fromCurrency.equals(toCurrency, ignoreCase = true)) {
            return amount
        }

        val response = currencyUseCase.getSingleRateFlow(fromCurrency.lowercase(), toCurrency.lowercase())
        val rate = response.firstOrNull()

        if (rate == null) {
            println("convertAmount: Rate not found for $fromCurrency -> $toCurrency")
            return null
        }

        val convertedAmount = amount * rate
        return convertedAmount
    }
}