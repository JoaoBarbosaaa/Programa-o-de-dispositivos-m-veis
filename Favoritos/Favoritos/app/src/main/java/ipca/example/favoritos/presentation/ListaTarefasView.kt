package ipca.example.favoritos.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ipca.example.favoritos.domain.Tarefa
import ipca.example.favoritos.presentation.theme.FavoritosTheme

@Composable
fun ListaTarefasView(navController: NavController = rememberNavController()) {
    val viewModel: ListaTarefasViewModel = viewModel()
    val uiState by viewModel.uiState

    var tituloNovaTarefa by remember { mutableStateOf("") }
    var descricaoNovaTarefa by remember { mutableStateOf("") }

    // Estado para tarefa que está a ser editada
    var tarefaEditando by remember { mutableStateOf<Tarefa?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Minhas Tarefas",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Formulário para criar nova tarefa
        OutlinedTextField(
            value = tituloNovaTarefa,
            onValueChange = { tituloNovaTarefa = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = descricaoNovaTarefa,
            onValueChange = { descricaoNovaTarefa = it },
            label = { Text("Descrição") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Button(
            onClick = {
                if (tituloNovaTarefa.isNotBlank()) {
                    viewModel.adicionarTarefa(tituloNovaTarefa, descricaoNovaTarefa)
                    tituloNovaTarefa = ""
                    descricaoNovaTarefa = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Adicionar Tarefa")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.carregando) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn {
                items(uiState.tarefas) { tarefa ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = tarefa.titulo,
                                    style = MaterialTheme.typography.titleMedium,
                                    textDecoration = if (tarefa.concluida) TextDecoration.LineThrough else TextDecoration.None
                                )
                                Text(
                                    text = tarefa.descricao,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Prioridade: ${tarefa.prioridade}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            IconButton(onClick = { viewModel.alternarConcluida(tarefa) }) {
                                Icon(
                                    imageVector = if (tarefa.concluida) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = "Concluída"
                                )
                            }

                            IconButton(onClick = { viewModel.removerTarefa(tarefa.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remover"
                                )
                            }

                            IconButton(onClick = { tarefaEditando = tarefa }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog para editar tarefa
    tarefaEditando?.let { tarefa ->
        var novoTitulo by remember { mutableStateOf(tarefa.titulo) }
        var novaDescricao by remember { mutableStateOf(tarefa.descricao) }
        var novaPrioridade by remember { mutableStateOf(tarefa.prioridade) }

        AlertDialog(
            onDismissRequest = { tarefaEditando = null },
            title = { Text("Editar Tarefa") },
            text = {
                Column {
                    OutlinedTextField(
                        value = novoTitulo,
                        onValueChange = { novoTitulo = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = novaDescricao,
                        onValueChange = { novaDescricao = it },
                        label = { Text("Descrição") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = novaPrioridade,
                        onValueChange = { novaPrioridade = it },
                        label = { Text("Prioridade") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.atualizarTarefa(
                            tarefa.copy(
                                titulo = novoTitulo,
                                descricao = novaDescricao,
                                prioridade = novaPrioridade
                            )
                        )
                        tarefaEditando = null
                    }
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                Button(onClick = { tarefaEditando = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ListaTarefasViewPreview() {
    FavoritosTheme {
        ListaTarefasView()
    }
}
