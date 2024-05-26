package com.example.booksalesmanagement.bean

import java.io.Serializable
import java.time.LocalDate


/**
 *  Admin 管理员类
 * @param adminName 管理员姓名
 * @param passWord 管理员密码
 * @param registrationDate 注册日期
 * @param phoneNumber 管理员电话
 */
data class Administrator(
    var  adminName :String,
    var passWord :String,
    var registrationDate:LocalDate =  LocalDate.of(2003, 10, 24),
    var phoneNumber: String = ""
):Serializable