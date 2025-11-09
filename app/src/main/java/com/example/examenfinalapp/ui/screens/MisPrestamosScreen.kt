package com.example.examenfinalapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.examenfinalapp.model.Prestamo
import com.example.examenfinalapp.ui.viewmodel.MisPrestamosViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MisPrestamosScreen(vm: MisPrestamosViewModel) {
    val ui by vm.ui.collectAsState()
    if (ui.loading) CircularProgressIndicator()
    ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

    LazyColumn(Modifier.fillMaxSize().padding(12.dp)) {
        items(ui.prestamos) { p -> PrestamoItem(p) }
    }
}

@Composable
private fun PrestamoItem(p: Prestamo) {
    val fmt = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text("Equipo: ${p.equipoId}")
            Text("Estado: ${p.estado}")
            Text("Préstamo: ${if (p.fechaPrestamo>0) fmt.format(Date(p.fechaPrestamo)) else "-"}")
            Text("Devolución: ${if (p.fechaDevolucion>0) fmt.format(Date(p.fechaDevolucion)) else "-"}")
        }
    }
}
