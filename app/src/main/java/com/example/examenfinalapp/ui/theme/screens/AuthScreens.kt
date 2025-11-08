package com.example.examenfinalapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.examenfinalapp.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(vm: AuthViewModel, onOk: () -> Unit, onGoRegistro: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(email, { email = it }, label = { Text("Email") })
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(pass, { pass = it }, label = { Text("Contraseña") })
        Spacer(Modifier.height(12.dp))
        Button(onClick = { vm.signInYAsegurarPerfil(email, pass); onOk() }) { Text("Iniciar sesión") }
        TextButton(onClick = onGoRegistro) { Text("Crear cuenta") }
        vm.uiState.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}

@Composable
fun RegistroScreen(vm: AuthViewModel, onOk: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var carnet by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }

    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre completo") })
        OutlinedTextField(carnet, { carnet = it }, label = { Text("Carnet") })
        OutlinedTextField(carrera, { carrera = it }, label = { Text("Carrera") })
        OutlinedTextField(email, { email = it }, label = { Text("Email") })
        OutlinedTextField(pass, { pass = it }, label = { Text("Contraseña") })
        Spacer(Modifier.height(12.dp))
        Button(onClick = { vm.signUpYCrearPerfil(email, pass, nombre, carnet, carrera); onOk() }) {
            Text("Registrarme")
        }
        vm.uiState.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}
