package ipca.example.favoritos.domain

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val COLLECTION_TAREFAS = "tasks"
private const val COLLECTION_PERFIS = "users"

@Singleton
class ListaTarefasRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private fun getUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("Utilizador não autenticado ao tentar aceder a dados.")
    }

    fun getTarefasFlow(): Flow<List<Tarefa>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            close(Exception("Acesso negado: O utilizador não está autenticado."))
            return@callbackFlow
        }

        val tarefasRef = firestore
            .collection(COLLECTION_PERFIS)
            .document(userId)
            .collection(COLLECTION_TAREFAS)

        val subscription = tarefasRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val tarefas = snapshot.documents.mapNotNull { doc ->
                    doc.toObject<Tarefa>()?.copy(id = doc.id)
                }
                trySend(tarefas)
            }
        }

        awaitClose { subscription.remove() }
    }

    suspend fun addTask(tarefa: Tarefa) {
        val userId = getUserId()

        firestore
            .collection(COLLECTION_PERFIS)
            .document(userId)
            .collection(COLLECTION_TAREFAS)
            .add(tarefa)
            .await()
    }

    suspend fun updateTask(tarefa: Tarefa) {
        val userId = getUserId()

        if (tarefa.id.isBlank())
            throw IllegalArgumentException("ID da Tarefa não pode ser nulo para atualização.")

        val tarefaMap = mapOf(
            "titulo" to tarefa.titulo,
            "descricao" to tarefa.descricao,
            "prioridade" to tarefa.prioridade,
            "concluida" to tarefa.concluida
        )

        firestore
            .collection(COLLECTION_PERFIS)
            .document(userId)
            .collection(COLLECTION_TAREFAS)
            .document(tarefa.id)
            .update(tarefaMap)
            .await()
    }

    suspend fun deleteTask(tarefaId: String) {
        val userId = getUserId()

        firestore
            .collection(COLLECTION_PERFIS)
            .document(userId)
            .collection(COLLECTION_TAREFAS)
            .document(tarefaId)
            .delete()
            .await()
    }
}
