package com.example.booksalesmanagement.activity.dataBaseManage.systemManage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.dao.BackupRecoveryDBDao
import com.example.booksalesmanagement.databinding.ActivityDataBackupAndRecoveryBinding
import com.example.booksalesmanagement.databinding.ActivityStatementPermissionManageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataBackupAndRecoveryActivity : AppCompatActivity() {
    private var _binding: ActivityDataBackupAndRecoveryBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDataBackupAndRecoveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //数据备份
        binding.btnDataBackup.setOnClickListener {
            performDatabaseBackUp()
        }

        //数据恢复
        binding.btnDataRecovery.setOnClickListener {
            performDatabaseRecovery()
        }
    }


    private fun performDatabaseBackUp() {
        CoroutineScope(Dispatchers.IO).launch {
            val success = BackupRecoveryDBDao.backupDB()
            // 备份完成后更新UI
            runOnUiThread {
                if (success) {
                    Toast.makeText(this@DataBackupAndRecoveryActivity, "备份完成！", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@DataBackupAndRecoveryActivity, "备份失败！", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun performDatabaseRecovery() {
        CoroutineScope(Dispatchers.IO).launch {
            val success = BackupRecoveryDBDao.recoveryDB()
            // 恢复完成后更新UI
            runOnUiThread {
                if (success) {
                    Toast.makeText(this@DataBackupAndRecoveryActivity, "恢复完成！", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@DataBackupAndRecoveryActivity, "恢复失败！", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}