package com.example.booksalesmanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.booksalesmanagement.database.ConnectionSqlServer
import com.example.booksalesmanagement.bean.SC
import com.example.booksalesmanagement.databinding.ActivityMainBinding

import java.lang.StringBuilder
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private var  _binding :ActivityMainBinding? =  null
        private val  binding  get() = _binding!!

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            _binding= ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.btnGetMsg.setOnClickListener {
               // getMsgFromSqlServer()
                startActivity(Intent(this,BottomNavigationViewActivity::class.java))
                //startActivity(Intent(this, ConnectAlibabaBucketActivity::class.java))
                //startActivity(Intent(this, InsertBookMsgActivity::class.java))
            }
        }

    private fun getMsgFromSqlServer(){
        // 调用SQL Server数据库的代码
        val sb = StringBuilder()
        var SC = ArrayList<SC>()
        var Cno : String? = null
        thread {
            try {
                ConnectionSqlServer.getConnection("lp")
                SC = ConnectionSqlServer.querySCMsg()
                //Cno = ConnectionSqlServer.queryCourseMsg()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            for (sc in SC) {
                sb.append("学号：" + sc.Sno)
                sb.append("课程号：" + sc.Cno)
                sb.append("成绩：" + sc.Score)
                sb.append("\n")
            }

            runOnUiThread {
                binding.tvShowMsg.text = sb.toString()
            }
            //binding.tvShowMsg.text = Cno
            Log.e("TAG", "getMsgFromSqlServer: " + sb.toString())
            Log.e("TAG", "getMsgFromSqlServer: " + Cno)
        }
    }


    override fun onDestroy() {
            super.onDestroy()
            _binding = null
        }


}