package vn.com.rd.waterreminder.data.model

import android.os.Parcel
import android.os.Parcelable
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
) : Parcelable {

    constructor(parcel: Parcel) : this(
        id = parcel.readLong(),
        userId = parcel.readLong(),
        message = parcel.readString() ?: "",
        time = parcel.readString() ?: "",
        repeatType = parcel.readString() ?: "NONE",
        monday = parcel.readByte() != 0.toByte(),
        tuesday = parcel.readByte() != 0.toByte(),
        wednesday = parcel.readByte() != 0.toByte(),
        thursday = parcel.readByte() != 0.toByte(),
        friday = parcel.readByte() != 0.toByte(),
        saturday = parcel.readByte() != 0.toByte(),
        sunday = parcel.readByte() != 0.toByte(),
        isEnabled = parcel.readByte() != 0.toByte(),
        createdAt = parcel.readLong(),
        updatedAt = parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(userId)
        parcel.writeString(message)
        parcel.writeString(time)
        parcel.writeString(repeatType)
        parcel.writeByte(if (monday) 1 else 0)
        parcel.writeByte(if (tuesday) 1 else 0)
        parcel.writeByte(if (wednesday) 1 else 0)
        parcel.writeByte(if (thursday) 1 else 0)
        parcel.writeByte(if (friday) 1 else 0)
        parcel.writeByte(if (saturday) 1 else 0)
        parcel.writeByte(if (sunday) 1 else 0)
        parcel.writeByte(if (isEnabled) 1 else 0)
        parcel.writeLong(createdAt)
        parcel.writeLong(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Reminder> {
        override fun createFromParcel(parcel: Parcel): Reminder {
            return Reminder(parcel)
        }

        override fun newArray(size: Int): Array<Reminder?> {
            return arrayOfNulls(size)
        }
    }
}