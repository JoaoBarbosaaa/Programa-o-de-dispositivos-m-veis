package ipca.example.favoritos.domain

/**
 * Contrato (Interface) para o acesso a dados de Tarefas.
 * Define o que a camada de dados DEVE fazer, sem especificar COMO.
 * As ViewModels (Presentation) apenas conhecem esta interface.
 */
interface TaskRepository {

    // Funções para CRUD
    suspend fun getTasks(): List<Tarefa>
    suspend fun addTask(tarefa: Tarefa)
    suspend fun updateTask(tarefa: Tarefa)
    suspend fun deleteTask(tarefa: Tarefa)

    // Funções de Autenticação/Perfil (Se for tudo no mesmo repo)
    suspend fun login(email: String, password: String): Boolean
    // ... outras funções de Perfil

        // ... outros métodos (login, getTarefasFlow, etc.)

        // Adicione a função de registo
        suspend fun register(email: String, password: String): Boolean

        // Adicione a função para guardar o perfil no Firestore após o registo
        suspend fun saveUserProfile(nome: String, email: String)

}