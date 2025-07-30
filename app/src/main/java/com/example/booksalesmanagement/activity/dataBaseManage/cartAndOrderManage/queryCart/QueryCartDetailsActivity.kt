package com.example.booksalesmanagement.activity.dataBaseManage.cartAndOrderManage.queryCart

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.bean.CartDetails
import com.example.booksalesmanagement.dao.CartDetailsDao
import com.example.booksalesmanagement.databinding.ActivityQueryCartDetailsBinding
import kotlin.concurrent.thread

class QueryCartDetailsActivity : AppCompatActivity() {
    private var _binding: ActivityQueryCartDetailsBinding? = null
    private val binding get() = _binding!!

    private var cartList = ArrayList<CartDetails>()

    private var adapter: QueryCartDetailsManageAdapter? = null

    @SuppressLint("NotifyDataSetChanged", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityQueryCartDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //初始化适配器
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = layoutManager
        adapter = QueryCartDetailsManageAdapter(this, cartList)
        binding.recyclerView.adapter = adapter

        //查询按钮监听
        binding.btnQueryCartByName.setOnClickListener {
            //binding.SearchBookLineLayout.visibility = View.GONE
            val inputUserName = binding.etQueryCartByName.text.toString()
            thread {
                val newCartList = CartDetailsDao.queryCartListByUserName(inputUserName)
                if (newCartList?.isNotEmpty() == true) {
                    runOnUiThread {
                        Toast.makeText(this, "查询成功！", Toast.LENGTH_SHORT)
                            .show()
                        adapter!!.updateData(newCartList)
                        adapter!!.notifyDataSetChanged()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "没有找到该用户的购物车信息！", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        binding.swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent)
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshCart(adapter!!)
            //initBookMsgFromSqlServer()
        }


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshCart(adapter: QueryCartDetailsManageAdapter) {
        val inputUserName = binding.etQueryCartByName.text.toString()
        if (inputUserName.isEmpty()) {
            Toast.makeText(this, "请输入查询用户名！", Toast.LENGTH_SHORT)
                .show()
            binding.swipeRefreshLayout.isRefreshing = false
        } else {
            thread {
                val newCartList = CartDetailsDao.queryCartListByUserName(inputUserName)
                //随机打乱
                newCartList?.shuffle()

                if (newCartList?.isNotEmpty() == true) {
                    runOnUiThread {
                        Toast.makeText(this, "刷新成功！", Toast.LENGTH_SHORT)
                            .show()
                        adapter.updateData(newCartList)
                        adapter.notifyDataSetChanged()
                        binding.swipeRefreshLayout.isRefreshing = false

                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "没有找到该用户的购物车信息！", Toast.LENGTH_SHORT)
                            .show()
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}