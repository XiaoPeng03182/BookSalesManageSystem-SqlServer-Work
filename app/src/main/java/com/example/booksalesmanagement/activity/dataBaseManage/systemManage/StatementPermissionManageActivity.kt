package com.example.booksalesmanagement.activity.dataBaseManage.systemManage

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.dao.UsersDBPermissionsDao
import com.example.booksalesmanagement.databinding.ActivityStatementPermissionManageBinding
import kotlin.concurrent.thread

class StatementPermissionManageActivity : AppCompatActivity() {
    private var _binding: ActivityStatementPermissionManageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityStatementPermissionManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //授予所有语句级权限（select、update、delete、select）
        binding.btnGrantAllSatePermission.setOnClickListener {
            grantAllSatePermission()
        }
        //撤销所有语句级权限（select、update、delete、select）
        binding.btnRevokeAllSatePermission.setOnClickListener {
            revokeAllSatePermission()
        }
        //授予指定的语句级权限
        binding.btnGrantSpecificStatPermissions.setOnClickListener {
            grantSpecificStatPermissions()
        }
        //撤销指定的语句级权限
        binding.btnRevokeSpecificStatPermissions.setOnClickListener {
            revokeSpecificStatPermissions()
        }
    }

    //撤销指定的语句级权限
    private fun revokeSpecificStatPermissions() {
        // 创建对话框
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.dialog_grant_revoke_state_permission, null)

        val ckSelect = dialogView.findViewById<CheckBox>(R.id.ck_grant_select)
        val ckUpdate = dialogView.findViewById<CheckBox>(R.id.ck_grant_update)
        val ckDelete = dialogView.findViewById<CheckBox>(R.id.ck_grant_delete)
        val ckInsert = dialogView.findViewById<CheckBox>(R.id.ck_grant_insert)

        val dialog = AlertDialog.Builder(this)
            .setTitle("选择撤销权限")
            .setView(dialogView)
            .setPositiveButton("确定") { _, _ ->
                // 获取选中的权限
                val selectPermissions = mutableListOf<String>()
                if (ckSelect.isChecked) {
                    selectPermissions.add("SELECT")
                }
                if (ckUpdate.isChecked) {
                    selectPermissions.add("UPDATE")
                }
                if (ckDelete.isChecked) {
                    selectPermissions.add("DELETE")
                }
                if (ckInsert.isChecked) {
                    selectPermissions.add("INSERT")
                }
                // 授予选中的权限
                if (selectPermissions.isNotEmpty()) {
                    thread {
                        UsersDBPermissionsDao.revokeSpecialStatePermissionToUser(
                            this,
                            selectPermissions
                        )
                    }
                } else {
                    Toast.makeText(this, "请至少选择一个权限", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .create()
        dialog.show()
    }

    @SuppressLint("MissingInflatedId")
    private fun grantSpecificStatPermissions() {
        // 创建对话框
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.dialog_grant_revoke_state_permission, null)

        val ckSelect = dialogView.findViewById<CheckBox>(R.id.ck_grant_select)
        val ckUpdate = dialogView.findViewById<CheckBox>(R.id.ck_grant_update)
        val ckDelete = dialogView.findViewById<CheckBox>(R.id.ck_grant_delete)
        val ckInsert = dialogView.findViewById<CheckBox>(R.id.ck_grant_insert)

        val dialog = AlertDialog.Builder(this)
            .setTitle("选择授予权限")
            .setView(dialogView)
            .setPositiveButton("确定") { _, _ ->
                // 获取选中的权限
                val selectPermissions = mutableListOf<String>()
                if (ckSelect.isChecked) {
                    selectPermissions.add("SELECT")
                }
                if (ckUpdate.isChecked) {
                    selectPermissions.add("UPDATE")
                }
                if (ckDelete.isChecked) {
                    selectPermissions.add("DELETE")
                }
                if (ckInsert.isChecked) {
                    selectPermissions.add("INSERT")
                }
                // 授予选中的权限
                if (selectPermissions.isNotEmpty()) {
                    thread {
                        UsersDBPermissionsDao.gantSpecialStatePermissionToUser(
                            this,
                            selectPermissions
                        )
                    }
                } else {
                    Toast.makeText(this, "请至少选择一个权限", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .create()
        dialog.show()
    }

    private fun revokeAllSatePermission() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("撤销所有语句级权限")
            .setPositiveButton("确定") { _, _ ->
                thread {
                    UsersDBPermissionsDao.revokeALLStatePermissionsToUser(this)
                }
            }
            .setNegativeButton("取消", null)
            .create()
        dialog.show()
    }

    private fun grantAllSatePermission() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("授予所有语句级权限")
            .setPositiveButton("确定") { _, _ ->
                thread {
                    UsersDBPermissionsDao.grantALLStatePermissionsToUser(this)
                }
            }
            .setNegativeButton("取消", null)
            .create()
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}