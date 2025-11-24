package ipca.example.favoritos

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

data class RegistarState(
    var nome: String? = null,
    var email: String? = null,
    var password: String? = null,
    var confirmarPassword: String? = null,
    var erro: String? = null,
    var carregando: Boolean = false
)

class RegistarViewModel : ViewModel() {

    companion object {
        private const val TAG = "RegistarViewModel"
    }

    var uiState = mutableStateOf(RegistarState())
        private set

    private val auth: FirebaseAuth = Firebase.auth
    private val db = FirebaseFirestore.getInstance()

    fun atualizarNome(nome: String) {
        uiState.value = uiState.value.copy(nome = nome)
    }

    fun atualizarEmail(email: String) {
        uiState.value = uiState.value.copy(email = email)
    }

    fun atualizarPassword(password: String) {
        uiState.value = uiState.value.copy(password = password)
    }

    fun atualizarConfirmarPassword(confirmar: String) {
        uiState.value = uiState.value.copy(confirmarPassword = confirmar)
    }

    fun registar(onRegistoSucesso: () -> Unit) {
        val estado = uiState.value

        if (estado.nome.isNullOrEmpty() || estado.email.isNullOrEmpty() ||
            estado.password.isNullOrEmpty() || estado.confirmarPassword.isNullOrEmpty()
        ) {
            uiState.value = estado.copy(erro = "Por favor, preencha todos os campos.")
            return
        }

        if (estado.password != estado.confirmarPassword) {
            uiState.value = estado.copy(erro = "As palavras-passe não coincidem.")
            return
        }

        uiState.value = estado.copy(carregando = true, erro = null)

        auth.createUserWithEmailAndPassword(estado.email!!, estado.password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val utilizadorId = auth.currentUser?.uid
                    val dadosUtilizador = hashMapOf(
                        "nome" to estado.nome,
                        "email" to estado.email
                    )

                    if (utilizadorId != null) {
                        db.collection("utilizadores")
                            .document(utilizadorId)
                            .set(dadosUtilizador)
                            .addOnSuccessListener {
                                Log.d(TAG, "Utilizador registado e guardado no Firestore.")
                                uiState.value = uiState.value.copy(
                                    carregando = false,
                                    erro = null
                                )
                                onRegistoSucesso()
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Erro ao guardar no Firestore", e)
                                uiState.value = uiState.value.copy(
                                    carregando = false,
                                    erro = "Erro ao guardar no Firestore."
                                )
                            }
                    }
                } else {
                    Log.w(TAG, "Erro no registo", task.exception)
                    uiState.value = uiState.value.copy(
                        carregando = false,
                        erro = "Erro ao criar conta. Verifique o email ou ligação à internet."
                    )
                }
            }
    }
}