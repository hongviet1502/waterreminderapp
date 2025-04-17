package vn.com.rd.waterreminder.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import vn.com.rd.waterreminder.data.model.WaterGoal

@Dao
interface WaterGoalDao {
    @Insert
    suspend fun insertGoal(goal: WaterGoal): Long

    @Update
    suspend fun updateGoal(goal: WaterGoal)

    @Query("SELECT * FROM water_goals WHERE userId = :userId AND date = :date LIMIT 1")
    suspend fun getGoalForDate(userId: Long, date: Long): WaterGoal?

    @Query("SELECT * FROM water_goals WHERE userId = :userId ORDER BY date DESC LIMIT 30")
    fun getRecentGoals(userId: Long): LiveData<List<WaterGoal>>
}