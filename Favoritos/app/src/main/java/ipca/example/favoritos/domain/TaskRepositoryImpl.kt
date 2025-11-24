package ipca.example.favoritos.domain

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import ipca.example.favoritos.domain.TaskRepository
import ipca.example.favoritos.domain.Tarefa
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

// 🚨 CORREÇÃO DE NOMENCLATURA MAIS PROVÁVEL:
// Alterado para "users" e "tasks" (nomes comuns em inglês no Firebase).
// Se o seu Firestore for diferente, atualize estas constantes.
private const val COLLECTION_TAREFAS = "tasks" // Caminho no Firestore: users/{userId}/tasks
private const val COLLECTION_PERFIS = "users"   // Caminho no Firestore: users/{userId}/tasks

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : TaskRepository {

    // Helper para obter o ID do utilizador logado
    private fun getUserId(): String {
        return auth.currentUser?.uid ?: throw IllegalStateException("Utilizador não autenticado ao tentar aceder a dados.")
    }

    /**
     * Fluxo de dados em tempo real para a lista de tarefas do utilizador.
     * Consulta o caminho: users/{userId}/tasks
     */
    override fun getTarefasFlow(): Flow<List<Tarefa>> = callbackFlow {
        // Verifica o utilizador ANTES de iniciar o listener do Firestore
        val userId = auth.currentUser?.uid
        if (userId == null) {
            close(Exception("Acesso negado: O utilizador não está autenticado."))
            return@callbackFlow
        }

        // Caminho da coleção de tarefas específico do utilizador
        val tarefasRef = firestore
            .collection(COLLECTION_PERFIS) // users
            .document(userId)             // {userId}
            .collection(COLLECTION_TAREFAS) // tasks

        // Adiciona o Listener em tempo real do Firestore
        val subscription = tarefasRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                // Mapeia os documentos para a lista de Tarefas.
                // Usar .copy(id = doc.id) garante que o ID é atribuído.
                val tarefas = snapshot.documents.mapNotNull { doc ->
                    doc.toObject<Tarefa>()?.copy(id = doc.id)
                }
                trySend(tarefas)
            }
        }

        // O callbackFlow termina quando o coletor (ViewModel) é cancelado
        awaitClose { subscription.remove() }
    }

    // --- Funções CRUD (usam o mesmo caminho: users/{userId}/tasks) ---

    override suspend fun addTask(tarefa: Tarefa) {
        val userId = getUserId()
        firestore
            .collection(COLLECTION_PERFIS)
            .document(userId)
            .collection(COLLECTION_TAREFAS)
            .add(tarefa)
            .await()
    }

    override suspend fun updateTask(tarefa: Tarefa) {
        val userId = getUserId()
        if (tarefa.id.isBlank()) throw IllegalArgumentException("ID da Tarefa não pode ser nulo para atualização.")

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

    override suspend fun deleteTask(tarefaId: String) {
        val userId = getUserId()
        firestore
            .collection(COLLECTION_PERFIS)
            .document(userId)
            .collection(COLLECTION_TAREFAS)
            .document(tarefaId)
            .delete()
            .await()
    }

    // --- Funções de Autenticação e Perfil ---

    override suspend fun login(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun register(email: String, password: String): Boolean {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun saveUserProfile(nome: String, email: String) {
        val userId = getUserId()
        val profileData = mapOf(
            "nome" to nome,
            "email" to email,
            "dataRegisto" to com.google.firebase.Timestamp.now()
        )
        firestore
            .collection(COLLECTION_PERFIS) // users
            .document(userId)
            .set(profileData)
            .await()
    }
}