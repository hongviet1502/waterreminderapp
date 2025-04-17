package vn.com.rd.waterreminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_goals")
data class WaterGoal(
    @PrimaryKey(autoGenerate = true)
    val goalId: Long = 0,
    val userId: Long,
    val targetAmount: Int,  // in ml
    val date: Long,         // date in milliseconds
    val createdAt: Long = System.currentTimeMillis()
)