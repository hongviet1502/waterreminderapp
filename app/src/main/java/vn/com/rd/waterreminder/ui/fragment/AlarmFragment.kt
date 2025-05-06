package vn.com.rd.waterreminder.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vn.com.rd.waterreminder.R
import vn.com.rd.waterreminder.databinding.FragmentAlarmBinding

class AlarmFragment : Fragment() {
    private lateinit var _binding : FragmentAlarmBinding
    private val binding get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)
        return inflater.inflate(R.layout.fragment_alarm, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AlarmFragment().apply {

            }
    }
}