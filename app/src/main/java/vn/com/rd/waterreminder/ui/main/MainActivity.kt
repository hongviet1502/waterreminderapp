package vn.com.rd.waterreminder.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import vn.com.rd.waterreminder.databinding.ActivityMainBinding
import androidx.activity.viewModels
import vn.com.rd.waterreminder.WaterReminderApplication
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as WaterReminderApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        viewModel.getTodayLogs(startOfDay).observe(this) { logs ->
            binding.tvCount.text = "Bạn đã uống nước ${logs.size} lần hôm nay"
        }

        viewModel.totalAmount.observe(this) { total ->
            binding.tvTotalAmount.text = "Đã uống: ${total ?: 0} ml"
        }

        binding.btnDrink.setOnClickListener {
            viewModel.logWater()
        }
    }
}