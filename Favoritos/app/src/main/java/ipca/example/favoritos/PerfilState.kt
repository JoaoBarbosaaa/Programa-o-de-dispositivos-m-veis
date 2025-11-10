package ipca.example.favoritos

data class PerfilState(
    var nome: String? = null,
    var email: String? = null,
    var erro: String? = null,
    var carregando: Boolean = true,
    var sucesso: String? = null
)
