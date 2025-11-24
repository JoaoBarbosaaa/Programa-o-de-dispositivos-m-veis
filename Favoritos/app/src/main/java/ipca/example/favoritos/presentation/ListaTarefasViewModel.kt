package ipca.example.favoritos.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ipca.example.favoritos.domain.ListaTarefasRepository
import ipca.example.favoritos.domain.Tarefa
import ipca.example.favoritos.domain.ListaTarefasState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListaTarefasViewModel @Inject constructor(
    private val repository: ListaTarefasRepository
) : ViewModel() {

    val uiState: StateFlow<ListaTarefasState> = repository.getTarefasFlow()
        .map { listaTarefas ->
            ListaTarefasState(
                tarefas = listaTarefas,
                carregando = false,
                erro = null
            )
        }
        .catch { erro ->
            emit(
                ListaTarefasState(
                    erro = "Erro ao carregar tarefas: ${erro.message}",
                    carregando = false
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ListaTarefasState(carregando = true)
        )

    fun adicionarTarefa(titulo: String, descricao: String, prioridade: String = "Normal") {
        viewModelScope.launch {
            val novaTarefa = Tarefa(
                titulo = titulo,
                descricao = descricao,
                prioridade = prioridade
            )
            repository.addTask(novaTarefa)
        }
    }

    fun removerTarefa(tarefaId: String) {
        viewModelScope.launch {
            repository.deleteTask(tarefaId)
        }
    }

    fun atualizarTarefa(tarefa: Tarefa) {
        viewModelScope.launch {
            repository.updateTask(tarefa)
        }
    }

    fun alternarConcluida(tarefa: Tarefa) {
        atualizarTarefa(tarefa.copy(concluida = !tarefa.concluida))
    }
}
