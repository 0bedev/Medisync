package com.example.medisync.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * BroadcastReceiver que se activa cuando se dispara una alarma programada.
 * Funciona incluso si la app está cerrada.
 */
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "¡Recordatorio Medisync!"
        val message = intent.getStringExtra("message") ?: "Es hora de tu medicamento."
        
        // Datos extra para la reprogramación
        val idTratamiento = intent.getStringExtra("idTratamiento")
        val idAlarma = intent.getIntExtra("id", 0)

        // Usamos la nueva función del Helper que maneja el lanzamiento de pantalla completa
        NotificationHelper.showAlarmNotification(context, title, message, idTratamiento, idAlarma)
    }
}
