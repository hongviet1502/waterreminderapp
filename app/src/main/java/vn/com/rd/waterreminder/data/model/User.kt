package vn.com.rd.waterreminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0,
    val name: String,
    val gender: String?,
    val age: Int?,
    val weight: Float?,  // in kg
    val height: Float?,  // in cm
    val dailyWaterGoal: Int,  // in ml
    val wakeUpTime: String?,  // Format: "HH:mm"
    val sleepTime: String?,   // Format: "HH:mm"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)