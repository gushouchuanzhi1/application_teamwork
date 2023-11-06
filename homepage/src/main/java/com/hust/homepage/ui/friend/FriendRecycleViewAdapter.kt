package com.hust.homepage.ui.friend

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hust.homepage.databinding.ItemFriendBinding
import com.hust.netbase.UserLike

class FriendRecycleViewAdapter : ListAdapter<UserLike, FriendRecycleViewAdapter.UserLikeViewHolder>(DiffCallback)  {
    private lateinit var onItemClickListener: OnItemClickListener

    inner class UserLikeViewHolder(
        val binding: ItemFriendBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(userLike: UserLike) {
            binding.likeSong = userLike

            binding.like.setOnClickListener {
                onItemClickListener.onLikeClick(binding.like, userLike.info.songId)
            }
            binding.latestChat.setOnClickListener {
                onItemClickListener.onSongClick(userLike.info.songId)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendRecycleViewAdapter.UserLikeViewHolder {
        return UserLikeViewHolder(
            ItemFriendBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: FriendRecycleViewAdapter.UserLikeViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        holder.bind(item)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onLikeClick(view: ImageView, songId: String)

        fun onSongClick(songId: String)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<UserLike>() {
        override fun areItemsTheSame(oldItem: UserLike, newItem: UserLike): Boolean {
            return oldItem.info == newItem.info
        }

        override fun areContentsTheSame(oldItem: UserLike, newItem: UserLike): Boolean {
            return oldItem.isLike == newItem.isLike
        }
    }
}