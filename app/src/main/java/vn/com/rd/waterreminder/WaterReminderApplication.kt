package vn.com.rd.waterreminder

import android.app.Application
import vn.com.rd.waterreminder.data.db.WaterDatabase
import vn.com.rd.waterreminder.data.repository.WaterRepository

class WaterReminderApplication : Application() {
    lateinit var repository: WaterRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val database = WaterDatabase.getInstance(this)
        repository = WaterRepository(database.waterLogDao())
    }
}
