plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("kotlin-kapt") // <--- NECESARIO para Room
}

android {
    namespace = "com.example.petcareconnect"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.petcareconnect"
        minSdk = 24
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

    //librerias nuevas
    // --- Navegación Compose ---
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // --- Material 3 (versión estable) ---
    implementation("androidx.compose.material3:material3:1.2.1")

    // --- ViewModel + Lifecycle + StateFlow ---
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // --- Corrutinas ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // --- Material Icons Extendidos ---
    implementation("androidx.compose.material:material-icons-extended")

    // --- Room (Base de datos local) ---
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // --- Carga de imágenes con Coil ---
    implementation("io.coil-kt:coil-compose:2.4.0")

    implementation("com.google.code.gson:gson:2.10.1")

    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")

    // ======================================================================
// ✔ LIBRERÍAS PARA PRUEBAS UNITARIAS (NECESARIAS PARA ViewModelTest)
// ======================================================================

// JUnit (ya lo tienes, pero lo dejo para consistencia)
    testImplementation("junit:junit:4.13.2")

// Mockito para mocks
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")

// Permite mockear clases final y funciones suspend
    testImplementation("org.mockito:mockito-inline:5.2.0")

// Coroutines Test — OBLIGATORIO para StateFlow y viewModelScope
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")

// Para asserts más claros (opcional pero recomendado)
    testImplementation("com.google.truth:truth:1.4.0")

// Para testear LiveData/StateFlow fácilmente (contiene InstantTaskExecutorRule)
    testImplementation("androidx.arch.core:core-testing:2.2.0")

// ======================================================================
// ✔ PRUEBAS INSTRUMENTADAS (androidTest) – SOLO SI LUEGO PRUEBAS UI
// ======================================================================
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    // JaCoCo para reportes de cobertura
    testImplementation("org.jacoco:org.jacoco.core:0.8.8")

// MockWebServer para simular servidor en pruebas unitarias (evita fallos por red)
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")

// MockWebServer para pruebas instrumentadas (opcional, pero útil para APIs reales)

    androidTestImplementation("androidx.compose.ui:ui-test-junit4")  // Para Compose

    androidTestImplementation("androidx.test.ext:junit:1.1.5")  // O versión más reciente
}