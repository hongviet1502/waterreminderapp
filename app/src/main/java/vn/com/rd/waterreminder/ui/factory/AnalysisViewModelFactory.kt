package vn.com.rd.waterreminder.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import vn.com.rd.waterreminder.data.db.WaterDatabase
import vn.com.rd.waterreminder.data.repository.ReminderRepository
import vn.com.rd.waterreminder.data.repository.WaterGoalRepository
import vn.com.rd.waterreminder.data.repository.WaterIntakeRepository
import vn.com.rd.waterreminder.ui.viewmodel.AnalysisViewModel

class AnalysisViewModelFactory (
    private val context: Context,
    private val userId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnalysisViewModel::class.java)) {
            val database = WaterDatabase.getInstance(context)
            val dao = database.waterGoalDao()
            val repository = WaterGoalRepository(dao)

            val waterIntakeDao = database.waterIntakeDao()
            val waterIntakeRepository = WaterIntakeRepository(waterIntakeDao)

            val reminderDao = database.reminderDao()
            val reminderRepository = ReminderRepository(reminderDao, context, CoroutineScope(SupervisorJob()))

            return AnalysisViewModel(waterIntakeRepository, reminderRepository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}