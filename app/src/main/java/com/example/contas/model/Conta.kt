package com.example.contas.model

import java.math.BigDecimal

data class Conta( // Define the primary constructor here
     val id: String = "", // Para armazenar o ID do documento Firestore
     //val cod: Int,
     val conta: String = "",
     //val em_uso: Boolean = true,
   //  val enq: Double = 0.0,
   //  val enquadramento: String = "",
   //  val mod_despesa: String = "",
   //  val natureza: String = "",
     val saldo: Double = 0.0) {
     // You can leave this empty if you don't have any additional logic
}
