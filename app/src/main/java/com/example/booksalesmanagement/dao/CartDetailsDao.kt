package com.example.booksalesmanagement.dao

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.booksalesmanagement.bean.CartDetails
import com.example.booksalesmanagement.database.ConnectionSqlServer
import java.sql.PreparedStatement
import java.sql.SQLException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object CartDetailsDao {

    //添加预购图书到购物车中
    fun insertPreOrderToSQLServer(
        context: Context,
        userId: Int,
        bookId: Int,
        quantity: Int
    ): Boolean {
        // 1. 检查是否已经存在用户的购物车
        val checkCartSql = "SELECT TOP 1 cartId FROM Cart WHERE userId = ? ORDER BY createdAt DESC"
        // 2. 若该用户不存在购物车，则创建新的购物车
        val createCartSql = "INSERT INTO Cart (userId) VALUES (?)"
        // 3. 将图书添加到购物车详情表
        val addBookSql =
            "INSERT INTO CartDetails (cartId, bookId, quantity) VALUES (?, ?, ?)"

        try {
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            conn?.prepareStatement(checkCartSql).use { stmt ->
                stmt?.setInt(1, userId)
                val result = stmt?.executeQuery()
                //保存购物车id
                var cartId = 1
                if (result?.next() == true) {  //用户已经有购物车
                    cartId = result.getInt("cartId");
                } else { // 用户没有购物车，创建新的购物车
                    conn?.prepareStatement(createCartSql, PreparedStatement.RETURN_GENERATED_KEYS)
                        .use { stmt ->
                            stmt?.setInt(1, userId)
                            stmt?.executeUpdate()
                            // 获取新创建的购物车ID,cartId
                            val rs = stmt?.generatedKeys;
                            if (rs?.next() == true) {
                                cartId = rs.getInt(1);
                            } else {
                                throw SQLException("创建购物车失败，未能获取生成的 cartId!")
                            }
                        }
                }
                conn?.prepareStatement(addBookSql).use { stmt ->
                    stmt?.setInt(1, cartId)
                    stmt?.setInt(2, bookId)
                    stmt?.setInt(3, quantity)

                    val affectedRows = stmt?.executeUpdate() // 执行插入操作并获取受影响的行数
                    return affectedRows?.let { it > 0 }
                        ?: false // 如果受影响的行数大于 0，则表示插入成功，返回 true，否则返回 false
                }

            }
        } catch (e: SQLException) {
            // 捕获触发器引发的错误
            val errorMessage: String? = e.message
            if (errorMessage!!.contains("Stock is insufficient")) {
                // 处理库存不足错误消息
                (context as Activity).runOnUiThread {
                    Toast.makeText(context, "库存不足！", Toast.LENGTH_SHORT).show()
                }
                println("Stock is insufficient for this book. Please choose a smaller quantity.")
                // 在这里可以向用户显示警告消息或采取其他补救措施
            } else {
                // 其他数据库错误
                e.printStackTrace()
            }
        }
        return false // 出现异常时返回 false
    }

    //加载该用户购物车中的商品
    fun queryPreOrderMsgFromSQLServer(userId: Int): ArrayList<CartDetails>? {
        // 1. 检查是否已经存在用户的购物车,若存在则继续查询，否则直接返回null
        val checkCartSql = "SELECT TOP 1 cartId FROM Cart WHERE userId = ? ORDER BY createdAt DESC"

        // 2. 查询该用户的购物车
        val queryCartSql =
            "select cd.cartDetailId as 购物车列表编号,cd.cartId as 购物车编号, cd.bookId as 图书编号, publisher as 出版社, cd.quantity as 数量,\n" +
                    "b.bookname as 书名,b.author as 作者,cd.createdAt as 预购时间\n" +
                    "from Book as b,Cart as c,CartDetails as cd\n" +
                    "where cd.cartId = c.cartId and c.userId = ? \n" +
                    "and b.book_id = cd.bookId"

        try {
            //检查是否已经存在用户的购物车,若存在则继续查询，否则直接返回null
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            conn?.prepareStatement(checkCartSql).use { stmt ->
                stmt?.setInt(1, userId)
                val result = stmt?.executeQuery()
                //保存购物车id
                var cartId = 1
                if (result?.next() == false) {  //用户还未有购物车
                    return null //直接返回null
                }
            }
            //查询该用户的购物车
            conn?.prepareStatement(queryCartSql).use { stmt ->
                stmt?.setInt(1, userId)
                val rs = stmt?.executeQuery() // 使用 executeQuery 方法来执行查询操作

                val cartList = ArrayList<CartDetails>()

                while (rs?.next() == true) { // 表示有结果

                    val dateString = rs.getString("预购时间")
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val truncatedDateString = dateString.substring(0, 19)
                    val createdAtTime = /*try {*/
                        LocalDateTime.parse(truncatedDateString, formatter)

                    Log.e("createdAtTime", createdAtTime.toString())
                    /*                    } catch (e: DateTimeParseException) {
                                            // Handle parse exception
                                            LocalDateTime.of(2003, 10, 24, 0, 0, 0) // Default value in case of parsing error
                                        }*/

                    val cartDetail = CartDetails(
                        cardDetailId = rs.getInt("购物车列表编号"),
                        cardId = rs.getInt("购物车编号"),
                        bookId = rs.getInt("图书编号"),
                        quantity = rs.getInt("数量"),
                        publisher = rs.getString("出版社"),
                        bookName = rs.getString("书名"),
                        author = rs.getString("作者"),
                        createdAt = createdAtTime
                    )
                    cartList.add(cartDetail)
                }
                if (cartList.size != 0) {
                    return cartList
                } else {
                    return null
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null // 出现异常时返回 true
    }

    //根据用户名查找购物车列表
    fun queryCartListByUserName(userName: String): ArrayList<CartDetails>? {
        //查询该用户的购物车
        val queryCartSql =
            "select cd.cartDetailId as 购物车列表编号,cd.cartId as 购物车编号, cd.bookId as 图书编号, publisher as 出版社, cd.quantity as 数量,\n" +
                    "b.bookname as 书名,b.author as 作者,cd.createdAt as 预购时间\n" +
                    "from Book as b,Cart as c,CartDetails as cd,Users as u\n" +
                    "where cd.cartId = c.cartId and c.userId = u.userId \n" +
                    "and b.book_id = cd.bookId and u.userName = ?"
        try {
            //检查是否已经存在用户的购物车,若存在则继续查询，否则直接返回null
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            //查询该用户的购物车
            conn?.prepareStatement(queryCartSql).use { stmt ->
                stmt?.setString(1, userName)
                val rs = stmt?.executeQuery() // 使用 executeQuery 方法来执行查询操作

                val cartList = ArrayList<CartDetails>()

                while (rs?.next() == true) { // 表示有结果

                    val dateString = rs.getString("预购时间")
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val truncatedDateString = dateString.substring(0, 19)
                    val createdAtTime =
                        LocalDateTime.parse(truncatedDateString, formatter)

                    Log.e("createdAtTime", createdAtTime.toString())

                    val cartDetail = CartDetails(
                        userName =  userName,
                        cardDetailId = rs.getInt("购物车列表编号"),
                        cardId = rs.getInt("购物车编号"),
                        bookId = rs.getInt("图书编号"),
                        quantity = rs.getInt("数量"),
                        publisher = rs.getString("出版社"),
                        bookName = rs.getString("书名"),
                        author = rs.getString("作者"),
                        createdAt = createdAtTime
                    )
                    cartList.add(cartDetail)
                }
                if (cartList.size != 0) {
                    return cartList
                } else {
                    return null
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null // 出现异常时返回 true
    }

    //删除购物车中的预订信息
    fun deleteCartDetails(cardDetailId: Int): Boolean {
        val deleteCartDetailsSQL = "delete from CartDetails where cartDetailId = ?"

        try {
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            conn?.prepareStatement(deleteCartDetailsSQL).use { stmt ->
                stmt?.setInt(1, cardDetailId)
                val affectedRows = stmt?.executeUpdate() // 执行删除操作并获取受影响的行数
                return affectedRows?.let { it > 0 }
                    ?: false // 如果受影响的行数大于 0，则表示删除成功，返回 true，否则返回 false
            }

        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false // 出现异常时返回 false
    }
}

