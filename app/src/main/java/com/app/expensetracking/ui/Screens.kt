package com.app.expensetracking.ui

import com.app.expensetracking.R
import kotlinx.serialization.Serializable

sealed class Screens {

    @Serializable
    data object Login : Screens()

    @Serializable
    data object Register : Screens()

    @Serializable
    data object Add : Screens()

    @Serializable
    data class Edit (val expenseId: String) : Screens()

}

sealed class NavigationItem(val route: String, val title: String, val icon: Int) {

    data object Home : NavigationItem("home", "Home", R.drawable.baseline_home_24)
    data object Chart : NavigationItem("chart", "Chart", R.drawable.baseline_pie_chart_24)
    data object List : NavigationItem("list", "Expenses", R.drawable.baseline_format_list_bulleted_24)
    data object Settings : NavigationItem("settings", "Settings", R.drawable.baseline_settings_24)


}