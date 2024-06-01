package com.example.booksalesmanagement.activity.dataBaseManage.bookMsgManage.deletebook

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.activity.dataBaseManage.bookMsgManage.updatebook.UpdateBookAdapter
import com.example.booksalesmanagement.bean.Book
import com.example.booksalesmanagement.dao.BookDao
import com.example.booksalesmanagement.databinding.ActivityDeleteBookBinding
import kotlin.concurrent.thread

class DeleteBookActivity : AppCompatActivity() {
    private var _binding: ActivityDeleteBookBinding? = null
    private val binding get() = _binding!!

    private var bookList = ArrayList<Book>()

    private var adapter: DeleteBookAdapter? =null

    @SuppressLint("NotifyDataSetChanged", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDeleteBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //初始化适配器
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = layoutManager
        adapter = DeleteBookAdapter(this, bookList)
        binding.recyclerView.adapter = adapter

        //从SqlServer数据库中获取全部图书数据
        if (bookList.size == 0) {
            initAllBookMsgList()
        }

        //查询按钮监听
        binding.btnSearchBookByName.setOnClickListener {
            //binding.SearchBookLineLayout.visibility = View.GONE
            val inputBookName = binding.etSearchBookByName.text.toString()
            thread {
                val newBookList = BookDao.queryBookByName(inputBookName)
                if(newBookList?.isNotEmpty() == true) {
                    runOnUiThread {
                        Toast.makeText(this, "查询成功！", Toast.LENGTH_SHORT)
                            .show()
                        adapter!!.updateData(newBookList)
                        adapter!!.notifyDataSetChanged()
                    }
                }else{
                    runOnUiThread {
                        Toast.makeText(this, "没有匹配和书籍！", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        binding.swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent)
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshBooks(adapter!!)
            //initBookMsgFromSqlServer()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshBooks(adapter: DeleteBookAdapter) {
        thread {
            val newBookList = BookDao.queryAllBookMsg()
            //随机打乱
            newBookList?.shuffle()
            runOnUiThread {
                if (newBookList != null) {
                    adapter.updateData(newBookList)
                }
                adapter.notifyDataSetChanged()
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initAllBookMsgList() {
        // 初始化购物车列表
        thread {
            val newBookList = BookDao.queryAllBookMsg()
            runOnUiThread {
                if (newBookList != null) {
                    adapter!!.updateData(newBookList)
                }
                adapter!!.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}