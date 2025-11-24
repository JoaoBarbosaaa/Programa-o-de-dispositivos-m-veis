// ipca.example.favoritos/FavoritosApp.kt

package ipca.example.favoritos

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// ➡️ A anotação @HiltAndroidApp é essencial.
@HiltAndroidApp
class FavoritosApp : Application()