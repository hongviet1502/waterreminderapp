package vn.com.rd.waterreminder.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import vn.com.rd.waterreminder.Params
import vn.com.rd.waterreminder.data.model.Reminder
import vn.com.rd.waterreminder.data.model.WaterDayHistoryItem
import vn.com.rd.waterreminder.data.model.toInfoItems
import vn.com.rd.waterreminder.databinding.FragmentAnalysisBinding
import vn.com.rd.waterreminder.ui.component.InfoItem
import vn.com.rd.waterreminder.ui.component.InfoItemAdapter
import vn.com.rd.waterreminder.ui.component.ReminderItemAdapter
import vn.com.rd.waterreminder.ui.factory.AnalysisViewModelFactory
import vn.com.rd.waterreminder.ui.viewmodel.AnalysisViewModel

class AnalysisFragment : Fragment() {
    private lateinit var _binding: FragmentAnalysisBinding
    private val binding get() = _binding
    private val TAG = "AnalysisFragment"
    private lateinit var viewModel: AnalysisViewModel
    private var adapter: InfoItemAdapter? = null
    private lateinit var list: MutableList<InfoItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)

        val factory = AnalysisViewModelFactory(requireActivity(), Params.USER_ID)
        viewModel = ViewModelProvider(this, factory)[AnalysisViewModel::class.java]

        observeViewModel()
        setupRecyclerView()
        return binding.root
    }

    private fun observeViewModel() {
        viewModel.historyItems.observe(viewLifecycleOwner) { historyItems ->
            showBarChart(binding.barChart, historyItems)
        }
        viewModel.allIntakes.observe(viewLifecycleOwner) { allIntakes ->
            val list = allIntakes.toInfoItems()
            adapter?.submitList(list)
        }
    }

    private fun showBarChart(barChart: BarChart, data: List<WaterDayHistoryItem>) {
        val entries = ArrayList<BarEntry>()
        val targetEntries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        data.reversed().forEachIndexed { index, item ->
            entries.add(BarEntry(index.toFloat(), item.amount.toFloat()))
            targetEntries.add(BarEntry(index.toFloat(), item.target.toFloat()))
            labels.add(item.date.substring(5)) // "05-24"
        }

        val drinkSet = BarDataSet(entries, "Drink (ml)")
        drinkSet.color = Color.parseColor("#42A5F5")

        val targetSet = BarDataSet(targetEntries, "Target (ml)")
        targetSet.color = Color.parseColor("#90CAF9")

        val barData = BarData(drinkSet, targetSet)
        barData.barWidth = 0.4f

        // Đặt group spacing
        barChart.data = barData
        barChart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(labels)
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            setDrawGridLines(false)
            labelRotationAngle = -45f
        }

        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = true

        barChart.setVisibleXRangeMaximum(7f)
        barChart.groupBars(0f, 0.2f, 0.05f)
        barChart.invalidate()
    }

    private fun setupRecyclerView() {
        list = ArrayList()
        adapter = InfoItemAdapter(InfoItemAdapter.ViewType.SIMPLE).apply {
            adapter?.submitList(list)
        }
        binding.rvIntakeHistory.adapter = adapter
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