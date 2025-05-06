package vn.com.rd.waterreminder.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vn.com.rd.waterreminder.data.model.WaterIntake
import vn.com.rd.waterreminder.data.repository.WaterIntakeRepository
import java.time.LocalDate

class WaterIntakeViewModel(
    private val waterIntakeRepository: WaterIntakeRepository,
    private val userId: Long,
) : ViewModel() {
    fun getAllIntakes(): LiveData<List<WaterIntake>> {
        return waterIntakeRepository.getAllIntakes(userId)
    }

    fun getTodayIntake(): LiveData<Int?> {
        return waterIntakeRepository.getTodayIntake(userId)
    }

    fun getIntakesForDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): LiveData<List<WaterIntake>> {
        return waterIntakeRepository.getIntakesForDateRange(userId, startDate, endDate)
    }

    fun addWaterIntake(waterIntake: WaterIntake) {
        viewModelScope.launch {
            waterIntakeRepository.addWaterIntake(waterIntake)
        }
    }

    fun updateWaterIntake(waterIntake: WaterIntake) {
        viewModelScope.launch {
            waterIntakeRepository.updateWaterIntake(waterIntake)
        }
    }

    fun deleteWaterIntake(waterIntake: WaterIntake) {
        viewModelScope.launch {
            waterIntakeRepository.deleteWaterIntake(waterIntake)
        }
    }
}
