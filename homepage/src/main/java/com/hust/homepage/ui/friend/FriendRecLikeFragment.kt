package com.hust.homepage.ui.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.hust.homepage.R
import com.hust.homepage.databinding.FragmentFriendLikeRecBinding

class FriendRecLikeFragment : Fragment() {
    private lateinit var binding: FragmentFriendLikeRecBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendLikeRecBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vpHoleStar.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = titleList.size

            override fun createFragment(position: Int): Fragment {
                val bundle = Bundle()
                bundle.putInt("type", position)
                return FriendFragment.newInstance(bundle)
            }
        }
        TabLayoutMediator(binding.tlHoleStar, binding.vpHoleStar) { tab, position ->
            val tabView = LayoutInflater.from(context).inflate(R.layout.tab_mine, binding.tlHoleStar, false)
            val tv = tabView.findViewById(R.id.title) as TextView
            tv.text = titleList[position]
            tab.customView = tabView
        }.attach()

    }

    companion object {
        val titleList = listOf(
            "朋友圈",
            "推荐",
            "喜欢"
        )
    }
}