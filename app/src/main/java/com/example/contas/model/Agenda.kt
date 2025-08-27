package com.example.contas.model

import com.google.firebase.Timestamp
import java.math.BigDecimal

data class Agenda( // Define the primary constructor here

     val id: String = "",
     val agendado_para: Timestamp,
     val ativa: Boolean = true,
     val atualizado_em: Timestamp,
     val criado_em: Timestamp,
     val descricao: String ="",
     val origem: String="",
     val recor: String="",
     val valor: Double=0.0, )



