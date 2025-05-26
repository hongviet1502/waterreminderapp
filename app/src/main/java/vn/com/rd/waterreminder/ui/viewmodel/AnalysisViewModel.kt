package vn.com.rd.waterreminder.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.com.rd.waterreminder.data.model.WaterDayHistoryItem
import vn.com.rd.waterreminder.data.model.WaterIntake
import vn.com.rd.waterreminder.data.repository.ReminderRepository
import vn.com.rd.waterreminder.data.repository.WaterGoalRepository
import vn.com.rd.waterreminder.data.repository.WaterIntakeRepository
import java.time.LocalDate

class AnalysisViewModel (
    private val waterIntakeRepository: WaterIntakeRepository,
    private val reminderRepository: ReminderRepository,
    private val userId: Long, // Lấy từ Auth/Session
) : ViewModel() {

    val historyItems: LiveData<List<WaterDayHistoryItem>> = waterIntakeRepository.getHistoryItems(userId)
    val allIntakes: LiveData<List<WaterIntake>> = waterIntakeRepository.getAllIntakes(userId)

    fun getTodayIntake(): LiveData<Int?> {
        return waterIntakeRepository.getTodayIntake(userId)
    }

    fun getIntakesForDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): LiveData<List<WaterIntake>> {
        return waterIntakeRepository.getIntakesForDateRange(userId, startDate, endDate)
    }
}