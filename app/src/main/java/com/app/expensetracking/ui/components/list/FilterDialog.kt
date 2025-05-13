package com.app.expensetracking.ui.components.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.expensetracking.domain.model.ExpenseCategory

@Composable
fun FilterDialog(
    selectedCategories: Set<ExpenseCategory>,
    onCategoryToggle: (ExpenseCategory) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
    onClearFilters: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Filter by Category")
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ExpenseCategory.entries.toTypedArray()) { category ->
                        CategoryFilterChip(
                            category = category,
                            isSelected = category in selectedCategories,
                            onToggle = { onCategoryToggle(category) }
                        )
                    }
                }

                if (selectedCategories.isNotEmpty()) {
                    TextButton(
                        onClick = onClearFilters,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 8.dp)
                    ) {
                        Text("Clear Filters")
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onApply) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CategoryFilterChip(
    category: ExpenseCategory,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = ButtonDefaults.outlinedButtonBorder,
        modifier = Modifier
            .padding(4.dp)
            .toggleable(
                value = isSelected,
                role = Role.Checkbox,
                onValueChange = { onToggle() }
            )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = category.icon),
                contentDescription = "",
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = category.displayName,
                fontSize = 12.sp,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}