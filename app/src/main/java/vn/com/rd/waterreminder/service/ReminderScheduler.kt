package vn.com.rd.waterreminder.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import vn.com.rd.waterreminder.data.model.Reminder
import java.util.Calendar

class ReminderScheduler(private val context: Context) {

    private val alarmManager: AlarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    /**
     * Lên lịch thông báo cho một reminder
     */
    fun scheduleReminder(reminder: Reminder) {
        if (!reminder.isEnabled) {
            cancelReminder(reminder)
            return
        }

        // Phân tích thời gian thông báo
        val timeParts = reminder.time.split(":")
        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()

        // Tạo calendar cho thời gian cụ thể
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Nếu thời gian đã qua trong ngày, cộng thêm 1 ngày
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        when (reminder.repeatType) {
            "NONE" -> scheduleSingleReminder(reminder, calendar.timeInMillis)
            "DAY" -> scheduleDailyReminder(reminder, calendar.timeInMillis)
            "WEEK" -> scheduleWeeklyReminder(reminder, hour, minute)
        }
    }

    /**
     * Lên lịch cho reminder một lần
     */
    private fun scheduleSingleReminder(reminder: Reminder, triggerTime: Long) {
        val intent = createReminderIntent(reminder)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    /**
     * Lên lịch cho reminder hàng ngày
     */
    private fun scheduleDailyReminder(reminder: Reminder, firstTriggerTime: Long) {
        val intent = createReminderIntent(reminder)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            firstTriggerTime,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    /**
     * Lên lịch cho reminder hàng tuần theo các ngày được chọn
     */
    private fun scheduleWeeklyReminder(reminder: Reminder, hour: Int, minute: Int) {
        // Danh sách các ngày trong tuần cần báo thức
        val weekDays = arrayListOf<Int>()
        if (reminder.sunday) weekDays.add(Calendar.SUNDAY)
        if (reminder.monday) weekDays.add(Calendar.MONDAY)
        if (reminder.tuesday) weekDays.add(Calendar.TUESDAY)
        if (reminder.wednesday) weekDays.add(Calendar.WEDNESDAY)
        if (reminder.thursday) weekDays.add(Calendar.THURSDAY)
        if (reminder.friday) weekDays.add(Calendar.FRIDAY)
        if (reminder.saturday) weekDays.add(Calendar.SATURDAY)

        if (weekDays.isEmpty()) {
            // Không có ngày nào được chọn, không cần lên lịch
            return
        }

        // Lên lịch riêng cho từng ngày trong tuần
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val currentTimeMillis = System.currentTimeMillis()

        for (weekDay in weekDays) {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = currentTimeMillis
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // Tính toán số ngày cần thêm để đạt đến ngày trong tuần cần thông báo
            var daysToAdd = (weekDay - today + 7) % 7
            if (daysToAdd == 0 && calendar.timeInMillis <= currentTimeMillis) {
                daysToAdd = 7 // Nếu là cùng ngày nhưng đã qua giờ, đặt báo thức cho tuần sau
            }

            calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)

            // Tạo PendingIntent riêng cho mỗi ngày trong tuần
            val daySpecificId = "${reminder.id}$weekDay".hashCode() // ID duy nhất cho mỗi cặp reminder-weekday
            val intent = createReminderIntent(reminder)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                daySpecificId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Lên lịch cho alarm hàng tuần
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }

            // Lưu ý: Cho các phiên bản thiết bị cũ hơn, bạn cần thực hiện lại scheduling
            // sau mỗi lần báo thức kích hoạt để có repeatType là "WEEK"
        }
    }

    /**
     * Hủy bỏ reminder
     */
    fun cancelReminder(reminder: Reminder) {
        val intent = createReminderIntent(reminder)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)

        // Nếu là reminder hàng tuần, cần hủy bỏ các báo thức cho từng ngày
        if (reminder.repeatType == "WEEK") {
            val weekDays = arrayOf(
                Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
                Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY
            )

            for (weekDay in weekDays) {
                val daySpecificId = "${reminder.id}$weekDay".hashCode()
                val weekdayIntent = createReminderIntent(reminder)
                val weekdayPendingIntent = PendingIntent.getBroadcast(
                    context,
                    daySpecificId,
                    weekdayIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.cancel(weekdayPendingIntent)
            }
        }
    }

    /**
     * Tạo Intent cho reminder
     */
    private fun createReminderIntent(reminder: Reminder): Intent {
        return Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_REMINDER_ID, reminder.id)
            putExtra(ReminderReceiver.EXTRA_REMINDER_MESSAGE, reminder.message)
        }
    }
}