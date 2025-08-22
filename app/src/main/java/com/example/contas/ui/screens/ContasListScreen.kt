package com.example.contas.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.contas.model.Conta
import com.example.contas.viewmodel.ContasViewModel
import kotlin.text.format

import androidx.compose.material3.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContasListScreen(
    viewModel: ContasViewModel = androidx.lifecycle.viewmodel.compose.viewModel() // Obtenha a instância do ViewModel
) {
    val contas by viewModel.contas.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Minhas Contas 2025") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                error != null -> {
                    Text(
                        text = "Erro: $error",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                contas.isEmpty() -> {
                    Text("Nenhuma conta encontrada.")
                }

                else -> {
                    ContasList(contas = contas)
                }
            }
        }
    }
}

@Composable
fun ContasList(contas: List<Conta>) {
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        items(contas) { conta ->
            ContaItem(conta = conta)
        }
    }
}

@androidx.compose.runtime.Composable
fun ContaItem(conta: Conta) {
    androidx.compose.material3.Card(
        modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = androidx.compose.ui.Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            androidx.compose.material3.Text(
                text = conta.conta,
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(4.dp))
            androidx.compose.material3.Text(text = "Valor: R$ ${String.format("%.2f", conta.saldo)}")
            androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(4.dp))

            // Adicione mais Textos para outros campos da sua conta
        }
    }
}
