package com.hust.homepage.ui.find

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hust.homepage.databinding.FragmentFindBinding
import com.hust.resbase.SpaceItemDecoration

class FindFragment : Fragment() {

    private var _binding: FragmentFindBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FindFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFindBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {
        val adapter = FindRecycleViewAdapter()
        adapter.submitList(viewModel.findList.value)
        binding.rvFindList.adapter = adapter
        binding.rvFindList.addItemDecoration(object : SpaceItemDecoration(0, 0) {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val spacePosition = listOf(1, 3, 5, 7, 8, 10)
                val layoutManager: LinearLayoutManager = parent.layoutManager as LinearLayoutManager
                if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                    //最后一项需要 bottom
                    if (parent.getChildAdapterPosition(view) == layoutManager.itemCount - 1) {
                        outRect.bottom = topBottom
                    }
                    if(spacePosition.any {it == parent.getChildAdapterPosition(view)}) {
                        outRect.top = topBottom + 20
                    } else {
                        outRect.top = topBottom
                    }
                    outRect.left = leftRight
                    outRect.right = leftRight
                } else {
                    //最后一项需要right
                    if (parent.getChildAdapterPosition(view) == layoutManager.itemCount - 1) {
                        outRect.right = leftRight
                    }
                    outRect.top = topBottom
                    outRect.left = leftRight
                    outRect.bottom = topBottom
                }
            }
        })
        adapter.setOnItemClickListener(object : FindRecycleViewAdapter.OnItemClickListener {
            override fun onClick(view: View, position: Int) {
                viewModel.tip.value = "功能还在开发中"
            }
        })
        viewModel.tip.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.doneShowingTip()
            }
        }
    }
}