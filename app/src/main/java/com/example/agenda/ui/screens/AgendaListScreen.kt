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
import java.text.NumberFormat



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
                    modifier = Modifier.padding(paddingValues),
                    viewModel = viewModel // Passa o padding do Scaffold para a lista
                )
            }
        }
    }
}


@Composable
fun AgendaList(agenda: List<Agenda>, modifier: Modifier = Modifier, viewModel: AgendaViewModel) {
    LazyColumn(
        modifier = modifier.fillMaxSize(), // Aplica o modifier recebido
        contentPadding = PaddingValues(16.dp), // Usa contentPadding para espaçamento interno
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(agenda) { agenda ->
            AgendaItem(agenda = agenda, onReagendarClick = {itemClicado ->
                viewModel.reagendarParaProximoMesBaseadoNoAgendamento(itemClicado)
            })
        }
    }
}


@Composable
fun AgendaItem(agenda: Agenda, onReagendarClick: (item:Agenda)-> Unit) {

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
                    onReagendarClick(agenda)}){
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Reagendar para o mês seguinte",
                        tint = Color.Black // verde escuro
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Crie uma instância do NumberFormat para a localidade PT-BR
            val formatoMoeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            // Formate o valor da agenda
            val valorFormatado = formatoMoeda.format(agenda.valor)

            Text(text = "Valor: $valorFormatado") // "Valor: R$ 1.234,56"



            Spacer(modifier = Modifier.height(4.dp))
        }
    }


}



