package com.example.booksalesmanagement.bean

import java.io.Serializable
import java.time.LocalDate

/**
 *  书评实体类
 *  @param rating 评分
 *  @param comment 评论
 *  @param userId 用户id
 *  @param bookId 书籍id
 *  @param createdAt 评论时间
 *  @param userName 用户名
 */

data class Reviews(
    var rating : Int,
    var comment:String,
    var userId:Int,
    var bookId:Int,
    var createdAt:LocalDate = LocalDate.of(2003, 10, 24),
    var userName :String = ""
) :Serializable
