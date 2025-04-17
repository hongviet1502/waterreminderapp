package vn.com.rd.waterreminder.data.repository

import androidx.lifecycle.LiveData
import vn.com.rd.waterreminder.data.dao.WaterGoalDao
import vn.com.rd.waterreminder.data.model.WaterGoal
import java.time.LocalDate
import java.time.ZoneOffset

class WaterGoalRepository(private val waterGoalDao: WaterGoalDao) {
    suspend fun addGoal(goal : WaterGoal) = waterGoalDao.insertGoal(goal)

    suspend fun updateGoal(goal : WaterGoal) = waterGoalDao.updateGoal(goal)
}