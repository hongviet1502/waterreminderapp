package vn.com.rd.waterreminder.data.repository

import androidx.lifecycle.LiveData
import vn.com.rd.waterreminder.data.dao.WaterIntakeDao
import vn.com.rd.waterreminder.data.model.WaterIntake
import java.time.LocalDate
import java.time.ZoneOffset

class WaterIntakeRepository(private val waterIntakeDao: WaterIntakeDao) {
    fun getAllIntakes(userId: Long) = waterIntakeDao.getAllIntakes(userId)

    fun getTodayIntake(userId: Long): LiveData<Int?> {
        val todayStart = LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
        return waterIntakeDao.getTodayTotalIntake(userId, todayStart)
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