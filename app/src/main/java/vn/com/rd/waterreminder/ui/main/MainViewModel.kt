package vn.com.rd.waterreminder.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vn.com.rd.waterreminder.data.model.WaterLog
import vn.com.rd.waterreminder.data.repository.WaterRepository

class MainViewModel(private val repository: WaterRepository) : ViewModel() {

    fun getTodayLogs(startOfDay: Long): LiveData<List<WaterLog>> {
        return repository.getTodayLogs(startOfDay)
    }

    fun logWater() {
        val log = WaterLog(timestamp = System.currentTimeMillis(), amountMl = 100)
        viewModelScope.launch {
            repository.insert(log)
        }
    }

    val totalAmount: LiveData<Int> = repository.getTotalAmount()

}