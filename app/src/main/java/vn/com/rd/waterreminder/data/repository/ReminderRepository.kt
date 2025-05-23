package vn.com.rd.waterreminder.data.repository

import vn.com.rd.waterreminder.data.dao.ReminderDao
import vn.com.rd.waterreminder.data.model.Reminder

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.com.rd.waterreminder.service.ReminderScheduler

class ReminderRepository(
    private val reminderDao: ReminderDao,
    private val context: Context,
    private val externalScope: CoroutineScope
) {
    private val reminderScheduler = ReminderScheduler(context)

    suspend fun insertReminder(reminder: Reminder): Long {
        val id = reminderDao.insertReminder(reminder)
        // Cập nhật ID cho reminder nếu là reminder mới
        val updatedReminder = if (reminder.id == 0L) reminder.copy(id = id) else reminder
        reminderScheduler.scheduleReminder(updatedReminder)
        return id
    }

    suspend fun updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(reminder)
        // Cập nhật thông báo cho reminder
        reminderScheduler.scheduleReminder(reminder)
    }

    suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
        // Hủy thông báo khi xóa reminder
        reminderScheduler.cancelReminder(reminder)
    }

    fun getAllReminders(userId: Long) = reminderDao.getAllReminders(userId)

    suspend fun getReminderById(reminderId: Long) = reminderDao.getReminderById(reminderId)

    suspend fun toggleReminderEnabled(reminder: Reminder, isEnabled: Boolean) {
        val updatedReminder = reminder.copy(
            isEnabled = isEnabled,
            updatedAt = System.currentTimeMillis()
        )
        updateReminder(updatedReminder)
    }

    fun rescheduleAllReminders() {
        externalScope.launch {
            val activeReminders = withContext(Dispatchers.IO) {
                reminderDao.getAllActiveReminders()
            }

            for (reminder in activeReminders) {
                reminderScheduler.scheduleReminder(reminder)
            }
        }
    }
}