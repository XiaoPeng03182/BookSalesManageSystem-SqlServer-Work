package com.example.booksalesmanagement.activity.register_login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.booksalesmanagement.bean.Administrator
import com.example.booksalesmanagement.bean.User
import com.example.booksalesmanagement.dao.AdminDao
import com.example.booksalesmanagement.dao.AdminVerifyCodeDao
import com.example.booksalesmanagement.dao.UserDao
import com.example.booksalesmanagement.databinding.ActivityRegistrationBinding
import com.example.booksalesmanagement.utils.SelectUserOrManager
import java.util.Random
import kotlin.concurrent.thread

class RegistrationActivity : AppCompatActivity() {
    private var _binding: ActivityRegistrationBinding? = null
    private val binding get() = _binding!!
    private var mVerifyCode: String = "-1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //如果选择的是管理员，则要选择不同的注册界面展示
        if (!SelectUserOrManager.isSelectUser) {
            binding.llManagerVerifyCode.visibility = View.VISIBLE
        } else {
            binding.llManagerVerifyCode.visibility = View.GONE
        }

        //返回按钮
        binding.ibRigisterBack.setOnClickListener {
            finish()
        }

        //注册
        binding.btnRegister.setOnClickListener {
            register()
        }

        //获取验证码
        binding.btnGetverificationcode.setOnClickListener {
            // 点击了“获取验证码”按钮
            // 生成六位随机数字的验证码
            mVerifyCode = String.format("%04d", Random().nextInt(9999))
            // 以下弹出提醒对话框，提示用户记住六位验证码数字
            val buider = AlertDialog.Builder(this)
            buider.setTitle("请记住验证码")
            buider.setMessage("本次验证码是\n$mVerifyCode")
            buider.setPositiveButton("好的", null)
            val dialog = buider.create()
            dialog.show()
        }

    }

    private fun register() {
        val userNameInput = binding.etUsername.text.toString()
        val passWordInput = binding.etUserpassword.text.toString()
        val verifyPasswordInput = binding.etUserpwdagain.text.toString()
        val inputVerifyCode = binding.etVerificationcode.text.toString()

        var inputAdminVerifyCode = ""

        if (!SelectUserOrManager.isSelectUser) {
            inputAdminVerifyCode = binding.etManagerVerifyCode.text.toString()
        }

        if (userNameInput == "" || passWordInput == "") {
            Toast.makeText(this, "用户名或密码不能为空！", Toast.LENGTH_SHORT).show()
            return
        }

        if (verifyPasswordInput != passWordInput) {
            Toast.makeText(this, "两次输入的密码不一致！", Toast.LENGTH_SHORT).show()
            return
        }

        if (!SelectUserOrManager.isSelectUser && inputAdminVerifyCode == "") {
            Toast.makeText(this, "管理员权限密码不能为空！", Toast.LENGTH_SHORT).show()
            return
        }

        thread {
            if (AdminVerifyCodeDao.queryAdminVerifyCode() != "") {
                if (inputAdminVerifyCode != AdminVerifyCodeDao.queryAdminVerifyCode()) {
                    runOnUiThread {
                        Toast.makeText(this, "管理员权限密码错误！", Toast.LENGTH_SHORT).show()
                        return@runOnUiThread
                    }
                    return@thread
                }
            }

            if (inputVerifyCode != mVerifyCode || inputVerifyCode == "") {
                runOnUiThread {
                    Toast.makeText(this, "验证码错误！", Toast.LENGTH_SHORT).show()
                    return@runOnUiThread
                }
                return@thread
            }
            //判断待注册的用户名是否已存在
            val registrationNameIsExist =
                UserDao.queryRegisterNameIsExist(userNameInput, SelectUserOrManager.isSelectUser)
            var isRegisteredSuccess = false
            if (!registrationNameIsExist) {
                if (SelectUserOrManager.isSelectUser) {
                    val user = User(userNameInput, passWordInput)
                    isRegisteredSuccess = UserDao.insertUserToSQlServer(user)
                } else {
                    val admin = Administrator(userNameInput, passWordInput)
                    isRegisteredSuccess = AdminDao.insertAdminToSQlServer(admin)
                }
            } else { //用户名已存在
                runOnUiThread {
                    Toast.makeText(this, "该用户名已存在\n请重新选择用户名！", Toast.LENGTH_SHORT)
                        .show()
                    return@runOnUiThread
                }
                return@thread
            }
            if (isRegisteredSuccess) {
                runOnUiThread {
                    Toast.makeText(this, "注册成功！", Toast.LENGTH_SHORT).show()
                    return@runOnUiThread
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this, "注册失败！", Toast.LENGTH_SHORT).show()
                    return@runOnUiThread
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}