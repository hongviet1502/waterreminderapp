package vn.com.rd.waterreminder.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import vn.com.rd.waterreminder.R
import vn.com.rd.waterreminder.databinding.FragmentAnalysisBinding

class AnalysisFragment : Fragment() {
    private lateinit var _binding : FragmentAnalysisBinding
    private val binding get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AnalysisFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}