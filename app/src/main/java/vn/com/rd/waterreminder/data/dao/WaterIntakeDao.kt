package vn.com.rd.waterreminder.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import vn.com.rd.waterreminder.data.model.WaterIntake

@Dao
interface WaterIntakeDao {
    @Insert
    suspend fun insertIntake(intake: WaterIntake): Long

    @Update
    suspend fun updateIntake(intake: WaterIntake)

    @Delete
    suspend fun deleteIntake(intake: WaterIntake)

    @Query("SELECT * FROM water_intakes WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllIntakes(userId: Long): LiveData<List<WaterIntake>>

    @Query("SELECT * FROM water_intakes WHERE userId = :userId AND timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getIntakesForDateRange(userId: Long, startTime: Long, endTime: Long): LiveData<List<WaterIntake>>

    @Query("SELECT SUM(amount) FROM water_intakes WHERE userId = :userId AND timestamp BETWEEN :startTime AND :endTime")
    suspend fun getTotalIntakeForDateRange(userId: Long, startTime: Long, endTime: Long): Int?

    @Query("SELECT SUM(amount) FROM water_intakes WHERE userId = :userId AND timestamp >= :todayStart")
    fun getTodayTotalIntake(userId: Long, todayStart: Long): LiveData<Int?>
}