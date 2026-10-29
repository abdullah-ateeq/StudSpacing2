package com.example.studspacing.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.studspacing.R
import com.example.studspacing.data.model.SavedCalculation
import com.example.studspacing.databinding.ItemHistoryBinding

/**
 * RecyclerView adapter for displaying calculation history items.
 */
class HistoryAdapter(
    private val onItemClick: (SavedCalculation) -> Unit,
    private val onDeleteClick: (SavedCalculation) -> Unit
) : ListAdapter<SavedCalculation, HistoryAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val context = holder.itemView.context

        holder.binding.tvHistoryDate.text = item.formattedDate
        holder.binding.tvHistoryStuds.text = context.getString(
            R.string.history_item_studs_format,
            item.totalStuds
        )
        holder.binding.tvHistoryDetails.text = context.getString(
            R.string.history_item_wall_format,
            item.wallLength,
            item.unit,
            item.spacing,
            item.unit
        )

        holder.binding.root.setOnClickListener {
            onItemClick(item)
        }

        holder.binding.btnDeleteHistory.setOnClickListener {
            onDeleteClick(item)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<SavedCalculation>() {
        override fun areItemsTheSame(oldItem: SavedCalculation, newItem: SavedCalculation): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SavedCalculation, newItem: SavedCalculation): Boolean {
            return oldItem == newItem
        }
    }
}
