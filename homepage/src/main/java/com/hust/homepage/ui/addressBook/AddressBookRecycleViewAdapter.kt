package com.hust.homepage.ui.addressBook

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hust.homepage.databinding.ItemAddressBookBinding
import com.hust.netbase.ChatUnit

class AddressBookRecycleViewAdapter(

) : ListAdapter<ChatUnit, AddressBookRecycleViewAdapter.ChatUnitViewHolder>(DiffCallback) {
    private lateinit var onItemClickListener: OnItemClickListener
    inner class ChatUnitViewHolder(
        val binding: ItemAddressBookBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatUnit: ChatUnit, label: String?) {
            binding.chatUnit = chatUnit
            binding.label = label
            binding.ivProPic.setImageURI(Uri.parse(chatUnit.profilePicPath) ?: Uri.parse("android.resource://com.hust.mychat/drawable/ic_mychat"))
            label?.let {
                binding.addressLabel.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUnitViewHolder {
        return ChatUnitViewHolder(
            ItemAddressBookBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatUnitViewHolder, position: Int) {
        val item = getItem(position)
        val label = when (position) {
            5 -> "企业微信"
            7 -> "联系人"
            else -> ""
        }
        holder.bind(item, label)
        onItemClickListener.let {
            holder.binding.addressBookItem.apply {
                setOnClickListener {
                    onItemClickListener.onClick(it, position, item)
                }
            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onClick(view: View, position: Int, data: ChatUnit)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ChatUnit>() {
        override fun areItemsTheSame(oldItem: ChatUnit, newItem: ChatUnit): Boolean {
            return oldItem.use_id == newItem.use_id
        }

        override fun areContentsTheSame(oldItem: ChatUnit, newItem: ChatUnit): Boolean {
            return oldItem == newItem
        }
    }
}