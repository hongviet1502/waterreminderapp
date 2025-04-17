package vn.com.rd.waterreminder.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TimeUtil {
    companion object {
        fun getCurrentTime(): String {
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            return dateFormat.format(calendar.time)
        }
    }
}