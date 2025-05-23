package vn.com.rd.waterreminder.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.com.rd.waterreminder.WaterReminderApplication

/**
 * BroadcastReceiver để khôi phục các thông báo khi thiết bị khởi động lại
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {

            // Lấy repository từ Application
            val application = context.applicationContext as WaterReminderApplication
            val repository = application.reminderRepository

            // Khởi động lại tất cả các thông báo đang hoạt động
            CoroutineScope(Dispatchers.IO).launch {
                repository.rescheduleAllReminders()
            }
        }
    }
}