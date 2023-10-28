package com.hust.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.hust.chat.databinding.FragmentSpecificChatBinding
import com.hust.resbase.SpaceItemDecoration
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SpecificChatFragment : Fragment() {

    private var _binding: FragmentSpecificChatBinding? = null

    private val binding get() = _binding!!
    private val viewModel: SpecificFragmentViewModel by viewModels()
    private val parentViewModel: ChatActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpecificChatBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    override fun onResume() {
        viewModel.getChatList(parentViewModel.chatUnit.value?.message?.chatId ?: "")
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {
        val adapter = SpecificChatRecycleViewAdapter()
        binding.rvChatList.adapter = adapter
        binding.rvChatList.addItemDecoration(SpaceItemDecoration(0, 0))
        lifecycleScope.launch {
            viewModel.chatList.collectLatest {
                adapter.submitList(it)
            }
        }
        binding.etReplyPost.addTextChangedListener {
            binding.btnSend.isEnabled = (it?.isNotEmpty() == true)
        }
        binding.btnSend.setOnClickListener {
            viewModel.sendAMessage(binding.etReplyPost.text, parentViewModel.chatUnit.value?.message?.chatId ?: "")
        }
        viewModel.tip.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.doneTipShow()
            }
        }
    }
}