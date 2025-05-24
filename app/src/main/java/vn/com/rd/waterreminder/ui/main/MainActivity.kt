package vn.com.rd.waterreminder.ui.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import vn.com.rd.waterreminder.databinding.ActivityMainBinding
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import vn.com.rd.waterreminder.R
import vn.com.rd.waterreminder.WaterReminderApplication
import vn.com.rd.waterreminder.ui.factory.MainViewModelFactory
import vn.com.rd.waterreminder.service.WaterAlarmService
import vn.com.rd.waterreminder.ui.fragment.AlarmFragment
import vn.com.rd.waterreminder.ui.fragment.AnalysisFragment
import vn.com.rd.waterreminder.ui.fragment.HomeFragment
import vn.com.rd.waterreminder.ui.fragment.ProfileFragment
import vn.com.rd.waterreminder.ui.fragment.SettingFragment
import vn.com.rd.waterreminder.ui.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as WaterReminderApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
//        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
//            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
//            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, bottomInset)
//            insets
//        }
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
                R.id.menu_alarm -> {
                    loadFragment(AlarmFragment())
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

        // Kiểm tra quyền thông báo (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        // Khởi động Service
        val serviceIntent = Intent(this, WaterAlarmService::class.java)
        startForegroundService(serviceIntent)

    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}