package com.app.expensetracking.presentation.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.app.expensetracking.presentation.settings.SettingsScreenViewModel
import com.app.expensetracking.ui.Screens
import com.app.expensetracking.ui.components.expense.ExpenseCard
import com.app.expensetracking.ui.components.list.FilterDialog

@Composable
fun ListScreen(navHostController: NavHostController) {
    val viewModel = hiltViewModel<ListScreenViewModel>()
    val settingsViewModel = hiltViewModel<SettingsScreenViewModel>()

    val currency by settingsViewModel.selectedCurrency.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategories by viewModel.selectedCategories.collectAsStateWithLifecycle()
    val showFilterDialog by viewModel.showFilterDialog.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getExpenses()
        settingsViewModel.loadCurrency()
    }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                shape = CircleShape,
                label = { Text("Search by title") },
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = { viewModel.toggleFilterDialogVisibility() },
                modifier = Modifier.padding(start = 8.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    tint = MaterialTheme.colorScheme.primary

                )
            }
        }

        if (selectedCategories.isNotEmpty()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .horizontalScroll(rememberScrollState())
            ) {
                selectedCategories.forEach { category ->
                    FilterChip(
                        selected = true,
                        onClick = { viewModel.toggleCategoryFilter(category) },
                        label = { Text(category.displayName) },
                        modifier = Modifier.padding(end = 4.dp),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = category.icon),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove filter"
                            )
                        }
                    )
                }

                TextButton(
                    onClick = { viewModel.clearCategoryFilters() },
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text("Clear")
                }
            }
        }

        if (expenses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (selectedCategories.isEmpty())
                        "No expenses found"
                    else
                        "No expenses match the selected filters",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                items(expenses) { expense ->
                    ExpenseCard(
                        expense = expense,
                        currency = currency,
                        onDelete = {
                            viewModel.deleteExpense(expense) {}
                        },
                        onEdit = {
                            navHostController.navigate(Screens.Edit(expense.expenseId))
                        }
                    )
                }
            }
        }
    }

    if (showFilterDialog) {
        FilterDialog(
            selectedCategories = selectedCategories,
            onCategoryToggle = { category -> viewModel.toggleCategoryFilter(category) },
            onDismiss = { viewModel.toggleFilterDialogVisibility() },
            onApply = { viewModel.toggleFilterDialogVisibility() },
            onClearFilters = { viewModel.clearCategoryFilters() }
        )
    }
}