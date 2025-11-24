package ipca.example.favoritos.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ipca.example.favoritos.domain.TaskRepository // Importar a INTERFACE do Domínio
import ipca.example.favoritos.domain.Tarefa
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListaTarefasState(
    val tarefas: List<Tarefa> = emptyList(),
    val erro: String? = null,
    val carregando: Boolean = true
)

// ➡️ Usar Hilt para injetar o Repositório
@HiltViewModel
class ListaTarefasViewModel @Inject constructor(
    // ➡️ O ViewModel apenas conhece o Contrato (Interface)
    private val repository: TaskRepository
) : ViewModel() {

    // ➡️ StateFlow para lidar com o fluxo de dados em tempo real do Repositório
    val uiState: StateFlow<ListaTarefasState> = repository.getTarefasFlow()
        .map { listaTarefas ->
            // Mapear a lista de domínio para o State da View
            ListaTarefasState(
                tarefas = listaTarefas,
                carregando = false,
                erro = null
            )
        }
        .catch { erro ->
            // Capturar e lidar com erros vindos do Repositório
            emit(ListaTarefasState(erro = "Erro ao carregar tarefas: ${erro.message}", carregando = false))
        }
        .stateIn(
            scope = viewModelScope,
            // Iniciar o fluxo quando houver coletores (Views)
            started = SharingStarted.WhileSubscribed(5000),
            // Valor inicial
            initialValue = ListaTarefasState(carregando = true)
        )

    fun adicionarTarefa(titulo: String, descricao: String, prioridade: String = "Normal") {
        viewModelScope.launch {
            val novaTarefa = Tarefa(
                titulo = titulo,
                descricao = descricao,
                prioridade = prioridade
            )
            // ➡️ Delega a ação para o Repositório
            repository.addTask(novaTarefa)
        }
    }

    fun removerTarefa(tarefaId: String) {
        viewModelScope.launch {
            // ➡️ Delega a ação para o Repositório
            repository.deleteTask(tarefaId)
        }
    }

    fun atualizarTarefa(tarefa: Tarefa) {
        viewModelScope.launch {
            // ➡️ Delega a ação para o Repositório
            repository.updateTask(tarefa)
        }
    }

    fun alternarConcluida(tarefa: Tarefa) {
        atualizarTarefa(tarefa.copy(concluida = !tarefa.concluida))
    }
}