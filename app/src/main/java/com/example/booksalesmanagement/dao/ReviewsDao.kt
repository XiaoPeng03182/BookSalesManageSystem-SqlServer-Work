package com.example.booksalesmanagement.dao

import com.example.booksalesmanagement.activity.SaveUserMsg
import com.example.booksalesmanagement.bean.Reviews
import com.example.booksalesmanagement.bean.User
import com.example.booksalesmanagement.database.ConnectionSqlServer
import java.sql.SQLException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ReviewsDao {

    /**
     * @param review 评论实体类
     * @return
     * @throws SQLException
     * @Description: 插入评论到数据库
     * @Author: xiaopeng
     * @Date: 2024/5/26
     */
    fun insertReviewToSQlServer(review: Reviews): Boolean {
        val insertReviewSQL = "insert into Reviews(bookId,userId,rating,comment)" +
                "values(?,?,?,?)"

        try {
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            conn?.prepareStatement(insertReviewSQL).use { stmt ->
                stmt?.setInt(1, review.bookId)
                stmt?.setInt(2, review.userId)
                stmt?.setInt(3, review.rating)
                stmt?.setString(4, review.comment)

                val affectedRows = stmt?.executeUpdate() // 执行插入操作并获取受影响的行数
                return affectedRows?.let { it > 0 }
                    ?: false // 如果受影响的行数大于 0，则表示插入成功，返回 true，否则返回 false
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false // 出现异常时返回 false
    }

    fun queryReviewFromSQLServer(bookId: Int): ArrayList<Reviews>? {
        val queryReviewSQL = "select u.userId as userId,b.book_id as bookId,\n" +
                "u.userName as userName,r.rating as rating,\n" +
                "r.createdAt as createdAt, r.comment as comment\n" +
                "from Reviews as r,Users as u,Book as b\n" +
                "where r.userId = u.userId and r.bookId = b.book_id " +
                "and r.bookId = ?"
        //利用视图简化操作
        val queryReviewsByViewSQL = "select * from dbo.View_QueryReviews where book_id = ?"

        try {
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            conn?.prepareStatement(queryReviewsByViewSQL).use { stmt ->
                stmt?.setInt(1, bookId)
                val rs = stmt?.executeQuery() // 使用 executeQuery 方法来执行查询操作


                val reviewList = ArrayList<Reviews>()

                while (rs?.next() == true) { // 表示有结果
                    val dateString = rs.getString("createdAt")
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val truncatedDateString = dateString.substring(0, 19)
                    val createdAtTime =
                        LocalDateTime.parse(truncatedDateString, formatter)

                    val review = Reviews(
                        userName = rs.getString("userName"),
                        rating = rs.getInt("rating"),
                        createdAt = createdAtTime,
                        comment = rs.getString("comment"),
                        bookId = rs.getInt("book_Id"),
                        userId = rs.getInt("userId")
                    )
                    reviewList.add(review)
                }
                if (reviewList.size != 0) {
                    return reviewList
                } else {
                    return null
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null // 出现异常时返回 true
    }

}