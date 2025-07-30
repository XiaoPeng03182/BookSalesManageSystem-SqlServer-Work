package com.example.booksalesmanagement.dao

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.example.booksalesmanagement.database.ConnectionSqlServer
import java.sql.Connection
import java.sql.SQLException

//对数据库中，普通用户进行访问权限的控制
object UsersDBPermissionsDao {
    //授予用户对表的全部语句级访问权限
    fun grantALLStatePermissionsToUser(context: Context) {
        val grantAllTablePermissionSQL = "GRANT select,insert,update,delete ON SCHEMA::dbo TO users"
        var conn: Connection? = null
        try {
            conn = ConnectionSqlServer.getConnectionAdmin("BookSalesdb")
            conn?.prepareStatement(grantAllTablePermissionSQL).use { stmt ->
                stmt?.execute()
                (context as Activity).runOnUiThread {
                    Toast.makeText(context, "成功授予用户对表的全部访问权限!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            (context as Activity).runOnUiThread {
                Toast.makeText(context, "授予用户对表的全部访问权限失败!", Toast.LENGTH_SHORT)
                    .show()
            }
        } finally {
            conn?.close()
        }
    }

    //授予用户对表的全部语句级访问权限
    fun revokeALLStatePermissionsToUser(context: Context) {
        val revokeAllTablePermissionSQL =
            "Revoke select,insert,update,delete ON SCHEMA::dbo From users"
        var conn: Connection? = null
        try {
            conn = ConnectionSqlServer.getConnectionAdmin("BookSalesdb")
            conn?.prepareStatement(revokeAllTablePermissionSQL).use { stmt ->
                stmt?.execute()
                (context as Activity).runOnUiThread {
                    Toast.makeText(context, "成功撤销用户对表的全部访问权限!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            (context as Activity).runOnUiThread {
                Toast.makeText(context, "撤销用户对表的全部访问权限失败!", Toast.LENGTH_SHORT)
                    .show()
            }
        } finally {
            conn?.close()
        }
    }

    //授予users用户指定的语句级权限
    fun gantSpecialStatePermissionToUser(context: Context, updates: MutableList<String>) {
        val grantSpecialStatePermissionSQL = StringBuilder("GRANT ")
        val grantSatePermissionList = StringBuilder()
        updates.forEachIndexed { index, column ->
            grantSpecialStatePermissionSQL.append(column)
            grantSatePermissionList.append("$column ")
            if (index != updates.size - 1) { // 去除最后的逗号
                grantSpecialStatePermissionSQL.append(", ")
            }
        }
        grantSpecialStatePermissionSQL.append(" On SCHEMA::dbo TO users")

        var conn: Connection? = null
        try {
            conn = ConnectionSqlServer.getConnectionAdmin("BookSalesdb")
            conn?.prepareStatement(grantSpecialStatePermissionSQL.toString()).use { stmt ->
                stmt?.execute()
                (context as Activity).runOnUiThread {
                    Toast.makeText(
                        context,
                        "成功授予！",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            (context as Activity).runOnUiThread {
                Toast.makeText(
                    context,
                    "授予失败!",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        } finally {
            conn?.close()
        }
    }

    //撤销用户指定的语句级权限
    fun revokeSpecialStatePermissionToUser(context: Context, updates: MutableList<String>) {
        val revokeSpecialStatePermissionSQL = StringBuilder("Revoke ")
        val revokeSatePermissionList = StringBuilder()
        updates.forEachIndexed { index, column ->
            revokeSpecialStatePermissionSQL.append(column)
            revokeSatePermissionList.append("$column ")
            if (index != updates.size - 1) { // 去除最后的逗号
                revokeSpecialStatePermissionSQL.append(", ")
            }
        }
        revokeSpecialStatePermissionSQL.append(" On SCHEMA::dbo From users")

        var conn: Connection? = null
        try {
            conn = ConnectionSqlServer.getConnectionAdmin("BookSalesdb")
            conn?.prepareStatement(revokeSpecialStatePermissionSQL.toString()).use { stmt ->
                stmt?.execute()
                (context as Activity).runOnUiThread {
                    Toast.makeText(
                        context,
                        "成功撤销！",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            (context as Activity).runOnUiThread {
                Toast.makeText(
                    context,
                    "撤销失败!",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        } finally {
            conn?.close()
        }
    }

    //授予对象级访问权限
    fun grantObjectPermission(
        context: Context,
        tableName: String,
        updates: MutableList<String>
    ) {
        val grantObjectPermissionSQL = StringBuilder("GRANT ")
        val grantObjectPermissionList = StringBuilder()
        updates.forEachIndexed { index, column ->
            grantObjectPermissionSQL.append(column)
            grantObjectPermissionList.append("$column ")
            if (index != updates.size - 1) { // 去除最后的逗号
                grantObjectPermissionSQL.append(", ")
            }
        }
        grantObjectPermissionSQL.append(" On $tableName TO users")

        var conn: Connection? = null
        try {
            conn = ConnectionSqlServer.getConnectionAdmin("BookSalesdb")
            conn?.prepareStatement(grantObjectPermissionSQL.toString()).use { stmt ->
                stmt?.execute()
                (context as Activity).runOnUiThread {
                    Toast.makeText(
                        context,
                        "${tableName}对象权限成功授予！",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            (context as Activity).runOnUiThread {
                Toast.makeText(
                    context,
                    "${tableName}授予失败!",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        } finally {
            conn?.close()
        }
    }

    //撤销对象级访问权限
    fun revokeObjectPermission(
        context: Context,
        tableName: String,
        updates: MutableList<String>
    ) {
        val revokeObjectPermissionSQL = StringBuilder("Revoke ")
        val revokeObjectPermissionList = StringBuilder()
        updates.forEachIndexed { index, column ->
            revokeObjectPermissionSQL.append(column)
            revokeObjectPermissionList.append("$column ")
            if (index != updates.size - 1) { // 去除最后的逗号
                revokeObjectPermissionSQL.append(", ")
            }
        }
        revokeObjectPermissionSQL.append(" On $tableName From users")

        var conn: Connection? = null
        try {
            conn = ConnectionSqlServer.getConnectionAdmin("BookSalesdb")
            conn?.prepareStatement(revokeObjectPermissionSQL.toString()).use { stmt ->
                stmt?.execute()
                (context as Activity).runOnUiThread {
                    Toast.makeText(
                        context,
                        "${tableName}撤销成功！",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            (context as Activity).runOnUiThread {
                Toast.makeText(
                    context,
                    "${tableName}撤销失败!",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        } finally {
            conn?.close()
        }
    }
}