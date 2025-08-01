package com.example.booksalesmanagement.activity.dataBaseManage.cartAndOrderManage.queryOrder

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.bean.OrderDetails
import com.example.booksalesmanagement.dao.OrderDetailsDao
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread

class QueryOrderDetailsAdapter(
    private val context: Context, private var orderList: ArrayList<OrderDetails>
) : RecyclerView.Adapter<QueryOrderDetailsAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderPublisher: TextView = view.findViewById(R.id.tv_shoppingOrder_publisher)
        val orderQuantity: TextView = view.findViewById(R.id.tv_shoppingOrder_quantity)
        val orderBookName: TextView = view.findViewById(R.id.tv_shoppingOrder_bookName)
        val orderAuthor: TextView = view.findViewById(R.id.tv_shoppingOrder_author)
        val orderCreateTime: TextView = view.findViewById(R.id.tv_shoppingOrder_createdTime)
        val orderPrice: TextView = view.findViewById(R.id.tv_shoppingOrder_price)
        val orderTotalAmount: TextView = view.findViewById(R.id.tv_shoppingOrder_totalAmount)
        val orderAddress: TextView = view.findViewById(R.id.tv_shoppingOrder_address)
        val orderPhoneNumber: TextView = view.findViewById(R.id.tv_shoppingOrder_phoneNumber)
        val orderUserName :TextView = view.findViewById(R.id.tv_shoppingOrder_userName)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recycler_manage_query_order_item, parent, false)
        // 创建 ViewHolder 实例，并将其与新创建的视图相关联
        val holder = ViewHolder(view)

        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
        }
        return holder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orderList[position]
        holder.orderPublisher.text = order.publisher
        holder.orderBookName.text = order.bookName
        holder.orderQuantity.text = order.quantity.toString()
        holder.orderAuthor.text = order.author
        holder.orderPrice.text = order.price.toString()
        holder.orderTotalAmount.text = order.totalAmount.toString()
        holder.orderAddress.text = order.address
        holder.orderPhoneNumber.text = order.phoneNumber
        holder.orderUserName.text = order.userName

        // 定义输出格式
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss")

        // 格式化日期时间并输出
        val formattedDateTime: String = order.orderDate.format(outputFormatter)
        holder.orderCreateTime.text = formattedDateTime

    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    fun updateData(newData: ArrayList<OrderDetails>) {
        // 更新适配器中的数据源
        orderList.clear()
        //communityMsgList.addAll(newData)
        orderList = newData
    }
}