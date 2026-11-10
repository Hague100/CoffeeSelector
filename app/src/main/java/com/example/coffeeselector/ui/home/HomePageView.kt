package com.example.coffeeselector.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NoUserHomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToLocations: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Coffee Selector", fontSize = 40.sp, modifier = Modifier.padding(all = 20.dp))
        Button(onNavigateToLogin) {Text("Log In")}
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = onNavigateToRegister){Text("Register")}
        Spacer(modifier = Modifier.height(10.dp))
        Button(onNavigateToLocations) {Text("Locations")}

    }
}

@Composable
fun UserHomeScreen(
    onNavigateToAccount: () -> Unit,
    onNavigateToLocations: () -> Unit,
    onLogOut: () -> Unit,
    onLogOutNavigateTo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Coffee Selector", fontSize = 40.sp, modifier = Modifier.padding(all = 20.dp))
        Button(onNavigateToAccount) {Text("Account")}
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = onNavigateToLocations){Text("Locations")}
        Spacer(modifier = Modifier.height(10.dp))
        Button({
            onLogOut()
            onLogOutNavigateTo()
        }) {Text("Log Out")}
    }
}

@Preview (showBackground = true)
@Composable
fun NoUserHomeScreenPreview() {
    NoUserHomeScreen(
        onNavigateToLogin = {},
        onNavigateToRegister = {},
        onNavigateToLocations = {}
    )
}

@Preview (showBackground = true)
@Composable
fun UserHomeScreenPreview() {
    UserHomeScreen(
        {},
        {},
        {},
        {}
    )
}