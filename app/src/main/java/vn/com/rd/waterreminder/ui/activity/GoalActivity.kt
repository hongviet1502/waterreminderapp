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
import vn.com.rd.waterreminder.ui.fragment.AlarmFragment
import vn.com.rd.waterreminder.ui.viewmodel.GoalViewModel
import java.util.Locale


class GoalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoalBinding
    private lateinit var alarmFragment: AlarmFragment

    private val TAG = "GoalActivity"
    private lateinit var viewModel: GoalViewModel
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

        observeViewModel()
        setupSpinner()
        setUpNumberPicker()
        setupFragment()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun setupFragment() {
        // Tạo và thêm Fragment vào Activity
        alarmFragment = AlarmFragment.newInstance()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, alarmFragment)
            .commit()
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
                Toast.makeText(this@GoalActivity, "Selected: $selectedUnit", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Xử lý khi không có item nào được chọn
            }
        }
    }

    private fun setUpNumberPicker() {
        binding.numberPicker.setOnScrollListener { picker, scrollState ->
            if (scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
                Log.d(TAG, String.format(Locale.US, "newVal: %d", picker.value))
                viewModel.updateUnitAmount(picker.value)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.currentGoal.observe(this) { goal ->
            if (goal != null) {
                // Update UI khi có goal
                binding.numberPicker.value = goal.unitAmount
                binding.spnUnit.setSelection(goal.unit)
            } else {
            }
        }
    }
}