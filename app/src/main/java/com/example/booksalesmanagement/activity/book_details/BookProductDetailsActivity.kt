package com.example.booksalesmanagement.activity.book_details

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.activity.SaveUserMsg
import com.example.booksalesmanagement.activity.comment.BookCommentActivity
import com.example.booksalesmanagement.bean.Book
import com.example.booksalesmanagement.dao.CartDetailsDao
import com.example.booksalesmanagement.dao.OrderDetailsDao
import com.example.booksalesmanagement.databinding.ActivityBookProductDetailsBinding
import com.example.booksalesmanagement.fragment.home.adapter.BookListImageBitmap
import kotlin.concurrent.thread

class BookProductDetailsActivity : AppCompatActivity() {

    companion object {
        const val BOOK_ID = "bookId"
        const val BOOK_OBJECT = "bookObject"
        const val BOOK_NAME = "bookName"
        const val BOOK_IMAGE_BitMAP_KEY_NAME = "bookImageBitmapKeyName"
    }

    private var _binding: ActivityBookProductDetailsBinding? = null
    private val binding get() = _binding!!

    private var bookId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBookProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //获取界面跳转，传递过来的图片信息
        bookId = intent.getIntExtra(BOOK_ID,0)
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

        binding.flbComment.setOnClickListener {
            val intent = Intent(this, BookCommentActivity::class.java)
            intent.putExtra(BOOK_ID, bookId)
            startActivity(intent)
        }

        //加入购物车
        binding.btnAddToCart.setOnClickListener{
            //弹出选择数量的对话框,并添加到数据库中
            addBookIntoCartByDialog()
        }
        //立即购买，加入到订单列表
        binding.btnBuyNow.setOnClickListener{
            //弹出对话框，选择购买图书的数量，并添加到订单中
            purchaseBookIntoOrder()
        }
    }

    @SuppressLint("MissingInflatedId")
    fun purchaseBookIntoOrder() {
        // 创建对话框
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_set_purchase_msg, null)

        val etPurchaseNum = dialogView.findViewById<EditText>(R.id.et_purchase_num)
        val etPurchasePhoneNum = dialogView.findViewById<EditText>(R.id.et_purchase_phoneNum)
        etPurchasePhoneNum.setText(SaveUserMsg.phoneNumber)
        val etPurchaseAddress = dialogView.findViewById<EditText>(R.id.et_purchase_address)
        etPurchaseAddress.setText(SaveUserMsg.address)

        val dialog = AlertDialog.Builder(this)
            .setTitle("完善订购信息")
            .setView(dialogView)
            .setPositiveButton("确定") { _, _ ->
                val inputPurchaseNum = etPurchaseNum.text.toString()
                val quantities = inputPurchaseNum.toInt()

                if (inputPurchaseNum.isEmpty()) {
                    // 处理提交的信息
                    Toast.makeText(this, "请输入预订数量！", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (etPurchasePhoneNum.text.isEmpty()) {
                    // 处理提交的信息
                    Toast.makeText(this, "请输入联系电话！", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (etPurchaseAddress.text.isEmpty()) {
                    // 处理提交的信息
                    Toast.makeText(this, "请输入收获地址！", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                //向订单详细表中添加订单信息
                thread {
                    if (OrderDetailsDao.insertOrdersToSQLServer(this,SaveUserMsg.userId,bookId,quantities)){
                        runOnUiThread {
                            Toast.makeText(this, "购买成功！", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("取消", null)
            .create()
        dialog.show()
    }

    @SuppressLint("MissingInflatedId")
    private fun addBookIntoCartByDialog() {
        // 创建对话框
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_select_booking_nums, null)
        val etBookingNum = dialogView.findViewById<EditText>(R.id.et_bookingNums)

        val dialog = AlertDialog.Builder(this)
            .setTitle("选择预订数量")
            .setView(dialogView)
            .setPositiveButton("确定") { _, _ ->
                val inputBookingNum = etBookingNum.text.toString()
                val bookingNum = inputBookingNum.toInt()

                if (inputBookingNum.isEmpty()) {
                    // 处理提交的评论
                    Toast.makeText(this, "请输入预订数量！", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                //向购物车Cart中添加预订信息
                thread {
                    if (CartDetailsDao.insertPreOrderToSQLServer(this,SaveUserMsg.userId,bookId,bookingNum)){
                        runOnUiThread {
                            Toast.makeText(this, "预购成功！", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("取消", null)
            .create()
        dialog.show()
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