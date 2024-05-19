package com.example.booksalesmanagement.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.example.booksalesmanagement.databinding.ActivityBookProductDetailsBinding

class BookProductDetailsActivity : AppCompatActivity() {

    companion object {
        const val BOOK_NAME = "book_name"
        const val BOOK_IMAGE_ID = "book_image_id"
    }

    private var _binding: ActivityBookProductDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBookProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //获取界面跳转，传递过来的图片信息
        val bookName = intent.getStringExtra(BOOK_NAME) ?:""
        val bookImageId = intent.getIntExtra(BOOK_IMAGE_ID,0)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//显示Home按钮
        binding.collapsingToolbarLayout.title = bookName
        Glide.with(this).load(bookImageId).into(binding.bookImageView)
        binding.bookContentText.text = generateCoBookContent(bookName)
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

    private fun generateCoBookContent(fruitName: String) = fruitName.repeat(500)
}