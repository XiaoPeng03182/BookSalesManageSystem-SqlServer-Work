package com.example.booksalesmanagement.bean

import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 购物车详情表
 * @param cardDetailId 购物车详情表id
 * @param cardId 购物车id
 * @param bookId 图书id
 * @param quantity 数量
 * @param publisher 出版社
 * @param bookName 书名
 * @param author 作者
 * @param createdAt 预购订单创建时间
 */
data class CartDetails(
    var userName :String = "",
    var cardDetailId: Int = 0,
    var cardId: Int,
    var bookId: Int,
    var quantity: Int,
    var publisher:String = "",
    var bookName:String = "",
    var author:String = "",
    var createdAt: LocalDateTime = LocalDateTime.of(2003, 10, 24, 0, 0, 0)
):Serializable
