package com.hust.homepage.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hust.homepage.databinding.FragmentHomeBinding
import com.hust.resbase.PlaceholderType
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private val isHaveService = false
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRefresh()
        initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun finishRefreshAnim() {
        binding.refreshLayout.finishRefresh() //结束下拉刷新动画
        binding.refreshLayout.finishLoadMore() //结束上拉加载动画
        binding.rvChatlist.isEnabled = true
    }

    private fun initView() {
        val adapter = HomeRecycleViewAdapter()
        if(isHaveService) {
            viewModel.getChatList()
        }else {
            viewModel.getLocalChatList()
        }
        binding.rvChatlist.adapter = adapter
        viewModel.apply {
            tip.observe(viewLifecycleOwner) {
                it?.let {
                    Toast.makeText(this@HomeFragment.requireContext(), it, Toast.LENGTH_SHORT)
                        .show()
                    doneShowingTip()
                }
            }
            showingPlaceholder.observe(viewLifecycleOwner) {
                showPlaceHolderBy(it)
            }
        }

        lifecycleScope.launch {
            viewModel.chatList.onEach {
                finishRefreshAnim()
            }.collectLatest {
                adapter.submitList(it)
                if (it.isEmpty()) {
                    binding.rvChatlist.visibility = View.GONE
                    binding.minePlaceholder.visibility = View.VISIBLE
                } else {
                    binding.rvChatlist.visibility = View.VISIBLE
                    binding.minePlaceholder.visibility = View.GONE
                }
            }
        }
    }

    private fun initRefresh() {
        binding.refreshLayout.apply {
            setRefreshHeader(ClassicsHeader(activity))
            setRefreshFooter(ClassicsFooter(activity))
            setEnableLoadMore(true)
            setEnableRefresh(true)
            setOnRefreshListener {
                if(isHaveService) {
                    viewModel.getChatList()
                }else {
                    viewModel.getLocalChatList()
                }
                binding.rvChatlist.isEnabled = false
            }
            setOnLoadMoreListener {
                if(isHaveService) {
                    viewModel.loadMoreChat()
                }else {

                }
                binding.rvChatlist.isEnabled = false
            }
        }
    }

    private fun showPlaceHolderBy(placeholderType: PlaceholderType) {
        when (placeholderType) {
            PlaceholderType.PLACEHOLDER_NETWORK_ERROR -> {
                binding.placeholderHomeNetError.visibility = View.VISIBLE
                binding.placeholderHomeNoContent.visibility = View.GONE
                viewModel.tip.value = "出错啦！"
            }

            PlaceholderType.PLACEHOLDER_NO_CONTENT -> {
                binding.placeholderHomeNoContent.visibility = View.VISIBLE
                binding.placeholderHomeNetError.visibility = View.GONE
            }
        }
    }
}