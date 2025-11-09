package com.example.examenfinalapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.examenfinalapp.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    vm: AuthViewModel,
    onOk: () -> Unit,
    onGoRegistro: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var pass by rememberSaveable { mutableStateOf("") }
    var passVisible by rememberSaveable { mutableStateOf(false) }

    val formOk = email.isNotBlank() && pass.isNotBlank()

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Iniciar sesión", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                label = { Text("Contraseña") },
                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { passVisible = !passVisible }) {
                        Text(if (passVisible) "Ocultar" else "Ver")
                    }
                }
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    vm.signInYAsegurarPerfil(email, pass)
                    onOk()
                },
                enabled = formOk,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Entrar") }

            TextButton(onClick = onGoRegistro) { Text("Crear cuenta") }

            val errorMsg = vm.uiState.value.error
            if (errorMsg != null) {
                Spacer(Modifier.height(8.dp))
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
            }
        }

        if (vm.uiState.value.loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
