package vn.com.rd.waterreminder.data.repository

import androidx.lifecycle.LiveData
import vn.com.rd.waterreminder.data.dao.ReminderDao
import vn.com.rd.waterreminder.data.model.Reminder

class ReminderRepository(private val reminderDao: ReminderDao) {
    // Lấy tất cả reminder của user
    fun getRemindersByUser(userId: Long): LiveData<List<Reminder>> {
        return reminderDao.getRemindersByUser(userId)
    }

    // Hàm upsert (update hoặc insert)
    suspend fun upsertReminder(reminder: Reminder): Long {
        return if (reminder.id == 0L) {
            // ID = 0 nghĩa là reminder mới (chưa có trong DB)
            reminderDao.insertReminder(reminder)
        } else {
            // ID khác 0 nghĩa là reminder đã tồn tại, cần update
            reminderDao.updateReminder(reminder)
            reminder.id // Trả về ID hiện tại
        }
    }

    // Xóa reminder
    suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
    }
}