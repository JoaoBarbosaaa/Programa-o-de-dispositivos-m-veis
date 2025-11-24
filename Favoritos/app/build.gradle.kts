// build.gradle.kts (Module: app)

plugins {
    id("com.android.application")
    // O id("com.android.application") é a mesma coisa que alias(libs.plugins.android.application)
    // Se quiser usar o alias, retire o id("com.android.application")

    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")

    // ➡️ CORREÇÃO 1: Aplicar o plugin Hilt (isto resolve o problema original do Hilt)
    id("com.google.dagger.hilt.android")

    // ➡️ CORREÇÃO 2: Aplicar o plugin KSP (isto resolve o erro 'Unresolved reference: ksp')
    id("com.google.devtools.ksp")

    // Nota: As versões dos plugins (ex: version "2.57.2" apply false) devem estar no build.gradle.kts de nível superior (project/root)
    // e NÃO aqui no build.gradle.kts do módulo. Você já as tem lá, por isso basta remover o 'version' e o 'apply false' daqui.
}

android {
    namespace = "ipca.example.favoritos"
    compileSdk = 36

    defaultConfig {
        applicationId = "ipca.example.favoritos"
        minSdk = 25
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // ⚠️ Atenção: Estava a duplicar o Firebase BOM, mantive o mais recente que colocou (34.4.0 e 34.5.0) e a configuração que faz sentido.
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.3")
    implementation("androidx.compose.material:material-icons-extended:1.5.0")

    // Dagger - Hilt (Permanecem no bloco dependencies)
    implementation("com.google.dagger:hilt-android:2.57.2")
    // O 'ksp' agora é reconhecido porque o plugin ksp foi aplicado acima.
    ksp("com.google.dagger:hilt-android-compiler:2.57.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

}