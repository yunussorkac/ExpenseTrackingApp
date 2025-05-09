package com.app.expensetracking.domain.model

import androidx.annotation.DrawableRes
import com.app.expensetracking.R

data class Expense(
    val expenseId : String = "",
    val title : String = "",
    val category: ExpenseCategory = ExpenseCategory.OTHER,
    val description : String = "",
    val amount : Double = 0.0,
    val date : Long = 0,
    val fullDate : String = "",
    val currency : String = ""

) {
}

enum class ExpenseCategory(val displayName: String, @DrawableRes val icon: Int) {
    FOOD_DRINKS("Food & Drinks", R.drawable.baseline_fastfood_24),
    TRANSPORT("Transport", R.drawable.baseline_directions_transit_24),
    SHOPPING("Shopping", R.drawable.baseline_shopping_cart_24),
    ENTERTAINMENT("Entertainment", R.drawable.baseline_grade_24),
    HEALTH("Health", R.drawable.baseline_health_and_safety_24),
    HOME_BILLS("Home & Bills", R.drawable.baseline_home_24),
    EDUCATION_SELFCARE("Education & Self-care", R.drawable.baseline_cast_for_education_24),
    OTHER("Other", R.drawable.baseline_menu_24);

    override fun toString(): String = displayName
}