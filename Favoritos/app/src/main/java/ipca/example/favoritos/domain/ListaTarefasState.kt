package ipca.example.favoritos.domain

data class ListaTarefasState(
    val tarefas: List<Tarefa> = emptyList(),
    val erro: String? = null,
    val carregando: Boolean = true
)