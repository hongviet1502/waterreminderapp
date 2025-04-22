package vn.com.rd.waterreminder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vn.com.rd.waterreminder.data.model.WaterGoal
import vn.com.rd.waterreminder.data.repository.WaterGoalRepository

class GoalViewModel(
    private val repository: WaterGoalRepository,
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
}