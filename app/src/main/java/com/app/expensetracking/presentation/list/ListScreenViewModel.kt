package com.app.expensetracking.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.expensetracking.domain.usecase.expense.DeleteExpenseUseCase
import com.app.expensetracking.domain.usecase.expense.GetExpensesUseCase
import com.app.expensetracking.domain.model.Expense
import com.app.expensetracking.domain.model.ExpenseCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListScreenViewModel @Inject constructor(
    private val getExpensesFlowUseCase: GetExpensesUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase
) : ViewModel() {

    private val _allExpenses = MutableStateFlow<List<Expense>>(emptyList())

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategories = MutableStateFlow<Set<ExpenseCategory>>(emptySet())
    val selectedCategories: StateFlow<Set<ExpenseCategory>> = _selectedCategories

    private val _showFilterDialog = MutableStateFlow(false)
    val showFilterDialog: StateFlow<Boolean> = _showFilterDialog

    init {
        viewModelScope.launch {
            combine(
                _allExpenses,
                _searchQuery,
                _selectedCategories
            ) { allExpenses, query, categories ->
                applyFilters(allExpenses, query, categories)
            }.collect { filteredExpenses ->
                _expenses.value = filteredExpenses
            }
        }
    }

    fun getExpenses() {
        viewModelScope.launch {
            getExpensesFlowUseCase()
                .collect { expenseList ->
                    _allExpenses.value = expenseList
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
    }

    fun toggleCategoryFilter(category: ExpenseCategory) {
        val currentCategories = _selectedCategories.value.toMutableSet()
        if (currentCategories.contains(category)) {
            currentCategories.remove(category)
        } else {
            currentCategories.add(category)
        }
        _selectedCategories.value = currentCategories
    }

    fun clearCategoryFilters() {
        _selectedCategories.value = emptySet()
    }

    fun toggleFilterDialogVisibility() {
        _showFilterDialog.value = !_showFilterDialog.value
    }

    private fun applyFilters(expenses: List<Expense>, query: String, categories: Set<ExpenseCategory>): List<Expense> {
        var result = expenses

        if (query.isNotBlank()) {
            result = result.filter { it.title.contains(query, ignoreCase = true) }
        }

        if (categories.isNotEmpty()) {
            result = result.filter { it.category in categories }
        }

        return result
    }
}