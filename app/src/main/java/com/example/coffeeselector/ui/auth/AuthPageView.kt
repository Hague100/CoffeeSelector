package com.example.coffeeselector.ui.auth


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp


@Composable
fun LoginScreen(onLogin: (String, String) -> Unit, onNavigateToRegister: () -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Login to Coffee Selector Account", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(email, { email = it }, label = { Text("Email") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(password, { password = it }, label = { Text("Password") },
            singleLine = true, visualTransformation = PasswordVisualTransformation())
        if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
        Row() {
            Button(onClick = {
                error = if (email.isBlank() || password.isBlank()) "Enter email and password" else ""
                if (error.isEmpty()) onLogin(email.trim(), password)
            }) { Text("Sign in") }
            Button(onClick = onNavigateToRegister){Text("Register")}
        }

    }
}

@Preview (showBackground = true)
@Composable
fun LoginScreenPreview(){
    LoginScreen ( {_, _ -> }, {} )
}

@Composable
fun RegisterScreen(onRegisterUserData: (String, String, String, String, String, String) -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var emailConfirmation by rememberSaveable { mutableStateOf("") }
    var passwordConfirmation by rememberSaveable { mutableStateOf("") }
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var phoneNo by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Register a Coffee Selector Account", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(email, { email = it }, label = { Text("Email") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        OutlinedTextField(emailConfirmation, { emailConfirmation = it }, label = { Text("Email Confirmation") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        OutlinedTextField(password, { password = it }, label = { Text("Password") },
            singleLine = true, visualTransformation = PasswordVisualTransformation())
        OutlinedTextField(passwordConfirmation, { passwordConfirmation = it }, label = { Text("Password Confirmation") },
            singleLine = true, visualTransformation = PasswordVisualTransformation())
        OutlinedTextField(firstName,{ firstName = it }, label = { Text("First Name") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text))
        OutlinedTextField(lastName,{ lastName = it }, label = { Text("Last Name") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text))
        OutlinedTextField(username,{ username = it }, label = { Text("Username") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text))
        OutlinedTextField(phoneNo,{ phoneNo = it }, label = { Text("Phone Number") },
            singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        Button(onClick = {
            error = ""
            error = if (
                email.isBlank() || emailConfirmation.isBlank() || password.isBlank() ||
                passwordConfirmation.isBlank() || firstName.isBlank() || lastName.isBlank() ||
                username.isBlank() || phoneNo.isBlank()
                ) "Fill all fields. " else ""

            error += if(email != emailConfirmation) "Emails are not the same. " else ""
            error += if(password != passwordConfirmation) "Passwords don't match. " else ""
            if (error.isEmpty()) {
                onRegisterUserData(email.trim(), password,firstName.trim(), lastName.trim(),username.trim(),phoneNo.trim())
            }
        }) { Text("Register") }
        if(error != "")Text(text = error, color = Color.Red)

    }

}

@Preview (showBackground = true)
@Composable
fun RegisterScreenPreview(){
    RegisterScreen { _, _, _, _, _, _ -> }
}
