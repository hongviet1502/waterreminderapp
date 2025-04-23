package vn.com.rd.waterreminder.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import vn.com.rd.waterreminder.data.dao.WaterIntakeDao
import vn.com.rd.waterreminder.data.model.WaterDayHistoryItem
import vn.com.rd.waterreminder.data.model.WaterIntake
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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

    fun getHistoryItems(userId: Long): LiveData<List<WaterDayHistoryItem>> {
        return waterIntakeDao.getDailyTotalIntake(userId = userId).map { dailyIntakes ->
            dailyIntakes.map { intake ->
                WaterDayHistoryItem(
                    date = intake.date,
                    amount = intake.totalAmount,
                    target = 2000 // Hoặc lấy từ cài đặt người dùng
                )
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        Log.i("DATE_ERROR", "formatDate: $timestamp")
        return try {
            val sdf = SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault())
            sdf.timeZone = TimeZone.getDefault() // Hoặc TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
            sdf.format(Date(timestamp)) // Timestamp phải là milliseconds
        } catch (e: Exception) {
            Log.e("DATE_ERROR", "Lỗi convert timestamp: $timestamp", e)
            "Unknown date"
        }
    }
}