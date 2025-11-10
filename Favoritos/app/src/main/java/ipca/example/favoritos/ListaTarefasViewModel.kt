package ipca.example.favoritos

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Firebase

data class ListaTarefasState(
    val tarefas: List<Tarefa> = emptyList(),
    val erro: String? = null,
    val carregando: Boolean = true
)

class ListaTarefasViewModel : ViewModel() {

    companion object {
        private const val TAG = "ListaTarefasViewModel"
    }

    var uiState = mutableStateOf(ListaTarefasState())
        private set

    private val auth: FirebaseAuth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()

    init {
        carregarTarefas()
    }

    private fun carregarTarefas() {
        val utilizadorId = auth.currentUser?.uid ?: return

        db.collection("utilizadores")
            .document(utilizadorId)
            .collection("tarefas")
            .addSnapshotListener { snapshot, erro ->
                if (erro != null) {
                    uiState.value = uiState.value.copy(
                        erro = "Erro ao carregar tarefas",
                        carregando = false
                    )
                    Log.w(TAG, "Erro ao carregar tarefas", erro)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val lista = snapshot.documents.map { doc ->
                        Tarefa(
                            id = doc.id,
                            titulo = doc.getString("titulo") ?: "",
                            descricao = doc.getString("descricao") ?: "",
                            prioridade = doc.getString("prioridade") ?: "Normal",
                            concluida = doc.getBoolean("concluida") ?: false
                        )
                    }
                    uiState.value = uiState.value.copy(
                        tarefas = lista,
                        carregando = false,
                        erro = null
                    )
                }
            }
    }

    fun adicionarTarefa(titulo: String, descricao: String, prioridade: String = "Normal") {
        val utilizadorId = auth.currentUser?.uid ?: return

        val novaTarefa = hashMapOf(
            "titulo" to titulo,
            "descricao" to descricao,
            "prioridade" to prioridade,
            "concluida" to false
        )

        db.collection("utilizadores")
            .document(utilizadorId)
            .collection("tarefas")
            .add(novaTarefa)
            .addOnFailureListener { erro ->
                Log.w(TAG, "Erro ao adicionar tarefa", erro)
            }
    }

    fun removerTarefa(tarefaId: String) {
        val utilizadorId = auth.currentUser?.uid ?: return

        db.collection("utilizadores")
            .document(utilizadorId)
            .collection("tarefas")
            .document(tarefaId)
            .delete()
            .addOnFailureListener { erro ->
                Log.w(TAG, "Erro ao remover tarefa", erro)
            }
    }

    fun atualizarTarefa(tarefa: Tarefa) {
        val utilizadorId = auth.currentUser?.uid ?: return

        db.collection("utilizadores")
            .document(utilizadorId)
            .collection("tarefas")
            .document(tarefa.id)
            .set(tarefa)
            .addOnFailureListener { erro ->
                Log.w(TAG, "Erro ao atualizar tarefa", erro)
            }
    }

    fun alternarConcluida(tarefa: Tarefa) {
        atualizarTarefa(tarefa.copy(concluida = !tarefa.concluida))
    }
}
