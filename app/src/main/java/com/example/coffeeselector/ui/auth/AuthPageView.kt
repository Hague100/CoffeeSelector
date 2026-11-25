package com.example.coffeeselector.ui.auth


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp


@Composable
fun LoginScreen(onLogin: (String, String) -> Unit, onNavigateToRegister: () -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    val passwordState = rememberTextFieldState()
    var error by remember { mutableStateOf("") }
    Column(Modifier.padding(24.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Login to Coffee Selector Account", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(email, { email = it }, label = { Text("Email") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedSecureTextField(state = passwordState, label = { Text("Password") },
            textObfuscationMode = TextObfuscationMode.Hidden, modifier = Modifier.fillMaxWidth()
        )
        Row {
            Button(
                onClick = {
                    val password = passwordState.text.toString()
                    error = if (email.isBlank() || password.isBlank()) "Enter email and password" else ""
                    if (error.isEmpty()) onLogin(email.trim(), password)
                },
                modifier = Modifier.padding(5.dp).weight(1f)
            ) { Text("Sign in") }
            Button(
                onClick = onNavigateToRegister,
                modifier = Modifier.padding(5.dp).weight(1f)
            ) {Text("Register")}
        }

    }
}

@Preview(showBackground = true, apiLevel = 35)
@Composable
fun LoginScreenPreview() {
    LoginScreen(onLogin = { _, _ -> }, onNavigateToRegister = {})
}

@Composable
fun RegisterScreen(onRegisterUserData: (String, String, String, String, String, String) -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    var emailConfirmation by rememberSaveable { mutableStateOf("") }
    val passwordState = rememberTextFieldState()
    val passwordConfirmationState = rememberTextFieldState()
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var phoneNo by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {Text(text = "Register a Coffee Selector Account",
            style = MaterialTheme.typography.headlineSmall)}
        item {
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
        }
        item {
            OutlinedTextField(value = emailConfirmation, onValueChange = { emailConfirmation = it },
                label = { Text("Email Confirmation") }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
        }
        item {
            OutlinedSecureTextField(state = passwordState, label = { Text("Password") },
                textObfuscationMode = TextObfuscationMode.Hidden, modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedSecureTextField(state = passwordConfirmationState, label = { Text("Password Confirmation") },
                textObfuscationMode = TextObfuscationMode.Hidden, modifier = Modifier.fillMaxWidth()
            )
        }
            item {
            OutlinedTextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First Name") },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
        }
        item {
            OutlinedTextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last Name") },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
        }
        item {
            OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") },
                singleLine = true, modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
        }
        item {
            OutlinedTextField(value = phoneNo, onValueChange = { phoneNo = it }, label = { Text("Phone Number") }, singleLine = true,
                modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
        }
        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val password = passwordState.text.toString()
                    val passwordConfirmation = passwordConfirmationState.text.toString()
                    error = if (
                        email.isBlank() || emailConfirmation.isBlank() || password.isBlank() ||
                        passwordConfirmation.isBlank() || firstName.isBlank() || lastName.isBlank() ||
                        username.isBlank() || phoneNo.isBlank()
                    ) "Fill all fields. " else ""
                    error += if (email != emailConfirmation) "Emails are not the same. " else ""
                    error += if (password != passwordConfirmation) "Passwords don't match. " else ""
                    if (error.isEmpty()) {
                        onRegisterUserData(
                            email.trim(),
                            password,
                            firstName.trim(),
                            lastName.trim(),
                            username.trim(),
                            phoneNo.trim()
                        )
                    }
                }
            ) { Text("Register")}
        }
        if (error.isNotEmpty()) {
            item { Text(text = error, color = Color.Red)}
        }
    }

}

@Preview (showBackground = true, apiLevel = 35)
@Composable
fun RegisterScreenPreview(){
    RegisterScreen { _, _, _, _, _, _ -> }
}
