package com.example.booksalesmanagement.dao

import com.example.booksalesmanagement.database.ConnectionSqlServer
import java.sql.SQLException

object BackupRecoveryDBDao {
    //备份数据库
    fun backupDB(): Boolean {
        val backupDBSQL =
            "BACKUP DATABASE [BookSalesdb] TO DISK = " +
                    "'E:\\大二学习\\大二SQL数据库学习\\Backups\\your_database_name.bak' "
        return try {
            val conn = ConnectionSqlServer.getConnectionAdmin("BookSalesdb")
            conn?.prepareStatement(backupDBSQL).use { stmt ->

                stmt?.execute()
                println("Database backup completed successfully.")
                true
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            println("Failed to backup database")
            false
        }
    }

    //恢复数据库
    fun recoveryDB(): Boolean {
        //使用master
        val useMasterSQL = "USE [master];"
        //将数据库设置为单用户模式
        val setSingleUserSQL = "ALTER DATABASE [BookSalesdb] SET SINGLE_USER WITH ROLLBACK IMMEDIATE;"
        //恢复数据库:确定日志尾部的数据可以被覆盖，可以使用WITH REPLACE选项强制覆盖现有数据库。
        val restoreDBSQL = "RESTORE DATABASE [BookSalesdb] FROM DISK = " +
                "'E:\\大二学习\\大二SQL数据库学习\\Backups\\your_database_name.bak' WITH REPLACE;"
        //将数据库恢复为多用户模式
        val setMultiUserSQL = "ALTER DATABASE [BookSalesdb] SET MULTI_USER;"

        val connection = ConnectionSqlServer.getConnectionAdmin("BookSalesdb")
        return try {
            connection?.autoCommit = false // 禁用自动提交

            connection?.createStatement().use { statement ->
                //使用master
                statement?.execute(useMasterSQL)

                // 将数据库设置为单用户模式
                statement?.execute(setSingleUserSQL)
                println("Database set to SINGLE_USER mode.")

                // 执行恢复操作并覆盖现有数据库
                statement?.execute(restoreDBSQL)
                println("Database restore from backup completed successfully.")

                // 将数据库恢复为多用户模式
                statement?.execute(setMultiUserSQL)
                println("Database set to MULTI_USER mode.")
            }

            connection?.commit() // 提交更改
            println("Transaction committed successfully.")
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            println("Failed to recover database. Rolling back transaction.")
            try {
                connection?.rollback() // 回滚更改
                println("Transaction rolled back successfully.")
            } catch (rollbackEx: SQLException) {
                rollbackEx.printStackTrace()
                println("Failed to roll back transaction.")
            }
            false
        } finally {
            connection?.close()
        }
    }

}