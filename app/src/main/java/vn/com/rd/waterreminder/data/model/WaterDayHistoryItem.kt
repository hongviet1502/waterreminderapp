package vn.com.rd.waterreminder.data.model

data class WaterDayHistoryItem(
    val date: String,          // Ngày tháng (ví dụ: "Thứ 3, 12/12/2023")
    val amount: Int,            // Lượng nước (ml)
    val target: Int             // Mục tiêu nước
)