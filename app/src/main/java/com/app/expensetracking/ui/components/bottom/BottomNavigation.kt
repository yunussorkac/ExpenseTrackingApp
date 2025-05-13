package com.app.expensetracking.ui.components.bottom

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.expensetracking.ui.NavigationItem

@Composable
fun BottomNavigation(
    items: List<NavigationItem>,
    selectedItemIndex: Int,
    onItemSelected: (index: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar(
        modifier = modifier.height(65.dp),
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedItemIndex == index
                BottomNavigationItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onItemSelected(index) }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationItem(
    item: NavigationItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tintColor = if (isSelected) Color.Black else Color.White

    Column(
        modifier = modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ){
            onClick()
        }
        ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = item.icon),
            contentDescription = item.title,
            modifier = Modifier.size(24.dp),
            tint = tintColor
        )

        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = item.title,
            color = tintColor,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
    }

}