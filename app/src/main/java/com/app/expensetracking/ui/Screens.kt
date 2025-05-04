package com.app.expensetracking.ui

import androidx.compose.ui.graphics.vector.ImageVector
import com.app.expensetracking.R
import kotlinx.serialization.Serializable

sealed class Screens {

    @Serializable
    data object Login : Screens()

    @Serializable
    data object Register : Screens()

    @Serializable
    data object Add : Screens()

}

sealed class NavigationItem(val route: String, val title: String, val icon: Int) {

    data object Home : NavigationItem("home", "Home", R.drawable.baseline_home_24)
    data object List : NavigationItem("list", "List", R.drawable.baseline_format_list_bulleted_24)
    data object Settings : NavigationItem("settings", "Settings", R.drawable.baseline_settings_24)


}