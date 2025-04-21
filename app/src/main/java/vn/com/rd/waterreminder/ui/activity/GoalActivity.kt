package vn.com.rd.waterreminder.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.shawnlin.numberpicker.NumberPicker
import vn.com.rd.waterreminder.Params
import vn.com.rd.waterreminder.R
import vn.com.rd.waterreminder.data.db.WaterDatabase
import vn.com.rd.waterreminder.data.repository.WaterGoalRepository
import vn.com.rd.waterreminder.databinding.ActivityGoalBinding
import vn.com.rd.waterreminder.factory.WaterGoalViewModelFactory
import vn.com.rd.waterreminder.ui.component.InfoItem
import vn.com.rd.waterreminder.ui.component.InfoItemAdapter
import vn.com.rd.waterreminder.viewmodel.WaterGoalViewModel
import java.util.Locale


class GoalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoalBinding
    private var itemType = 0
    private var adapter: InfoItemAdapter? = null
    private lateinit var list : MutableList<InfoItem>
    private val TAG = "GoalActivity"
    private lateinit var viewModel: WaterGoalViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backArrowDrawable = ContextCompat.getDrawable(this, R.drawable.ic_back)
        setSupportActionBar(binding.tbGoal)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        supportActionBar?.setHomeAsUpIndicator(backArrowDrawable)

        // 1. Khởi tạo các dependency
        val database = WaterDatabase.getInstance(this)
        val dao = database.waterGoalDao()
        val repository = WaterGoalRepository(dao)

        // 2. Tạo ViewModel với Factory
        val factory = WaterGoalViewModelFactory(repository, Params.USER_ID)
        viewModel = ViewModelProvider(this, factory)[WaterGoalViewModel::class.java]

        // 3. Theo dõi dữ liệu từ ViewModel
        observeViewModel()


        setupSpinner()
        setupRecyclerView()
        setUpNumberPicker()
        // Add sample data
        loadSampleData()
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
        adapter = InfoItemAdapter().apply {
            setData(list)
        }
        binding.rvHistory.adapter = adapter
    }

    private fun loadSampleData() {
        // Sample data for the last 7 days
        list.add(InfoItem("April 18, 2025", "8"))
        list.add(InfoItem("April 18, 2025", "8"))
        list.add(InfoItem("April 18, 2025", "8"))
        list.add(InfoItem("April 18, 2025", "8"))
        list.add(InfoItem("April 18, 2025", "8"))
        adapter?.notifyDataSetChanged()
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
    }
}