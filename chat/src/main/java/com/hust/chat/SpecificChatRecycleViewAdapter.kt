package com.hust.chat

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hust.chat.databinding.ItemFriendChatBinding
import com.hust.chat.databinding.ItemMineChatBinding
import com.hust.database.BaseApplication
import com.hust.database.tables.ChatRecord


class SpecificChatRecycleViewAdapter: ListAdapter<ChatRecord, RecyclerView.ViewHolder>(DiffCallback) {
    inner class MyChatViewHolder(
        val binding: ItemMineChatBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatRecord: ChatRecord) {
            binding.chatRecord = chatRecord
            Glide.with(binding.root).load(Uri.parse(BaseApplication.currentUsePicPath)).into(binding.minePic)
        }
    }

    inner class FriendChatViewHolder(
        val binding: ItemFriendChatBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatRecord: ChatRecord) {
            binding.chatRecord = chatRecord
            Glide.with(binding.root).load(Uri.parse(BaseApplication.certainFriendPicPath)).into(binding.friendPic)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == MYSELF) MyChatViewHolder(
            ItemMineChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        else FriendChatViewHolder(
            ItemFriendChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if(holder is MyChatViewHolder) {
            holder.bind(item)
        }else if(holder is FriendChatViewHolder) {
            holder.bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return if (item.ownerId == BaseApplication.currentUseId)
            MYSELF
        else
            FRIEND
    }

    companion object {
        object DiffCallback : DiffUtil.ItemCallback<ChatRecord>() {
            override fun areItemsTheSame(oldItem: ChatRecord, newItem: ChatRecord): Boolean {
                return oldItem.msgSeq == newItem.msgSeq
            }

            override fun areContentsTheSame(oldItem: ChatRecord, newItem: ChatRecord): Boolean {
                return oldItem.content == newItem.content
            }
        }

        const val MYSELF = 0
        const val FRIEND = 1
    }
}