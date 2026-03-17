package com.example.medisync // Nombre del paquete de la aplicación


import android.os.Bundle // Importa la clase Bundle para manejar el estado de la actividad
import androidx.appcompat.app.AppCompatActivity // Importa AppCompatActivity para la actividad compatible con versiones antiguas
import android.content.Intent
import android.widget.Button

import com.example.medisync.utils.NotificationHelper
import android.os.Build

import android.content.Context
import android.app.AlarmManager
import androidx.appcompat.app.AppCompatDelegate
import com.example.medisync.services.AppointmentService


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

        // Fuerza el modo claro en toda la aplicación
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Crea el canal de notificación
        NotificationHelper.init(this)
        //---------------------------
        //Permisos de notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                startActivity(Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            }
        }


        // Servicio de notificaciones
        val intent = Intent(this, AppointmentService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }





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


        val btnAggMedicamento = findViewById<Button>(R.id.btnAggMedicamento)

        btnAggMedicamento.setOnClickListener {
            val intent = Intent(this, AgregarMedicamentoActivity::class.java)
            startActivity(intent)

        }

        val btnCrearTratamiento = findViewById<Button>(R.id.btnCrearTratamiento)

        btnCrearTratamiento.setOnClickListener {
            val intent = Intent(this, CrearTratamientoActivity::class.java)
            startActivity(intent)
        }


        val btnDosis = findViewById<Button>(R.id.btnAdminDosis)

        btnDosis.setOnClickListener {
            val intent = Intent(this, AdministrarDosisActivity::class.java)
            startActivity(intent)
        }

    }
}


















