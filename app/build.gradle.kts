plugins {
    alias(libs.plugins.android.application)
    // Habilita KSP (Kotlin Symbol Processing), necesario para procesar las anotaciones de Room
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.medisync"
    compileSdk {
        version = release(36)
    }

    // Configuraciones de la aplicación
    defaultConfig {
        applicationId = "com.example.medisync"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Habilita la optimización de código para tipo de release
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Configura la versión de Java para el proyecto
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Define la versión de Room para mantener consistencia en todas sus librerías
    val room_version = "2.8.4"

    // Procesador de anotaciones: Genera el código interno de Room durante la compilación
    ksp("androidx.room:room-compiler:$room_version")
    
    // Extensiones de Kotlin: Permite usar Corrutinas y Flow con Room para operaciones asíncronas
    implementation("androidx.room:room-ktx:${room_version}")
    
    // Librería principal: Contiene las clases base y la lógica para manejar la base de datos
    implementation("androidx.room:room-runtime:${room_version}")

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // Dependencias de CalendarView
    implementation("com.github.prolificinteractive:material-calendarview:2.0.1") {
        exclude(group = "com.android.support") // Excluye las dependencias de soporte de Android
    }

    // Dependencias base del proyecto
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
