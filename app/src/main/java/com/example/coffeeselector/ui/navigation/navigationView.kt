package com.example.coffeeselector.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * https://developer.android.com/develop/ui/compose/graphics/images/material
 * https://fonts.google.com/icons
 */
@Composable
private fun NavTopBarButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BadgedBox(
            badge = {
                if (badgeCount > 0) {
                    Badge {
                        Text(text = if (badgeCount > 99) "99+" else badgeCount.toString())
                    }
                }
            }
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
fun NavigationTopBar(
    unreadCount: Int = 0,
    onUserClick: () -> Unit = {},
    onLocationsClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NavTopBarButton(
            label = "User",
            icon = Icons.Default.Person,
            onClick = onUserClick,
            modifier = Modifier.weight(1f)
        )
        NavTopBarButton(
            label = "Locations",
            icon = Icons.Default.LocationOn,
            onClick = onLocationsClick,
            modifier = Modifier.weight(1f)
        )
        NavTopBarButton(
            label = "Notifications",
            icon = Icons.Default.Notifications,
            badgeCount = unreadCount,
            onClick = onNotificationsClick,
            modifier = Modifier.weight(1f)
        )
        NavTopBarButton(
            label = "Orders",
            icon = Icons.Default.ShoppingCart,
            onClick = onOrdersClick,
            modifier = Modifier.weight(1f)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun NavigationTopBarPreview() {
    NavigationTopBar(unreadCount = 3)
}