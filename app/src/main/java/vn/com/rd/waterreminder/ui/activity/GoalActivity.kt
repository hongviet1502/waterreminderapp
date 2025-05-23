package vn.com.rd.waterreminder.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.shawnlin.numberpicker.NumberPicker
import vn.com.rd.waterreminder.Params
import vn.com.rd.waterreminder.R
import vn.com.rd.waterreminder.data.model.Reminder
import vn.com.rd.waterreminder.databinding.ActivityGoalBinding
import vn.com.rd.waterreminder.ui.factory.GoalViewModelFactory
import vn.com.rd.waterreminder.ui.component.ReminderItemAdapter
import vn.com.rd.waterreminder.ui.viewmodel.GoalViewModel
import java.util.Locale


class GoalActivity : AppCompatActivity(), ReminderItemAdapter.OnReminderActionListener {
    private lateinit var binding: ActivityGoalBinding
    private lateinit var list : MutableList<Reminder>
    private val TAG = "GoalActivity"
    private lateinit var viewModel: GoalViewModel
    private var adapter: ReminderItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGoalBinding.inflate(layoutInflater)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, bottomInset)
            insets
        }
        setContentView(binding.root)

        val backArrowDrawable = ContextCompat.getDrawable(this, R.drawable.ic_back)
        setSupportActionBar(binding.tbGoal)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        supportActionBar?.setHomeAsUpIndicator(backArrowDrawable)

        val factory = GoalViewModelFactory(this, Params.USER_ID)
        viewModel = ViewModelProvider(this, factory)[GoalViewModel::class.java]

        // 3. Theo dõi dữ liệu từ ViewModel
        observeViewModel()

        setupSpinner()
        setupRecyclerView()
        setUpNumberPicker()

        binding.llAddNew.setOnClickListener {
            startActivity(Intent(this, ReminderActivity::class.java))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.unit_array,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.spnUnit.adapter = adapter
        binding.spnUnit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedUnit = parent?.getItemAtPosition(position) as String
                // Xử lý khi chọn đơn vị
                viewModel.updateWaterUnit(position) // Gọi ViewModel nếu cần
                Toast.makeText(this@GoalActivity, "Selected: $selectedUnit", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Xử lý khi không có item nào được chọn
            }
        }
    }

    private fun setupRecyclerView() {
        list = ArrayList()
        adapter = ReminderItemAdapter(this).apply {
            adapter?.submitList(list)
        }
        binding.rvReminder.adapter = adapter
    }

    private fun setUpNumberPicker(){
        binding.numberPicker.setOnScrollListener { picker, scrollState ->
            if (scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
                Log.d(TAG, String.format(Locale.US, "newVal: %d", picker.value))
                viewModel.updateUnitAmount(picker.value)
            }
        }
    }

    private fun observeViewModel(){
        viewModel.currentGoal.observe(this) { goal ->
            if (goal != null) {
                // Update UI khi có goal
                binding.numberPicker.value = goal.unitAmount
                binding.spnUnit.setSelection(goal.unit)
            } else { }
        }

        viewModel.reminders.observe(this) { reminders ->
            adapter?.submitList(reminders)
        }
    }

    override fun onReminderClick(reminder: Reminder) {
        Log.i(TAG, "onReminderClick: $reminder")
    }

    override fun onReminderToggle(
        reminder: Reminder,
        isEnabled: Boolean
    ) {
        viewModel.toggleReminderEnabled(reminder, isEnabled)
    }

    override fun onReminderDelete(reminder: Reminder) {
        showBottomSheetDeleteDialog(reminder)
    }

    private fun showBottomSheetDeleteDialog(reminder: Reminder) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_delete, null)

        view.findViewById<TextView>(R.id.tv_reminder_name).text = reminder.message

        view.findViewById<LinearLayout>(R.id.layout_delete).setOnClickListener {
            viewModel.deleteReminder(reminder)
            bottomSheetDialog.dismiss()
            Toast.makeText(this, "Reminder deleted successfully", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<LinearLayout>(R.id.layout_cancel).setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

}