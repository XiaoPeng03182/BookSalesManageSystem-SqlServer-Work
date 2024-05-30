package com.example.booksalesmanagement.activity.comment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.bean.Reviews

class BookCommentAdapter(private val context: Context, private var reviewList: ArrayList<Reviews>) :
    RecyclerView.Adapter<BookCommentAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val reviewUserName: TextView = view.findViewById(R.id.tv_comment_username)
        val reviewRating: TextView =  view.findViewById(R.id.tv_comment_rating)
        val reviewContent: TextView =  view.findViewById(R.id.tv_comment_content)
        val reviewTime: TextView =  view.findViewById(R.id.tv_comment_datetime)
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_book_reviews_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookCommentAdapter.ViewHolder, position: Int) {
        val review = reviewList[position]
        holder.reviewUserName.text = review.userName
        holder.reviewRating.text = review.rating.toString()
        holder.reviewContent.text = review.comment
        holder.reviewTime.text = review.createdAt.toString()
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    fun updateData(newData: ArrayList<Reviews>) {
        // 更新适配器中的数据源
        reviewList.clear()
        //communityMsgList.addAll(newData)
        reviewList = newData
    }
}