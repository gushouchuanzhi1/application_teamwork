package com.hust.homepage.ui.find

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hust.homepage.databinding.ItemFindBinding
import com.hust.homepage.ui.mine.MineRecycleViewAdapter
import com.hust.netbase.FindUnit

class FindRecycleViewAdapter  : ListAdapter<FindUnit, FindRecycleViewAdapter.FindUnitViewHolder>(DiffCallback) {
    private lateinit var onItemClickListener: OnItemClickListener
    inner class FindUnitViewHolder(
        val binding: ItemFindBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(findUnit: FindUnit) {
            binding.findUnit = findUnit
            binding.ivFindIcon.setImageURI(Uri.parse(findUnit.profilePicPath))
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
        onItemClickListener.let {
            holder.binding.clItemFind.apply {
                setOnClickListener {
                    onItemClickListener.onClick(it, position)
                }
            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onClick(view: View, position: Int)
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