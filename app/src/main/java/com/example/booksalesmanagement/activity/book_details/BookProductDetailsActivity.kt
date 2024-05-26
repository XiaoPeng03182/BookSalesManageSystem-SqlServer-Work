package com.example.booksalesmanagement.activity.book_details

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.example.booksalesmanagement.bean.Book
import com.example.booksalesmanagement.databinding.ActivityBookProductDetailsBinding
import com.example.booksalesmanagement.fragment.home.adapter.BookListImageBitmap

class BookProductDetailsActivity : AppCompatActivity() {

    companion object {
        const val BOOK_OBJECT = "bookObject"
        const val BOOK_NAME = "bookName"
        const val BOOK_IMAGE_BitMAP_KEY_NAME = "bookImageBitmapKeyName"
    }

    private var _binding: ActivityBookProductDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBookProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //获取界面跳转，传递过来的图片信息
        val bookName = intent.getStringExtra(BOOK_NAME) ?:""
        val bookImageBitmapKeyName = intent.getStringExtra(BOOK_IMAGE_BitMAP_KEY_NAME)
        val bookObject = intent.getSerializableExtra(BOOK_OBJECT) as Book

        //获取图书详细数据
        initBookDetails(bookObject)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//显示Home按钮
        binding.collapsingToolbarLayout.title = bookName
        val bookImageBitmap = bookImageBitmapKeyName?.let { BookListImageBitmap.getBookImage(it) }
        Glide.with(this).load(bookImageBitmap).into(binding.bookImageView)

    }

    @SuppressLint("SetTextI18n")
    private fun initBookDetails(bookObject: Book) {
        binding.tvBookAuthor.text = bookObject.author
        binding.tvBookStock.text = bookObject.stock.toString()
        binding.tvBookPrice.text = bookObject.price.toString()
        binding.tvBookInfo.text = bookObject.bookInfo
        binding.tvBookCategory.text = bookObject.category
        binding.tvBookPublicationDate.text = bookObject.publicationDate.toString()
        binding.tvBookPublisher.text = bookObject.publisher
        binding.tvBookISBN.text = bookObject.isbn

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}