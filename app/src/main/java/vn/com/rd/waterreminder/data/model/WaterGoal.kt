package vn.com.rd.waterreminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_goals")
data class WaterGoal(
    @PrimaryKey // PrimaryKey là userId, đảm bảo 1 user chỉ có 1 bản ghi
    val userId: Long,

    val targetAmount: Int,
    val unit: Int = 0,
    val unitAmount: Int = 0,
    // Metadata
    val createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
)