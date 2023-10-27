package com.hust.homepage.ui.mine

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.hust.database.MMKVUtil
import com.hust.homepage.R
import com.hust.homepage.databinding.FragmentMineBinding
import com.hust.resbase.SpaceItemDecoration
import com.hust.resbase.ArouterConfig
import com.hust.resbase.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MineFragment : Fragment() {
    private var _binding: FragmentMineBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: MineFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        initAdapter()
    }

    override fun onResume() {
        viewModel.getPersonalProfile { user ->
            user?.let {
                withContext(Dispatchers.Main) {
                    binding.ivMineProfilePic.setImageURI(Uri.parse(it.profilePicPath))
                    binding.user = it
                }
            }
        }
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {
        viewModel.tip.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.doneShowingTip()
            }
        }
    }

    private fun initAdapter() {
        val adapter = MineRecycleViewAdapter()
        adapter.submitList(viewModel.mineList.value)
        binding.rvMineList.adapter = adapter

        binding.rvMineList.addItemDecoration(object : SpaceItemDecoration(0, 0) {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val spacePosition = listOf(1, 5)
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

        adapter.setOnItemClickListener(object : MineRecycleViewAdapter.OnItemClickListener {
            override fun onClick(view: View, position: Int) {
                when(position) {
                    4 -> initShareCardView()
                    5 -> initLogOutDialog()
                    else -> viewModel.tip.value = "功能还在开发中"
                }
            }
        })
    }

    private fun cancelDarkBackGround() {
        val lp = this.requireActivity().window.attributes
        lp.alpha = 1f // 0.0~1.0
        this.requireActivity().window.attributes = lp
    }   //取消暗背景

    private fun initShareCardView() {
        AsyncLayoutInflater(requireContext()).inflate(R.layout.app_share_card,null
        ) { view, _, _ ->
            val ppwShare = PopupWindow(view)
            val shareCardView = View.inflate(context, R.layout.ppw_share, null)
            val shareCard = shareCardView.findViewById<LinearLayout>(R.id.share_card)
            val cancel = shareCardView.findViewById<TextView>(R.id.share_cancel_button)
            val mainContent = view.findViewById<ScrollView>(R.id.main_content)
            val code =  view.findViewById<ImageView>(R.id.QR_code)
            val ppwFunc = PopupWindow(shareCardView)

            val window = this.requireActivity().window
            with(ppwShare) {
                isOutsideTouchable = true  //点击卡片外部退出
                isFocusable = true     //按返回键允许退出
                width = ViewGroup.LayoutParams.WRAP_CONTENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                animationStyle = com.hust.resbase.R.style.PageAnim
            }
            with(ppwFunc) {
                isOutsideTouchable = false  //点击卡片外部退出
                isFocusable = false     //按返回键允许退出
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                animationStyle = com.hust.resbase.R.style.PageAnim
            }

            //减弱背景亮度
            window.attributes.alpha = 0.6f
            window.setWindowAnimations(com.hust.resbase.R.style.darkScreenAnim)
            ppwShare.showAtLocation(
                window.decorView, Gravity.TOP,
                0, 0
            )
            ppwFunc.showAtLocation(
                window.decorView, Gravity.BOTTOM,
                0, 0
            )    //设置显示位置
            var bitmap: Bitmap? = null
            viewModel.viewModelScope.launch {
                bitmap = viewModel.generateQRCode("https://husthole.com/#/download")
                code.setImageBitmap(bitmap)
            }
            ppwShare.setOnDismissListener {
                ppwFunc.dismiss()
                cancelDarkBackGround()
            }
            cancel.setOnClickListener {
                ppwShare.dismiss()
            }
            shareCard.setOnClickListener {
                viewModel.viewModelScope.launch {
                    val now = Date()
                    val ft2 = SimpleDateFormat("yyyyMMddhhmmss", Locale.CHINA)
                    viewModel.generate(mainContent)?.let {
                        viewModel.save(requireContext(),it, "AppShare" + ft2.format(now))
                        bitmap?.recycle()
                        ppwShare.dismiss()
                    }
                }
                ppwShare.dismiss()
            }
        }
        //val shareCardView = View.inflate(context, R.layout.ppw_share, null)
    }

    private fun initLogOutDialog() {
        val dialog = Dialog(requireContext())
        val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_logout, null)
        dialog.setContentView(dialogView)
        val btnCancel = dialogView.findViewById<Button>(R.id.cancel)
        val btnLogout = dialogView.findViewById<Button>(R.id.logout)
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnLogout.setOnClickListener {
            dialog.dismiss()
            ARouter.getInstance().build(ArouterConfig.ACTIVITY_LAR).navigation(requireContext())
            val mmkvUtil = MMKVUtil.getMMKV(requireContext())
            mmkvUtil.put(Constant.IS_LOGIN, false)
            this.requireActivity().finish()
        }
        dialog.show()
    }
}