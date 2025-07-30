package com.example.booksalesmanagement.activity.dataBaseManage.userMsgManage.queryUserMsg

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.bean.User
import com.example.booksalesmanagement.dao.UserDao
import com.example.booksalesmanagement.databinding.ActivityQueryUserMsgBinding
import kotlin.concurrent.thread

class QueryUserMsgActivity : AppCompatActivity() {
    private var _binding: ActivityQueryUserMsgBinding? = null
    private val binding get() = _binding!!

    private var userList = ArrayList<User>()

    private var adapter: QueryUserMsgAdapter? = null

    @SuppressLint("NotifyDataSetChanged", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityQueryUserMsgBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //初始化适配器
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = layoutManager
        adapter = QueryUserMsgAdapter(this, userList)
        binding.recyclerView.adapter = adapter

        //查询按钮监听
        binding.btnQueryUserMsgByName.setOnClickListener {
            //binding.SearchBookLineLayout.visibility = View.GONE
            val inputUserName = binding.etQueryUserMsgByName.text.toString()
            thread {
                val user = UserDao.queryUserMsgByName(inputUserName)
                val newUserMsgList = ArrayList<User>()
                if (user != null) {
                    newUserMsgList.add(user)
                    runOnUiThread {
                        Toast.makeText(this, "查询成功！", Toast.LENGTH_SHORT)
                            .show()
                        adapter!!.updateData(newUserMsgList)
                        adapter!!.notifyDataSetChanged()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "没有找到该用户的信息！", Toast.LENGTH_SHORT)
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
    private fun refreshCart(adapter: QueryUserMsgAdapter) {
        thread {
            val newUserMsgList = UserDao.queryAllUserMsg()
            //随机打乱
            newUserMsgList?.shuffle()

            if (newUserMsgList?.isNotEmpty() == true) {
                runOnUiThread {
                    Toast.makeText(this, "刷新成功！", Toast.LENGTH_SHORT)
                        .show()
                    adapter.updateData(newUserMsgList)
                    adapter.notifyDataSetChanged()
                    binding.swipeRefreshLayout.isRefreshing = false

                }
            } else {
                runOnUiThread {
                    Toast.makeText(this, "没有找到用户信息！", Toast.LENGTH_SHORT)
                        .show()
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}