package com.example.petcareconnect.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.petcareconnect.MainActivity
import com.example.petcareconnect.R
import java.util.Locale

/**
 * Helper para gestionar notificaciones locales en Android
 */
object NotificationHelper {

    private const val CHANNEL_ID = "petcare_general"
    private const val CHANNEL_NAME = "PetCare Notificaciones"
    private const val CHANNEL_DESCRIPTION = "Notificaciones generales de PetCare Connect"

    // IDs únicos para diferentes tipos de notificaciones
    private const val NOTIFICATION_ID_REGISTRO = 1001
    private const val NOTIFICATION_ID_LOGIN = 1002
    private const val NOTIFICATION_ID_COMPRA = 1003

    /**
     * Crear el canal de notificaciones (necesario para Android 8.0+)
     * Se debe llamar al iniciar la app
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Mostrar notificación de registro exitoso
     */
    fun showRegistroExitosoNotification(context: Context, nombreUsuario: String) {
        // Verificar permisos (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // No tenemos permiso, no mostramos notificación
                return
            }
        }

        // Intent para abrir la app cuando se toque la notificación
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Construir la notificación
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_petcare_logo) // Usa el ícono de tu app
            .setContentTitle("¡Registro Exitoso!")
            .setContentText("Bienvenido/a $nombreUsuario a PetCare Connect")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Tu cuenta ha sido creada exitosamente. Ya puedes iniciar sesión y disfrutar de todos nuestros servicios para el cuidado de tu mascota.")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Se cierra al tocarla
            .setVibrate(longArrayOf(0, 500, 200, 500)) // Patrón de vibración
            .build()

        // Mostrar la notificación
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_REGISTRO, notification)
    }

    /**
     * Mostrar notificación de login exitoso (opcional)
     */
    fun showLoginExitosoNotification(context: Context, nombreUsuario: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_petcare_logo)
            .setContentTitle("Inicio de Sesión Exitoso")
            .setContentText("¡Hola $nombreUsuario! Bienvenido/a de nuevo")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_LOGIN, notification)
    }

    /**
     * Mostrar notificación de compra realizada (opcional)
     */
    @SuppressLint("MissingPermission")
    fun showCompraRealizadaNotification(context: Context, total: Double) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_petcare_logo)
            .setContentTitle("¡Compra Realizada!")
            .setContentText("Tu compra por $${String.format(Locale.getDefault(), "%.2f", total)} se ha procesado exitosamente")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_COMPRA, notification)
    }
}

