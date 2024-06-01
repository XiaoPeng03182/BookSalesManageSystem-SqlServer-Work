package com.example.booksalesmanagement.activity.dataBaseManage.adminMsgManage.queryAdminMsg

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.bean.Administrator


class QueryAdminMsgAdapter(
    private val context: Context,
    private var adminList: ArrayList<Administrator>
) : RecyclerView.Adapter<QueryAdminMsgAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val adminId : TextView = view.findViewById(R.id.tv_admin_id)
        val adminName : TextView = view.findViewById(R.id.tv_admin_name)
        val adminPassWord :TextView = view.findViewById(R.id.tv_admin_password)
        val adminPhone : TextView = view.findViewById(R.id.tv_admin_phoneNum)
        val adminCreatedTime :TextView = view.findViewById(R.id.tv_admin_createdTime)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QueryAdminMsgAdapter.ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.recycler_manage_query_admin_msg_item, parent, false)
        // 创建 ViewHolder 实例，并将其与新创建的视图相关联
        val holder = ViewHolder(view)

        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
        }
        return holder
    }

    override fun onBindViewHolder(holder: QueryAdminMsgAdapter.ViewHolder, position: Int) {
        val admin = adminList[position]
        holder.adminId.text = admin.adminId.toString()
        holder.adminName.text = admin.adminName
        holder.adminPassWord.text = admin.passWord
        holder.adminPhone.text = admin.phoneNumber
        holder.adminCreatedTime.text = admin.registrationDate.toString()
    }

    override fun getItemCount(): Int {
       return  adminList.size
    }

    fun updateData(newData: ArrayList<Administrator>) {
        // 更新适配器中的数据源
        adminList.clear()
        //communityMsgList.addAll(newData)
        adminList = newData
    }
}