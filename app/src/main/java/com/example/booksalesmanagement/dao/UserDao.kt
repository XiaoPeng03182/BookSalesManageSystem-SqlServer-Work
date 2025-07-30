package com.example.booksalesmanagement.dao

import com.example.booksalesmanagement.activity.SaveUserMsg
import com.example.booksalesmanagement.bean.Book
import com.example.booksalesmanagement.bean.User
import com.example.booksalesmanagement.database.ConnectionSqlServer
import java.sql.SQLException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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


    //更新密码
    fun updatePassword(userId: Int, newPassword: String): Boolean {
        val updatePasswordSQL = "update Users set passWord = ? where userId = ?"

        try {
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            conn?.prepareStatement(updatePasswordSQL).use { stmt ->
                stmt?.setString(1, newPassword)
                stmt?.setInt(2, userId)

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
                    //保存结果
                    SaveUserMsg.userId = rs.getInt("userId")
                    SaveUserMsg.password = rs.getString("passWord")
                    val dateString = rs.getString("registrationDate")
                    //val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                    //val registrationDate = LocalDate.parse(dateString, formatter)

                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val truncatedDateString = dateString.substring(0, 10)
                    val registrationDate = /*try {*/
                        LocalDate.parse(truncatedDateString, formatter)

                    SaveUserMsg.registrationDate = registrationDate
                    //返回用户信息
                    return User(rs.getString("userName"), rs.getString("password"))
                } else {
                    return null //没有结果返回
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null // 出现异常时返回 true
    }


    /**
     * @param userId 用户id
     * @param updateType 更新类型 1 更新手机号 2 更新地址 3 更新邮箱
     * @param updateMsg 更新信息
     */
    fun updateUserMsg(userId: Int, updateType: Int, updateMsg: String): Boolean {
        val updateUserPhoneSQL = "update Users set phoneNumber = ? where userId = ?"
        val updateUserLocationSQL = "update Users set address = ? where userId = ?"
        val updateUserEmailSQL = "update Users set email = ? where userId = ?"

        var updateSQL = ""
        when (updateType) {
            1 -> updateSQL = updateUserPhoneSQL
            2 -> updateSQL = updateUserLocationSQL
            3 -> updateSQL = updateUserEmailSQL
        }
        try {
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            conn?.prepareStatement(updateSQL).use { stmt ->
                stmt?.setString(1, updateMsg)
                stmt?.setInt(2, userId)

                val affectedRows = stmt?.executeUpdate() // 执行插入操作并获取受影响的行数

                return affectedRows?.let { it > 0 }
                    ?: false // 如果受影响的行数大于 0，则表示插入成功，返回 true，否则返回 false
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false // 出现异常时返回 false
    }

    fun queryUserAllMsg(userId: Int): User? {
        val queryUserSQL = "select * from Users where userId = ?"

        try {
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            conn?.prepareStatement(queryUserSQL).use { stmt ->
                stmt?.setInt(1, userId)

                val rs = stmt?.executeQuery() // 使用 executeQuery 方法来执行查询操作

                if (rs?.next() == true) { // 表示有结果
                    //保存结果
                    SaveUserMsg.userId = rs.getInt("userId")
                    val dateString = rs.getString("registrationDate")
                    /*                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                                        val registrationDate = LocalDate.parse(dateString, formatter)*/
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val truncatedDateString = dateString.substring(0, 10)
                    val registrationDate = /*try {*/
                        LocalDate.parse(truncatedDateString, formatter)

                    SaveUserMsg.registrationDate = registrationDate
                    SaveUserMsg.phoneNumber = rs.getString("phoneNumber") ?: "Default Phone Number"
                    SaveUserMsg.address = rs.getString("address") ?: "Default Address"
                    SaveUserMsg.email = rs.getString("email") ?: "Default Email"
                    SaveUserMsg.userName = rs.getString("userName")
                    //返回用户信息
                    return User(
                        userName = SaveUserMsg.userName,
                        password = "",
                        registrationDate = SaveUserMsg.registrationDate,
                        phoneNumber = SaveUserMsg.phoneNumber,
                        address = SaveUserMsg.address,
                        email = SaveUserMsg.email
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

    //管理员，通过姓名查询用户信息
    fun queryUserMsgByName(userName: String): User? {
        val queryUserSQL = "select * from Users where userName = ?"

        try {
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            conn?.prepareStatement(queryUserSQL).use { stmt ->
                stmt?.setString(1, userName)

                val rs = stmt?.executeQuery() // 使用 executeQuery 方法来执行查询操作

                if (rs?.next() == true) { // 表示有结果
                    //保存结果
                    val dateString = rs.getString("registrationDate")
                    /*                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                                        val registrationDate = LocalDate.parse(dateString, formatter)*/
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val truncatedDateString = dateString.substring(0, 10)
                    val registrationDate = /*try {*/
                        LocalDate.parse(truncatedDateString, formatter)

                    //返回用户信息
                    return User(
                        userId = rs.getInt("userId"),
                        userName = rs.getString("userName"),
                        password = rs.getString("passWord"),
                        registrationDate = registrationDate,
                        phoneNumber = rs.getString("phoneNumber") ?: "Default Phone Number",
                        address = rs.getString("address") ?: "Default Address",
                        email = rs.getString("email") ?: "Default Email"
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

    //管理员查询所有用户信息
    fun queryAllUserMsg(): ArrayList<User>? {
        val queryAllUserMsgSql = "select * from Users"
        try {
            //检查是否已经存在用户的购物车,若存在则继续查询，否则直接返回null
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            conn?.prepareStatement(queryAllUserMsgSql).use { stmt ->

                val rs = stmt?.executeQuery()

                val userList = ArrayList<User>()

                while (rs?.next() == true) { // 表示有结果
                    //保存结果
                    val dateString = rs.getString("registrationDate")
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val truncatedDateString = dateString.substring(0, 10)
                    val registrationDate = /*try {*/
                        LocalDate.parse(truncatedDateString, formatter)
                    val user = User(
                        userId = rs.getInt("userId"),
                        userName = rs.getString("userName"),
                        password = rs.getString("passWord"),
                        registrationDate = registrationDate,
                        phoneNumber = rs.getString("phoneNumber") ?: "Default Phone Number",
                        address = rs.getString("address") ?: "Default Address",
                        email = rs.getString("email") ?: "Default Email"
                    )
                    userList.add(user)
                }
                if (userList.size != 0) {
                    return userList
                } else {
                    return null
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null // 出现异常时返回 true
    }

    //更具用户Id删除用户
    fun deleteUserById(userId: Int): Boolean {
        val deleteUserMsgByIdSQL = "DELETE FROM Users WHERE userId = ?"
        try {
            //检查是否已经存在用户的购物车,若存在则继续查询，否则直接返回null
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            conn?.prepareStatement(deleteUserMsgByIdSQL).use { stmt ->

                stmt?.setInt(1, userId)
                val affectedRows = stmt?.executeUpdate() // 执行删除操作并获取受影响的行数
                return affectedRows?.let { it > 0 }
                    ?: false // 如果受影响的行数等于集合大小，则表示全部更新成功，返回 true，否则返回 false
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return false
    }
}