package vn.com.rd.waterreminder.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import vn.com.rd.waterreminder.data.api.RetrofitInstance
import vn.com.rd.waterreminder.data.model.WaterGoal
import vn.com.rd.waterreminder.data.model.WaterIntake
import vn.com.rd.waterreminder.data.model.WeatherResponse
import vn.com.rd.waterreminder.data.repository.WaterGoalRepository
import vn.com.rd.waterreminder.data.repository.WaterIntakeRepository
import java.time.LocalDate

class HomeViewModel(
    private val repository: WaterGoalRepository,
    private val waterIntakeRepository: WaterIntakeRepository,
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

    fun loadGoalData() {
        viewModelScope.launch {
            _currentGoal.value = repository.getOrCreateDefaultGoal(userId) // Refresh data
        }
    }
    private suspend fun refreshData(){
        _currentGoal.value = repository.getOrCreateDefaultGoal(userId) // Refresh data
    }

    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> = _weatherData

    private val apiKey = "c144a2735006bbb2bacf8f7c69a4635e"

    fun fetchWeather(city: String) {
        RetrofitInstance.api.getWeatherByCity(city, apiKey).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    _weatherData.value = response.body()
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                // xử lý lỗi nếu cần
            }
        })
    }
}