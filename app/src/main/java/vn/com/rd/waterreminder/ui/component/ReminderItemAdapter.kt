package vn.com.rd.waterreminder.ui.component

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.com.rd.waterreminder.data.model.Reminder
import vn.com.rd.waterreminder.databinding.ItemReminderBinding

class ReminderItemAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private var listReminder = listOf<Reminder>()

    constructor() : super()
    constructor(listReminder: List<Reminder>) : super() {
        this.listReminder = listReminder
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ReminderItemViewHolder(
            ItemReminderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return listReminder.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = listReminder[position]
        when (holder) {
            is ReminderItemViewHolder -> {
                val itemBinding = holder.viewBinding
                itemBinding.tvReminderMessage.text = item.message
                itemBinding.tvReminderTime.text= item.time
            }
        }
    }

    fun setData(list: List<Reminder>) {
        this.listReminder = list
        notifyDataSetChanged()
    }

    internal class ReminderItemViewHolder(val viewBinding: ItemReminderBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
}