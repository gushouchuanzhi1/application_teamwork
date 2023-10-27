package com.hust.homepage.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.hust.chat.ChatActivity
import com.hust.homepage.HomePageActivityViewModel
import com.hust.homepage.databinding.FragmentHomeBinding
import com.hust.homepage.ui.find.FindRecycleViewAdapter
import com.hust.netbase.ChatUnit
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
    private val parentViewModel: HomePageActivityViewModel by activityViewModels()

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

    override fun onResume() {
        initData()
        super.onResume()
    }

    private fun initData() {
        if(isHaveService) {
            viewModel.getChatList()
        }else {
            viewModel.getLocalChatList()
        }
    }


    private fun finishRefreshAnim() {
        binding.refreshLayout.finishRefresh() //结束下拉刷新动画
        binding.refreshLayout.finishLoadMore() //结束上拉加载动画
        binding.rvChatlist.isEnabled = true
    }

    private fun initView() {
        val adapter = HomeRecycleViewAdapter()
        binding.rvChatlist.adapter = adapter
        adapter.setOnItemClickListener(object : HomeRecycleViewAdapter.OnItemClickListener {
            override fun onClick(view: View, position: Int, data: ChatUnit) {
                val intent = Intent(this@HomeFragment.requireContext(), ChatActivity::class.java)
                intent.putExtra("chatUnit", data)
                this@HomeFragment.requireContext().startActivity(intent)
            }
        })
        viewModel.apply {
            tip.observe(viewLifecycleOwner) {
                it?.let {
                    if(it.isNotEmpty()) {
                        Toast.makeText(this@HomeFragment.requireContext(), it, Toast.LENGTH_SHORT)
                            .show()
                        doneShowingTip()
                    }
                }
            }
            parentViewModel.isRefresh.observe(viewLifecycleOwner) {
                initData()
            }
            showingPlaceholder.observe(viewLifecycleOwner) {
                showPlaceHolderBy(it)
            }
        }

        lifecycleScope.launch {
            viewModel.chatList.onEach {
                finishRefreshAnim()
            }.collectLatest {
                adapter.submitList(it.reversed())
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