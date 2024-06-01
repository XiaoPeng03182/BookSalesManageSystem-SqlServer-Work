package com.example.booksalesmanagement.activity.dataBaseManage.systemManage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.dao.UsersDBPermissionsDao
import com.example.booksalesmanagement.databinding.ActivityObjectPermissionManageBinding
import kotlin.concurrent.thread

class ObjectPermissionManageActivity : AppCompatActivity() {
    private var _binding: ActivityObjectPermissionManageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityObjectPermissionManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Book表对象级访问权限管理
        binding.btnTableBookObjectPermissionManage.setOnClickListener {
            tableObjectPermissionByDialog("Book")
        }
        //购物车表对象级访问权限管理
        binding.btnTableCartObjectPermissionManage.setOnClickListener {
            tableObjectListPermissionByDialog(arrayListOf("CartDetails", "Book", "Users","Cart"))
        }
        //订单表对象级访问权限管理
        binding.btnTableOrderObjectPermissionManage.setOnClickListener {
            tableObjectListPermissionByDialog(arrayListOf("Book", "OrderDetails", "Users","Orders"))
        }
        //User表对象级访问权限管理
        binding.btnTableUserObjectPermissionManage.setOnClickListener {
            tableObjectPermissionByDialog("Users")
        }

    }


    private fun tableObjectPermissionByDialog(tableName: String) {
        // 创建对话框
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.dialog_grant_revoke_object_permission, null)

        val ckSelect = dialogView.findViewById<CheckBox>(R.id.ck_grant_select)
        val ckUpdate = dialogView.findViewById<CheckBox>(R.id.ck_grant_update)
        val ckDelete = dialogView.findViewById<CheckBox>(R.id.ck_grant_delete)
        val ckInsert = dialogView.findViewById<CheckBox>(R.id.ck_grant_insert)

        // 获取RadioGroup实例
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroup)

        var selectGrantOrRevoke = 0
        // 设置单选框选择监听器
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            // 根据选中的单选框做出相应的操作
            when (checkedId) {
                R.id.rb_grant -> {  // 选中了授予权限单选框
                    selectGrantOrRevoke = 1
                }

                R.id.rb_revoke -> { // 选中了撤销权限单选框
                    selectGrantOrRevoke = -1
                }
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("选择授予权限")
            .setView(dialogView)
            .setPositiveButton("确定") { _, _ ->
                if (selectGrantOrRevoke == 0) {
                    Toast.makeText(this, "请选择授予权限或撤销权限", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

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
                    if (selectGrantOrRevoke == 1) {
                        thread {
                            UsersDBPermissionsDao.grantObjectPermission(
                                this,
                                tableName,
                                selectPermissions
                            )
                        }
                    } else if (selectGrantOrRevoke == -1) {
                        thread {
                            UsersDBPermissionsDao.revokeObjectPermission(
                                this,
                                tableName,
                                selectPermissions
                            )
                        }
                    }
                } else {
                    Toast.makeText(this, "请至少选择一个权限", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
            }
            .setNegativeButton("取消", null)
            .create()
        dialog.show()
    }

    private fun tableObjectListPermissionByDialog(tableNameList: ArrayList<String>) {
        // 创建对话框
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.dialog_grant_revoke_object_permission, null)

        val ckSelect = dialogView.findViewById<CheckBox>(R.id.ck_grant_select)
        val ckUpdate = dialogView.findViewById<CheckBox>(R.id.ck_grant_update)
        val ckDelete = dialogView.findViewById<CheckBox>(R.id.ck_grant_delete)
        val ckInsert = dialogView.findViewById<CheckBox>(R.id.ck_grant_insert)

        // 获取RadioGroup实例
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroup)

        var selectGrantOrRevoke = 0
        // 设置单选框选择监听器
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            // 根据选中的单选框做出相应的操作
            when (checkedId) {
                R.id.rb_grant -> {  // 选中了授予权限单选框
                    selectGrantOrRevoke = 1
                }

                R.id.rb_revoke -> { // 选中了撤销权限单选框
                    selectGrantOrRevoke = -1
                }
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("选择授予权限")
            .setView(dialogView)
            .setPositiveButton("确定") { _, _ ->
                if (selectGrantOrRevoke == 0) {
                    Toast.makeText(this, "请选择授予权限或撤销权限", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

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
                    for (tableName in tableNameList) {
                        if (selectGrantOrRevoke == 1) {
                            thread {
                                UsersDBPermissionsDao.grantObjectPermission(
                                    this,
                                    tableName,
                                    selectPermissions
                                )
                            }

                        } else if (selectGrantOrRevoke == -1) {
                            thread {
                                UsersDBPermissionsDao.revokeObjectPermission(
                                    this,
                                    tableName,
                                    selectPermissions
                                )
                            }
                        }
                    }

                } else {
                    Toast.makeText(this, "请至少选择一个权限", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
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