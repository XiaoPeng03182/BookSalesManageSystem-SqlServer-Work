package com.example.booksalesmanagement.fragment.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.booksalesmanagement.activity.SaveUserMsg
import com.example.booksalesmanagement.activity.dataBaseManage.adminMsgManage.AdminMsgManageActivity
import com.example.booksalesmanagement.activity.dataBaseManage.bookMsgManage.BookMsgManageActivity
import com.example.booksalesmanagement.activity.dataBaseManage.bookMsgManage.insertbook.InsertBookMsgActivity
import com.example.booksalesmanagement.activity.dataBaseManage.cartAndOrderManage.CartAndOrderManageActivity
import com.example.booksalesmanagement.activity.dataBaseManage.systemManage.SystemManageActivity
import com.example.booksalesmanagement.activity.dataBaseManage.userMsgManage.UserMsgManageActivity
import com.example.booksalesmanagement.databinding.FragmentManageBinding
import com.example.booksalesmanagement.utils.SelectUserOrManager

class ManageFragment : Fragment() {

    private var _binding: FragmentManageBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentManageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (SelectUserOrManager.isSelectUser) {
            binding.tvManage.visibility = View.VISIBLE
            binding.constraintLayout.visibility = View.GONE
        }else{
            binding.tvManage.visibility = View.GONE
            binding.constraintLayout.visibility = View.VISIBLE
        }

        //图书信息管理
        binding.btnBookMsgManage.setOnClickListener {
            startActivity(Intent(context, BookMsgManageActivity::class.java))
        }
        //购物车和订单管理
        binding.btnCartAndOrderMsgManage.setOnClickListener {
            startActivity(Intent(context, CartAndOrderManageActivity::class.java))
        }
        //客户信息管理
        binding.btnUserMsgManage.setOnClickListener {
            startActivity(Intent(context, UserMsgManageActivity::class.java))
        }

        //管理员信息管理
        binding.btnAdminMsgManage.setOnClickListener {
            startActivity(Intent(context, AdminMsgManageActivity::class.java))
        }

        //系统维护
        binding.btnSystemManage.setOnClickListener {
            startActivity(Intent(context, SystemManageActivity::class.java))
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}