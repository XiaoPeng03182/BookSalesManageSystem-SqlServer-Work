package com.example.booksalesmanagement.dao

import android.util.Log
import com.example.booksalesmanagement.bean.Book
import com.example.booksalesmanagement.bean.CartDetails
import com.example.booksalesmanagement.database.ConnectionSqlServer
import java.lang.StringBuilder
import java.sql.Connection
import java.sql.Date
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object BookDao {

    //解析出版社日期：2003-1-1 与2003-01-01 等四种格式
    fun parseDate(dateStr: String): LocalDate? {
        val patterns = listOf(
            "yyyy-MM-dd",
            "yyyy-M-d",
            "yyyy-MM-d",
            "yyyy-M-dd"
        )

        for (pattern in patterns) {
            try {
                val formatter = DateTimeFormatter.ofPattern(pattern)
                return LocalDate.parse(dateStr, formatter)
            } catch (e: DateTimeParseException) {
                // 如果解析失败，则继续尝试下一个模式
            }
        }

        // 如果所有模式都尝试失败，则返回 null
        return null
    }

    fun insertBook(book: Book): Boolean {
        val insertSQL = "insert into Book(book_id, bookname,publisher, " +
                "author, price,stock,bookinfo,publication_date,category,isbn) " +

                "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        /* "values(${book.bookId}, '${book.bookName}', ${book.publisherId}, '${book.author}'," +
         " ${book.price}, ${book.inventory}, '${book.publicationDate}')"*/


        try {
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            conn?.prepareStatement(insertSQL).use { stmt ->
                stmt?.setInt(1, book.bookId)
                stmt?.setString(2, book.bookName)
                stmt?.setString(3, book.publisher)
                stmt?.setString(4, book.author)
                stmt?.setBigDecimal(5, book.price)
                stmt?.setInt(6, book.stock)
                stmt?.setString(7, book.bookInfo)
                stmt?.setDate(8, Date.valueOf(book.publicationDate.toString()))
                stmt?.setString(9, book.category)
                stmt?.setString(10, book.isbn)

                val rowsInserted = stmt?.executeUpdate() // 执行插入操作并获取受影响的行数
                return rowsInserted?.let { it > 0 }
                    ?: false // 如果受影响的行数大于 0，则表示插入成功，返回 true，否则返回 false
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return false // 出现异常时返回 false
    }

    fun queryAll(): ArrayList<Book> {
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
                val publisher = resultSet.getString("publisher")
                val price = resultSet.getBigDecimal("price")
                val author = resultSet.getString("author")
                val stock = resultSet.getInt("stock")

                val dateString = resultSet.getString("publication_date")
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val publicationDate = LocalDate.parse(dateString, formatter)

                val bookInfo = resultSet.getString("bookinfo")
                val category = resultSet.getString("category")
                val isbn = resultSet.getString("isbn")
                val creationTime = resultSet.getTimestamp("creationtime")

                val book =
                    Book(
                        bookId,
                        bookName,
                        publisher,
                        price,
                        author,
                        category,
                        stock,
                        publicationDate,
                        bookInfo,
                        isbn,
                        creationTime
                    )
                //val sc = SC(Sno, Cno, Score)
                //SC.add(sc)
                bookList.add(book)
            }
            val sb = StringBuilder()
            for (book in bookList) {
                sb.append("图书编号：" + book.bookId)
                sb.append("图书名称：" + book.bookName)
                sb.append("类别：" + book.category)
                sb.append("出版社：" + book.publisher)
                sb.append("作者：" + book.author)
                sb.append("价格：" + book.price)
                sb.append("库存：" + book.stock)
                sb.append("出版日期：" + book.publicationDate)
                sb.append("图书信息：" + book.bookInfo)
                sb.append("图书ISBN：" + book.isbn)
                sb.append("创建时间：" + book.creationTime)
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

    //根据书名查找
    fun queryBookByName(bookName: String): ArrayList<Book>? {
        // 2. 查询该用户的购物车
        val queryBookByNameSql = "select * from Book as b where b.bookname like ? "

        try {
            //检查是否已经存在用户的购物车,若存在则继续查询，否则直接返回null
            val conn = ConnectionSqlServer.getConnection("BookSalesdb")
            conn?.prepareStatement(queryBookByNameSql).use { stmt ->
                stmt?.setString(1,  "%$bookName%")
                val resultSet = stmt?.executeQuery()

                val bookList = ArrayList<Book>()

                while (resultSet?.next() == true) { // 表示有结果

                    val dateString = resultSet.getString("publication_date")
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val publicationDate = LocalDate.parse(dateString, formatter)

                    val book =
                        Book(
                            bookId=resultSet.getInt("book_id"),
                            bookName = resultSet.getString("bookname"),
                            publisher=resultSet.getString("publisher"),
                            price =resultSet.getBigDecimal("price"),
                            author = resultSet.getString("author"),
                            category = resultSet.getString("category"),
                            stock = resultSet.getInt("stock"),
                            publicationDate = publicationDate,
                            bookInfo = resultSet.getString("bookinfo"),
                            isbn = resultSet.getString("isbn"),
                            creationTime = resultSet.getTimestamp("creationtime")
                        )
                    bookList.add(book)
                }
                if (bookList.size != 0) {
                    return bookList
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