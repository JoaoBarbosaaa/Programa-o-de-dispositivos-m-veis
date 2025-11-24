package ipca.example.favoritos.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ipca.example.favoritos.domain.Tarefa
import ipca.example.favoritos.presentation.theme.FavoritosTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaTarefasView(navController: NavController = rememberNavController()) {
    val viewModel: ListaTarefasViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    var tituloNovaTarefa by remember { mutableStateOf("") }
    var descricaoNovaTarefa by remember { mutableStateOf("") }

    var tarefaEditando by remember { mutableStateOf<Tarefa?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("As Minhas Tarefas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = { },
                actions = { }
            )
        },

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = tituloNovaTarefa,
                    onValueChange = { tituloNovaTarefa = it },
                    label = { Text("Nova Tarefa") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (tituloNovaTarefa.isNotBlank()) {
                            viewModel.adicionarTarefa(tituloNovaTarefa, descricaoNovaTarefa)
                            tituloNovaTarefa = ""
                            descricaoNovaTarefa = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = tituloNovaTarefa.isNotBlank() // Desativa se o título estiver vazio
                ) {
                    Text("Adicionar Tarefa")
                }

                Spacer(modifier = Modifier.height(16.dp)) // Espaço antes da lista
            }

            when {
                uiState.carregando -> {
                    CircularProgressIndicator(modifier = Modifier.padding(32.dp))
                    Text("A carregar tarefas...", modifier = Modifier.padding(top = 8.dp))
                }
                uiState.erro != null -> {
                    Text(
                        "🚨 ERRO: ${uiState.erro}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(onClick = { navController.navigate("login") }) {
                        Text("Voltar ao Login")
                    }
                }
                uiState.tarefas.isEmpty() -> {
                    Text(
                        "Nenhuma tarefa por agora.",
                        modifier = Modifier.padding(32.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(top = 8.dp)
                    ) {
                        items(uiState.tarefas, key = { it.id }) { tarefa ->
                            TarefaItem(
                                tarefa = tarefa,
                                onToggleConcluida = { viewModel.alternarConcluida(tarefa) },
                                onDelete = { viewModel.removerTarefa(tarefa.id) },
                                onEdit = { tarefaEditando = tarefa }
                            )
                        }
                    }
                }
            }
        }
    }

    tarefaEditando?.let { tarefa ->
        DialogoEdicaoTarefa(
            tarefa = tarefa,
            onDismiss = { tarefaEditando = null },
            viewModel = viewModel
        )
    }
}

@Composable
fun TarefaItem(
    tarefa: Tarefa,
    onToggleConcluida: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleConcluida() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tarefa.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (tarefa.concluida) TextDecoration.LineThrough else null,
                    color = if (tarefa.concluida) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                if (tarefa.descricao.isNotBlank()) {
                    Text(
                        text = tarefa.descricao,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            IconButton(onClick = onToggleConcluida) {
                Icon(
                    imageVector = if (tarefa.concluida) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                    contentDescription = "Alternar Concluída",
                    tint = if (tarefa.concluida) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Editar Tarefa",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar Tarefa",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun DialogoEdicaoTarefa(
    tarefa: Tarefa,
    onDismiss: () -> Unit,
    viewModel: ListaTarefasViewModel
) {
    var novoTitulo by remember { mutableStateOf(tarefa.titulo) }
    var novaDescricao by remember { mutableStateOf(tarefa.descricao) }
    var novaPrioridade by remember { mutableStateOf(tarefa.prioridade) }

    AlertDialog(
        onDismissRequest = onDismiss,
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
                    onDismiss()
                }
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
fun ListaTarefasViewPreview() {
    FavoritosTheme {
        Text("Preview indisponível devido à dependência Hilt.")
    }
}