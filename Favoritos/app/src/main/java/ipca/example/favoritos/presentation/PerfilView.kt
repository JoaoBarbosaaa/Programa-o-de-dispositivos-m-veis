package ipca.example.favoritos.presentation

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
import ipca.example.favoritos.presentation.theme.FavoritosTheme
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PerfilView(
    navController: NavController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val viewModel: PerfilViewModel = viewModel()
    val uiState by viewModel.uiState

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Perfil do Utilizador",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.carregando) {
            CircularProgressIndicator()
        } else {
            uiState.erro?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            uiState.sucesso?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            TextField(
                value = uiState.nome ?: "",
                onValueChange = { viewModel.alterarNomeLocal(it) },
                label = { Text("Nome") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                singleLine = true
            )

            TextField(
                value = uiState.email ?: "",
                onValueChange = {},
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                enabled = false,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.atualizarNomeFirestore() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Atualizar Nome")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    authSair()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Sair da Conta")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PerfilViewPreview() {
    FavoritosTheme {
        PerfilView()
    }
}

private fun authSair() {
    val auth = FirebaseAuth.getInstance()
    auth.signOut()
}