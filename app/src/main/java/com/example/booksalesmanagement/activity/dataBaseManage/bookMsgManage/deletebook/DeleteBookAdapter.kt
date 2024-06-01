package com.example.booksalesmanagement.activity.dataBaseManage.bookMsgManage.deletebook

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.activity.SaveUserMsg
import com.example.booksalesmanagement.activity.dataBaseManage.bookMsgManage.updatebook.UpdateBookAdapter
import com.example.booksalesmanagement.activity.dataBaseManage.bookMsgManage.updatebook.UpdateMsgActivity
import com.example.booksalesmanagement.bean.Book
import com.example.booksalesmanagement.dao.BookDao
import com.example.booksalesmanagement.dao.OrderDetailsDao
import kotlin.concurrent.thread

class DeleteBookAdapter(private val context: Context, private var bookList: ArrayList<Book>) :
    RecyclerView.Adapter<DeleteBookAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookId: TextView = view.findViewById(R.id.tv_bookMsg_bookId)
        val bookStock: TextView = view.findViewById(R.id.tv_bookMsg_bookStock)
        val bookName: TextView = view.findViewById(R.id.tv_bookMsg_bookName)
        val bookAuthor: TextView = view.findViewById(R.id.tv_bookMsg_author)
        val bookPublisher: TextView = view.findViewById(R.id.tv_bookMsg_publisher)
        val bookPrice: TextView = view.findViewById(R.id.tv_bookMsg_price)

        val btnDelete: TextView = view.findViewById(R.id.btn_updateBookMsg)
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

        holder.btnDelete.text = "删除"
        //删除图书信息
        holder.btnDelete.setOnClickListener {
            val dialog = AlertDialog.Builder(context)
                .setTitle("删除图书")
                .setPositiveButton("确定") { _, _ ->
                    thread {
                        if (BookDao.deleteBookById(book.bookId)) {
                            (context as Activity).runOnUiThread {
                                Toast.makeText(context, "删除成功！", Toast.LENGTH_SHORT).show()
                                bookList.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, bookList.size)
                            }
                        } else {
                            (context as Activity).runOnUiThread {
                                Toast.makeText(context, "删除失败！", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .setNegativeButton("取消", null)
                .create()
            dialog.show()
        }
    }

    fun updateData(newData: ArrayList<Book>) {
        // 更新适配器中的数据源
        bookList.clear()
        //communityMsgList.addAll(newData)
        bookList = newData
    }

}