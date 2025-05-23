package vn.com.rd.waterreminder.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import vn.com.rd.waterreminder.R
import vn.com.rd.waterreminder.ui.main.MainActivity

class ReminderReceiver : BroadcastReceiver() {
    companion object {
        const val CHANNEL_ID = "water_reminder_channel"
        const val NOTIFICATION_ID = 1
        const val EXTRA_REMINDER_ID = "reminder_id"
        const val EXTRA_REMINDER_MESSAGE = "reminder_message"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, 0)
        val message = intent.getStringExtra(EXTRA_REMINDER_MESSAGE) ?: "Đã đến giờ uống nước!"

        // Tạo notification channel (chỉ cần thiết cho Android 8.0+)
        createNotificationChannel(context)

        // Tạo intent để mở ứng dụng khi nhấn vào thông báo
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("REMINDER_ID", reminderId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            reminderId.toInt(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Xây dựng thông báo
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water_drop) // Bạn cần tạo một icon này
            .setContentTitle("Nhắc nhở uống nước")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Hiển thị thông báo
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(reminderId.toInt(), notification)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Water Reminder"
            val descriptionText = "Thông báo nhắc nhở uống nước"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
