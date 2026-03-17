package com.example.medisync

import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.medisync.data.AppDataBase
import com.example.medisync.utils.AlarmHelper
import com.example.medisync.utils.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * Actividad que se muestra a pantalla completa cuando se dispara una alarma.
 * Soporta la reprogramación automática de dosis para tratamientos continuos.
 */
class AlarmDisplayActivity : AppCompatActivity() {

    private var ringtone: Ringtone? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        NotificationHelper.cancelNotification(this)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }

        setContentView(R.layout.activity_alarm_display)

        val title = intent.getStringExtra("title") ?: "¡RECORDATORIO!"
        val message = intent.getStringExtra("message") ?: "Es hora de tu medicamento."
        val idTratamiento = intent.getStringExtra("idTratamiento")
        val idAlarma = intent.getIntExtra("idAlarma", -1)

        findViewById<TextView>(R.id.tvAlarmTitle).text = title
        findViewById<TextView>(R.id.tvAlarmMessage).text = message

        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        
        ringtone = RingtoneManager.getRingtone(applicationContext, alarmUri)
        ringtone?.play()

        findViewById<Button>(R.id.btnStopAlarm).setOnClickListener {
            detenerYReprogramar(idTratamiento, idAlarma, title, message)
        }
    }

    private fun detenerYReprogramar(idTratamiento: String?, idAlarma: Int, title: String, message: String) {
        ringtone?.stop()

        if (idTratamiento != null && idAlarma != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDataBase.getDatabase(applicationContext)
                val dosis = db.daoDosis().getById(idTratamiento).firstOrNull()

                if (dosis != null && dosis.activo) {
                    val ahora = System.currentTimeMillis()
                    val duracionMilis = dosis.duracionDias.toLong() * 24 * 60 * 60 * 1000
                    val finTratamiento = dosis.fechaHoraInicio + duracionMilis

                    // Calcular la siguiente toma
                    val proximaToma = ahora + (dosis.intervaloHoras.toLong() * 60 * 60 * 1000)

                    if (proximaToma < finTratamiento) {
                        // Aún no termina el tratamiento, reprogramamos
                        AlarmHelper.setExactAlarm(
                            context = applicationContext,
                            id = idAlarma,
                            timeInMillis = proximaToma,
                            title = title,
                            message = message,
                            idTratamiento = idTratamiento
                        )
                    }
                }
            }
        }
        finish()
    }

    override fun onDestroy() {
        ringtone?.stop()
        super.onDestroy()
    }
}
