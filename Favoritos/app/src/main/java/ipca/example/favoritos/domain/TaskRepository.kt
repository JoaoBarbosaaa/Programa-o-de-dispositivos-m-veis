package ipca.example.favoritos.domain

import kotlinx.coroutines.flow.Flow

/**
 * Contrato (Interface) para o acesso a dados de Tarefas.
 * Define o que a camada de dados DEVE fazer, sem especificar COMO.
 * As ViewModels (Presentation) apenas conhecem esta interface.
 */
interface TaskRepository {

    // ➡️ CORREÇÃO: Esta função substitui a obsoleta 'getTasks()' e usa Flow para tempo real.
    fun getTarefasFlow(): Flow<List<Tarefa>>

    // --- Funções CRUD ---
    suspend fun addTask(tarefa: Tarefa)
    suspend fun updateTask(tarefa: Tarefa)

    // ➡️ CORREÇÃO: Usa o ID (String) para eliminar, igual à implementação.
    suspend fun deleteTask(tarefaId: String)

    // --- Funções de Autenticação e Perfil ---
    suspend fun login(email: String, password: String): Boolean
    suspend fun register(email: String, password: String): Boolean
    suspend fun saveUserProfile(nome: String, email: String)
}