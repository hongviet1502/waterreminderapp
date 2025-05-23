package vn.com.rd.waterreminder.ui.component

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vn.com.rd.waterreminder.data.model.Reminder
import vn.com.rd.waterreminder.databinding.ItemReminderBinding

class ReminderItemAdapter(
    private val listener: OnReminderActionListener? = null
) : ListAdapter<Reminder, ReminderItemAdapter.ReminderViewHolder>(ReminderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = ItemReminderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, listener)
    }

    inner class ReminderViewHolder(
        private val binding: ItemReminderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Reminder, listener: OnReminderActionListener?) {
            // Bind data
            binding.tvReminderMessage.text = item.message
            binding.tvReminderTime.text = item.time
            binding.swReminderEnable.isChecked = item.isEnabled

            // Remove listener trước khi set value để tránh trigger không mong muốn
            binding.swReminderEnable.setOnCheckedChangeListener(null)
            binding.swReminderEnable.isChecked = item.isEnabled

            // Set listener cho switch
            binding.swReminderEnable.setOnCheckedChangeListener { _, isChecked ->
                listener?.onReminderToggle(item, isChecked)
            }

            // Set click listeners
            binding.root.setOnClickListener {
                listener?.onReminderClick(item)
            }

             binding.ivDeleteReminder.setOnClickListener {
                 listener?.onReminderDelete(item)
             }
        }
    }

    // Interface để handle các actions
    interface OnReminderActionListener {
        fun onReminderClick(reminder: Reminder)
        fun onReminderToggle(reminder: Reminder, isEnabled: Boolean)
        fun onReminderDelete(reminder: Reminder)
    }
}

// DiffCallback để optimize performance
class ReminderDiffCallback : DiffUtil.ItemCallback<Reminder>() {
    override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem.id == newItem.id // Assuming Reminder has an id field
    }

    override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem == newItem
    }
}