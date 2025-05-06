package vn.com.rd.waterreminder.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import vn.com.rd.waterreminder.Params
import vn.com.rd.waterreminder.R
import vn.com.rd.waterreminder.data.model.Reminder
import vn.com.rd.waterreminder.databinding.ActivityReminderBinding
import vn.com.rd.waterreminder.ui.factory.GoalViewModelFactory
import vn.com.rd.waterreminder.ui.factory.ReminderViewModelFactory
import vn.com.rd.waterreminder.ui.viewmodel.GoalViewModel
import vn.com.rd.waterreminder.ui.viewmodel.ReminderViewModel
import java.util.Calendar

class ReminderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReminderBinding
    private val selectedDays = mutableSetOf<String>()
    private lateinit var viewModel: ReminderViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityReminderBinding.inflate(layoutInflater)
        val factory = ReminderViewModelFactory(this, Params.USER_ID)
        viewModel = ViewModelProvider(this, factory)[ReminderViewModel::class.java]
        setContentView(binding.root)

        val backArrowDrawable = ContextCompat.getDrawable(this, R.drawable.ic_back)
        setSupportActionBar(binding.tbReminder)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        supportActionBar?.setHomeAsUpIndicator(backArrowDrawable)

        setUpClickListener()
        setUpDayPicker()
        setUpRepeat()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setUpDayPicker() {
        val buttons = listOf(
            binding.btnMon,
            binding.btnTue,
            binding.btnWed,
            binding.btnThu,
            binding.btnFri,
            binding.btnSat,
            binding.btnSun
        )

        val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        buttons.forEachIndexed { index, button ->
            button.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selectedDays.add(dayNames[index])
                else selectedDays.remove(dayNames[index])
            }
        }
    }

    private fun setUpClickListener() {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val formattedTime = String.format("%02d:%02d", currentHour, currentMinute)
        binding.tvSelectedTime.text = formattedTime
        binding.cvReminderTime.setOnClickListener {
            showMaterialTimePicker()
        }
        binding.btnSaveReminder.setOnClickListener {
            // Thu thập dữ liệu từ UI
            val message = binding.edtReminderMessage.text.toString() // Thay bằng ID thực của EditText
            val time = binding.tvSelectedTime.text.toString()

            // Xác định loại lặp lại
            val repeatType = when {
                binding.rbDay.isChecked -> "DAY"
                binding.rbWeek.isChecked -> "WEEK"
                else -> "NONE"
            }

            // Tạo reminder mới
            val reminder = Reminder(
                userId = Params.USER_ID,
                message = message,
                time = time,
                repeatType = repeatType,
                monday = selectedDays.contains("Mon"),
                tuesday = selectedDays.contains("Tue"),
                wednesday = selectedDays.contains("Wed"),
                thursday = selectedDays.contains("Thu"),
                friday = selectedDays.contains("Fri"),
                saturday = selectedDays.contains("Sat"),
                sunday = selectedDays.contains("Sun")
            )

            // Lưu reminder vào database (sử dụng viewModel hoặc repository)
            viewModel.upsertReminder(reminder)

            // Hiển thị thông báo thành công
            Toast.makeText(this, "Reminder saved successfully", Toast.LENGTH_SHORT).show()

            // Đóng activity
            finish()
        }
    }

    private fun setUpRepeat() {
        binding.repeatGroup.check(R.id.rb_none)
        binding.repeatGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_day -> {
                    // Day selected
                }

                R.id.rb_week -> {
                    // Week selected
                }

                R.id.rb_none -> {
                    // None selected
                }
            }
        }
    }

    private fun showMaterialTimePicker() {
        // Lấy thời gian hiện tại
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H) // hoặc TimeFormat.CLOCK_12H
            .setHour(currentHour)  // Sử dụng giờ hiện tại
            .setMinute(currentMinute) // Sử dụng phút hiện tại
            .setTitleText("Select Time")
            .build()

        picker.show(supportFragmentManager, "time_picker")

        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour
            val minute = picker.minute
            val formattedTime = String.format("%02d:%02d", hour, minute)
            binding.tvSelectedTime.text = formattedTime
        }
    }
}