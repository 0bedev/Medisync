package com.example.medisync.services

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.example.medisync.data.AppDataBase
import com.example.medisync.utils.NotificationHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class AppointmentService : Service() {

    private val serviceJob = SupervisorJob()

    private val serviceScope =
        CoroutineScope(Dispatchers.Main + serviceJob)

    private val handler =
        Handler(Looper.getMainLooper())

    private var lastMessage = ""

    private val updateRunnable =
        object : Runnable {
            override fun run() {
                updateNotification()
                handler.postDelayed(
                    this,
                    60000
                )
            }
        }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.init(this)
        startForeground(
            NotificationHelper.PERSISTENT_NOTIFICATION_ID,
            NotificationHelper.createPersistentNotification(
                this,
                "Medisync",
                "Buscando próximas citas..."
            )
        )
        handler.post(updateRunnable)
    }

    private fun updateNotification(){
        serviceScope.launch {
            val database =
                AppDataBase.getDatabase(applicationContext)
            val citas =
                withContext(Dispatchers.IO){
                    database
                        .daoCitas()
                        .getAll()
                        .first()
                }

            // Si no hay citas detener servicio
            if(citas.isEmpty()){
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return@launch
            }

            val ahora =
                System.currentTimeMillis()

            val proximaCita =
                citas
                    .filter {
                        it.fechaHora > ahora
                    }
                    .minByOrNull {
                        it.fechaHora
                    }

            if(proximaCita == null){
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return@launch
            }

            val diff =
                proximaCita.fechaHora - ahora
            val dias =
                TimeUnit.MILLISECONDS.toDays(diff)
            val horas =
                TimeUnit.MILLISECONDS.toHours(diff)%24
            val minutos =
                TimeUnit.MILLISECONDS.toMinutes(diff)%60

            val mensaje = when{
                dias>0 ->
                    "Faltan $dias d $horas h $minutos m para ${proximaCita.citaNombre}"
                horas>0 ->
                    "Faltan $horas h $minutos m para ${proximaCita.citaNombre}"
                else ->
                    "Faltan $minutos min para ${proximaCita.citaNombre}"
            }

            // Solo actualizar si cambia
            if(mensaje != lastMessage){
                NotificationHelper
                    .updatePersistentNotification(
                        applicationContext,
                        "Próxima Cita",
                        mensaje
                    )
                lastMessage = mensaje
            }
        }

    }

    override fun onStartCommand(
        intent:Intent?,
        flags:Int,
        startId:Int
    ):Int{
        return START_STICKY
    }

    override fun onBind(
        intent:Intent?
    ):IBinder? = null
    override fun onDestroy(){
        handler.removeCallbacks(updateRunnable)
        serviceJob.cancel()
        super.onDestroy()
    }
}