package vn.com.rd.waterreminder.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import vn.com.rd.waterreminder.data.db.WaterDatabase
import vn.com.rd.waterreminder.data.repository.ReminderRepository
import vn.com.rd.waterreminder.ui.viewmodel.ReminderViewModel

class ReminderViewModelFactory(
    private val context: Context,
    private val userId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReminderViewModel::class.java)) {
            val database = WaterDatabase.getInstance(context)
            val reminderDao = database.reminderDao()
            val reminderRepository =
                ReminderRepository(reminderDao, context, CoroutineScope(SupervisorJob()))

            return ReminderViewModel(reminderRepository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}