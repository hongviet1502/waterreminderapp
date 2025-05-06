package vn.com.rd.waterreminder.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import vn.com.rd.waterreminder.data.model.Reminder

@Dao
interface ReminderDao {
    @Insert
    suspend fun insertReminder(reminder: Reminder): Long

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Query("SELECT * FROM reminders WHERE userId = :userId ORDER BY time ASC")
    fun getRemindersByUser(userId: Long): LiveData<List<Reminder>>
}