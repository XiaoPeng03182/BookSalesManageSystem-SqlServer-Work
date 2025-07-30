package com.example.booksalesmanagement.bean

import java.io.Serializable

/**
 *  管理员权限实体类
 *  @param adminVerifyCode 管理员权限验证码
 */
data class AdminVerifyCode (
    var adminVerifyCode :String
):Serializable