package com.example.booksalesmanagement.activity.register_login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.booksalesmanagement.BottomNavigationViewActivity
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.bean.Administrator
import com.example.booksalesmanagement.bean.User
import com.example.booksalesmanagement.dao.AdminDao
import com.example.booksalesmanagement.dao.UserDao
import com.example.booksalesmanagement.databinding.ActivityLoginBinding
import com.example.booksalesmanagement.utils.SelectUserOrManager
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            login()

        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    private fun login() {
        val inputName = binding.etUsername.text.toString()
        val inputPassWord = binding.etPassword.text.toString()
        if (inputName.isEmpty() || inputPassWord.isEmpty()) {
            Toast.makeText(this,"用户名或密码不能为空！",Toast.LENGTH_SHORT).show()
            return
        }

        thread {
            if (SelectUserOrManager.isSelectUser) {
                val user = User(inputName,inputPassWord)
                if (UserDao.queryUserFromSQLServer(user)!=null) {
                    runOnUiThread {
                        Toast.makeText(this,"登录成功！",Toast.LENGTH_SHORT).show()
                    }
                    startActivity(Intent(this, BottomNavigationViewActivity::class.java)
                        .apply {//在这里可以对对象进行操作,操作完毕后，返回对象本身
                            // 设置 FLAG_ACTIVITY_CLEAR_TASK 标志位，清除任务栈
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            /*在 Intent 中同时设置了这两个标志位时，系统会先创建一个新的任务栈，并清除该任务栈中的所有 Activity，
                            然后将目标 Activity 放入该任务栈的栈顶，从而创建一个全新的任务栈。这种组合通常用于实现“清除并启动”新任务栈的操作，
                            例如在用户注销或切换用户时清除所有之前的 Activity，并启动一个全新的登录界面*/
                        }
                    )
                }else {
                    runOnUiThread {
                        Toast.makeText(this,"登录失败,该用户不存在！",Toast.LENGTH_SHORT).show()
                    }
                    return@thread
                }
            }else {
                val admin = Administrator(inputName,inputPassWord)
                if (AdminDao.queryAdminFromSQLServer(admin)!=null) {
                    runOnUiThread {
                        Toast.makeText(this,"登录成功！",Toast.LENGTH_SHORT).show()
                    }
                    startActivity(Intent(this, BottomNavigationViewActivity::class.java)
                        .apply {//在这里可以对对象进行操作,操作完毕后，返回对象本身
                            // 设置 FLAG_ACTIVITY_CLEAR_TASK 标志位，清除任务栈
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            /*在 Intent 中同时设置了这两个标志位时，系统会先创建一个新的任务栈，并清除该任务栈中的所有 Activity，
                            然后将目标 Activity 放入该任务栈的栈顶，从而创建一个全新的任务栈。这种组合通常用于实现“清除并启动”新任务栈的操作，
                            例如在用户注销或切换用户时清除所有之前的 Activity，并启动一个全新的登录界面*/
                        }
                    )
                }else {
                    runOnUiThread {
                        Toast.makeText(this,"登录失败,该用户不存在！",Toast.LENGTH_SHORT).show()
                    }
                    return@thread
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}