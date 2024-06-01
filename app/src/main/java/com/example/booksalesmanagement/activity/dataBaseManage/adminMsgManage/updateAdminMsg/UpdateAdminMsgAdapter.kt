package com.example.booksalesmanagement.activity.dataBaseManage.adminMsgManage.updateAdminMsg

import android.annotation.SuppressLint
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
import com.example.booksalesmanagement.bean.Administrator
import com.example.booksalesmanagement.dao.AdminDao
import com.example.booksalesmanagement.dao.BookDao
import com.example.booksalesmanagement.dao.OrderDetailsDao
import java.sql.SQLException
import kotlin.concurrent.thread

class UpdateAdminMsgAdapter(
    private val context: Context,
    private var adminList: ArrayList<Administrator>
) : RecyclerView.Adapter<UpdateAdminMsgAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val adminId: TextView = view.findViewById(R.id.tv_admin_id)
        val adminName: TextView = view.findViewById(R.id.tv_admin_name)
        val adminPassWord: TextView = view.findViewById(R.id.tv_admin_password)
        val adminPhone: TextView = view.findViewById(R.id.tv_admin_phoneNum)
        val adminCreatedTime: TextView = view.findViewById(R.id.tv_admin_createdTime)

        val btnDeleteAdmin: Button = view.findViewById(R.id.btn_deleteAdmin)
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

    @SuppressLint("MissingInflatedId")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val admin = adminList[position]
        holder.adminId.text = admin.adminId.toString()
        holder.adminName.text = admin.adminName
        holder.adminPassWord.text = admin.passWord
        holder.adminPhone.text = admin.phoneNumber
        holder.adminCreatedTime.text = admin.registrationDate.toString()

        holder.btnDeleteAdmin.text = "修改\n管理员"
        holder.btnDeleteAdmin.setOnClickListener {
            // 创建对话框
            val dialogView =
                LayoutInflater.from(context).inflate(R.layout.dialog_update_admin_msg, null)

            val etAdminName = dialogView.findViewById<EditText>(R.id.et_new_admin_name)
            val etAdminPassWord = dialogView.findViewById<EditText>(R.id.et_new_admin_password)
            val etAdminPhoneNum = dialogView.findViewById<EditText>(R.id.et_new_admin_phoneNum)

            // 确认 EditText 控件非空
            if (etAdminName == null || etAdminPassWord == null || etAdminPhoneNum == null) {
                Toast.makeText(context, "对话框布局加载失败", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dialog = AlertDialog.Builder(context)
                .setTitle("修改管理员信息")
                .setView(dialogView)
                .setPositiveButton("确定") { _, _ ->
                    val inputAdminName = etAdminName.text.toString()
                    val inputAdminPassWord = etAdminPassWord.text.toString()
                    val inputAdminPhoneNum = etAdminPhoneNum.text.toString()

                    //保存更新的数据项
                    val updates = mutableMapOf<String, Any>()
                    if (inputAdminName.isNotEmpty()) {
                        updates["adminName"] = inputAdminName
                    }
                    if (inputAdminPassWord.isNotEmpty()) {
                        updates["passWord"] = inputAdminPassWord
                    }
                    if (inputAdminPhoneNum.isNotEmpty()) {
                        updates["phoneNumber"] = inputAdminPhoneNum
                    }

                    thread {
                        // 调用更新方法
                        if (updates.isNotEmpty()) {
                            try {
                                if (AdminDao.updateAdminMsg(
                                        context,
                                        holder.adminId.text.toString().toInt(),
                                        updates
                                    )
                                ) {
                                    // 提示用户更新成功
                                    (context as Activity).runOnUiThread {
                                        Toast.makeText(context, "更新成功！", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                } else {
                                    // 提示用户更新失败
                                    (context as Activity).runOnUiThread {
                                        Toast.makeText(context, "更新失败！", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                            } catch (e: SQLException) {
                                e.printStackTrace()
                                // 提示用户更新失败
                                (context as Activity).runOnUiThread {
                                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            // 提示用户没有任何更新
                            (context as Activity).runOnUiThread {
                                Toast.makeText(context, "相比原来没有任何更新！", Toast.LENGTH_SHORT)
                                    .show()
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