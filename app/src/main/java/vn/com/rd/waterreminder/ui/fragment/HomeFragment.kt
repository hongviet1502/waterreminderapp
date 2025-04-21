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
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import vn.com.rd.waterreminder.Params
import vn.com.rd.waterreminder.data.db.WaterDatabase
import vn.com.rd.waterreminder.data.repository.WaterGoalRepository
import vn.com.rd.waterreminder.databinding.FragmentHomeBinding
import vn.com.rd.waterreminder.factory.WaterGoalViewModelFactory
import vn.com.rd.waterreminder.ui.activity.GoalActivity
import vn.com.rd.waterreminder.ui.main.MainActivity
import vn.com.rd.waterreminder.util.TimeUtil
import vn.com.rd.waterreminder.viewmodel.WaterGoalViewModel

class HomeFragment : Fragment() {
    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timeRunnable: Runnable
    private lateinit var waterGoalViewModel: WaterGoalViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
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

        binding.waterProgressView.setProgress(0.8f)

        binding.waterProgressView.setOnClickListener {
            binding.waterProgressView.animateDrinking(
                drinkDuration = 1500,
                refillDuration = 2000,
                delayBetween = 600,
                onAnimationEnd = {
                    // This will be called when the entire animation is complete
                    Toast.makeText(requireActivity(), "Water refilled!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.btnSetYourGoal.setOnClickListener {
            val intent = Intent(requireActivity(), GoalActivity::class.java)
            startActivity(intent)
        }

        val database = WaterDatabase.getInstance(requireActivity())
        val dao = database.waterGoalDao()
        val repository = WaterGoalRepository(dao)

        val factory = WaterGoalViewModelFactory(repository, Params.USER_ID)
        waterGoalViewModel = ViewModelProvider(this, factory)[WaterGoalViewModel::class.java]

        observeViewModel()
        return binding.root
    }

    companion object {

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

    private fun observeViewModel(){
        waterGoalViewModel.currentGoal.observe(viewLifecycleOwner) { goal ->
            if (goal != null) {
                // Update UI khi có goal
                val unit = when (goal.unit) {
                    Params.GLASS -> "glass(es)"
                    Params.BOTTLE -> "bottle(s)"
                    Params.MUG -> "mug(s)"
                    else -> "unknown"
                }

                val unitVolume = when (goal.unit) {
                    Params.GLASS -> Params.GLASS_VOL
                    Params.BOTTLE -> Params.BOTTLE_VOL
                    Params.MUG -> Params.MUG_VOL
                    else -> 0
                }
                val targetGoal = unitVolume * (goal.unitAmount)
                binding.tvTarget.text = targetGoal.toString() + "ml"
                binding.waterProgressView.setValue("${unitVolume}ml")
                binding.tvGoal.text = "${targetGoal}ml water (${goal.unitAmount} ${unit})"
            } else { }
        }
    }
}