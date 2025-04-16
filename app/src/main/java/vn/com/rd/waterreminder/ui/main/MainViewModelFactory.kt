package vn.com.rd.waterreminder.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vn.com.rd.waterreminder.data.repository.WaterRepository

class MainViewModelFactory(private val repository: WaterRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}