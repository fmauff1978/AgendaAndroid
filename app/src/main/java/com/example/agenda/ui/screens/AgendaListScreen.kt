package com.example.agenda.ui.screens

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

import kotlin.text.format

import com.example.agenda.model.Agenda
import com.example.agenda.viewmodel.AgendaViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import java.util.Date


import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaListScreen(
    viewModel: AgendaViewModel = androidx.lifecycle.viewmodel.compose.viewModel() // Obtenha a instância do ViewModel
) {
    val agenda by viewModel.agenda.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Agenda") })
        }
//    ) { paddingValues ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(16.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            when {
//                isLoading -> {
//                    CircularProgressIndicator()
//                }
//                error != null -> {
//                    Text(
//                        text = "Erro: $error",
//                        color = MaterialTheme.colorScheme.error,
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                }
//                contas.isEmpty() -> {
//                    Text("Nenhuma conta encontrada.")
//                }
//
//                else -> {
//                    ContasList(contas = contas)
//                }
//            }
//        }
//    }
//}

    ) { paddingValues ->
        // O "when" agora é o elemento principal dentro do Scaffold.
        // A lógica de layout muda dependendo do estado.
        when {
            isLoading -> {
                // ESTE CASO USA O BOX PARA CENTRALIZAR
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                // ESTE CASO TAMBÉM USA O BOX PARA CENTRALIZAR
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Erro: $error",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            agenda.isEmpty() -> {
                // E ESTE CASO TAMBÉM
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nenhuma conta encontrada.")
                }
            }
            else -> {
                // IMPORTANTE: A LISTA NÃO USA O BOX CENTRALIZADOR
                // Ela é colocada diretamente, recebendo restrições finitas do Scaffold.
                AgendaList(
                    agenda = agenda,
                    modifier = Modifier.padding(paddingValues) // Passa o padding do Scaffold para a lista
                )
            }
        }
    }
}


//@Composable
//fun ContasList(contas: List<Conta>) {
//    androidx.compose.foundation.lazy.LazyColumn(
//        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
//        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
//    ) {
//        items(contas) { conta ->
//            ContaItem(conta = conta)
//        }
//    }
//}
@Composable
fun AgendaList(agenda: List<Agenda>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(), // Aplica o modifier recebido
        contentPadding = PaddingValues(16.dp), // Usa contentPadding para espaçamento interno
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(agenda) { agenda ->
            AgendaItem(agenda = agenda)
        }
    }
}







            @Composable
            fun AgendaItem(agenda: Agenda) {

                val formatoData = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                val dataAgendada = agenda.agendado_para.toDate()
                val dataFormatada = formatoData.format(dataAgendada)
                val hoje = Date()
                val estaAtrasado = dataAgendada.before(hoje)
                val corDeFundo = if (estaAtrasado) Color(0xFFFFE0B2) else MaterialTheme.colorScheme.surface

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = corDeFundo),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$dataFormatada - ${agenda.descricao}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            if (estaAtrasado) {

                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Atrasado",
                                    tint = Color(0xFFD84315)
                                )
                            }

                            // Ícone de "tick" para reagendar para o mês seguinte
                            IconButton(onClick = {
                                val calendario = Calendar.getInstance()
                                calendario.time = Date()
                                calendario.add(Calendar.MONTH, 1) // Adiciona 1 mês
                                val novaData = Timestamp(calendario.time)

                                val db = FirebaseFirestore.getInstance()
                                val docRef = db.collection("agenda").document(agenda.id) // ajuste conforme sua estrutura

                                docRef.update("agendado_para", novaData)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Reagendar para o mês seguinte",
                                    tint = Color(0xFF2E7D32) // verde escuro
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Valor: R$ ${String.format("%.2f", agenda.valor)}")

                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }


            }



