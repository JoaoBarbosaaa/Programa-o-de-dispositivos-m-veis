package ipca.example.favoritos.domain

data class Tarefa(
    var id: String = "",
    var titulo: String = "",
    var descricao: String = "",
    var prioridade: String = "Normal", // Normal, Alta, Baixa
    var concluida: Boolean = false
)