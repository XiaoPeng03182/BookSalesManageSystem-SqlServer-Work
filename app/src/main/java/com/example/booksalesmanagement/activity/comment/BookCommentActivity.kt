package com.example.booksalesmanagement.activity.comment

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.activity.SaveUserMsg
import com.example.booksalesmanagement.activity.book_details.BookProductDetailsActivity
import com.example.booksalesmanagement.bean.Reviews
import com.example.booksalesmanagement.dao.ReviewsDao
import com.example.booksalesmanagement.databinding.ActivityBookCommentBinding
import com.example.booksalesmanagement.fragment.home.adapter.BookCoverAdapter
import kotlin.concurrent.thread

class BookCommentActivity : AppCompatActivity() {
    private var bookId: Int = 1
    private var _binding: ActivityBookCommentBinding? = null
    private val binding get() = _binding!!

    private var reviewList = ArrayList<Reviews>()

    private var adapter: BookCommentAdapter? = null

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBookCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookId = intent.getIntExtra(BookProductDetailsActivity.BOOK_ID, 1)

        setSupportActionBar(binding.commentToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//显示Home按钮
        // binding.collapsingToolbarLayout.title = "书籍评论"

        //初始化适配器
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.commentRecyclerView.layoutManager = layoutManager
        adapter = BookCommentAdapter(this, reviewList)
        binding.commentRecyclerView.adapter = adapter

        //初始化评论列表
        initCommentList()

        binding.fabAddComment.setOnClickListener {
            // 添加评论
            addCommentByDialog()
        }

        binding.commentSwipeRefreshLayout.setColorSchemeColors(R.color.colorAccent)
        binding.commentSwipeRefreshLayout.setOnRefreshListener {
            refreshReviews()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshReviews() {
        thread {
            val newReviewList = ReviewsDao.queryReviewFromSQLServer(bookId)
            //随机打乱
            newReviewList?.shuffle()
            runOnUiThread {
                if (newReviewList != null) {
                    adapter!!.updateData(newReviewList)
                }
                adapter!!.notifyDataSetChanged()
                binding.commentSwipeRefreshLayout.isRefreshing = false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initCommentList() {
        thread {
            val newReviewList = ReviewsDao.queryReviewFromSQLServer(bookId)
            runOnUiThread {
                if (newReviewList != null) {
                    adapter!!.updateData(newReviewList)
                }
                adapter!!.notifyDataSetChanged()
            }
        }
    }

    private fun addCommentByDialog() {
        // 弹出对话框，输入评论内容
        // 创建对话框
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_comment, null)
        val etRating = dialogView.findViewById<EditText>(R.id.et_rating)
        val etContent = dialogView.findViewById<EditText>(R.id.et_content)

        val dialog = AlertDialog.Builder(this)
            .setTitle("添加评论")
            .setView(dialogView)
            .setPositiveButton("发布评论") { _, _ ->
                val inputRating = etRating.text.toString()
                val inputComment = etContent.text.toString()
                val rating = inputRating.toInt()

                if (inputComment.isEmpty() || inputRating.isEmpty()) {
                    Toast.makeText(this, "请输入完整的信息！", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (rating < 0 || rating > 5) {
                    Toast.makeText(this, "请输入0-5之间的评分！", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (inputComment.isEmpty()) {
                    // 处理提交的评论
                    Toast.makeText(this, "请输入评论！", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                //像数据库中添加评论
                val review = Reviews(
                    userId = SaveUserMsg.userId,
                    bookId = bookId,
                    rating = rating,
                    comment = inputComment
                )
                Log.e("TAG", "addCommentByDialog: ${review.bookId}")

                thread {
                    if (ReviewsDao.insertReviewToSQlServer(review)) {
                        runOnUiThread {
                            Toast.makeText(this, "评论成功！", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, "评论失败！\n注意只能评论一次！", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

            }
            .setNegativeButton("取消", null)
            .create()
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}