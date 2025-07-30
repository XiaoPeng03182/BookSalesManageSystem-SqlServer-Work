package com.example.booksalesmanagement.activity.dataBaseManage.cartAndOrderManage.deleteOrder

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.bean.OrderDetails
import com.example.booksalesmanagement.dao.CartDetailsDao
import com.example.booksalesmanagement.dao.OrderDetailsDao
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread

class DeleteOrderDetailsAdapter(
    private val context: Context, private var orderList: ArrayList<OrderDetails>
) : RecyclerView.Adapter<DeleteOrderDetailsAdapter.ViewHolder>() {
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

        //删除订单按钮
        val orderDeleteOrder: Button = view.findViewById(R.id.btn_shoppingOrder_delete)

        //确认收货按钮
        val orderConfirmReceipt: Button = view.findViewById(R.id.btn_shoppingOrder_getGoods)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recycler_manage_delete_order_item, parent, false)
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

        // 定义输出格式
        val outputFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss")

        // 格式化日期时间并输出
        val formattedDateTime: String = order.orderDate.format(outputFormatter)
        holder.orderCreateTime.text = formattedDateTime

        //处理删除订单的点击事件
        holder.orderDeleteOrder.setOnClickListener {
            val dialog = AlertDialog.Builder(context)
                .setTitle("删除订单")
                .setPositiveButton("确定") { _, _ ->
                    thread {
                        if (OrderDetailsDao.deleteOrderDetails(order.orderDetailId)) {
                            (context as Activity).runOnUiThread {
                                Toast.makeText(context, "删除订单成功！", Toast.LENGTH_SHORT).show()
                                orderList.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, orderList.size)
                            }
                        } else {
                            (context as Activity).runOnUiThread {
                                Toast.makeText(context, "删除订单失败！", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .setNegativeButton("取消", null)
                .create()
            dialog.show()

        }

        //处理确认收货的点击事件
        holder.orderConfirmReceipt.setOnClickListener {
            val dialog = AlertDialog.Builder(context)
                .setTitle("确认收货")
                .setPositiveButton("确定") { _, _ ->
                    thread {
                        if (OrderDetailsDao.deleteOrderDetails(order.orderDetailId)) {
                            (context as Activity).runOnUiThread {
                                Toast.makeText(context, "确认收货成功！", Toast.LENGTH_SHORT).show()
                                orderList.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, orderList.size)
                            }
                        } else {
                            (context as Activity).runOnUiThread {
                                Toast.makeText(context, "确认收货失败！", Toast.LENGTH_SHORT).show()
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
        return orderList.size
    }

    fun updateData(newData: ArrayList<OrderDetails>) {
        // 更新适配器中的数据源
        orderList.clear()
        //communityMsgList.addAll(newData)
        orderList = newData
    }

}