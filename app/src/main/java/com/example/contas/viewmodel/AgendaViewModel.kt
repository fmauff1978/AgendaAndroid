package com.example.contas.viewmodel

import android.util.Log
import androidx.activity.result.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contas.model.Agenda
import com.example.contas.model.Conta
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class AgendaViewModel: ViewModel(){

    private val db = Firebase.firestore
    private val _agenda = MutableStateFlow<List<Agenda>>(emptyList())
    val agenda: StateFlow<List<Agenda>> = _agenda
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    companion object {
        private const val TAG = "AgendaViewModel"
        private const val COLECAO_CONTAS = "Agenda"
    }

    init {
        fetchContas()
    }

    fun fetchContas() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val snapshot = db.collection("agenda").whereEqualTo("ativa",true) .orderBy("agendado_para") .get().await()
                _agenda.value = snapshot.documents.mapNotNull { document ->
                    // Mapeia o documento para o seu objeto Conta
                    // Adicione .toObject(Conta::class.java) se você tiver os campos correspondentes
                    // e queira que o Firebase faça o mapeamento automático.
                    // Certifique-se que sua classe Conta tem um construtor vazio se usar toObject.
                    Agenda(
                        id = document.id,
                        agendado_para = document.getTimestamp("agendado_para") ?: Timestamp.now(),
                        ativa = document.getBoolean("ativa") ?: true,
                        atualizado_em = document.getTimestamp("atualizado_em") ?: Timestamp.now(),
                        criado_em = document.getTimestamp("criado_em") ?: Timestamp.now(),
                        descricao = document.getString("descricao") ?: "",

                        origem = document.getString("origem") ?: "",
                        recor = document.getString("recor") ?: "",
                        valor = document.getDouble("valor") ?: 0.0,




                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao buscar contas: ", e)
                _error.value = "Erro ao buscar contas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }



}}