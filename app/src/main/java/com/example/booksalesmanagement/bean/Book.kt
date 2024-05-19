package com.example.booksalesmanagement.bean

import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDateTime


/** Bean for book
 *
 * Primary Key：bookId、publisherId、author
 * @param bookId 书籍id
 * @param bookName 书籍名称
 * @param publisherId 出版社id
 * @param price 书籍价格
 * @param author 作者
 * @param bookType 书籍类型
 * @param inventory 库存
 * @param publicationDate 出版日期
 */

data class Book(
    var bookId: Int,
    var bookName: String,
    var publisherId: Int,
    var price: BigDecimal,
    var author: String,
    // var bookType: String,
    var inventory: Int,
    var publicationDate: Timestamp
)
