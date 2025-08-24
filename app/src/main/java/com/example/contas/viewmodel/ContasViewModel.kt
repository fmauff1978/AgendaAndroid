package com.example.contas.viewmodel

import android.util.Log
import androidx.activity.result.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contas.model.Conta
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class ContasViewModel: ViewModel(){

    private val db = Firebase.firestore
    private val _contas = MutableStateFlow<List<Conta>>(emptyList())
    val contas: StateFlow<List<Conta>> = _contas
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    companion object {
        private const val TAG = "ContasViewModel"
        private const val COLECAO_CONTAS = "Contas2025"
    }

    init {
        fetchContas()
    }

    fun fetchContas() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val snapshot = db.collection("contas2025").whereEqualTo("em_uso",true) .orderBy("cod") .get().await()
                _contas.value = snapshot.documents.mapNotNull { document ->
                    // Mapeia o documento para o seu objeto Conta
                    // Adicione .toObject(Conta::class.java) se você tiver os campos correspondentes
                    // e queira que o Firebase faça o mapeamento automático.
                    // Certifique-se que sua classe Conta tem um construtor vazio se usar toObject.
                    Conta(
                        id = document.id,
                     //  cod = document.getInt("cod") ?: 0,
                        conta = document.getString("conta") ?: "",
                        saldo = document.getDouble("saldo") ?: 0.0,

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