package com.example.booksalesmanagement.dao

import com.example.booksalesmanagement.bean.User
import com.example.booksalesmanagement.database.ConnectionSqlServer
import java.sql.SQLException

object UserDao {

    /**
     * 注册时从数据库中查询userName是否存在的函数；
     * 接受待注册的用户名(包括普通用户和管理员)作为参数
     * @param registrationName 待注册的用户名
     * @param isSelectUser 是否选择了普通用户
     * @return 返回查询结果
     * @throws SQLException
     * @author xiaopeng
     * @date 2024/5/25
     */
    fun queryRegisterNameIsExist(registrationName: String, isSelectUser: Boolean): Boolean {
        val queryUserNameSQL = "select * from Users where userName = ?"
        val queryAdminNameSQL = "select * from Admins where adminName = ?"

        if (isSelectUser) { //普通用户
            try {
                val conn = ConnectionSqlServer.getConnection("BookSalesdb")
                conn?.prepareStatement(queryUserNameSQL).use { stmt ->
                    stmt?.setString(1, registrationName)

                    val rs = stmt?.executeQuery() // 使用 executeQuery 方法来执行查询操作

                    return rs?.next() == true // 如果有结果，表示该用户名已存在，返回 true
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            }
            return true // 出现异常时返回 true
        } else { //管理员用户
            try {
                val conn = ConnectionSqlServer.getConnection("BookSalesdb")
                conn?.prepareStatement(queryAdminNameSQL).use { stmt ->
                    stmt?.setString(1, registrationName)

                    val rs = stmt?.executeQuery() // 使用 executeQuery 方法来执行查询操作
                    return rs?.next() == true // 如果有结果，表示该用户名已存在，返回 true
                }
            } catch (e: SQLException) {
                e.printStackTrace()
            }
            return true // 出现异常时返回 true
        }
    }


    /**
     * 向数据库中插入普通用户信息
     * @param user 普通用户信息
     * @return true：插入成功；false：插入失败
     * @datetime 2024/5/25
     * @author xiaopeng
     */
    fun insertUserToSQlServer(user: User): Boolean {
        val insertUserSQL = "insert into Users(userName,password)" +
                "values(?,?)"

        try {
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            conn?.prepareStatement(insertUserSQL).use { stmt ->
                stmt?.setString(1, user.userName)
                stmt?.setString(2, user.password)

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
     *  从数据库中查询用户注册信息，可用于检测是否符合登录条件
     *  @param user 用户信息
     */
    fun queryUserFromSQLServer(user: User): User? {
        val queryUserSQL = "select * from Users where userName = ? and passWord = ?"

        try {
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            conn?.prepareStatement(queryUserSQL).use { stmt ->
                stmt?.setString(1, user.userName)
                stmt?.setString(2, user.password)

                val rs = stmt?.executeQuery() // 使用 executeQuery 方法来执行查询操作

                 if (rs?.next() == true) { // 表示有结果
                     return User(rs.getString("userName"), rs.getString("password"))
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