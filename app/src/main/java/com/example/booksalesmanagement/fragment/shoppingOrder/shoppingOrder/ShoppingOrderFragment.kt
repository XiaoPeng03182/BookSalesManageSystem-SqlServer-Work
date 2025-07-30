package com.example.booksalesmanagement.fragment.shoppingOrder.shoppingOrder

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.activity.SaveUserMsg
import com.example.booksalesmanagement.bean.OrderDetails
import com.example.booksalesmanagement.dao.OrderDetailsDao
import com.example.booksalesmanagement.databinding.FragmentShoppingOrderBinding
import com.example.booksalesmanagement.fragment.shoppingOrder.shoppingOrder.adapter.ShoppingOrderAdapter
import kotlin.concurrent.thread

class ShoppingOrderFragment : Fragment() {

    private var _binding: FragmentShoppingOrderBinding? = null

    private val binding get() = _binding!!

    private var orderList = ArrayList<OrderDetails>()

    private var adapter: ShoppingOrderAdapter? = null


    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingOrderBinding.inflate(inflater, container, false)

        //初始化适配器
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.shoppingOrderRecyclerView.layoutManager = layoutManager
        adapter = ShoppingOrderAdapter(requireContext(), orderList)
        binding.shoppingOrderRecyclerView.adapter = adapter

        //初始化订单列表
        initShoppingOrder()

        //监听下滑刷新
        binding.shoppingOrderSwipeRefreshLayout.setColorSchemeColors(R.color.colorAccent)
        binding.shoppingOrderSwipeRefreshLayout.setOnRefreshListener {
            refreshOrder(adapter!!)
            //initBookMsgFromSqlServer()
        }

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initShoppingOrder() {
        // 初始化订单列表
        thread {
            val newOrderList = OrderDetailsDao.queryOrderDetailsFromSQLServer(SaveUserMsg.userId)
            activity?.runOnUiThread {
                if (newOrderList != null) {
                    adapter!!.updateData(newOrderList)
                }
                adapter!!.notifyDataSetChanged()
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun refreshOrder(adapter: ShoppingOrderAdapter) {
        // 初始化订单列表
        thread {
            val newOrderList = OrderDetailsDao.queryOrderDetailsFromSQLServer(SaveUserMsg.userId)
            //随机打乱
            newOrderList?.shuffle()
            activity?.runOnUiThread {
                if (newOrderList != null) {
                    adapter.updateData(newOrderList)
                }
                adapter.notifyDataSetChanged()
                binding.shoppingOrderSwipeRefreshLayout.isRefreshing = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}