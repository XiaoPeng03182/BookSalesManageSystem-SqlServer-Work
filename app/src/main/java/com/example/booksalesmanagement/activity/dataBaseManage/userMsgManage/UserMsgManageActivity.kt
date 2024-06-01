package com.example.booksalesmanagement.activity.dataBaseManage.userMsgManage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.activity.dataBaseManage.userMsgManage.deleteUserMsg.DeleteUserMsgActivity
import com.example.booksalesmanagement.activity.dataBaseManage.userMsgManage.queryUserMsg.QueryUserMsgActivity
import com.example.booksalesmanagement.databinding.ActivityUserMsgManageBinding

class UserMsgManageActivity : AppCompatActivity() {
    private var _binding: ActivityUserMsgManageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUserMsgManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnQueryUserMsg.setOnClickListener {
            startActivity(Intent(this, QueryUserMsgActivity::class.java))
        }

        binding.btnDeleteUserMsg.setOnClickListener {
            startActivity(Intent(this, DeleteUserMsgActivity::class.java))
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}