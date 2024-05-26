package com.example.booksalesmanagement.dao

import com.example.booksalesmanagement.bean.Administrator
import com.example.booksalesmanagement.bean.User
import com.example.booksalesmanagement.database.ConnectionSqlServer
import java.sql.SQLException

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
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
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
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            conn?.prepareStatement(queryAdminSQL).use { stmt ->
                stmt?.setString(1, admin.adminName)
                stmt?.setString(2, admin.passWord)

                val rs = stmt?.executeQuery() // 使用 executeQuery 方法来执行查询操作

                if (rs?.next() == true) { // 表示有结果
                    return Administrator(rs.getString("adminName"), rs.getString("passWord"))
                }else {
                    return null //没有结果返回
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null // 出现异常时返回 true
    }
}