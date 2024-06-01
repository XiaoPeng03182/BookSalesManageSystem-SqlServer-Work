package com.example.booksalesmanagement.activity.dataBaseManage.adminMsgManage.deleteAdminMsg

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
import com.example.booksalesmanagement.bean.Administrator
import com.example.booksalesmanagement.dao.AdminDao
import com.example.booksalesmanagement.dao.UserDao
import kotlin.concurrent.thread

class DeleteAdminMsgAdapter(
    private val context: Context,
    private var adminList: ArrayList<Administrator>
) : RecyclerView.Adapter<DeleteAdminMsgAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val adminId: TextView = view.findViewById(R.id.tv_admin_id)
        val adminName: TextView = view.findViewById(R.id.tv_admin_name)
        val adminPassWord: TextView = view.findViewById(R.id.tv_admin_password)
        val adminPhone: TextView = view.findViewById(R.id.tv_admin_phoneNum)
        val adminCreatedTime: TextView = view.findViewById(R.id.tv_admin_createdTime)

        val btnDeleteAdmin :Button = view.findViewById(R.id.btn_deleteAdmin)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recycler_manage_delete_admin_msg_item, parent, false)
        // 创建 ViewHolder 实例，并将其与新创建的视图相关联
        val holder = ViewHolder(view)

        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val admin = adminList[position]
        holder.adminId.text = admin.adminId.toString()
        holder.adminName.text = admin.adminName
        holder.adminPassWord.text = admin.passWord
        holder.adminPhone.text = admin.phoneNumber
        holder.adminCreatedTime.text = admin.registrationDate.toString()

        holder.btnDeleteAdmin.setOnClickListener {
            val dialog = AlertDialog.Builder(context)
                .setTitle("删除管理员信息")
                .setPositiveButton("确定") { _, _ ->
                    thread {
                        // 处理删除按钮点击事件
                        if(AdminDao.deleteAdminById(holder.adminId.text.toString().toInt())) {
                            (context as Activity).runOnUiThread {
                                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show()
                                adminList.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, adminList.size)
                            }
                        } else {
                            (context as Activity).runOnUiThread {
                                Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show()
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
        return adminList.size
    }

    fun updateData(newData: ArrayList<Administrator>) {
        // 更新适配器中的数据源
        adminList.clear()
        //communityMsgList.addAll(newData)
        adminList = newData
    }
}