package com.example.medisync // Nombre del paquete de la aplicación


import android.os.Bundle // Importa la clase Bundle para manejar el estado de la actividad
import androidx.appcompat.app.AppCompatActivity // Importa AppCompatActivity para la actividad compatible con versiones antiguas
import android.content.Intent
import android.widget.Button

/**
 * MainActivity es la pantalla principal de la aplicación Medisync.
 * Se encarga de mostrar las opciones principales al usuario.
 */
class MainActivity : AppCompatActivity() { // MainActivity hereda de AppCompatActivity

    /**
     * Inicializa la actividad, establece el diseño de la interfaz de usuario
     * y configura los componentes iniciales.
     *
     * @param savedInstanceState Si la actividad se está recreando a partir de un estado guardado
     * anterior, este es el estado.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnAgendar = findViewById<Button>(R.id.btnAgendCitas)

        btnAgendar.setOnClickListener {
            val intent = Intent(this, AgendarCitaActivity::class.java)
            startActivity(intent)
        }

        val btnMedicamentos = findViewById<Button>(R.id.btnAdminMedicamentos)

        btnMedicamentos.setOnClickListener {
            val intent = Intent(this, MisMedicamentosActivity::class.java)
            startActivity(intent)
        }

        val btnCitas = findViewById<Button>(R.id.btnAdminCitasMedicas)

        btnCitas.setOnClickListener {
            val intent = Intent(this, MisCitasActivity::class.java)
            startActivity(intent)
        }


        val btnDosis = findViewById<Button>(R.id.btnAdminDosis)

        btnDosis.setOnClickListener {
            val intent = Intent(this, AdministrarDosisActivity::class.java)
            startActivity(intent)
        }

    }
}


















