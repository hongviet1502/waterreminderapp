package vn.com.rd.waterreminder.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import vn.com.rd.waterreminder.data.dao.WaterLogDao
import vn.com.rd.waterreminder.data.model.WaterLog

@Database(entities = [WaterLog::class], version = 2)
abstract class WaterDatabase : RoomDatabase() {
    abstract fun waterLogDao(): WaterLogDao

    companion object {
        @Volatile
        private var INSTANCE: WaterDatabase? = null

        fun getInstance(context: Context): WaterDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    WaterDatabase::class.java,
                    "water_database"
                ).fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}