package com.hust.homepage.ui.find

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hust.homepage.databinding.ItemFindBinding
import com.hust.netbase.FindUnit

class FindRecycleViewAdapter  : ListAdapter<FindUnit, FindRecycleViewAdapter.FindUnitViewHolder>(DiffCallback) {
    inner class FindUnitViewHolder(
        private val binding: ItemFindBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(findUnit: FindUnit) {
            binding.ivFindGo.setOnClickListener {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindUnitViewHolder {
        return FindUnitViewHolder(
            ItemFindBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FindUnitViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<FindUnit>() {
        override fun areItemsTheSame(oldItem: FindUnit, newItem: FindUnit): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: FindUnit, newItem: FindUnit): Boolean {
            return oldItem == newItem
        }
    }
}