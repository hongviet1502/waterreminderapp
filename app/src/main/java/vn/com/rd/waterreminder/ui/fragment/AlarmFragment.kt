package vn.com.rd.waterreminder.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import vn.com.rd.waterreminder.Params
import vn.com.rd.waterreminder.R
import vn.com.rd.waterreminder.data.model.Reminder
import vn.com.rd.waterreminder.databinding.FragmentAlarmBinding
import vn.com.rd.waterreminder.ui.activity.ReminderActivity
import vn.com.rd.waterreminder.ui.component.ReminderItemAdapter
import vn.com.rd.waterreminder.ui.factory.GoalViewModelFactory
import vn.com.rd.waterreminder.ui.viewmodel.GoalViewModel

class AlarmFragment : Fragment(), ReminderItemAdapter.OnReminderActionListener {
    private lateinit var _binding: FragmentAlarmBinding
    private val binding get() = _binding
    private lateinit var list: MutableList<Reminder>
    private val TAG = "AlarmFragment"
    private lateinit var viewModel: GoalViewModel
    private var adapter: ReminderItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)

        val factory = GoalViewModelFactory(requireActivity(), Params.USER_ID)
        viewModel = ViewModelProvider(this, factory)[GoalViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

        binding.llAddNew.setOnClickListener {
            ReminderActivity.startForCreate(requireActivity())
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        list = ArrayList()
        adapter = ReminderItemAdapter(this).apply {
            adapter?.submitList(list)
        }
        binding.rvReminder.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.reminders.observe(viewLifecycleOwner) { reminders ->
            adapter?.submitList(reminders)
        }
    }

    override fun onReminderClick(reminder: Reminder) {
        ReminderActivity.startForEdit(requireActivity(), reminder)
    }

    override fun onReminderToggle(
        reminder: Reminder,
        isEnabled: Boolean
    ) {
        viewModel.toggleReminderEnabled(reminder, isEnabled)
    }

    override fun onReminderDelete(reminder: Reminder) {
        showBottomSheetDeleteDialog(reminder)
    }

    private fun showBottomSheetDeleteDialog(reminder: Reminder) {
        val bottomSheetDialog = BottomSheetDialog(requireActivity())
        val view =
            LayoutInflater.from(requireActivity()).inflate(R.layout.bottom_sheet_delete, null)

        view.findViewById<TextView>(R.id.tv_reminder_name).text = reminder.message

        view.findViewById<LinearLayout>(R.id.layout_delete).setOnClickListener {
            viewModel.deleteReminder(reminder)
            bottomSheetDialog.dismiss()
            Toast.makeText(requireActivity(), "Reminder deleted successfully", Toast.LENGTH_SHORT)
                .show()
        }

        view.findViewById<LinearLayout>(R.id.layout_cancel).setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AlarmFragment().apply {

            }
    }
}