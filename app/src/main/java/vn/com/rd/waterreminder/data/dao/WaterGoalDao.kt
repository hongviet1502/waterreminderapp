package vn.com.rd.waterreminder.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import vn.com.rd.waterreminder.data.model.WaterGoal

@Dao
interface WaterGoalDao {
    // Tự động thay thế nếu userId đã tồn tại
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGoal(goal: WaterGoal)

    // Lấy goal theo userId (trả về null nếu chưa có)
    @Query("SELECT * FROM water_goals WHERE userId = :userId")
    suspend fun getGoalByUser(userId: Long): WaterGoal?

    // Cập nhật đơn vị (unit) theo userId
    @Query("UPDATE water_goals SET unit = :newUnit, updatedAt = :currentTime WHERE userId = :userId")
    suspend fun updateUnit(userId: Long, newUnit: Int, currentTime: Long = System.currentTimeMillis())

    // Cập nhật lượng mục tiêu (targetAmount) theo userId
    @Query("UPDATE water_goals SET targetAmount = :newTargetAmount, updatedAt = :currentTime WHERE userId = :userId")
    suspend fun updateTargetAmount(userId: Long, newTargetAmount: Int, currentTime: Long = System.currentTimeMillis())

    @Query("UPDATE water_goals SET unitAmount = :newUnitAmount, updatedAt = :currentTime WHERE userId = :userId")
    suspend fun updateUnitAmount(userId: Long, newUnitAmount: Int, currentTime: Long = System.currentTimeMillis())
}