package com.example.booksalesmanagement.fragment.home.adapter

import android.graphics.Bitmap

object BookListImageBitmap {
    private val bookImageBitmap:HashMap<String,Bitmap> = HashMap()

    fun getBookListImageBitmap(): HashMap<String, Bitmap> {
        return bookImageBitmap
    }
    // 添加书籍封面图像
    fun addBookImage(key: String, image: Bitmap) {
        bookImageBitmap[key] = image
    }

    // 获取书籍封面图像
    fun getBookImage(key: String): Bitmap? {
        return bookImageBitmap[key]
    }


    // 删除书籍封面图像
    fun removeBookImage(key: String) {
        bookImageBitmap.remove(key)
    }
}