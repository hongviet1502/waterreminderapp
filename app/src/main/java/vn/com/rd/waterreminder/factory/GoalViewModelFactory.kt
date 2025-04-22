package vn.com.rd.waterreminder.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vn.com.rd.waterreminder.data.db.WaterDatabase
import vn.com.rd.waterreminder.data.repository.WaterGoalRepository
import vn.com.rd.waterreminder.viewmodel.GoalViewModel

class GoalViewModelFactory(
    private val context: Context,
    private val userId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoalViewModel::class.java)) {
            val database = WaterDatabase.getInstance(context)
            val dao = database.waterGoalDao()
            val repository = WaterGoalRepository(dao)

            return GoalViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}