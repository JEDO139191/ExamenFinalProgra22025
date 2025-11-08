package com.example.examenfinalapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.examenfinalapp.model.Prestamo
import com.example.examenfinalapp.ui.viewmodel.AdminViewModel

@Composable
fun AdminSolicitudesScreen(
    vm: AdminViewModel,
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
    ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(12.dp)) }

    LazyColumn(Modifier.fillMaxSize().padding(12.dp)) {
        items(ui.pendientes) { p -> SolicitudCard(p, vm) }
    }
}

@Composable
private fun SolicitudCard(p: Prestamo, vm: AdminViewModel) {
    Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text("UID: ${p.uid}")
            Text("Equipo: ${p.equipoId}")
            Text("Estado: ${p.estado}")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { vm.rechazar(p.id, "Sin disponibilidad") }) { Text("Rechazar") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { vm.aprobar(p.id) }) { Text("Aprobar") }
            }
        }
    }
}
