package vn.com.rd.waterreminder.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vn.com.rd.waterreminder.databinding.FragmentHomeBinding
import vn.com.rd.waterreminder.ui.activity.GoalActivity
import vn.com.rd.waterreminder.ui.main.MainActivity
import vn.com.rd.waterreminder.util.TimeUtil

class HomeFragment : Fragment() {
    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timeRunnable: Runnable

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

        binding.waterProgressView.setProgress(0.5f)
        binding.waterProgressView.setValue("500ml")

        binding.btnSetYourGoal.setOnClickListener {
            val intent = Intent(requireActivity(), GoalActivity::class.java)
            startActivity(intent)
        }
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

}