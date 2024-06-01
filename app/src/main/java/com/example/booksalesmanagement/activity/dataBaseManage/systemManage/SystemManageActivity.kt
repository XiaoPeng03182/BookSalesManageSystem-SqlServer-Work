package com.example.booksalesmanagement.activity.dataBaseManage.systemManage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.booksalesmanagement.databinding.ActivitySystemManageBinding

class SystemManageActivity : AppCompatActivity() {
    private var _binding: ActivitySystemManageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySystemManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //语句级权限管理
        binding.btnStatementLevelPermissionManagement.setOnClickListener {
            startActivity(Intent(this, StatementPermissionManageActivity::class.java))
        }
        //对象及权限管理
        binding.btnObjectLevelPermissionManagement.setOnClickListener {
            startActivity(Intent(this, ObjectPermissionManageActivity::class.java))
        }
        //数据备份和恢复
        binding.btnDataBackupAndRecovery.setOnClickListener {
            startActivity(Intent(this, DataBackupAndRecoveryActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}