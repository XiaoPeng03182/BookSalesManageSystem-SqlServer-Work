package com.example.booksalesmanagement.dao

import com.example.booksalesmanagement.database.ConnectionSqlServer
import java.sql.SQLException

object AdminVerifyCodeDao {

    fun queryAdminVerifyCode(): String {
        val queryAdminVerifyCodeSQL = "select * from AdminVerifyCode"

        try {
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            conn?.prepareStatement(queryAdminVerifyCodeSQL).use { stmt ->

                val resultSet = stmt?.executeQuery() // 使用 executeQuery 方法来执行查询操作

                resultSet?.next()
                if (resultSet != null) {
                    return resultSet.getString("verifyCode")
                } else {
                    return "" //结果集为空 返回空字符串
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return "" // 出现异常时返回 空字符串
    }
}