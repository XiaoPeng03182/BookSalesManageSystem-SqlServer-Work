package com.example.booksalesmanagement.dao

import android.util.Log
import com.example.booksalesmanagement.bean.Book
import com.example.booksalesmanagement.database.ConnectionSqlServer
import java.lang.StringBuilder
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.sql.Timestamp
import java.time.LocalDateTime

object BookDao {
    fun insertBook(book: Book) :Boolean{
        val insertSQL = "insert into Book(book_id, bookname, " +
                "publisher_id, author, price, " +
                "inventory, publication_date) " +
                "values(?, ?, ?, ?, ?, ?, ?)"
               /* "values(${book.bookId}, '${book.bookName}', ${book.publisherId}, '${book.author}'," +
                " ${book.price}, ${book.inventory}, '${book.publicationDate}')"*/

        val insertSQL2 = "insert into Book(book_id, bookname, " +
                "publisher_id, author, price) " +
                "values(?, ?, ?, ?, ?, ?)"

        try{
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            conn?.prepareStatement(insertSQL).use { stmt->
                stmt?.setInt(1, book.bookId)
                stmt?.setString(2, book.bookName)
                stmt?.setInt(3, book.publisherId)
                stmt?.setString(4, book.author)
                stmt?.setBigDecimal(5,book.price)
                stmt?.setInt(6,book.inventory)
                stmt?.setTimestamp(7,book.publicationDate)

                val rowsInserted = stmt?.executeUpdate() // 执行插入操作并获取受影响的行数
                return rowsInserted?.let { it > 0 } ?: false // 如果受影响的行数大于 0，则表示插入成功，返回 true，否则返回 false
            }
        }catch (e:SQLException) {
            e.printStackTrace()
        }
        return false // 出现异常时返回 false
    }

    fun queryAll():ArrayList<Book> {
        var bookList = ArrayList<Book>()
        var conn: Connection? = null
        var stmt: Statement? = null
        var resultSet: ResultSet? = null

        try {
            conn = ConnectionSqlServer.getConnection("BookSalesdb")
            stmt = conn!!.createStatement()
            val sql =
                "SELECT * FROM Book"  // 注意使用正确的表名和架构前缀
            resultSet = stmt.executeQuery(sql)

            while (resultSet.next()) {
                val bookId = resultSet.getInt("book_id")
                val bookName = resultSet.getString("bookname")
                val publisherId = resultSet.getInt("publisher_id")
                val price = resultSet.getBigDecimal("price")
                val author = resultSet.getString("author")
                val inventory = resultSet.getInt("inventory")
                val publicationDate =resultSet.getTimestamp("publication_date")

                val book = Book(bookId, bookName, publisherId, price, author, inventory, publicationDate)
                //val sc = SC(Sno, Cno, Score)
                //SC.add(sc)
                bookList.add(book)
            }
            val sb = StringBuilder()
            for (book in bookList) {
                sb.append("图书编号：" + book.bookId)
                sb.append("图书名称：" + book.bookName)
                sb.append("出版社编号：" + book.publisherId)
                sb.append("作者：" + book.author)
                sb.append("价格：" + book.price)
                sb.append("库存：" + book.inventory)
                sb.append("出版日期：" + book.publicationDate)
                sb.append("\n")
            }
            Log.e("Dao", sb.toString())

        } catch (ex: SQLException) {
            ex.printStackTrace()
        } finally {
            resultSet?.close()
            stmt?.close()
            conn?.close()
        }
        //Log.e("queryCourseMsg", resultCno)
        return bookList
    }

}