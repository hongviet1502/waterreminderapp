package vn.com.rd.waterreminder.service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import vn.com.rd.waterreminder.R
import kotlin.random.Random

// ReminderReceiver.kt
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        showNotification(context)
    }

    private fun showNotification(context: Context) {
        val channelId = "water_reminder_channel"
        val notificationId = Random.nextInt(1, 1000)

        // 2. Tạo âm thanh và rung
        val soundUri = Uri.parse("android.resource://${context.packageName}/${R.raw.sample_sound}")
        val vibrationPattern = longArrayOf(0, 500, 300, 500) // Rung 2 lần

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Đã đến giờ uống nước!")
            .setContentText("Bạn đã uống đủ nước chưa? 🚰")
            .setSmallIcon(R.drawable.ic_water_drop)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(soundUri) // Âm thanh
            .setVibrate(vibrationPattern) // Rung (cần quyền)
            .build()

        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification)
    }
}