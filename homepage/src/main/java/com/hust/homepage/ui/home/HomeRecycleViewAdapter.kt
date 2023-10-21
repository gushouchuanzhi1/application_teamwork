package com.hust.homepage.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hust.homepage.databinding.ItemHomeChatBinding
import com.hust.netbase.ChatUnit

class HomeRecycleViewAdapter(

) : ListAdapter<ChatUnit, HomeRecycleViewAdapter.ChatUnitViewHolder>(DiffCallback) {
    inner class ChatUnitViewHolder(
        private val binding: ItemHomeChatBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatUnit: ChatUnit) {
            binding.chatUnit = chatUnit
            binding.chatItem.setOnClickListener {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUnitViewHolder {
        return ChatUnitViewHolder(
            ItemHomeChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatUnitViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ChatUnit>() {
        override fun areItemsTheSame(oldItem: ChatUnit, newItem: ChatUnit): Boolean {
            return oldItem.use_id == newItem.use_id
        }

        override fun areContentsTheSame(oldItem: ChatUnit, newItem: ChatUnit): Boolean {
            return oldItem.message == newItem.message
        }
    }
}