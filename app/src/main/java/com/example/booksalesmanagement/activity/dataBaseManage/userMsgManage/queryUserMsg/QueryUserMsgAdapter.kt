package com.example.booksalesmanagement.activity.dataBaseManage.userMsgManage.queryUserMsg

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.bean.User

class QueryUserMsgAdapter(
    private val context: Context,
    private var userList: ArrayList<User>
) : RecyclerView.Adapter<QueryUserMsgAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userId :TextView = view.findViewById(R.id.tv_user_id)
        val userName :TextView = view.findViewById(R.id.tv_user_name)
        val userPassWord :TextView = view.findViewById(R.id.tv_user_password)
        val userPhoneNum :TextView = view.findViewById(R.id.tv_user_phoneNum)
        val userCreatedTime :TextView = view.findViewById(R.id.tv_user_createdTime)
        val userEmail:TextView = view.findViewById(R.id.tv_user_email)
        val userAddress:TextView = view.findViewById(R.id.tv_user_address)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recycler_manage_query_user_item, parent, false)
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