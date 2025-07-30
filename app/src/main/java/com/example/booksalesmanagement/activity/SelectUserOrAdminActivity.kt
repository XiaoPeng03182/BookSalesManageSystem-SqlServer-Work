package com.example.booksalesmanagement.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.booksalesmanagement.activity.register_login.LoginActivity
import com.example.booksalesmanagement.databinding.ActivitySelectUserOrAdminBinding
import com.example.booksalesmanagement.utils.SelectUserOrManager

class SelectUserOrAdminActivity : AppCompatActivity() {
    private var  _binding :ActivitySelectUserOrAdminBinding? =  null
        private val  binding  get() = _binding!!

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            _binding= ActivitySelectUserOrAdminBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.btnSelectUser.setOnClickListener {
                SelectUserOrManager.isSelectUser = true
                startActivity(Intent(this,LoginActivity::class.java))
            }

            binding.btnSelectManager.setOnClickListener {
                SelectUserOrManager.isSelectUser = false
                startActivity(Intent(this,LoginActivity::class.java))
            }

        }

        override fun onDestroy() {
            super.onDestroy()
            _binding = null
        }
}