package com.example.booksalesmanagement.activity.dataBaseManage.bookMsgManage.updatebook

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.bean.Book

class UpdateBookAdapter(private val context: Context, private var bookList: ArrayList<Book>) :
    RecyclerView.Adapter<UpdateBookAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookId: TextView = view.findViewById(R.id.tv_bookMsg_bookId)
        val bookStock: TextView = view.findViewById(R.id.tv_bookMsg_bookStock)
        val bookName: TextView = view.findViewById(R.id.tv_bookMsg_bookName)
        val bookAuthor: TextView = view.findViewById(R.id.tv_bookMsg_author)
        val bookPublisher: TextView = view.findViewById(R.id.tv_bookMsg_publisher)
        val bookPrice: TextView = view.findViewById(R.id.tv_bookMsg_price)

        val btnUpdate: TextView = view.findViewById(R.id.btn_updateBookMsg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.recycler_update_book_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = bookList[position]
        holder.bookId.text = book.bookId.toString()
        holder.bookStock.text = book.stock.toString()
        holder.bookName.text = book.bookName
        holder.bookAuthor.text = book.author
        holder.bookPublisher.text = book.publisher
        holder.bookPrice.text = book.price.toString()

        //跳转到修改图书信息界面
        holder.btnUpdate.setOnClickListener {
            val intent = Intent(context, UpdateMsgActivity::class.java).apply {
                putExtra(UpdateMsgActivity.BOOK_OBJECT, book)
            }
            context.startActivity(intent)
        }
    }

    fun updateData(newData: ArrayList<Book>) {
        // 更新适配器中的数据源
        bookList.clear()
        //communityMsgList.addAll(newData)
        bookList = newData
    }

}