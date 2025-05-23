package vn.com.rd.waterreminder.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vn.com.rd.waterreminder.data.model.Reminder
import vn.com.rd.waterreminder.data.repository.ReminderRepository

class ReminderViewModel(
    private val repository: ReminderRepository,
    private val userId: Long
) : ViewModel() {

    val reminders: LiveData<List<Reminder>> = repository.getAllReminders(userId)

    fun createReminder(
        message: String,
        time: String,
        repeatType: String,
        monday: Boolean = false,
        tuesday: Boolean = false,
        wednesday: Boolean = false,
        thursday: Boolean = false,
        friday: Boolean = false,
        saturday: Boolean = false,
        sunday: Boolean = false
    ) {
        viewModelScope.launch {
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
                sunday = sunday
            )
            repository.insertReminder(reminder)
        }
    }

    fun createReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.insertReminder(reminder)
        }
    }

    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.updateReminder(reminder)
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
        }
    }

    fun toggleReminderEnabled(reminder: Reminder, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.toggleReminderEnabled(reminder, isEnabled)
        }
    }
}
