package ipca.example.favoritos.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import ipca.example.favoritos.domain.PerfilState

class PerfilViewModel : ViewModel() {

    companion object {
        private const val TAG = "PerfilViewModel"
    }

    var uiState = mutableStateOf(PerfilState())
        private set

    private val auth: FirebaseAuth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()

    init {
        carregarDados()
    }

    private fun carregarDados() {
        val utilizadorId = auth.currentUser?.uid
        if (utilizadorId == null) {
            uiState.value = uiState.value.copy(
                erro = "Utilizador não autenticado",
                carregando = false
            )
            return
        }

        db.collection("utilizadores")
            .document(utilizadorId)
            .get()
            .addOnSuccessListener { documento ->
                if (documento.exists()) {
                    val nome = documento.getString("nome")
                    val email = documento.getString("email")
                    uiState.value = uiState.value.copy(
                        nome = nome,
                        email = email,
                        carregando = false,
                        erro = null
                    )
                } else {
                    uiState.value = uiState.value.copy(
                        erro = "Documento não encontrado",
                        carregando = false
                    )
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Erro ao carregar dados", e)
                uiState.value = uiState.value.copy(
                    erro = "Erro ao carregar dados",
                    carregando = false
                )
            }
    }


    fun alterarNomeLocal(novoNome: String) {
        uiState.value = uiState.value.copy(nome = novoNome, sucesso = null, erro = null)
    }


    fun atualizarNomeFirestore() {
        val utilizadorId = auth.currentUser?.uid ?: return
        val nome = uiState.value.nome ?: return

        uiState.value = uiState.value.copy(carregando = true, sucesso = null, erro = null)

        db.collection("utilizadores")
            .document(utilizadorId)
            .update("nome", nome)
            .addOnSuccessListener {
                uiState.value = uiState.value.copy(
                    carregando = false,
                    sucesso = "Nome atualizado com sucesso ✅"
                )
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Erro ao atualizar nome", e)
                uiState.value = uiState.value.copy(
                    carregando = false,
                    erro = "Erro ao atualizar nome"
                )
            }
    }
}