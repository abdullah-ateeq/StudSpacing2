package com.example.studspacing.ui.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.studspacing.R
import com.example.studspacing.data.model.StudPosition
import com.example.studspacing.data.model.StudTag
import com.example.studspacing.databinding.ItemStudPositionBinding

/**
 * RecyclerView adapter for displaying sequential stud layout positions.
 */
class StudPositionAdapter(private val unitSymbol: String) :
    ListAdapter<StudPosition, StudPositionAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(val binding: ItemStudPositionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStudPositionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val context = holder.itemView.context

        holder.binding.tvStudNumber.text = context.getString(R.string.stud_number_format, item.studNumber)
        holder.binding.tvCenterPos.text = context.getString(
            R.string.stud_center_pos_format,
            item.centerPosition,
            unitSymbol
        )
        holder.binding.tvEdgePos.text = context.getString(
            R.string.stud_edge_pos_format,
            item.edgePosition,
            unitSymbol
        )

        // Configure role badge text and badge color
        val (tagTextRes, colorRes) = when (item.tag) {
            StudTag.START -> Pair(R.string.stud_tag_start, R.color.stud_start)
            StudTag.FIELD -> Pair(R.string.stud_tag_field, R.color.stud_field)
            StudTag.END -> Pair(R.string.stud_tag_end, R.color.stud_end)
        }

        holder.binding.tvStudTag.setText(tagTextRes)
        val color = ContextCompat.getColor(context, colorRes)
        holder.binding.tvStudTag.backgroundTintList = ColorStateList.valueOf(color)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<StudPosition>() {
        override fun areItemsTheSame(oldItem: StudPosition, newItem: StudPosition): Boolean {
            return oldItem.studNumber == newItem.studNumber
        }

        override fun areContentsTheSame(oldItem: StudPosition, newItem: StudPosition): Boolean {
            return oldItem == newItem
        }
    }
}
