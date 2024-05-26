package com.example.booksalesmanagement.fragment.home.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.activity.book_details.BookProductDetailsActivity
import com.example.booksalesmanagement.bean.Book

class BookCoverAdapter(private val context: Context, private var bookList: ArrayList<Book>) : RecyclerView.Adapter<BookCoverAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookImage: ImageView = view.findViewById(R.id.bookImage)
        val bookName: TextView = view.findViewById(R.id.bookName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.book_cover_item,parent,false)
        /*使用 LayoutInflater 从 XML 布局文件中实例化一个新的视图。
        R.layout.fruit_item 是要实例化的布局资源文件，它定义了每个 RecyclerView 项的布局。
        parent 参数是 RecyclerView 的父容器，用于正确配置视图。
        false 参数表示在加载布局资源时不将其附加到父容器中。*/

        // 创建 ViewHolder 实例，并将其与新创建的视图相关联
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            val book = bookList[position]
            val bookImageBitmapKeyName = "${book.bookId}_${book.bookName}.jpg"
            val intent = Intent(context, BookProductDetailsActivity::class.java).apply {
                putExtra(BookProductDetailsActivity.BOOK_OBJECT,book)
                putExtra(BookProductDetailsActivity.BOOK_NAME,book.bookName)
                putExtra(BookProductDetailsActivity.BOOK_IMAGE_BitMAP_KEY_NAME,bookImageBitmapKeyName)
            }
            context.startActivity(intent)
            // 设置点击事件，当用户点击 RecyclerView 项时，启动 FruitActivity 并传递相关数据
        }
        return holder
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book  = bookList[position]
        holder.bookName.text = book.bookName
        val bookImageBitmapKeyName = "${book.bookId}_${book.bookName}.jpg"
        val bookImageBitmap = BookListImageBitmap.getBookImage(bookImageBitmapKeyName)
        Glide.with(context).load(bookImageBitmap).into(holder.bookImage)
        //holder.bookImage.setImageBitmap(bookImageBitmap)
        //Log.e("BookAdapter",bookImageBitmapKeyName)
    }

    fun updateData(newData: ArrayList<Book>) {
        // 更新适配器中的数据源
        bookList.clear()
        //communityMsgList.addAll(newData)
        bookList = newData
    }
}