package vn.com.rd.waterreminder

import android.app.Application
import vn.com.rd.waterreminder.data.db.WaterDatabase
import vn.com.rd.waterreminder.data.repository.WaterRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import vn.com.rd.waterreminder.data.repository.ReminderRepository

class WaterReminderApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    lateinit var repository: WaterRepository
        private set

    lateinit var reminderRepository: ReminderRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val database = WaterDatabase.getInstance(this)
        repository = WaterRepository(database.waterLogDao())

        reminderRepository = ReminderRepository(
                reminderDao = database.reminderDao(),
                context = applicationContext,
                externalScope = applicationScope
            )

        reminderRepository.rescheduleAllReminders()
    }
}
