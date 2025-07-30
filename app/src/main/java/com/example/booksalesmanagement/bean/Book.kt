package com.example.booksalesmanagement.bean

import java.io.Serializable
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDate


/** Bean for book
 *
 * Primary Key：bookId、publisherId、author
 * @param bookId 书籍id
 * @param bookName 书籍名称
 * @param publisher 出版社名称
 * @param price 书籍价格
 * @param author 作者
 * @param category 书籍类型
 * @param stock 库存
 * @param publicationDate 出版日期
 * @param bookInfo 书籍简介
 * @param isbn 书籍ISBN,ISBN代表国际标准书号，是一种13位代码,用于唯一标识图书或独立的出版物
 * @param creationTime 创建时间
 */


data class
Book(
    var bookId: Int,
    var bookName: String,
    var publisher: String,
    var price: BigDecimal,
    var author: String,
    var category: String,
    var stock: Int,
    var publicationDate: LocalDate,
    var bookInfo: String,
    var isbn: String,
    var creationTime: Timestamp? = null
) : Serializable
