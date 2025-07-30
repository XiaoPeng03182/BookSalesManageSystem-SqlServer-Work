package com.example.booksalesmanagement.activity.dataBaseManage.adminMsgManage.queryAdminMsg

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.bean.Administrator
import com.example.booksalesmanagement.dao.AdminDao
import com.example.booksalesmanagement.databinding.ActivityQueryAdminMsgBinding
import kotlin.concurrent.thread

class QueryAdminMsgActivity : AppCompatActivity() {
    private var _binding: ActivityQueryAdminMsgBinding? = null
    private val binding get() = _binding!!

    private var adminList = ArrayList<Administrator>()

    private var adapter: QueryAdminMsgAdapter? = null

    @SuppressLint("NotifyDataSetChanged", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityQueryAdminMsgBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //初始化适配器
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = layoutManager
        adapter = QueryAdminMsgAdapter(this, adminList)
        binding.recyclerView.adapter = adapter

        //查询按钮监听
        binding.btnQueryAdminMsgByName.setOnClickListener {
            val inputAdminName = binding.etQueryAdminMsgByName.text.toString()
            thread {
                val newAdminList = AdminDao.queryAdminMagByName(inputAdminName)
                if (newAdminList?.isNotEmpty() == true) {
                    runOnUiThread {
                        Toast.makeText(this, "查询成功！", Toast.LENGTH_SHORT)
                            .show()
                        adapter!!.updateData(newAdminList)
                        adapter!!.notifyDataSetChanged()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, "没有找到该管理员信息！", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        binding.swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent)
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshAdmin(adapter!!)
            //initBookMsgFromSqlServer()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshAdmin(adapter: QueryAdminMsgAdapter) {
        thread {
            val newAdminList = AdminDao.queryAllAdmin()
            //随机打乱
            newAdminList?.shuffle()

            if (newAdminList?.isNotEmpty() == true) {
                runOnUiThread {
                    Toast.makeText(this, "刷新成功！", Toast.LENGTH_SHORT)
                        .show()
                    adapter.updateData(newAdminList)
                    adapter.notifyDataSetChanged()
                    binding.swipeRefreshLayout.isRefreshing = false

                }
            } else {
                runOnUiThread {
                    Toast.makeText(this, "没有找到管理员信息！", Toast.LENGTH_SHORT)
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