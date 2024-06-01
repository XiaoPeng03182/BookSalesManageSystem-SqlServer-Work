package com.example.booksalesmanagement.activity.dataBaseManage.cartAndOrderManage.queryOrder

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.activity.dataBaseManage.cartAndOrderManage.queryCart.QueryCartDetailsManageAdapter
import com.example.booksalesmanagement.bean.OrderDetails
import com.example.booksalesmanagement.dao.OrderDetailsDao
import com.example.booksalesmanagement.databinding.ActivityQueryOrderDetailsBinding
import kotlin.concurrent.thread

class QueryOrderDetailsActivity : AppCompatActivity() {
    private var _binding: ActivityQueryOrderDetailsBinding? = null
    private val binding get() = _binding!!

    private var cartList = ArrayList<OrderDetails>()

    private var adapter: QueryOrderDetailsAdapter? = null

    @SuppressLint("NotifyDataSetChanged", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityQueryOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //初始化适配器
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = layoutManager
        adapter = QueryOrderDetailsAdapter(this, cartList)
        binding.recyclerView.adapter = adapter


        //查询按钮监听
        binding.btnQueryOrderByName.setOnClickListener {
            //binding.SearchBookLineLayout.visibility = View.GONE
            val inputUserName = binding.etQueryOrderByName.text.toString()
            thread {
                val newOrderList = OrderDetailsDao.queryOrderDetailsByUserName(inputUserName)
                if (newOrderList?.isNotEmpty() == true) {
                    runOnUiThread {
                        Toast.makeText(this, "查询成功！", Toast.LENGTH_SHORT)
                            .show()
                        adapter!!.updateData(newOrderList)
                        adapter!!.notifyDataSetChanged()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "没有找到该用户的订单信息！", Toast.LENGTH_SHORT)
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
    private fun refreshCart(adapter: QueryOrderDetailsAdapter) {
        val inputUserName = binding.etQueryOrderByName.text.toString()
        if (inputUserName.isEmpty()) {
            Toast.makeText(this, "请输入查询用户名！", Toast.LENGTH_SHORT)
                .show()
            binding.swipeRefreshLayout.isRefreshing = false
        } else {
            thread {
                val newOrderList = OrderDetailsDao.queryOrderDetailsByUserName(inputUserName)
                //随机打乱
                newOrderList?.shuffle()

                if (newOrderList?.isNotEmpty() == true) {
                    runOnUiThread {
                        Toast.makeText(this, "刷新成功！", Toast.LENGTH_SHORT)
                            .show()
                        adapter.updateData(newOrderList)
                        adapter.notifyDataSetChanged()
                        binding.swipeRefreshLayout.isRefreshing = false

                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "没有找到该用户的订单信息！", Toast.LENGTH_SHORT)
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