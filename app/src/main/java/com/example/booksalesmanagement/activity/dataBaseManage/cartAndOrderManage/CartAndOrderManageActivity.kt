package com.example.booksalesmanagement.activity.dataBaseManage.cartAndOrderManage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.activity.dataBaseManage.bookMsgManage.BookMsgManageActivity
import com.example.booksalesmanagement.activity.dataBaseManage.cartAndOrderManage.deleteCart.DeleteCartDetailsActivity
import com.example.booksalesmanagement.activity.dataBaseManage.cartAndOrderManage.deleteOrder.DeleteOrderDetailsActivity
import com.example.booksalesmanagement.activity.dataBaseManage.cartAndOrderManage.queryCart.QueryCartDetailsActivity
import com.example.booksalesmanagement.activity.dataBaseManage.cartAndOrderManage.queryOrder.QueryOrderDetailsActivity
import com.example.booksalesmanagement.databinding.ActivityBookMsgManageBinding
import com.example.booksalesmanagement.databinding.ActivityCartAndOrderManageBinding

class CartAndOrderManageActivity : AppCompatActivity() {
    private var _binding: ActivityCartAndOrderManageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCartAndOrderManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //查询购物车信息
        binding.btnCartQuery.setOnClickListener {
            startActivity(Intent(this, QueryCartDetailsActivity::class.java))
        }

        //删除购物车信息
        binding.btnCartDelete.setOnClickListener {
            startActivity(Intent(this, DeleteCartDetailsActivity::class.java))
        }

        //查询订单信息
        binding.btnOrderQuery.setOnClickListener {
            startActivity(Intent(this, QueryOrderDetailsActivity::class.java))

        }

        //删除订单信息
        binding.btnOrderDelete.setOnClickListener {
            startActivity(Intent(this, DeleteOrderDetailsActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}