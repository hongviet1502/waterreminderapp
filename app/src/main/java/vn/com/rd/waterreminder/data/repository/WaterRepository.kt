package vn.com.rd.waterreminder.data.repository

import androidx.lifecycle.LiveData
import vn.com.rd.waterreminder.data.dao.WaterLogDao
import vn.com.rd.waterreminder.data.model.WaterLog

class WaterRepository(private val dao: WaterLogDao) {
    fun getTodayLogs(startOfDay: Long): LiveData<List<WaterLog>> {
        return dao.getTodayLogs(startOfDay)
    }

    suspend fun insert(log: WaterLog) {
        dao.insert(log)
    }

    fun getTotalAmount(): LiveData<Int> = dao.getTotalAmount()

}