package com.example.booksalesmanagement.activity.dataBaseManage.bookMsgManage.searchbook

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.bean.Book
import com.example.booksalesmanagement.dao.BookDao
import com.example.booksalesmanagement.databinding.ActivitySearchBookMsgBinding
import com.example.booksalesmanagement.fragment.home.HomeFragment
import com.example.booksalesmanagement.fragment.home.adapter.BookCoverAdapter
import com.example.booksalesmanagement.fragment.home.adapter.BookListImageBitmap
import com.example.booksalesmanagement.utils.ConnectAlibabaOssToImage
import com.google.android.material.snackbar.Snackbar
import java.lang.StringBuilder
import kotlin.concurrent.thread

class SearchBookMsgActivity : AppCompatActivity() {
    private var _binding: ActivitySearchBookMsgBinding? = null
    private val binding get() = _binding!!

    private var bookList = ArrayList<Book>()

    private var adapter: BookCoverAdapter? = null

    @SuppressLint("ResourceAsColor", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchBookMsgBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = layoutManager
        adapter = BookCoverAdapter(this, bookList)
        binding.recyclerView.adapter = adapter

        //从SqlServer数据库中获取数据，并获取阿里云OSS云存储中的图片
        if (bookList.size == 0) {
            initBookMsgFromSqlServer()
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

    //从SqlServer数据库中获取数据，并获取阿里云OSS云存储中的图片
    private fun initBookMsgFromSqlServer() {
        getBookAllMsg(object : BookQueryCallback {
            override fun onSuccess(bookList: ArrayList<Book>) {
                this@SearchBookMsgActivity.bookList = bookList

                // 随机打乱 bookList
                this@SearchBookMsgActivity.bookList.shuffle()

                Log.e("SearchBookMsgActivity", "bookList.size before for : =${bookList.size}")

                // 用于跟踪已下载图片的计数器
                val totalBooks = this@SearchBookMsgActivity.bookList.size
                var downloadedImages = 0

                for (book in this@SearchBookMsgActivity.bookList) {
                    val bookImageFileName = "${book.bookId}_${book.bookName}.jpg"
                    ConnectAlibabaOssToImage.getImage(
                        context = this@SearchBookMsgActivity,
                        bookImageFileName,
                        object : ConnectAlibabaOssToImage.ImageCallback {
                            @SuppressLint("NotifyDataSetChanged")
                            override fun onImageLoaded(bitmap: Bitmap?) {
                                BookListImageBitmap.addBookImage(bookImageFileName, bitmap!!)
                                Log.e(
                                    "SearchBookMsgActivity", "BookListImageBitmap size" +
                                            "${BookListImageBitmap.getBookListImageBitmap().size}"
                                )
                                downloadedImages++
                                checkAllImagesDownloaded(totalBooks, downloadedImages)

                            }

                            override fun onError() {
                                TODO("Not yet implemented")
                            }

                        })
                }

                Log.e("SearchBookMsgActivity", "bookList.size after for : =${bookList.size}")

            }

            override fun onFailure(exception: Exception) {
                exception.printStackTrace()
                Toast.makeText(this@SearchBookMsgActivity, "更新图书数据失败！", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    // 检查是否所有图片都已下载完成
    private fun checkAllImagesDownloaded(totalBooks: Int, downloadedImages: Int) {
        if (downloadedImages == totalBooks) {
            //requireActivity().runOnUiThread {
            updateBookList(this@SearchBookMsgActivity.bookList)
            Log.e("SearchBookMsgActivity", "onImageLoaded: 所有图片加载完成")
            //}
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateBookList(newBookList: ArrayList<Book>) {
        runOnUiThread {
            Log.e("SearchBookMsgActivity", "updateBookList: ${newBookList.size}")
            //bookList.clear()
            for (book in bookList) {
                Log.e("SearchBookMsgActivity update before", book.toString())
            }

            Toast.makeText(this, "获取图书数据成功！", Toast.LENGTH_SHORT).show()
            //bookList.addAll(newBookList)
            //bookList = newBookList

            for (book in bookList) {
                Log.e("SearchBookMsgActivity update after", book.toString())
            }

            if (adapter == null) {
                Log.e("SearchBookMsgActivity", "adapter is null")
            } else {
                Log.e("SearchBookMsgActivity", "updateBookList: notifyDataSetChanged")
                adapter!!.updateData(bookList)
                adapter!!.notifyDataSetChanged()
            }
            // adapter?.notifyItemChanged(lastMsgIndex)

            //adapter?.notifyDataSetChanged()
            Log.e("SearchBookMsgActivity", "updateBookList02: ${bookList.size}")
        }
    }

    private fun getBookAllMsg(callback: BookQueryCallback) {
        // 调用SQL Server数据库的代码
        val sb = StringBuilder()
        var bookList = ArrayList<Book>()
        thread {
            try {
                bookList.addAll(BookDao.queryAll())
                //获取数据库数据后执行->成功回调
                callback.onSuccess(bookList)
            } catch (e: Exception) {
                e.printStackTrace()
                callback.onFailure(e)
            }

            for (book in bookList) {
                sb.append("图书编号：" + book.bookId)
                sb.append("图书名称：" + book.bookName)
                sb.append("类别：" + book.category)
                sb.append("出版社：" + book.publisher)
                sb.append("作者：" + book.author)
                sb.append("价格：" + book.price)
                sb.append("库存：" + book.stock)
                sb.append("出版日期：" + book.publicationDate)
                sb.append("图书信息：" + book.bookInfo)
                sb.append("图书ISBN：" + book.isbn)
                sb.append("创建时间：" + book.creationTime)
                sb.append("\n")
            }

            //binding.tvShowMsg.text = Cno
            Log.e("TAG", "getMsgFromSqlServer: $sb")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshBooks(adapter: BookCoverAdapter) {
        thread {
            Thread.sleep(500)
            runOnUiThread { //进入主线程修改视图；
                //initBooks()
                initBookMsgFromSqlServer()
                Log.e("SearchBookMsgActivity", "refreshBooks")
                adapter.notifyDataSetChanged()
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    interface BookQueryCallback {
        fun onSuccess(bookList: ArrayList<Book>)
        fun onFailure(exception: Exception)
    }
}