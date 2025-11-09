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
import com.example.examenfinalapp.model.RolUsuario
import com.example.examenfinalapp.ui.viewmodel.AuthViewModel

@Composable
fun RegistroScreen(
    vm: AuthViewModel,
    onOk: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var carnet by rememberSaveable { mutableStateOf("") }
    var carrera by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var pass by rememberSaveable { mutableStateOf("") }
    var pass2 by rememberSaveable { mutableStateOf("") }
    var passVisible by rememberSaveable { mutableStateOf(false) }
    var pass2Visible by rememberSaveable { mutableStateOf(false) }
    var rol by rememberSaveable { mutableStateOf(RolUsuario.Estudiante) }

    val formOk = nombre.isNotBlank() && carnet.isNotBlank() &&
            carrera.isNotBlank() && email.isNotBlank() &&
            pass.isNotBlank() && pass2 == pass

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Crear cuenta", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre completo") })
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = carnet, onValueChange = { carnet = it }, label = { Text("Carnet") })
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = carrera, onValueChange = { carrera = it }, label = { Text("Carrera") })
            Spacer(Modifier.height(8.dp))
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
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = pass2,
                onValueChange = { pass2 = it },
                label = { Text("Confirmar contraseña") },
                visualTransformation = if (pass2Visible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { pass2Visible = !pass2Visible }) {
                        Text(if (pass2Visible) "Ocultar" else "Ver")
                    }
                }
            )
            Spacer(Modifier.height(8.dp))

            Text("Tipo de usuario")
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = rol == RolUsuario.Estudiante, onClick = { rol = RolUsuario.Estudiante })
                Text("Estudiante")
                Spacer(Modifier.width(16.dp))
                RadioButton(selected = rol == RolUsuario.Administrador, onClick = { rol = RolUsuario.Administrador })
                Text("Administrador")
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    vm.signUpYCrearPerfil(email, pass, nombre, carnet, carrera, rol)
                    onOk()
                },
                enabled = formOk,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Registrarme") }

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
