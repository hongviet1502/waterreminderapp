package vn.com.rd.waterreminder.ui.component

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.com.rd.waterreminder.databinding.ItemListInfoMinimizedBinding
import vn.com.rd.waterreminder.databinding.ItemListInfoSingleLineBinding
import vn.com.rd.waterreminder.databinding.ItemSensorInfoBinding

class InfoItemAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    companion object {
        const val TYPE_SIMPLE = 1
        const val TYPE_MINIMIZED = 2
        const val TYPE_SINGLE_LINE = 3
    }

    var type = TYPE_SIMPLE
    private var list = listOf<InfoItem>()

    constructor() : super()
    constructor(list: List<InfoItem>) : super() {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (type) {
            TYPE_SIMPLE -> ItemViewHolder(
                ItemSensorInfoBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            TYPE_SINGLE_LINE -> SingleLineItemViewHolder(
                ItemListInfoSingleLineBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> MinimizedLineItemViewHolder(
                ItemListInfoMinimizedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        when (holder) {
            is ItemViewHolder -> {
                val itemBinding = holder.viewBinding
                itemBinding.titleTv.text = item.titleText
                itemBinding.contentTv.text = item.contentText
            }
            is SingleLineItemViewHolder -> {
                val itemBinding = holder.viewBinding
                itemBinding.titleTv.text = item.titleText
            }
            is MinimizedLineItemViewHolder -> {
                val itemBinding = holder.viewBinding
                itemBinding.titleTv.text = item.titleText
                itemBinding.contentTv.text = item.contentText
            }
        }
    }

    override fun getItemCount(): Int = list.size

    fun setData(list: List<InfoItem>) {
        this.list = list
        notifyDataSetChanged()
    }

    internal class ItemViewHolder(val viewBinding: ItemSensorInfoBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    internal class SingleLineItemViewHolder(val viewBinding: ItemListInfoSingleLineBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    internal class MinimizedLineItemViewHolder(val viewBinding: ItemListInfoMinimizedBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
}