package ipca.example.favoritos.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ipca.example.favoritos.domain.TaskRepository // ⬅️ Importar a interface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegistarState(
    var nome: String? = null,
    var email: String? = null,
    var password: String? = null,
    var confirmarPassword: String? = null,
    var erro: String? = null,
    var carregando: Boolean = false,
    var isRegistrationSuccessful: Boolean = false // ⬅️ Adicionado para gestão de navegação
)

@HiltViewModel // ⬅️ Anotação Hilt
class RegistarViewModel @Inject constructor(
    // ➡️ Injeção do Contrato (TaskRepository)
    private val repository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistarState())
    val uiState: StateFlow<RegistarState> = _uiState

    // ➡️ Funções de atualização de estado...
    fun atualizarNome(nome: String) {
        _uiState.value = _uiState.value.copy(nome = nome, erro = null)
    }

    fun atualizarEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email, erro = null)
    }

    fun atualizarPassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, erro = null)
    }

    fun atualizarConfirmarPassword(confirmar: String) {
        _uiState.value = _uiState.value.copy(confirmarPassword = confirmar, erro = null)
    }

    // ➡️ A função agora usa corrotinas e delega a lógica ao Repositório
    fun registar() {
        val estado = _uiState.value

        if (estado.nome.isNullOrEmpty() || estado.email.isNullOrEmpty() ||
            estado.password.isNullOrEmpty() || estado.confirmarPassword.isNullOrEmpty()
        ) {
            _uiState.value = estado.copy(erro = "Por favor, preencha todos os campos.")
            return
        }

        if (estado.password != estado.confirmarPassword) {
            _uiState.value = estado.copy(erro = "As palavras-passe não coincidem.")
            return
        }

        // ⚠️ O Callback (onRegistoSucesso) é substituído pela atualização de estado
        _uiState.value = estado.copy(carregando = true, erro = null)

        viewModelScope.launch {
            try {
                // ➡️ Chama o método registar no Repositório
                val registrationSuccess = repository.register(estado.email!!, estado.password!!)

                if (registrationSuccess) {
                    // Após criar a conta, guarda os dados do perfil (opcional)
                    repository.saveUserProfile(estado.nome!!, estado.email!!)

                    _uiState.value = _uiState.value.copy(
                        carregando = false,
                        isRegistrationSuccessful = true
                    )
                } else {
                    // O repositório falhou (ex: email já em uso)
                    _uiState.value = _uiState.value.copy(
                        carregando = false,
                        erro = "Erro ao criar conta. Email inválido ou já em uso."
                    )
                }
            } catch (e: Exception) {
                // Erro de rede ou outra exceção não tratada
                _uiState.value = _uiState.value.copy(
                    carregando = false,
                    erro = "Erro de comunicação: ${e.message}"
                )
            }
        }
    }
}