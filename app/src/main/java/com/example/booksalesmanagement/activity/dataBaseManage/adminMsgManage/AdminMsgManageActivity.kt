package com.example.booksalesmanagement.activity.dataBaseManage.adminMsgManage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.activity.SaveUserMsg
import com.example.booksalesmanagement.activity.dataBaseManage.adminMsgManage.deleteAdminMsg.DeleteAdminMsgActivity
import com.example.booksalesmanagement.activity.dataBaseManage.adminMsgManage.queryAdminMsg.QueryAdminMsgActivity
import com.example.booksalesmanagement.activity.dataBaseManage.adminMsgManage.updateAdminMsg.UpdateAdminMsgActivity
import com.example.booksalesmanagement.bean.Administrator
import com.example.booksalesmanagement.bean.Reviews
import com.example.booksalesmanagement.dao.AdminDao
import com.example.booksalesmanagement.dao.ReviewsDao
import com.example.booksalesmanagement.databinding.ActivityAdminMsgManageBinding
import kotlin.concurrent.thread

class AdminMsgManageActivity : AppCompatActivity() {
    private var _binding: ActivityAdminMsgManageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAdminMsgManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnOK.setOnClickListener {
            val superAdministratorPassWord = binding.etSuperAdministrator.text.toString()

            if (superAdministratorPassWord == "123456") {
                binding.constraintLayout.visibility = View.VISIBLE
                binding.linearLayout.visibility = View.GONE
                Toast.makeText(this, "身份验证成功", Toast.LENGTH_SHORT).show()
            } else {
                binding.constraintLayout.visibility = View.GONE
                binding.linearLayout.visibility = View.VISIBLE
                Toast.makeText(this, "身份验证失败", Toast.LENGTH_SHORT).show()
            }
        }

        //添加管理员信息
        binding.btnAddAdminMsg.setOnClickListener {
            addAdminMsgByDialog()
        }

        //查询管理员信息
        binding.btnSearchAdminMsg.setOnClickListener {
            startActivity(Intent(this, QueryAdminMsgActivity::class.java))
        }

        //删除管理员信息
        binding.btnDeleteAdminMsg.setOnClickListener {
            startActivity(Intent(this, DeleteAdminMsgActivity::class.java))
        }

        //修改管理员信息
        binding.btnUpdateAdminMsg.setOnClickListener {
            startActivity(Intent(this, UpdateAdminMsgActivity::class.java))
        }

    }

    private fun addAdminMsgByDialog() {
        // 创建对话框
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_admin_msg, null)
        val etName = dialogView.findViewById<EditText>(R.id.et_adminName)
        val etPassWord = dialogView.findViewById<EditText>(R.id.et_adminPassword)
        val etPhoneNum = dialogView.findViewById<EditText>(R.id.et_adminPhoneNum)

        val dialog = AlertDialog.Builder(this)
            .setTitle("添加管理员信息")
            .setView(dialogView)
            .setPositiveButton("确认添加") { _, _ ->
                val inputAdminName = etName.text.toString()
                val inputAdminPassword = etPassWord.text.toString()
                val inputAdminPhoneNum = etPhoneNum.text.toString()

                if (inputAdminName.isEmpty() || inputAdminPassword.isEmpty() || inputAdminPhoneNum.isEmpty()) {
                    Toast.makeText(this, "请输入完整的信息！", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val admin = Administrator(
                    adminName = inputAdminName,
                    passWord = inputAdminPassword,
                    phoneNumber = inputAdminPhoneNum
                )

                thread {
                    if (AdminDao.insertAdmin(admin)) {
                        runOnUiThread {
                            Toast.makeText(this, "信息添加成功！", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, "添加失败！", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
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