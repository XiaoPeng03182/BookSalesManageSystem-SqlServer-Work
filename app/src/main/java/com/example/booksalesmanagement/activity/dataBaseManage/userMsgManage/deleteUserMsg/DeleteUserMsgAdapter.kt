package com.example.booksalesmanagement.activity.dataBaseManage.userMsgManage.deleteUserMsg

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.bean.User
import com.example.booksalesmanagement.dao.UserDao
import kotlin.concurrent.thread

class DeleteUserMsgAdapter(
    private val context: Context,
    private var userList: ArrayList<User>
) : RecyclerView.Adapter<DeleteUserMsgAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userId: TextView = view.findViewById(R.id.tv_user_id)
        val userName: TextView = view.findViewById(R.id.tv_user_name)
        val userPassWord: TextView = view.findViewById(R.id.tv_user_password)
        val userPhoneNum: TextView = view.findViewById(R.id.tv_user_phoneNum)
        val userCreatedTime: TextView = view.findViewById(R.id.tv_user_createdTime)
        val userEmail: TextView = view.findViewById(R.id.tv_user_email)
        val userAddress: TextView = view.findViewById(R.id.tv_user_address)

        val btnDeleteUser: TextView = view.findViewById(R.id.btn_delete_user)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recycler_manage_delete_user_item, parent, false)
        // 创建 ViewHolder 实例，并将其与新创建的视图相关联
        val holder = ViewHolder(view)

        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.userId.text = user.userId.toString()
        holder.userName.text = user.userName
        holder.userPassWord.text = user.password
        holder.userPhoneNum.text = user.phoneNumber
        holder.userEmail.text = user.email
        holder.userAddress.text = user.address

        holder.userCreatedTime.text = user.registrationDate.toString()

        holder.btnDeleteUser.setOnClickListener {
            val dialog = AlertDialog.Builder(context)
                .setTitle("删除用户信息")
                .setPositiveButton("确定") { _, _ ->
                    thread {
                        // 处理删除按钮点击事件
                        if(UserDao.deleteUserById(holder.userId.text.toString().toInt())) {
                            (context as Activity).runOnUiThread {
                                Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show()
                                userList.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, userList.size)
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
        return userList.size
    }

    fun updateData(newData: ArrayList<User>) {
        // 更新适配器中的数据源
        userList.clear()
        //communityMsgList.addAll(newData)
        userList = newData
    }
}