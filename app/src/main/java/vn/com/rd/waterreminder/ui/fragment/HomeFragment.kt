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
import androidx.lifecycle.ViewModelProvider
import vn.com.rd.waterreminder.Params
import vn.com.rd.waterreminder.data.db.WaterDatabase
import vn.com.rd.waterreminder.data.model.WaterIntake
import vn.com.rd.waterreminder.data.repository.WaterGoalRepository
import vn.com.rd.waterreminder.data.repository.WaterIntakeRepository
import vn.com.rd.waterreminder.databinding.FragmentHomeBinding
import vn.com.rd.waterreminder.factory.HomeViewModelFactory
import vn.com.rd.waterreminder.ui.activity.GoalActivity
import vn.com.rd.waterreminder.util.TimeUtil
import vn.com.rd.waterreminder.viewmodel.HomeViewModel
import vn.com.rd.waterreminder.viewmodel.WaterIntakeViewModel

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
        startUpdatingTime()
    }

    override fun onPause() {
        super.onPause()
        stopUpdatingTime()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val homeViewModelFactory = HomeViewModelFactory(requireActivity(), Params.USER_ID)
        homeViewModel = ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]
        setupListeners()
        observeViewModel()
        return binding.root
    }

    private fun startUpdatingTime() {
        timeRunnable = object : Runnable {
            override fun run() {
                val currentTime = TimeUtil.getCurrentTime()
                binding.tvTime.text = currentTime
                handler.postDelayed(this, 10000)
            }
        }
        handler.post(timeRunnable)
    }

    private fun stopUpdatingTime() {
        handler.removeCallbacks(timeRunnable)
    }

    private fun setupListeners() {
        binding.waterProgressView.setProgress(0.8f)
        binding.waterProgressView.setOnClickListener {
            binding.waterProgressView.animateDrinking(
                drinkDuration = 1500,
                refillDuration = 2000,
                delayBetween = 600,
                onAnimationEnd = {
                    homeViewModel.addWaterIntake(WaterIntake(userId = Params.USER_ID, amount = unitVol, containerType = containerType, timestamp = System.currentTimeMillis()))
                }
            )
        }

        binding.btnSetYourGoal.setOnClickListener {
            val intent = Intent(requireActivity(), GoalActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel(){
        homeViewModel.currentGoal.observe(viewLifecycleOwner) { goal ->
            if (goal != null) {
                // Update UI khi có goal
                val unit = when (goal.unit) {
                    Params.GLASS -> "glass(es)"
                    Params.BOTTLE -> "bottle(s)"
                    Params.MUG -> "mug(s)"
                    else -> "unknown"
                }
                containerType = goal.unit
                val unitVolume = when (goal.unit) {
                    Params.GLASS -> Params.GLASS_VOL
                    Params.BOTTLE -> Params.BOTTLE_VOL
                    Params.MUG -> Params.MUG_VOL
                    else -> 0
                }
                unitVol = unitVolume
                val targetGoal = unitVolume * (goal.unitAmount)
                targetAmount = targetGoal
                binding.tvTarget.text = targetGoal.toString() + "ml"
                binding.waterProgressView.setValue("${unitVolume}ml")
                binding.tvGoal.text = "${targetGoal}ml water (${goal.unitAmount} ${unit})"
            } else { }
        }

        homeViewModel.getTodayIntake().observe(requireActivity()) { intake ->
            binding.tvTodayTotal.text = intake.toString() + "ml"
            val percent = (intake?.toDouble()?.div(targetAmount))?.times(100)
            binding.tvPercentToday.text = percent.toString()+"%"
            if (percent != null) {
                binding.prbTarget.progress = percent.toInt()
            }
        }
    }
}