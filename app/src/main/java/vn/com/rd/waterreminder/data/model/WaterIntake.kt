package vn.com.rd.waterreminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_intakes")
data class WaterIntake(
    @PrimaryKey(autoGenerate = true)
    val intakeId: Long = 0,
    val userId: Long,
    val amount: Int,  // in ml
    val containerType: Int?, // e.g., "Glass", "Bottle", "Cup"
    val timestamp: Long = System.currentTimeMillis(),
    val note: String? = null
)