package vn.com.rd.waterreminder.data.repository

import androidx.lifecycle.LiveData
import vn.com.rd.waterreminder.data.dao.WaterIntakeDao
import vn.com.rd.waterreminder.data.model.WaterIntake
import java.time.LocalDate
import java.time.ZoneOffset

class WaterIntakeRepository(private val waterIntakeDao: WaterIntakeDao) {
    fun getAllIntakes(userId: Long) = waterIntakeDao.getAllIntakes(userId)

    fun getTodayIntake(userId: Long): LiveData<Int?> {
        val today = LocalDate.now()
        val todayStart = today.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val todayEnd = todayStart + 86_400_000  // 24h sau
        return waterIntakeDao.getTodayTotalIntake(userId, todayStart, todayEnd)
    }

    fun getIntakesForDateRange(userId: Long, startDate: LocalDate, endDate: LocalDate): LiveData<List<WaterIntake>> {
        val startTime = startDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
        val endTime = endDate.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000 - 1
        return waterIntakeDao.getIntakesForDateRange(userId, startTime, endTime)
    }

    suspend fun addWaterIntake(intake: WaterIntake): Long = waterIntakeDao.insertIntake(intake)

    suspend fun updateWaterIntake(intake: WaterIntake) = waterIntakeDao.updateIntake(intake)

    suspend fun deleteWaterIntake(intake: WaterIntake) = waterIntakeDao.deleteIntake(intake)

    suspend fun getTotalForDate(userId: Long, date: LocalDate): Int {
        val startTime = date.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
        val endTime = date.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000 - 1
        return waterIntakeDao.getTotalIntakeForDateRange(userId, startTime, endTime) ?: 0
    }
}