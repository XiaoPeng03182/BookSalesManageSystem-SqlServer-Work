package com.example.booksalesmanagement.activity.dataBaseManage.bookMsgManage.updatebook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.activity.book_details.BookProductDetailsActivity
import com.example.booksalesmanagement.bean.Book
import com.example.booksalesmanagement.dao.BookDao
import com.example.booksalesmanagement.databinding.ActivityUpdateMsgBinding
import java.math.BigDecimal
import java.sql.SQLException
import kotlin.concurrent.thread

class UpdateMsgActivity : AppCompatActivity() {
    private var _binding: ActivityUpdateMsgBinding? = null
    private val binding get() = _binding!!

    // 获取传递过来的书籍对象
    private lateinit var bookObject: Book

    companion object {
        const val BOOK_OBJECT = "bookObject"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUpdateMsgBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookObject = intent.getSerializableExtra(BOOK_OBJECT) as Book

        //初始化原始书籍信息
        initBookMsg()

        binding.btnCommitUpdateBook.setOnClickListener {
            updateBookMsg()
        }

    }

    private fun updateBookMsg() {
        //从输入框中获取数据
        //val bookId = binding.etBookId.text.toString().toInt()
        val updates = mutableMapOf<String, Any>()

        // 检查每个输入框是否有输入，并将修改后的信息添加到待更新的集合中
        val bookName = binding.etBookName.text.toString()
        if (bookName.isNotEmpty()) {
            updates["bookname"] = bookName
        }

        val author = binding.etBookAuthor.text.toString()
        if (author.isNotEmpty()) {
            updates["author"] = author
        }

        val priceText = binding.etBookPrice.text.toString()
        if (priceText.isNotEmpty()) {
            val price = BigDecimal(priceText)
            updates["price"] = price
        }

        val publisher = binding.etPublisher.text.toString()
        if (publisher.isNotEmpty()) {
            updates["publisher"] = publisher
        }

        val publicationDateStr = binding.etPublisherTime.text.toString()
        if (publicationDateStr.isNotEmpty()) {
            val publicationDate = BookDao.parseDate(publicationDateStr)!!
            updates["publication_date"] = publicationDate
        }

        val stockText = binding.etInventory.text.toString()
        if (stockText.isNotEmpty()) {
            val stock = stockText.toInt()
            updates["stock"] = stock
        }

        val bookInfo = binding.etBookInfo.text.toString()
        if (bookInfo.isNotEmpty()) {
            updates["bookinfo"] = bookInfo
        }

        val isbn = binding.etIsbn.text.toString()
        if (isbn.isNotEmpty()) {
            updates["isbn"] = isbn
        }

        val category = binding.etBookCategory.text.toString()
        if (category.isNotEmpty()) {
            updates["category"] = category
        }

        thread {
            // 调用更新方法
            if (updates.isNotEmpty()) {
                try {
                    if (BookDao.updateBookMsg(this, bookObject.bookId, updates)) {
                        // 提示用户更新成功
                        runOnUiThread {
                            Toast.makeText(this, "更新成功！", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        // 提示用户更新失败
                        runOnUiThread {
                            Toast.makeText(this, "更新失败！", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: SQLException) {
                    e.printStackTrace()
                    // 提示用户更新失败
                    runOnUiThread {
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // 提示用户没有任何更新
                runOnUiThread {
                    Toast.makeText(this, "相比原来没有任何更新！", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun initBookMsg() {
        binding.etBookId.hint = bookObject.bookId.toString()
        binding.etBookName.hint = bookObject.bookName
        binding.etBookCategory.hint = bookObject.category
        binding.etBookAuthor.hint = bookObject.author
        binding.etBookPrice.hint = bookObject.price.toString()
        binding.etPublisher.hint = bookObject.publisher
        binding.etPublisherTime.hint = bookObject.publicationDate.toString()
        binding.etIsbn.hint = bookObject.isbn
        binding.etInventory.hint = bookObject.stock.toString()
        binding.etBookInfo.hint = bookObject.bookInfo
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}