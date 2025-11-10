package ipca.example.favoritos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ipca.example.favoritos.ui.theme.FavoritosTheme

@Composable
fun RegistarView(
    navController: NavController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val viewModel: RegistarViewModel = viewModel()
    val uiState by viewModel.uiState

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Criar Conta", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = uiState.nome ?: "",
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { viewModel.atualizarNome(it) }
        )

        TextField(
            value = uiState.email ?: "",
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            onValueChange = { viewModel.atualizarEmail(it) }
        )

        TextField(
            value = uiState.password ?: "",
            label = { Text("Palavra-passe") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            onValueChange = { viewModel.atualizarPassword(it) }
        )

        TextField(
            value = uiState.confirmarPassword ?: "",
            label = { Text("Confirmar palavra-passe") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            onValueChange = { viewModel.atualizarConfirmarPassword(it) }
        )

        if (uiState.erro != null) {
            Text(
                text = uiState.erro!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            onClick = {
                viewModel.registar {
                    navController.navigate("login")
                }
            }
        ) {
            Text("Registar")
        }

        if (uiState.carregando) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        TextButton(
            onClick = { navController.navigate("login") },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Já tenho conta")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistarViewPreview() {
    FavoritosTheme {
        RegistarView()
    }
}
