package vn.com.rd.waterreminder.ui.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import vn.com.rd.waterreminder.databinding.ActivityMainBinding
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import vn.com.rd.waterreminder.R
import vn.com.rd.waterreminder.WaterReminderApplication
import vn.com.rd.waterreminder.ui.fragment.AnalysisFragment
import vn.com.rd.waterreminder.ui.fragment.HomeFragment
import vn.com.rd.waterreminder.ui.fragment.ProfileFragment
import vn.com.rd.waterreminder.ui.fragment.SettingFragment
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as WaterReminderApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.menu_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.menu_analysis -> {
                    loadFragment(AnalysisFragment())
                    true
                }
                R.id.menu_setting -> {
                    loadFragment(SettingFragment())
                    true
                }
                R.id.menu_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
        binding.bottomNavigation.selectedItemId = R.id.menu_home

//        val startOfDay = Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, 0)
//            set(Calendar.MINUTE, 0)
//            set(Calendar.SECOND, 0)
//            set(Calendar.MILLISECOND, 0)
//        }.timeInMillis

//        viewModel.getTodayLogs(startOfDay).observe(this) { logs ->
//            binding.tvCount.text = "Bạn đã uống nước ${logs.size} lần hôm nay"
//        }
//
//        viewModel.totalAmount.observe(this) { total ->
//            binding.tvTotalAmount.text = "Đã uống: ${total ?: 0} ml"
//        }
//
//        binding.btnDrink.setOnClickListener {
//            viewModel.logWater()
//        }
    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}