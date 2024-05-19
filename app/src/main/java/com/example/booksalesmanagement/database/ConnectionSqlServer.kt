package com.example.booksalesmanagement.database

import android.util.Log
import com.example.booksalesmanagement.bean.SC
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

object ConnectionSqlServer {
    // 获取数据库连接的函数，接受数据库名作为参数
    @Throws(SQLException::class)
    fun getConnection(dbName: String): Connection? {
        var conn: Connection? = null
        try {
            //Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver") //加载驱动
            Class.forName("net.sourceforge.jtds.jdbc.Driver") //加载驱动
            val ip = "10.203.132.6"

            // 使用 DriverManager 建立数据库连接
            conn = DriverManager.getConnection(
                //"jdbc:jtds:sqlserver://$ip:1433;Database=$dbName;encrypt=true;trustServerCertificate=true", // 数据库连接 URL
                "jdbc:jtds:sqlserver://$ip:1433/$dbName;encrypt=true;trustServerCertificate=true", // 数据库连接 URL
                //"jdbc:sqlserver://$ip:1433;database=$dbName;encrypt=true;encrypt=true;trustServerCertificate=true", // 数据库连接 URL
                "sa", "123456"  // 数据库用户名和密码
            ) as Connection
            //TextLoginActivity.conn_on = 1 //用于向主函数传参，判断连接是否成功，// 设置连接状态为成功
        } catch (ex: SQLException) { // 捕获 SQL 异常
            ex.printStackTrace()
            //TextLoginActivity.conn_on = 2 // 设置连接状态为失败
        } catch (ex: ClassNotFoundException) {
            ex.printStackTrace()
            //TextLoginActivity.conn_on = 2  // 设置连接状态为失败
        }
        return conn //返回Connection型变量conn用于后续连接
    }

    fun querySCMsg(): ArrayList<SC> {
        var conn: Connection? = null
        conn = getConnection("jiaoxuedb")   //获取数据库连接

        val stmt = conn!!.createStatement() //使用Connection来创建一个Statment对象

        val SC = ArrayList<SC>()

        val sql = "SELECT Sno, Cno, Score FROM sc"
        val resultSet = stmt.executeQuery(sql)

        while (resultSet.next()) {

            val Sno = resultSet.getString("Sno")
            val Cno = resultSet.getString("Cno")
            val Score = resultSet.getString("Score")

            val sc = SC(Sno, Cno, Score)
            SC.add(sc)
        }

        // 关闭连接和资源
        resultSet.close()
        stmt.close()
        conn.close()

        return SC
    }


    fun queryCourseMsg(): String {

        var resultCno = ""
        var conn: Connection? = null
        var stmt: Statement? = null
        var resultSet: ResultSet? = null

        try {
            conn = getConnection("BookSalesDB")
            stmt = conn!!.createStatement()
            val sql =
                    "SELECT bookname FROM Book"  // 注意使用正确的表名和架构前缀
            resultSet = stmt.executeQuery(sql)

            if (resultSet.next()) {
                resultCno = resultSet.getString(1) // 获取查询结果中的第一列的值
            }
        } catch (ex: SQLException) {
            ex.printStackTrace()
        } finally {
            resultSet?.close()
            stmt?.close()
            conn?.close()
        }

        Log.e("queryCourseMsg", resultCno)
        return resultCno
    }


}