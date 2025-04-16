package vn.com.rd.waterreminder.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import vn.com.rd.waterreminder.data.model.WaterLog

@Dao
interface WaterLogDao {
    @Query("SELECT * FROM water_logs WHERE timestamp >= :startOfDay")
    fun getTodayLogs(startOfDay: Long): LiveData<List<WaterLog>>

    @Insert
    suspend fun insert(log: WaterLog)

    @Query("SELECT SUM(amountMl) FROM water_logs")
    fun getTotalAmount(): LiveData<Int>
}
