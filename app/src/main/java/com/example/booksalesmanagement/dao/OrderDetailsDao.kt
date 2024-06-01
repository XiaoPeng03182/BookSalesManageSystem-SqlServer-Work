package com.example.booksalesmanagement.dao

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.booksalesmanagement.bean.CartDetails
import com.example.booksalesmanagement.bean.OrderDetails
import com.example.booksalesmanagement.database.ConnectionSqlServer
import java.sql.PreparedStatement
import java.sql.SQLException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object OrderDetailsDao {

    //添加预购图书到购物车中
    fun insertOrdersToSQLServer(
        context: Context,
        userId: Int,
        bookId: Int,
        quantity: Int
    ): Boolean {
        // 1. 检查是否已经存在该用户的订单
        val checkOrderSql =
            "SELECT TOP 1 orderId FROM Orders WHERE userId = ? ORDER BY orderDate DESC"
        // 2. 若该用户不存在订单，则创建新的订单
        val createOrderSql = "INSERT INTO Orders(userId) VALUES (?)"
        // 3. 将该图书订单添加到订单详情表
        val addOrderDetailsSql =
            "INSERT INTO OrderDetails (orderId, userId, bookId, quantity) VALUES (?, ?, ?, ?)"

        try {
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            conn?.prepareStatement(checkOrderSql).use { stmt ->
                stmt?.setInt(1, userId)
                val result = stmt?.executeQuery()
                //保存订单id
                var orderId = 1
                if (result?.next() == true) {  //用户已经有订单
                    orderId = result.getInt("orderId");
                } else { // 用户没有订单，创建新的订单
                    conn?.prepareStatement(createOrderSql, PreparedStatement.RETURN_GENERATED_KEYS)
                        .use { stmt ->
                            stmt?.setInt(1, userId)
                            stmt?.executeUpdate()
                            // 获取新创建的订单ID orderId
                            val rs = stmt?.generatedKeys;
                            if (rs?.next() == true) {
                                orderId = rs.getInt(1);
                            } else {
                                throw SQLException("创建订单失败，未能获取生成的 orderId!")
                            }
                        }
                }
                conn?.prepareStatement(addOrderDetailsSql).use { stmt ->
                    stmt?.setInt(1, orderId)
                    stmt?.setInt(2, userId)
                    stmt?.setInt(3, bookId)
                    stmt?.setInt(4, quantity)

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
    fun queryOrderDetailsFromSQLServer(userId: Int): ArrayList<OrderDetails>? {
        // 1. 检查是否已经存在用户的订单,若存在则继续查询，否则直接返回null
        val checkOrderSql =
            "SELECT TOP 1 orderId FROM Orders WHERE userId = ? ORDER BY orderDate DESC"

        // 2. 查询该用户的订单
        val queryOrderDetailsSql =
            "select od.orderDetailId as 订单列表编号,od.orderId as 订单详情编号,b.book_id as 图书编号,publisher as 出版社, od.quantity as 数量,\n" +
                    "b.bookname as 书名,b.author as 作者,od.createdAt as 下单时间,\n" +
                    "b.price as 单价,b.price*od.quantity as 总金额,u.address as 收货地址,\n" +
                    "u.phoneNumber as 联系电话\n" +
                    "from Book as b,OrderDetails as od,Users as u,Orders as o\n" +
                    "where o.orderId = od.orderId and o.userId = ? \n" +
                    "and b.book_id = od.bookId and u.userId = od.userId"

        try {
            //检查是否已经存在用户的订单,若存在则继续查询，否则直接返回null
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            conn?.prepareStatement(checkOrderSql).use { stmt ->
                stmt?.setInt(1, userId)
                val result = stmt?.executeQuery()
                //保存购物车id
                var orderId = 1
                if (result?.next() == false) {  //用户还未有订单
                    return null //直接返回null
                }
            }
            //查询该用户的购物车
            conn?.prepareStatement(queryOrderDetailsSql).use { stmt ->
                stmt?.setInt(1, userId)
                val rs = stmt?.executeQuery() // 使用 executeQuery 方法来执行查询操作

                val orderList = ArrayList<OrderDetails>()

                while (rs?.next() == true) { // 表示有结果
                    val dateString = rs.getString("下单时间")
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val truncatedDateString = dateString.substring(0, 19)
                    val createdAtTime = /*try {*/
                        LocalDateTime.parse(truncatedDateString, formatter)

                    Log.e("createdAtTime", createdAtTime.toString())
                    /*                    } catch (e: DateTimeParseException) {
                                            // Handle parse exception
                                            LocalDateTime.of(2003, 10, 24, 0, 0, 0) // Default value in case of parsing error
                                        }*/

                    val orderDetail = OrderDetails(
                        orderDetailId = rs.getInt("订单列表编号"),
                        orderId = rs.getInt("订单详情编号"),
                        userId = userId,
                        bookId = rs.getInt("图书编号"),
                        quantity = rs.getInt("数量"),
                        price = rs.getBigDecimal("单价"),
                        totalAmount = rs.getDouble("总金额"),
                        publisher = rs.getString("出版社"),
                        bookName = rs.getString("书名"),
                        author = rs.getString("作者"),
                        address = rs.getString("收货地址"),
                        phoneNumber = rs.getString("联系电话"),
                        orderDate = createdAtTime
                    )
                    orderList.add(orderDetail)
                }
                if (orderList.size != 0) {
                    return orderList
                } else {
                    return null
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null // 出现异常时返回 true
    }

    //删除订单和确认收货
    fun deleteOrderDetails(orderDetailId: Int): Boolean {
        val deleteOrderDetailsSQL = "delete from OrderDetails where orderDetailId = ?"

        try {
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            conn?.prepareStatement(deleteOrderDetailsSQL).use { stmt ->
                stmt?.setInt(1, orderDetailId)
                val affectedRows = stmt?.executeUpdate() // 执行删除操作并获取受影响的行数
                return affectedRows?.let { it > 0 }
                    ?: false // 如果受影响的行数大于 0，则表示删除成功，返回 true，否则返回 false
            }

        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false // 出现异常时返回 false
    }

    //加载该用户订单中的商品
    fun queryOrderDetailsByUserName(userName: String): ArrayList<OrderDetails>? {
        // 2. 查询该用户的订单
        val queryOrderDetailsSql =
            "select u.userName as 用户名,od.orderDetailId as 订单列表编号,od.orderId as 订单详情编号,b.book_id as 图书编号,publisher as 出版社, od.quantity as 数量,\n" +
                    "b.bookname as 书名,b.author as 作者,od.createdAt as 下单时间,\n" +
                    "b.price as 单价,b.price*od.quantity as 总金额,u.address as 收货地址,\n" +
                    "u.phoneNumber as 联系电话\n" +
                    "from Book as b,OrderDetails as od,Users as u,Orders as o\n" +
                    "where o.orderId = od.orderId and o.userId = u.userId\n" +
                    "and b.book_id = od.bookId and u.userId = od.userId\n" +
                    "and u.userName = ?"

        try {
            //检查是否已经存在用户的订单,若存在则继续查询，否则直接返回null
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            //查询该用户的订单
            conn?.prepareStatement(queryOrderDetailsSql).use { stmt ->
                stmt?.setString(1, userName)
                val rs = stmt?.executeQuery() // 使用 executeQuery 方法来执行查询操作

                val orderList = ArrayList<OrderDetails>()

                while (rs?.next() == true) { // 表示有结果
                    val dateString = rs.getString("下单时间")
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val truncatedDateString = dateString.substring(0, 19)
                    val createdAtTime = /*try {*/
                        LocalDateTime.parse(truncatedDateString, formatter)

                    Log.e("createdAtTime", createdAtTime.toString())

                    val orderDetail = OrderDetails(
                        orderDetailId = rs.getInt("订单列表编号"),
                        orderId = rs.getInt("订单详情编号"),
                        userName = userName,
                        bookId = rs.getInt("图书编号"),
                        quantity = rs.getInt("数量"),
                        price = rs.getBigDecimal("单价"),
                        totalAmount = rs.getDouble("总金额"),
                        publisher = rs.getString("出版社"),
                        bookName = rs.getString("书名"),
                        author = rs.getString("作者"),
                        address = rs.getString("收货地址"),
                        phoneNumber = rs.getString("联系电话"),
                        orderDate = createdAtTime
                    )
                    orderList.add(orderDetail)
                }
                if (orderList.size != 0) {
                    return orderList
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