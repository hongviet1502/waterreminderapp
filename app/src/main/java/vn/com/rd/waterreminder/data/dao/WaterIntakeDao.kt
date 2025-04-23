package vn.com.rd.waterreminder.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import vn.com.rd.waterreminder.data.model.DailyWaterIntake
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
    fun getIntakesForDateRange(
        userId: Long,
        startTime: Long,
        endTime: Long
    ): LiveData<List<WaterIntake>>

    @Query("SELECT SUM(amount) FROM water_intakes WHERE userId = :userId AND timestamp BETWEEN :startTime AND :endTime")
    suspend fun getTotalIntakeForDateRange(userId: Long, startTime: Long, endTime: Long): Int?

    @Query(
        """
    SELECT COALESCE(SUM(amount), 0)
    FROM water_intakes 
    WHERE userId = :userId 
    AND timestamp >= :todayStart 
    AND timestamp < :todayEnd
    """
    )
    fun getTodayTotalIntake(userId: Long, todayStart: Long, todayEnd: Long): LiveData<Int?>

    // Query to get total daily water intake in ml for a specific user
    @Query(
        """
    SELECT 
        strftime('%Y-%m-%d', datetime(timestamp/1000, 'unixepoch', 'localtime')) as date,
        COALESCE(SUM(amount), 0) as totalAmount
    FROM water_intakes
    WHERE userId = :userId
    GROUP BY date
    ORDER BY date DESC
"""
    )
    fun getDailyTotalIntake(userId: Long): LiveData<List<DailyWaterIntake>>

    // You can also get data for a specific date range
    @Query(
        """
        SELECT 
            strftime('%Y-%m-%d', datetime(timestamp/1000, 'unixepoch')) as date,
            SUM(amount) as totalAmount
        FROM water_intakes
        WHERE userId = :userId 
        AND timestamp >= :startTime AND timestamp <= :endTime
        GROUP BY date
        ORDER BY date DESC
    """
    )
    fun getDailyTotalIntakeInRange(
        userId: Long,
        startTime: Long,
        endTime: Long
    ): LiveData<List<DailyWaterIntake>>
}