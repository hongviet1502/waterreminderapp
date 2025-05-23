package vn.com.rd.waterreminder.service

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import vn.com.rd.waterreminder.R

class WaterAlarmService : Service() {
    private val channelId = "water_reminder_channel"
    private val notificationId = 1

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            startForeground(notificationId, buildNotification("Service đang chạy"))
        } else {
            startForeground(notificationId, buildNotification("Service đang chạy"), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scheduleReminders()
        return START_STICKY
    }

    private fun scheduleReminders() {
        // Lịch trình nhắc uống nước (ví dụ: mỗi 2 tiếng)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Thiết lập lặp lại mỗi 2 tiếng
        val interval = 2 * 60 * 60 * 1000L // 2 tiếng
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + interval,
            interval,
            pendingIntent
        )
    }

    private fun buildNotification(message: String): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Nhắc uống nước 💧")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_water_drop)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            "Nhắc uống nước",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Thông báo nhắc uống nước"
        }
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}