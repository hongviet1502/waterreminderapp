package vn.com.rd.waterreminder.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vn.com.rd.waterreminder.Params
import vn.com.rd.waterreminder.data.model.Reminder
import vn.com.rd.waterreminder.data.repository.ReminderRepository

class ReminderViewModel(
    private val reminderRepository: ReminderRepository,
    private val userId: Long
) : ViewModel() {

    val reminders: LiveData<List<Reminder>> = reminderRepository.getRemindersByUser(userId)

    fun upsertReminder(reminder: Reminder) = viewModelScope.launch {
        reminderRepository.upsertReminder(reminder)
    }

    // Hàm delete reminder
    fun deleteReminder(reminder: Reminder) = viewModelScope.launch {
        reminderRepository.deleteReminder(reminder)
    }

    // Tạo reminder mới với user ID hiện tại
    fun createNewReminder(
        message: String,
        time: String,
        repeatType: String,
        monday: Boolean = false,
        tuesday: Boolean = false,
        wednesday: Boolean = false,
        thursday: Boolean = false,
        friday: Boolean = false,
        saturday: Boolean = false,
        sunday: Boolean = false,
        isEnabled: Boolean = true
    ) = viewModelScope.launch {
        val reminder = Reminder(
            userId = userId,
            message = message,
            time = time,
            repeatType = repeatType,
            monday = monday,
            tuesday = tuesday,
            wednesday = wednesday,
            thursday = thursday,
            friday = friday,
            saturday = saturday,
            sunday = sunday,
            isEnabled = isEnabled,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        reminderRepository.upsertReminder(reminder)
    }
}