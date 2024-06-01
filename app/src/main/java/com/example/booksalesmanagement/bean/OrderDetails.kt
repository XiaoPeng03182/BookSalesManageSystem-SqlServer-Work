package com.example.booksalesmanagement.bean

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * @param orderId 订单ID
 * @param userId 用户ID
 * @param bookId 书籍ID
 * @param quantity 数量
 * @param price 价格
 * @param totalAmount 总价
 * @param publisher 出版社
 * @param bookName 书名
 * @param author 作者
 * @param address 地址
 * @param phoneNumber 电话号码
 * @param orderDate 订单时间
 */
data class OrderDetails(
    var userName:String = "",
    var orderDetailId: Int = 0,
    var orderId: Int = 0,
    var userId:Int = 0,
    var bookId:Int = 0,
    var quantity:Int,
    var price: BigDecimal,
    var totalAmount:Double,
    var publisher:String,
    var bookName: String,
    var author:String,
    var address:String,
    var phoneNumber: String,
    var orderDate: LocalDateTime = LocalDateTime.of(2003, 10, 24, 0, 0, 0)
)
