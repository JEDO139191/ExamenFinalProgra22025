package com.example.examenfinalapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.examenfinalapp.model.Equipo
import com.example.examenfinalapp.ui.viewmodel.CatalogoViewModel

@Composable
fun CatalogoScreen(
    vm: CatalogoViewModel,
    snackbarHostState: SnackbarHostState
) {
    val ui by vm.ui.collectAsState()

    LaunchedEffect(ui.toast) {
        ui.toast?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            vm.clearToast()
        }
    }

    if (ui.loading) LinearProgressIndicator(Modifier.fillMaxWidth())

    ui.error?.let {
        Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(12.dp))
    }

    LazyColumn(Modifier.fillMaxSize().padding(12.dp)) {
        items(ui.equipos) { e -> EquipoItem(e) { vm.solicitar(e.id) } }
    }
}

@Composable
private fun EquipoItem(e: Equipo, onSolicitar: () -> Unit) {
    Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(e.nombre, style = MaterialTheme.typography.titleMedium)
            Text(e.descripcion)
            Text("Disponibles: ${e.disponibles}")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = onSolicitar, enabled = e.disponibles > 0) { Text("Solicitar") }
            }
        }
    }
}
