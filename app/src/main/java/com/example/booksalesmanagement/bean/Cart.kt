package com.example.booksalesmanagement.bean

import java.time.LocalDate

/**
 *  购物车实体类
 *  @param cartId 购物车id
 *  @param userId 用户id
 *  @param createdAt 创建时间
 */
data class Cart(
    var cartId :Int = 0,
    var userId :Int,
    var createdAt: LocalDate = LocalDate.of(2003, 10, 24)
)

