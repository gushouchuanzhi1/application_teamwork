package com.hust.homepage.ui.friend

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hust.homepage.databinding.FragmentFriendBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FriendFragment : Fragment() {
    private lateinit var binding: FragmentFriendBinding
    private val viewModel: FriendFragmentViewModel by viewModels()
    private var type: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initData()
        initView()
    }

    private fun initData() {
        type = requireArguments().getInt("type")
        when(type) {
            GET_FRIEND -> viewModel.getFriend()
            GET_REC -> viewModel.getRec()
            GET_LIKE -> viewModel.getLike()
        }
    }

    private fun initView() {
        val adapter = FriendRecycleViewAdapter()
        binding.rvFriendList.adapter = adapter
        adapter.setOnItemClickListener(object : FriendRecycleViewAdapter.OnItemClickListener {
            override fun onLikeClick(view: ImageView, songId: String) {
                viewModel.likeTheSong(view, songId)
            }

            override fun onSongClick(songId: String) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://music.163.com/#/song?id=${songId}"))
                startActivity(intent)
            }
        })

        viewModel.apply {
            tip.observe(viewLifecycleOwner) {
                it?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    viewModel.doneShowingTip()
                }
            }

            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    list.collectLatest {
                        adapter.submitList(it)
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(args: Bundle): FriendFragment {
            val newFragment = FriendFragment()
            newFragment.arguments = args
            return newFragment
        }
        const val GET_FRIEND = 0
        const val GET_REC = 1
        const val GET_LIKE = 2
    }
}