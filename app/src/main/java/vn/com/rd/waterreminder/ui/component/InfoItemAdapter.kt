package vn.com.rd.waterreminder.ui.component

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import vn.com.rd.waterreminder.databinding.ItemListInfoMinimizedBinding
import vn.com.rd.waterreminder.databinding.ItemListInfoSingleLineBinding
import vn.com.rd.waterreminder.databinding.ItemSensorInfoBinding

class InfoItemAdapter(
    private val viewType: ViewType = ViewType.SIMPLE
) : ListAdapter<InfoItem, RecyclerView.ViewHolder>(InfoItemDiffCallback()) {

    enum class ViewType(val value: Int) {
        SIMPLE(1),
        MINIMIZED(2),
        SINGLE_LINE(3)
    }

    override fun getItemViewType(position: Int): Int = viewType.value

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (ViewType.values().find { it.value == viewType }) {
            ViewType.SIMPLE -> SimpleViewHolder(
                ItemSensorInfoBinding.inflate(inflater, parent, false)
            )
            ViewType.SINGLE_LINE -> SingleLineViewHolder(
                ItemListInfoSingleLineBinding.inflate(inflater, parent, false)
            )
            ViewType.MINIMIZED -> MinimizedViewHolder(
                ItemListInfoMinimizedBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is SimpleViewHolder -> holder.bind(item)
            is SingleLineViewHolder -> holder.bind(item)
            is MinimizedViewHolder -> holder.bind(item)
        }
    }

    // ViewHolder classes with bind methods
    class SimpleViewHolder(
        private val binding: ItemSensorInfoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: InfoItem) {
            binding.apply {
                titleTv.text = item.titleText
                contentTv.text = item.contentText
            }
        }
    }

    class SingleLineViewHolder(
        private val binding: ItemListInfoSingleLineBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: InfoItem) {
            binding.titleTv.text = item.titleText
        }
    }

    class MinimizedViewHolder(
        private val binding: ItemListInfoMinimizedBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: InfoItem) {
            binding.apply {
                titleTv.text = item.titleText
                contentTv.text = item.contentText
            }
        }
    }
}

// DiffUtil callback for efficient list updates
class InfoItemDiffCallback : DiffUtil.ItemCallback<InfoItem>() {

    override fun areItemsTheSame(oldItem: InfoItem, newItem: InfoItem): Boolean {
        // Assuming InfoItem has an id field, adjust accordingly
        return oldItem.titleText == newItem.titleText
    }

    override fun areContentsTheSame(oldItem: InfoItem, newItem: InfoItem): Boolean {
        return oldItem == newItem
    }
}

// Extension function for easier adapter usage
fun InfoItemAdapter.updateData(newList: List<InfoItem>) {
    submitList(newList)
}