package com.example.booksalesmanagement.activity.dataBaseManage.bookMsgManage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.booksalesmanagement.activity.dataBaseManage.bookMsgManage.deletebook.DeleteBookActivity
import com.example.booksalesmanagement.activity.dataBaseManage.bookMsgManage.insertbook.InsertBookMsgActivity
import com.example.booksalesmanagement.activity.dataBaseManage.bookMsgManage.searchbook.SearchBookMsgActivity
import com.example.booksalesmanagement.activity.dataBaseManage.bookMsgManage.updatebook.UpdateBookMsgActivity
import com.example.booksalesmanagement.databinding.ActivityBookMsgManageBinding

class BookMsgManageActivity : AppCompatActivity() {
    private var  _binding :ActivityBookMsgManageBinding? =  null
        private val  binding  get() = _binding!!

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            _binding= ActivityBookMsgManageBinding.inflate(layoutInflater)
            setContentView(binding.root)

            //录入图书信息
            binding.btnAddBook.setOnClickListener {
                startActivity(Intent(this, InsertBookMsgActivity::class.java))
            }

            //查询图书信息
            binding.btnSearchBook.setOnClickListener {
                startActivity(Intent(this, SearchBookMsgActivity::class.java))
            }

            //修改图书信息
            binding.btnUpdateBook.setOnClickListener {
                startActivity(Intent(this, UpdateBookMsgActivity::class.java))
            }

            //删除图书信息
            binding.btnDeleteBook.setOnClickListener {
                startActivity(Intent(this, DeleteBookActivity::class.java))
            }


        }

        override fun onDestroy() {
            super.onDestroy()
            _binding = null
        }
}