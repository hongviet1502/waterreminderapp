package vn.com.rd.waterreminder.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vn.com.rd.waterreminder.data.db.WaterDatabase
import vn.com.rd.waterreminder.data.repository.WaterGoalRepository
import vn.com.rd.waterreminder.data.repository.WaterIntakeRepository
import vn.com.rd.waterreminder.ui.viewmodel.HomeViewModel

class HomeViewModelFactory(
    private val context: Context,
    private val userId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val database = WaterDatabase.getInstance(context)
            val waterIntakeDao = database.waterIntakeDao()
            val waterIntakeRepository = WaterIntakeRepository(waterIntakeDao)
            val waterGoalDao = database.waterGoalDao()
            val waterGoalRepository = WaterGoalRepository(waterGoalDao)
            return HomeViewModel(waterGoalRepository, waterIntakeRepository, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}