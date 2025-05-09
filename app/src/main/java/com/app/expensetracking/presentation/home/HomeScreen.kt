package com.app.expensetracking.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.app.expensetracking.presentation.currency.CurrencyViewModel
import com.app.expensetracking.presentation.settings.SettingsScreenViewModel
import com.app.expensetracking.ui.Screens
import com.app.expensetracking.ui.components.expense.ExpenseCard
import com.app.expensetracking.ui.components.home.CategoryItem
import com.app.expensetracking.ui.components.home.SummaryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navHostController: NavHostController) {

    val viewModel = hiltViewModel<HomeScreenViewModel>()

    val settingsViewModel = hiltViewModel<SettingsScreenViewModel>()
    val currency by settingsViewModel.selectedCurrency.collectAsStateWithLifecycle()
    val recentExpenses by viewModel.recentExpenses.collectAsStateWithLifecycle()



    val dailyConverted by viewModel.dailyTotalConverted.collectAsStateWithLifecycle()
    val weeklyConverted by viewModel.weeklyTotalConverted.collectAsStateWithLifecycle()
    val monthlyConverted by viewModel.monthlyTotalConverted.collectAsStateWithLifecycle()
    val categoryTotalsConverted by viewModel.categoryTotalsConverted.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getRecentExpenses()
    }

    LaunchedEffect(Unit) {
        settingsViewModel.loadCurrency()
    }

    LaunchedEffect(currency) {
        if (currency.isNotBlank()) {
            println("LaunchedEffect triggered with selectedCurrency: $currency")
            viewModel.loadConvertedTotals(currency)
            viewModel.loadConvertedCategoryTotals(currency)
        }
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryCard(
                    title = "Today",
                    amount = dailyConverted ?: 0.0,
                    icon = Icons.Default.DateRange,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    currency = currency
                )
                SummaryCard(
                    title = "Weekly",
                    amount = weeklyConverted ?: 0.0,
                    icon = Icons.Default.DateRange,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    currency = currency
                )
                SummaryCard(
                    title = "Monthly",
                    amount = monthlyConverted ?: 0.0,
                    icon = Icons.Default.DateRange,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    currency = currency
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Monthly by Categories",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    if (categoryTotalsConverted.isEmpty()) {
                        Text(
                            "No Data",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        categoryTotalsConverted.forEach { (category, amount) ->
                            CategoryItem(
                                categoryName = category.displayName,
                                amount = amount,
                                currency = currency
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Recent Expenses",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (recentExpenses.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        "No Data.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(recentExpenses) { expense ->
                ExpenseCard(
                    expense = expense,
                    currency = currency,
                    isHome = true,
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