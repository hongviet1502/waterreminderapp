package vn.com.rd.waterreminder.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vn.com.rd.waterreminder.Params
import vn.com.rd.waterreminder.data.model.Reminder
import vn.com.rd.waterreminder.data.model.WaterDayHistoryItem
import vn.com.rd.waterreminder.data.model.WaterGoal
import vn.com.rd.waterreminder.data.repository.ReminderRepository
import vn.com.rd.waterreminder.data.repository.WaterGoalRepository
import vn.com.rd.waterreminder.data.repository.WaterIntakeRepository

class GoalViewModel(
    private val repository: WaterGoalRepository,
    private val waterIntakeRepository: WaterIntakeRepository,
    private val reminderRepository: ReminderRepository,
    private val userId: Long, // Lấy từ Auth/Session
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(true)
) : ViewModel() {

    // LiveData để UI theo dõi goal hiện tại
    private val _currentGoal = MutableLiveData<WaterGoal?>()
    val currentGoal: LiveData<WaterGoal?> = _currentGoal

    // Khởi tạo: Load goal khi ViewModel được tạo
    init {
        viewModelScope.launch {
            isLoading.value = true
            _currentGoal.value = repository.getOrCreateDefaultGoal(userId)
            isLoading.value = false
        }
    }

    val historyItems: LiveData<List<WaterDayHistoryItem>> = waterIntakeRepository.getHistoryItems(userId)

    // Cập nhật goal (tạo mới hoặc update)
    fun updateGoal(amount: Int, unit: Int) {
        viewModelScope.launch {
            repository.saveGoal(userId, amount, unit)
            refreshData()
        }
    }

    fun updateWaterUnit(newUnit: Int) {
        viewModelScope.launch {
            repository.updateUnit(userId, newUnit)
            refreshData()
        }
    }

    fun updateWaterTarget(newTargetAmount: Int) {
        viewModelScope.launch {
            repository.updateTargetAmount(userId, newTargetAmount)
            refreshData()
        }
    }

    fun updateUnitAmount(newUnitAmount: Int) {
        viewModelScope.launch {
            repository.updateUnitAmount(userId, newUnitAmount)
            refreshData()
        }
    }

    private suspend fun refreshData() {
        _currentGoal.value = repository.getOrCreateDefaultGoal(userId) // Refresh data
    }

    val reminders: LiveData<List<Reminder>> = reminderRepository.getRemindersByUser(Params.USER_ID)
//
//    fun addReminder(time: String, message: String) {
//        viewModelScope.launch {
//            reminderRepository.addReminder(
//                Reminder(
//                    userId = Params.USER_ID,
//                    time = time,
//                    message = message
//                )
//            )
//        }
//    }
//
//    fun deleteReminder(reminder: Reminder) {
//        viewModelScope.launch {
//            reminderRepository.deleteReminder(reminder)
//        }
//    }
}