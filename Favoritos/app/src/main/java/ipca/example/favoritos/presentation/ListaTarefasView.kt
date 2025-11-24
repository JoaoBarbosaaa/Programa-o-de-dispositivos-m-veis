package ipca.example.favoritos.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable // Importação necessária para o modificador .clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add // Novo Icon
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

@OptIn(ExperimentalMaterial3Api::class) // ⬅️ OptIn para usar CenterAlignedTopAppBar
@Composable
fun ListaTarefasView(navController: NavController = rememberNavController()) {
    // CORREÇÃO CRÍTICA: Substituir viewModel() por hiltViewModel()
    val viewModel: ListaTarefasViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    var tituloNovaTarefa by remember { mutableStateOf("") }
    var descricaoNovaTarefa by remember { mutableStateOf("") }

    // Estado para tarefa que está a ser editada
    var tarefaEditando by remember { mutableStateOf<Tarefa?>(null) }

    Scaffold(
        topBar = {
            // 🚨 CORREÇÃO: Usar CenterAlignedTopAppBar para maior compatibilidade M3
            CenterAlignedTopAppBar(
                title = { Text("As Minhas Tarefas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                // Slots adicionados para satisfazer a assinatura (podem ficar vazios)
                navigationIcon = { /* Opcional: Icone de navegação */ },
                actions = { /* Opcional: Botões de Ação */ }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (tituloNovaTarefa.isNotBlank()) {
                    viewModel.adicionarTarefa(tituloNovaTarefa, descricaoNovaTarefa)
                    tituloNovaTarefa = ""
                    descricaoNovaTarefa = ""
                }
            }) {
                // Melhoria: Usar Icon em vez de Text no FAB
                Icon(Icons.Filled.Add, contentDescription = "Adicionar Tarefa")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 1. Área de Adicionar Nova Tarefa
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = tituloNovaTarefa,
                    onValueChange = { tituloNovaTarefa = it },
                    label = { Text("Nova Tarefa") },
                    modifier = Modifier.weight(1f)
                )
            }

            // 2. Estado de Carregamento ou Erro
            when {
                uiState.carregando -> {
                    CircularProgressIndicator(modifier = Modifier.padding(32.dp))
                    Text("A carregar tarefas...", modifier = Modifier.padding(top = 8.dp))
                }
                uiState.erro != null -> {
                    // Exibir erro do repositório, crucial para debugging
                    Text(
                        "🚨 ERRO: ${uiState.erro}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                    Button(onClick = { navController.navigate("login") }) {
                        Text("Voltar ao Login")
                    }
                }
                uiState.tarefas.isEmpty() -> { // ⬅️ Mensagem de estado vazio
                    Text(
                        "Nenhuma tarefa por agora.\nCrie uma nova usando o campo acima.",
                        modifier = Modifier.padding(32.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> {
                    // 3. Lista de Tarefas
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

    // Diálogo de Edição
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

            // Botão de Status (Concluído/Não Concluído)
            IconButton(onClick = onToggleConcluida) {
                Icon(
                    imageVector = if (tarefa.concluida) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                    contentDescription = "Alternar Concluída",
                    tint = if (tarefa.concluida) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Botão de Edição
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Editar Tarefa",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Botão de Eliminar
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
        // Nota: O Preview falhará porque a ViewModel requer injeção real do Hilt
        // Pode ser simulado usando um MOCK ViewModel ou um PreviewParameterProvider
        Text("Preview indisponível devido à dependência Hilt.")
    }
}