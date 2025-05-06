package vn.com.rd.waterreminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long, // Liên kết với user
    val message: String, // Nội dung thông báo (EditText ở đầu giao diện)
    val time: String, // Thời gian thông báo (VD: "08:30")

    // Loại lặp lại
    val repeatType: String, // "DAY", "WEEK", hoặc "NONE"

    // Các ngày được chọn trong tuần (chỉ có ý nghĩa khi repeatType là "WEEK")
    val monday: Boolean = false,
    val tuesday: Boolean = false,
    val wednesday: Boolean = false,
    val thursday: Boolean = false,
    val friday: Boolean = false,
    val saturday: Boolean = false,
    val sunday: Boolean = false,

    // Trạng thái kích hoạt của reminder
    val isEnabled: Boolean = true,

    // Thời gian tạo/cập nhật reminder
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)