package vn.com.rd.waterreminder.data.repository

import androidx.lifecycle.LiveData
import vn.com.rd.waterreminder.data.dao.WaterGoalDao
import vn.com.rd.waterreminder.data.model.WaterGoal
import java.time.LocalDate
import java.time.ZoneOffset

class WaterGoalRepository(private val dao: WaterGoalDao) {
    // Luôn đảm bảo 1 user chỉ có 1 goal
    suspend fun saveGoal(userId: Long, amount: Int, unit: Int) {
        val goal = WaterGoal(
            userId = userId,
            targetAmount = amount,
            unit = unit,
            updatedAt = System.currentTimeMillis()
        )
        dao.upsertGoal(goal)
    }

    suspend fun getCurrentGoal(userId: Long): WaterGoal? {
        return dao.getGoalByUser(userId)
    }

    suspend fun updateUnit(userId: Long, newUnit: Int) {
        dao.updateUnit(userId, newUnit)
    }

    suspend fun updateTargetAmount(userId: Long, newTargetAmount: Int) {
        dao.updateTargetAmount(userId, newTargetAmount)
    }

    suspend fun updateUnitAmount(userId: Long, newUnitAmount: Int) {
        dao.updateUnitAmount(userId, newUnitAmount)
    }

    suspend fun getOrCreateDefaultGoal(userId: Long): WaterGoal {
        return dao.getGoalByUser(userId) ?: run {
            val defaultGoal = WaterGoal(
                userId = userId,
                targetAmount = 2000,  // Giá trị mặc định
                unit = 0,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            dao.upsertGoal(defaultGoal)
            defaultGoal
        }
    }
}