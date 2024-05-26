package com.example.booksalesmanagement.bean

import java.io.Serializable
import java.time.LocalDate

/**
 * @param userId 用户ID
 * @param userName 用户名
 * @param password 密码
 * @param registrationDate 注册日期
 * @param phoneNumber 电话号码
 * @param address 地址
 */

data class User(
    var userId: Int = 0,
    var userName: String,
    var password: String,
    var registrationDate: LocalDate =  LocalDate.of(2003, 10, 24),
    var phoneNumber: String = "",
    var address: String = "",
) : Serializable
