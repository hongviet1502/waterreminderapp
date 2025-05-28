package vn.com.rd.waterreminder.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import vn.com.rd.waterreminder.Params
import vn.com.rd.waterreminder.R
import vn.com.rd.waterreminder.data.db.WaterDatabase
import vn.com.rd.waterreminder.data.model.WaterIntake
import vn.com.rd.waterreminder.data.repository.WaterGoalRepository
import vn.com.rd.waterreminder.data.repository.WaterIntakeRepository
import vn.com.rd.waterreminder.databinding.FragmentHomeBinding
import vn.com.rd.waterreminder.ui.factory.HomeViewModelFactory
import vn.com.rd.waterreminder.ui.activity.GoalActivity
import vn.com.rd.waterreminder.util.TimeUtil
import vn.com.rd.waterreminder.ui.viewmodel.HomeViewModel
import java.util.Calendar

class HomeFragment : Fragment() {
    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding
    private val handler = Handler(Looper.getMainLooper())
    private var unitVol = 0
    private var containerType = 0
    private var targetAmount = 0
    private lateinit var timeRunnable: Runnable
    private lateinit var homeViewModel: HomeViewModel
    private val TAG = "HomeFragment"

    companion object {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.loadGoalData()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val homeViewModelFactory = HomeViewModelFactory(requireActivity(), Params.USER_ID)
        homeViewModel = ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]
        setupListeners()
        setupUI()
        observeViewModel()
        return binding.root
    }

    private fun setupListeners() {
        binding.btn100ml.setOnClickListener { homeViewModel.addWaterIntake(100) }
        binding.btn250ml.setOnClickListener { homeViewModel.addWaterIntake(250) }
        binding.btn500ml.setOnClickListener { homeViewModel.addWaterIntake(500) }
        binding.btn750ml.setOnClickListener { homeViewModel.addWaterIntake(750) }
    }

    private fun setupUI() {
        // Set greeting based on time
        val greeting = getGreeting()
        binding.tvGreeting.text = greeting
        setupWeatherInfo()
    }

    private fun observeViewModel(){
        homeViewModel.currentGoal.observe(viewLifecycleOwner) { goal ->
            if (goal != null) {
                val unitVolume = when (goal.unit) {
                    Params.GLASS -> Params.GLASS_VOL
                    Params.BOTTLE -> Params.BOTTLE_VOL
                    Params.MUG -> Params.MUG_VOL
                    else -> 0
                }
                val targetGoal = unitVolume * (goal.unitAmount)
                targetAmount = targetGoal
                binding.tvTargetWater.text = "of " + targetGoal.toString() + "ml target"
            } else { }
        }

        homeViewModel.getTodayIntake().observe(requireActivity()) { intake ->
            binding.tvCurrentWater.text = intake.toString() + "ml"
            val percent = (intake?.toDouble()?.div(targetAmount))?.times(100)
            updateMotivationalMessage(percent?.toInt() ?: 0)
            binding.tvProgressPercentage.text = "$percent%"
            if (percent != null) {
                binding.progressBar.progress = percent.toInt()
            }
        }

        homeViewModel.weatherData.observe(viewLifecycleOwner, Observer { weather ->
            Log.i(TAG, "weatherData: $weather")
            binding.tvTemperature.text = "${weather.main.temp}°C"
            binding.tvHumidity.text = "${weather.main.humidity}%"
            binding.tvLocation.text = weather.name

            val weatherIconCode = weather.weather[0].icon
            when (weatherIconCode) {
                "01d" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_01d)
                "01n" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_01n)
                "02d" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_02d)
                "02n" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_02n)
                "03d" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_03d)
                "03n" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_03n)
                "04d" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_04d)
                "04n" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_04n)
                "09d" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_09d)
                "09n" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_09n)
                "10d" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_10d)
                "10n" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_10n)
                "11d" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_11d)
                "11n" -> binding.ivWeatherIcon.setImageResource(R.drawable.ic_11n)
                else -> {
                    // Xử lý trường hợp mặc định nếu cần
                    binding.ivWeatherIcon.setImageResource(R.drawable.ic_02d)
                }
            }
        })

        // Gọi API thời tiết
        homeViewModel.fetchWeather("Hanoi")
    }

    private fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "Good Morning"
            hour < 18 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }

    private fun setupWeatherInfo() {
        binding.tvTemperature.text = "28°C"
        binding.tvLocation.text = "Ho Chi Minh City"
        binding.tvHumidity.text = "65%"
        binding.tvWeatherRecommendation.text = "Hot weather! Drink more water 🔥"
        // Set weather icon based on condition
        binding.ivWeatherIcon.setImageResource(R.drawable.ic_01d)
    }

    private fun updateMotivationalMessage(percentage: Int) {
        val message = when {
            percentage >= 100 -> "🎉 Amazing! You've reached your daily goal!"
            percentage >= 75 -> "🌟 Almost there! You're doing great!"
            percentage >= 50 -> "💪 Great job! You're halfway to your goal!"
            percentage >= 25 -> "🚀 Good start! Keep it up!"
            else -> "💧 Time to start hydrating!"
        }
        binding.tvMotivationalMessage.text = message
    }
}