package com.example.booksalesmanagement.activity.dataBaseManage.cartAndOrderManage.deleteCart

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.activity.SaveUserMsg
import com.example.booksalesmanagement.bean.CartDetails
import com.example.booksalesmanagement.dao.BookDao
import com.example.booksalesmanagement.dao.CartDetailsDao
import com.example.booksalesmanagement.dao.OrderDetailsDao
import com.example.booksalesmanagement.fragment.shoppingCart.adapter.QueryCartDetailsAdapter
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread

class DeleteCartDetailsAdapter(
    private val context: Context,
    private var cartList: ArrayList<CartDetails>
) :
    RecyclerView.Adapter<DeleteCartDetailsAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cartPublisher: TextView = view.findViewById(R.id.tv_shoppingCart_publisher)
        val cartQuantity: TextView = view.findViewById(R.id.tv_shoppingCart_quantity)
        val cartBookName: TextView = view.findViewById(R.id.tv_shoppingCart_bookName)
        val cartAuthor: TextView = view.findViewById(R.id.tv_shoppingCart_author)
        val cartCreateTime: TextView = view.findViewById(R.id.tv_shoppingCart_createdTime)

        val cartDelete: Button = view.findViewById(R.id.btn_shoppingCart_delete)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recycler_manage_delete_cart_item, parent, false)
        // 创建 ViewHolder 实例，并将其与新创建的视图相关联
        val holder = ViewHolder(view)

        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartDetails = cartList[position]
        holder.cartPublisher.text = cartDetails.publisher
        holder.cartQuantity.text = cartDetails.quantity.toString()
        holder.cartBookName.text = cartDetails.bookName
        holder.cartAuthor.text = cartDetails.author

        // 定义输出格式
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss")

        // 格式化日期时间并输出
        val formattedDateTime: String = cartDetails.createdAt.format(outputFormatter)
        holder.cartCreateTime.text = formattedDateTime

        //取消预订
        holder.cartDelete.setOnClickListener {
            val dialog = AlertDialog.Builder(context)
                .setTitle("删除预购信息")
                .setPositiveButton("确定") { _, _ ->
                    val cardDetailId = cartDetails.cardDetailId
                    thread {
                        if (CartDetailsDao.deleteCartDetails(cardDetailId)) {
                            (context as Activity).runOnUiThread {
                                Toast.makeText(context, "取消预订成功！", Toast.LENGTH_SHORT).show()
                                cartList.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, cartList.size)
                            }

                        } else {
                            (context as Activity).runOnUiThread {
                                Toast.makeText(context, "取消预订失败！", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .setNegativeButton("取消", null)
                .create()
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    fun updateData(newData: ArrayList<CartDetails>) {
        // 更新适配器中的数据源
        cartList.clear()
        //communityMsgList.addAll(newData)
        cartList = newData
    }
}