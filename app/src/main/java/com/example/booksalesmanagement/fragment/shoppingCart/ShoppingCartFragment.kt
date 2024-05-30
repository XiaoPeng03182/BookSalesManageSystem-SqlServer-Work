package com.example.booksalesmanagement.fragment.shoppingCart

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.activity.SaveUserMsg
import com.example.booksalesmanagement.bean.CartDetails
import com.example.booksalesmanagement.dao.CartDetailsDao
import com.example.booksalesmanagement.databinding.FragmentShoppingCartBinding
import com.example.booksalesmanagement.fragment.shoppingCart.adapter.ShoppingCartAdapter
import kotlin.concurrent.thread

class ShoppingCartFragment : Fragment() {

    private var _binding: FragmentShoppingCartBinding? = null

    private val binding get() = _binding!!

    private var cartList = ArrayList<CartDetails>()

    private var adapter:ShoppingCartAdapter? =null

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingCartBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //初始化适配器
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.shoppingCartRecyclerView.layoutManager = layoutManager
        adapter = ShoppingCartAdapter(requireContext(), cartList)
        binding.shoppingCartRecyclerView.adapter = adapter

        //初始化购物车
        initShoppingCart()

/*        // 设置 Toolbar
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.shoppingCartToolbar)*/

        //监听下滑刷新
        binding.shoppingCartSwipeRefreshLayout.setColorSchemeColors(R.color.colorAccent)
        binding.shoppingCartSwipeRefreshLayout.setOnRefreshListener {
            refreshCart(adapter!!)
            //initBookMsgFromSqlServer()
        }


        return root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initShoppingCart() {
        // 初始化购物车列表
        thread {
            val newCartList = CartDetailsDao.queryPreOrderMsgFromSQLServer(SaveUserMsg.userId)
            activity?.runOnUiThread {
                if (newCartList != null) {
                    adapter!!.updateData(newCartList)
                }
                adapter!!.notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshCart(adapter: ShoppingCartAdapter) {
        // 初始化购物车列表
        thread {
            val newCartList = CartDetailsDao.queryPreOrderMsgFromSQLServer(SaveUserMsg.userId)
            //随机打乱
            newCartList?.shuffle()
            activity?.runOnUiThread {
                if (newCartList != null) {
                    adapter.updateData(newCartList)
                }
                adapter.notifyDataSetChanged()
                binding.shoppingCartSwipeRefreshLayout.isRefreshing = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}