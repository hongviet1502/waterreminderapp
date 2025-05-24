package vn.com.rd.waterreminder.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
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

    // Biến để track mode và reminder hiện tại
    private var currentReminder: Reminder? = null
    private var isEditMode: Boolean = false

    companion object {
        const val EXTRA_REMINDER = "extra_reminder"

        // Helper functions để start activity
        fun startForCreate(context: Context) {
            val intent = Intent(context, ReminderActivity::class.java)
            context.startActivity(intent)
        }

        fun startForEdit(context: Context, reminder: Reminder) {
            val intent = Intent(context, ReminderActivity::class.java)
            intent.putExtra(EXTRA_REMINDER, reminder)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityReminderBinding.inflate(layoutInflater)
        val factory = ReminderViewModelFactory(this, Params.USER_ID)
        viewModel = ViewModelProvider(this, factory)[ReminderViewModel::class.java]
        setContentView(binding.root)

        // Check xem có reminder được truyền vào không
        handleIntentData()

        setupToolbar()
        setUpClickListener()
        setUpDayPicker()
        setUpRepeat()

        // Nếu là edit mode thì populate UI
        if (isEditMode) {
            populateUIWithReminderData()
        } else {
            initializeForCreateMode()
        }
    }

    private fun handleIntentData() {
        currentReminder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_REMINDER, Reminder::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_REMINDER)
        }

        isEditMode = currentReminder != null
    }

    private fun setupToolbar() {
        val backArrowDrawable = ContextCompat.getDrawable(this, R.drawable.ic_back)
        setSupportActionBar(binding.tbReminder)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(backArrowDrawable)

        // Set title dựa vào mode
        supportActionBar?.title = if (isEditMode) "Chỉnh sửa nhắc nhở" else "Thêm nhắc nhở mới"
    }

    private fun initializeForCreateMode() {
        // Set thời gian hiện tại cho create mode
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val formattedTime = String.format("%02d:%02d", currentHour, currentMinute)
        binding.tvSelectedTime.text = formattedTime

        // Set default values
        binding.repeatGroup.check(R.id.rb_none)
        binding.btnSaveReminder.text = "Save reminder"
    }

    private fun populateUIWithReminderData() {
        currentReminder?.let { reminder ->
            // Populate message
            binding.edtReminderMessage.setText(reminder.message)

            // Populate time
            binding.tvSelectedTime.text = reminder.time

            // Populate repeat type
            when (reminder.repeatType) {
                "DAY" -> binding.repeatGroup.check(R.id.rb_day)
                "WEEK" -> binding.repeatGroup.check(R.id.rb_week)
                else -> binding.repeatGroup.check(R.id.rb_none)
            }

            // Populate selected days
            selectedDays.clear()
            if (reminder.monday) {
                selectedDays.add("Mon")
                binding.btnMon.isChecked = true
            }
            if (reminder.tuesday) {
                selectedDays.add("Tue")
                binding.btnTue.isChecked = true
            }
            if (reminder.wednesday) {
                selectedDays.add("Wed")
                binding.btnWed.isChecked = true
            }
            if (reminder.thursday) {
                selectedDays.add("Thu")
                binding.btnThu.isChecked = true
            }
            if (reminder.friday) {
                selectedDays.add("Fri")
                binding.btnFri.isChecked = true
            }
            if (reminder.saturday) {
                selectedDays.add("Sat")
                binding.btnSat.isChecked = true
            }
            if (reminder.sunday) {
                selectedDays.add("Sun")
                binding.btnSun.isChecked = true
            }

            // Change button text for edit mode
            binding.btnSaveReminder.text = "Edit reminder"
        }
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
        binding.cvReminderTime.setOnClickListener {
            showMaterialTimePicker()
        }

        binding.btnSaveReminder.setOnClickListener {
            handleSaveOrUpdate()
        }
    }

    private fun handleSaveOrUpdate() {
        // Validate input
        val message = binding.edtReminderMessage.text.toString().trim()
        if (message.isEmpty()) {
            binding.edtReminderMessage.error = "Message is empty"
            return
        }

        val time = binding.tvSelectedTime.text.toString()

        // Xác định loại lặp lại
        val repeatType = when {
            binding.rbDay.isChecked -> "DAY"
            binding.rbWeek.isChecked -> "WEEK"
            else -> "NONE"
        }

        if (isEditMode) {
            // Update existing reminder
            currentReminder?.let { existing ->
                val updatedReminder = existing.copy(
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

                viewModel.updateReminder(updatedReminder)
                Toast.makeText(this, "Reminder edited successfully", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Create new reminder
            val newReminder = Reminder(
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

            viewModel.createReminder(newReminder)
            Toast.makeText(this, "Reminder created successfully", Toast.LENGTH_SHORT).show()
        }

        // Đóng activity
        finish()
    }

    private fun setUpRepeat() {
        binding.repeatGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_day -> {
                    // Day selected - có thể ẩn day picker vì repeat mỗi ngày
                    setDayPickerVisibility(false)
                }

                R.id.rb_week -> {
                    // Week selected - hiện day picker để chọn ngày trong tuần
                    setDayPickerVisibility(true)
                }

                R.id.rb_none -> {
                    // None selected - có thể ẩn day picker
                    setDayPickerVisibility(false)
                    clearSelectedDays()
                }
            }
        }
    }

    private fun setDayPickerVisibility(visible: Boolean) {
        val visibility = if (visible) View.VISIBLE else View.GONE
        // Assuming you have a container for day buttons
        // binding.dayPickerContainer.visibility = visibility
    }

    private fun clearSelectedDays() {
        selectedDays.clear()
        binding.btnMon.isChecked = false
        binding.btnTue.isChecked = false
        binding.btnWed.isChecked = false
        binding.btnThu.isChecked = false
        binding.btnFri.isChecked = false
        binding.btnSat.isChecked = false
        binding.btnSun.isChecked = false
    }

    private fun showMaterialTimePicker() {
        // Parse thời gian hiện tại từ TextView
        val currentTimeText = binding.tvSelectedTime.text.toString()
        val timeParts = currentTimeText.split(":")
        val currentHour = timeParts.getOrNull(0)?.toIntOrNull() ?: Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val currentMinute = timeParts.getOrNull(1)?.toIntOrNull() ?: Calendar.getInstance().get(Calendar.MINUTE)

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(currentHour)
            .setMinute(currentMinute)
            .setTitleText("Chọn thời gian")
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