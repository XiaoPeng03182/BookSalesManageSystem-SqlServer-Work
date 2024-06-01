package com.example.booksalesmanagement.dao

import android.content.Context
import android.util.Log
import com.example.booksalesmanagement.bean.Administrator
import com.example.booksalesmanagement.database.ConnectionSqlServer
import java.sql.Date
import java.sql.SQLException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object AdminDao {
    /**
     * 向数据库中插入管理员信息
     * @param admin 管理员对象
     * @return 插入成功返回true，否则返回false
     * @throws SQLException
     * @author xiaopeng
     * @date 2024/5/25
     */
    fun insertAdminToSQlServer(admin: Administrator): Boolean {
        val insertAdminSQL = "insert into Admins(adminName,passWord)" +
                "values(?,?)"

        try {
            val conn = ConnectionSqlServer.getConnectionAdmin("BookSalesdb")
            conn?.prepareStatement(insertAdminSQL).use { stmt ->
                stmt?.setString(1, admin.adminName)
                stmt?.setString(2, admin.passWord)

                val affectedRows = stmt?.executeUpdate() // 执行插入操作并获取受影响的行数
                return affectedRows?.let { it > 0 }
                    ?: false // 如果受影响的行数大于 0，则表示插入成功，返回 true，否则返回 false
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false // 出现异常时返回 false

    }

    /**
     *  从数据库中查询管理员用户注册信息，可用于检测是否符合登录条件
     *  @param admin 用户信息
     */
    fun queryAdminFromSQLServer(admin: Administrator): Administrator? {
        val queryAdminSQL = "select * from Admins where adminName = ? and passWord = ?"

        try {
            val conn = ConnectionSqlServer.getConnectionAdmin("BookSalesdb")
            conn?.prepareStatement(queryAdminSQL).use { stmt ->
                stmt?.setString(1, admin.adminName)
                stmt?.setString(2, admin.passWord)

                val rs = stmt?.executeQuery() // 使用 executeQuery 方法来执行查询操作

                if (rs?.next() == true) { // 表示有结果
                    return Administrator(
                        rs.getString("adminName"), rs.getString("passWord"),
                        adminId = rs.getInt("adminId")
                    )
                } else {
                    return null //没有结果返回
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null // 出现异常时返回 true
    }

    fun insertAdmin(admin: Administrator): Boolean {
        val insertAdminSQL = "insert into Admins(adminName,passWord,phoneNumber)" +
                "values(?,?,?)"

        try {
            val conn = ConnectionSqlServer.getConnectionAdmin("BookSalesdb")
            conn?.prepareStatement(insertAdminSQL).use { stmt ->
                stmt?.setString(1, admin.adminName)
                stmt?.setString(2, admin.passWord)
                stmt?.setString(3, admin.phoneNumber)

                val affectedRows = stmt?.executeUpdate() // 执行插入操作并获取受影响的行数
                return affectedRows?.let { it > 0 }
                    ?: false // 如果受影响的行数大于 0，则表示插入成功，返回 true，否则返回 false
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false // 出现异常时返回 false
    }

    //根据姓名，查询管理员信息
    fun queryAdminMagByName(adminName: String): ArrayList<Administrator>? {
        val queryAdminMagByNameSQL = "select * from Admins where adminName = ?"

        try {
            val conn = ConnectionSqlServer.getConnectionAdmin("BookSalesdb")
            conn?.prepareStatement(queryAdminMagByNameSQL).use { stmt ->
                stmt?.setString(1, adminName)
                val rs = stmt?.executeQuery() // 使用 executeQuery 方法来执行查询操作


                val adminList = ArrayList<Administrator>()

                while (rs?.next() == true) { // 表示有结果
                    val dateString = rs.getString("registrationDate")
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val truncatedDateString = dateString.substring(0, 19)
                    val createdAtTime = /*try {*/
                        LocalDate.parse(truncatedDateString, formatter)

                    val review = Administrator(
                        rs.getString("adminName"), rs.getString("passWord"),
                        adminId = rs.getInt("adminId"),
                        registrationDate = createdAtTime,
                        phoneNumber = rs.getString("phoneNumber")
                    )
                    adminList.add(review)
                }
                if (adminList.size != 0) {
                    return adminList
                } else {
                    return null
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null // 出现异常时返回 true
    }

    //查询所有管理员信息
    fun queryAllAdmin(): ArrayList<Administrator>? {
        val queryAdminAllMsgSQL = "select * from Admins"

        try {
            //val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            val conn = ConnectionSqlServer.getConnectionAdmin("BookSalesdb")
            conn?.prepareStatement(queryAdminAllMsgSQL).use { stmt ->

                val rs = stmt?.executeQuery() // 使用 executeQuery 方法来执行查询操作

                val adminList = ArrayList<Administrator>()

                while (rs?.next() == true) { // 表示有结果
                    val dateString = rs.getString("registrationDate")
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val truncatedDateString = dateString.substring(0, 19)
                    val createdAtTime = /*try {*/
                        LocalDate.parse(truncatedDateString, formatter)

                    val review = Administrator(
                        rs.getString("adminName"),
                        rs.getString("passWord"),
                        adminId = rs.getInt("adminId"),
                        registrationDate = createdAtTime,
                        phoneNumber = rs.getString("phoneNumber") ?: "default phoneNumber"
                    )
                    adminList.add(review)
                }
                if (adminList.size != 0) {
                    return adminList
                } else {
                    return null
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null // 出现异常时返回 true
    }

    //根据adminId,删除管理员
    fun deleteAdminById(adminId: Int): Boolean {
        val deleteAdminByIdSQL = "DELETE FROM Admins WHERE adminId = ?"
        try {
            //检查是否已经存在用户的购物车,若存在则继续查询，否则直接返回null
            val conn = ConnectionSqlServer.getConnectionAdmin("BookSalesdb")
            conn?.prepareStatement(deleteAdminByIdSQL).use { stmt ->

                stmt?.setInt(1, adminId)
                val affectedRows = stmt?.executeUpdate() // 执行删除操作并获取受影响的行数
                return affectedRows?.let { it > 0 }
                    ?: false // 如果受影响的行数等于集合大小，则表示全部更新成功，返回 true，否则返回 false
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return false
    }

    //更新管理员信息
    fun updateAdminMsg(context: Context, adminId: Int, updates: Map<String, Any>): Boolean {
        val updateAdminMsgSQL = StringBuilder("UPDATE Admins SET ")
        updates.forEach { (column, value) -> updateAdminMsgSQL.append(column).append(" = ?, ") }
        updateAdminMsgSQL.setLength(updateAdminMsgSQL.length - 2) // 去除最后的逗号

        updateAdminMsgSQL.append(" WHERE adminId = ?")


        try {
            val conn = ConnectionSqlServer.getConnectionAdmin("BookSalesdb")
            conn?.prepareStatement(updateAdminMsgSQL.toString()).use { stmt ->
                var index = 1
                //循环用于遍历 updates 中的每个值，并依次设置到 PreparedStatement 中的占位符 ?
                for ((column, value) in updates) {
                    stmt?.setObject(index++, value) //stmt.setObject 方法会根据值的类型自动处理适当的数据类型
                }
                stmt?.setInt(index, adminId)
                val affectedRows = stmt?.executeUpdate() // 执行删除操作并获取受影响的行数

                return affectedRows?.let { it == updates.size }
                    ?: false // 如果受影响的行数等于集合大小，则表示全部更新成功，返回 true，否则返回 false
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return false
    }
}