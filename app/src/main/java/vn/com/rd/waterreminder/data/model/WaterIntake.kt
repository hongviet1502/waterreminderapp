package vn.com.rd.waterreminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import vn.com.rd.waterreminder.ui.component.InfoItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

fun WaterIntake.toInfoItem(): InfoItem {
    val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val formattedDateTime = dateTimeFormat.format(Date(this.timestamp))

    val title = "Đã uống ${this.amount}ml vào $formattedDateTime"

    return InfoItem(
        titleText = title,
        contentText = this.note,
        id = this.intakeId.toInt()
    )
}

// Extension function để convert List<WaterIntake> sang List<InfoItem>
fun List<WaterIntake>.toInfoItems(): List<InfoItem> {
    return this.map { it.toInfoItem() }
}

// Nếu muốn format thời gian khác (ví dụ: chỉ có giờ)
fun WaterIntake.toInfoItemTimeOnly(): InfoItem {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(this.timestamp))

    val title = "Đã uống ${this.amount}ml vào $formattedTime"

    return InfoItem(
        titleText = title,
        contentText = this.note,
        id = this.intakeId.toInt()
    )
}

// Utility class cho các format khác nhau
object WaterIntakeMapper {

    fun mapToInfoItem(
        waterIntake: WaterIntake,
        timeFormat: String = "dd/MM/yyyy HH:mm"
    ): InfoItem {
        val formatter = SimpleDateFormat(timeFormat, Locale.getDefault())
        val formattedTime = formatter.format(Date(waterIntake.timestamp))

        val title = "Đã uống ${waterIntake.amount}ml vào $formattedTime"

        return InfoItem(
            titleText = title,
            contentText = waterIntake.note,
            id = waterIntake.intakeId.toInt()
        )
    }

    fun mapToInfoItems(
        waterIntakes: List<WaterIntake>,
        timeFormat: String = "dd/MM/yyyy HH:mm"
    ): List<InfoItem> {
        return waterIntakes.map { mapToInfoItem(it, timeFormat) }
    }

    // Format cho các trường hợp đặc biệt
    fun mapToInfoItemsGroupedByDay(waterIntakes: List<WaterIntake>): Map<String, List<InfoItem>> {
        val dayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        return waterIntakes.groupBy { intake ->
            dayFormat.format(Date(intake.timestamp))
        }.mapValues { (_, intakes) ->
            intakes.map { intake ->
                val formattedTime = timeFormat.format(Date(intake.timestamp))
                InfoItem(
                    titleText = "Đã uống ${intake.amount}ml vào $formattedTime",
                    contentText = intake.note,
                    id = intake.intakeId.toInt()
                )
            }
        }
    }
}