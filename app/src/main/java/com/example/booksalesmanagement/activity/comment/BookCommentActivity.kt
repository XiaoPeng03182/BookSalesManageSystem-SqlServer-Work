package com.example.booksalesmanagement.activity.comment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.databinding.ActivityBookCommentBinding

class BookCommentActivity : AppCompatActivity() {
    private var  _binding :ActivityBookCommentBinding? =  null
        private val  binding  get() = _binding!!

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            _binding= ActivityBookCommentBinding.inflate(layoutInflater)
            setContentView(binding.root)
        }

        override fun onDestroy() {
            super.onDestroy()
            _binding = null
        }
}