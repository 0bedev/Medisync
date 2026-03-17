package com.example.medisync.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.medisync.AlarmDisplayActivity
import com.example.medisync.MainActivity
import com.example.medisync.R

/**
 * Helper para gestionar las notificaciones y alarmas de la aplicación.
 */
object NotificationHelper {

    private const val ALARM_CHANNEL_ID = "medisync_alarm_channel"
    private const val PERSISTENT_CHANNEL_ID = "medisync_persistent_channel"
    private const val CHANNEL_NAME_ALARM = "Alarmas de Medicamentos"
    private const val CHANNEL_NAME_PERSISTENT = "Estado de Citas Médicas"
    
    const val ALARM_NOTIFICATION_ID = 1001
    const val PERSISTENT_NOTIFICATION_ID = 2002

    /**
     * Inicializa los canales de notificación.
     */
    fun init(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (manager.getNotificationChannel(ALARM_CHANNEL_ID) == null) {
                val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build()

                val channel = NotificationChannel(ALARM_CHANNEL_ID, CHANNEL_NAME_ALARM, NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "Canal para alertas críticas de salud"
                    setSound(alarmSound, audioAttributes)
                    enableVibration(true)
                    lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                }
                manager.createNotificationChannel(channel)
            }

            if (manager.getNotificationChannel(PERSISTENT_CHANNEL_ID) == null) {
                val channel = NotificationChannel(PERSISTENT_CHANNEL_ID, CHANNEL_NAME_PERSISTENT, NotificationManager.IMPORTANCE_LOW).apply {
                    description = "Muestra el tiempo restante para tu próxima cita"
                    setShowBadge(false)
                }
                manager.createNotificationChannel(channel)
            }
        }
    }

    fun createPersistentNotification(context: Context, title: String, message: String): Notification {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, PERSISTENT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setSilent(true)
            .build()
    }

    fun updatePersistentNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = createPersistentNotification(context, title, message)
        notificationManager.notify(PERSISTENT_NOTIFICATION_ID, notification)
    }

    fun cancelPersistentNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(PERSISTENT_NOTIFICATION_ID)
    }

    fun cancelNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(ALARM_NOTIFICATION_ID)
    }

    fun showAlarmNotification(context: Context, title: String, message: String, idTratamiento: String? = null, idAlarma: Int? = null) {
        init(context)

        val fullScreenIntent = Intent(context, AlarmDisplayActivity::class.java).apply {
            putExtra("title", title)
            putExtra("message", message)
            putExtra("idTratamiento", idTratamiento)
            putExtra("idAlarma", idAlarma)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val requestCode = System.currentTimeMillis().toInt()
        val fullScreenPendingIntent = PendingIntent.getActivity(context, requestCode, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val builder = NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(alarmSound)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setAutoCancel(true)
            .setOngoing(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(ALARM_NOTIFICATION_ID, builder.build())
    }
}
