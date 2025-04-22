package vn.com.rd.waterreminder.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import vn.com.rd.waterreminder.data.dao.UserDao
import vn.com.rd.waterreminder.data.dao.WaterGoalDao
import vn.com.rd.waterreminder.data.dao.WaterIntakeDao
import vn.com.rd.waterreminder.data.dao.WaterLogDao
import vn.com.rd.waterreminder.data.model.User
import vn.com.rd.waterreminder.data.model.WaterGoal
import vn.com.rd.waterreminder.data.model.WaterIntake
import vn.com.rd.waterreminder.data.model.WaterLog

@Database(
    entities = [
        User::class,
        WaterIntake::class,
        WaterGoal::class,
        WaterLog::class
    ],
    version = 4,
    exportSchema = false
)
abstract class WaterDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun waterIntakeDao(): WaterIntakeDao
    abstract fun waterGoalDao(): WaterGoalDao
    abstract fun waterLogDao(): WaterLogDao

    companion object {
        @Volatile
        private var INSTANCE: WaterDatabase? = null

        fun getInstance(context: Context): WaterDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WaterDatabase::class.java,
                    "water_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}