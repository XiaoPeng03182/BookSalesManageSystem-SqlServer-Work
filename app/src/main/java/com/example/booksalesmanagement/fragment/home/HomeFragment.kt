package com.example.booksalesmanagement.fragment.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.booksalesmanagement.BottomNavigationViewActivity
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.activity.SaveUserMsg
import com.example.booksalesmanagement.activity.register_login.LoginActivity
import com.example.booksalesmanagement.fragment.home.adapter.BookCoverAdapter
import com.example.booksalesmanagement.bean.Book
import com.example.booksalesmanagement.bean.SC
import com.example.booksalesmanagement.dao.BookDao
import com.example.booksalesmanagement.dao.UserDao
import com.example.booksalesmanagement.database.ConnectionSqlServer
import com.example.booksalesmanagement.databinding.FragmentHomeBinding
import com.example.booksalesmanagement.fragment.home.adapter.BookListImageBitmap
import com.example.booksalesmanagement.utils.ConnectAlibabaOssToImage
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import java.lang.StringBuilder
import java.util.Collections
import kotlin.concurrent.thread

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private var bookList = ArrayList<Book>()

    private var adapter: BookCoverAdapter? = null

    private val Nav_Phone_Number = "nav_Phone_Number"
    private val Nav_Location = "nav_Location"
    private val Nav_Email = "nav_Email"


    @SuppressLint("ResourceAsColor", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Log.e("HomeFragment", "setAdapter")

        //val layoutManager = GridLayoutManager(requireContext(), 2)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = layoutManager
        adapter = BookCoverAdapter(requireContext(), bookList)
        binding.recyclerView.adapter = adapter

        //从SqlServer数据库中获取数据，并获取阿里云OSS云存储中的图片
        if (bookList.size == 0) {
            initBookMsgFromSqlServer()
        }

        //如果VieModel中变量的值发生变化，会触发观察者中的方法，从而更新UI界面
        homeViewModel.msgLiveData.observe(viewLifecycleOwner) {
            //binding.tvShowMsg.text = it
        }

        //初始化侧边导航界面的信息
        initNavigationViewMsg()

        /*
        启用 Fragment 的选项菜单：在 onCreate 或 onCreateView 中调用 setHasOptionsMenu(true)。
         */
        // 告知 Fragment 它有选项菜单
        setHasOptionsMenu(true)

        //setSupportActionBar(binding.toolbar)
        // 设置 Toolbar
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)


        //设置toolbar上的呼出左侧drawerLayout的导航图标
        (activity as? AppCompatActivity)?.supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        //设置drawerLayout的副页的菜单选项监听
        binding.navView.setCheckedItem(R.id.navPhoneNumber)  //设置默认选中
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navPhoneNumber -> {
                    updateMsgByDialog(Nav_Phone_Number)
                }

                R.id.navLocation -> {
                    updateMsgByDialog(Nav_Location)
                }

                R.id.navUpdatePassword -> {
                    updatePassWordByDialog()
                }

                R.id.navMail -> {
                    updateMsgByDialog(Nav_Email)
                }

                R.id.navLogOut -> {
                    startActivity(
                        Intent(context, LoginActivity::class.java)
                            .apply {//在这里可以对对象进行操作,操作完毕后，返回对象本身
                                // 设置 FLAG_ACTIVITY_CLEAR_TASK 标志位，清除任务栈
                                flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                /*在 Intent 中同时设置了这两个标志位时，系统会先创建一个新的任务栈，并清除该任务栈中的所有 Activity，
                                然后将目标 Activity 放入该任务栈的栈顶，从而创建一个全新的任务栈。这种组合通常用于实现“清除并启动”新任务栈的操作，
                                例如在用户注销或切换用户时清除所有之前的 Activity，并启动一个全新的登录界面*/
                            }
                    )
                }

                else -> {
                    // Handle other cases if needed
                }
            }

            //binding.drawLayout.closeDrawers()  //关闭drawerLayout的副页
            true
        }

        //查询按钮监听
        binding.btnSearchBookByName.setOnClickListener {
            //binding.SearchBookLineLayout.visibility = View.GONE
            val inputBookName = binding.etSearchBookByName.text.toString()
            thread {
                val newBookList = BookDao.queryBookByName(inputBookName)
                if (newBookList?.isNotEmpty() == true) {
                    activity?.runOnUiThread {
                        Toast.makeText(context, "查询成功！", Toast.LENGTH_SHORT)
                            .show()
                        adapter!!.updateData(newBookList)
                        adapter!!.notifyDataSetChanged()
                    }
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(context, "没有匹配和书籍！", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        //取消搜索按钮
        binding.btnCancelSearch.setOnClickListener {
            binding.SearchBookLineLayout.visibility = View.GONE
        }

        binding.fab.setOnClickListener { view ->
            //Toast.makeText(this,"FAB clicked",Toast.LENGTH_SHORT).show()
            Snackbar.make(view, "Data deleted", Snackbar.LENGTH_SHORT)
                .setAction("Undo") {
                    Toast.makeText(requireContext(), "Data restored", Toast.LENGTH_SHORT).show()
                }
                .show()
        }

        binding.swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent)
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshBooks(adapter!!)
            //initBookMsgFromSqlServer()
        }

        return root
    }

    @SuppressLint("MissingInflatedId")
    private fun updatePassWordByDialog() {
        // 创建对话框
        val dialogView =
            LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_update_user_password, null)

        val etOldPassWord: EditText = dialogView.findViewById(R.id.et_oldPassWord)
        val etNewPassWord: EditText = dialogView.findViewById(R.id.et_newPassWord)
        val etConfirmPassWord: EditText = dialogView.findViewById(R.id.et_confirmPassWord)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("修改密码")
            .setView(dialogView)
            .setPositiveButton("确认修改") { _, _ ->
                val inputOldPassWord = etOldPassWord.text.toString()
                val inputNewPassWord = etNewPassWord.text.toString()
                val inputConfirmPassWord =
                    etConfirmPassWord.text.toString()

                if (inputOldPassWord.isEmpty() || inputNewPassWord.isEmpty() || inputConfirmPassWord.isEmpty()) {
                    // 处理提交的评论
                    Toast.makeText(context, "请输入完整的修改信息！", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (inputOldPassWord != SaveUserMsg.password) {
                    Toast.makeText(context, "原密码错误！", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }else {
                    thread {
                        if (UserDao.updatePassword(SaveUserMsg.userId, inputNewPassWord)) {
                            activity?.runOnUiThread {
                                Toast.makeText(context,"密码修改成功！", Toast.LENGTH_SHORT).show()
                            }
                        }else {
                            activity?.runOnUiThread {
                                Toast.makeText(context,"密码修改失败！", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            .setNegativeButton("取消", null)
            .create()
        dialog.show()
    }

    private fun initNavigationViewMsg() {
        thread {
            val getUser = UserDao.queryUserAllMsg(SaveUserMsg.userId)
            val userName = getUser?.userName
            val email = getUser?.email
            /*            val phoneNumber = getUser?.phoneNumber
                        val address = getUser?.address*/

            // 将UI更新移到主线程
            activity?.runOnUiThread {
                val navView: NavigationView = binding.navView
                val headerView: View = navView.getHeaderView(0)
                val tvUserName: TextView = headerView.findViewById(R.id.tv_navUsername)
                val tvUserEmail: TextView = headerView.findViewById(R.id.tv_navEmail)
                tvUserName.text = userName
                tvUserEmail.text = email
            }
        }
    }


    //弹出输入框让用户输入修改内容
    private fun updateMsgByDialog(type: String) {
        // 弹出对话框，输入评论内容
        // 创建对话框
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_update_user_msg, null)

        val tvOldMsg = dialogView.findViewById<TextView>(R.id.tv_oldMsg)
        val tvOldMsgHint = dialogView.findViewById<TextView>(R.id.tv_oldMsgHint)
        val tvNewMsg = dialogView.findViewById<TextView>(R.id.tv_newMsg)
        val tvNewMsgHint =
            dialogView.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.tv_newMsgHint)
        val etNewUserMsg = dialogView.findViewById<EditText>(R.id.et_newUserMsg)

        var selectType = -1 //1表示修改电话，2表示修改收货地址，3表示修改邮箱；

        if (type == Nav_Phone_Number) {
            selectType = 1
            tvOldMsg.text = "原联系电话"
            tvNewMsg.text = "新联系电话"
            tvNewMsgHint.hint = "请输入新联系电话"
            if (SaveUserMsg.phoneNumber.isNotEmpty()) {
                tvOldMsgHint.text = SaveUserMsg.phoneNumber
            } else {
                tvOldMsgHint.text = "无"
            }
        } else if (type == Nav_Location) {
            selectType = 2
            tvOldMsg.text = "原收获地址"
            tvNewMsg.text = "新收货地址"
            tvNewMsgHint.hint = "请输入新收货地址"
            if (SaveUserMsg.address.isNotEmpty()) {
                tvOldMsgHint.text = SaveUserMsg.address
            } else {
                tvOldMsgHint.text = "无"
            }
        } else if (type == Nav_Email) {
            selectType = 3
            tvOldMsg.text = "原邮箱"
            tvNewMsg.text = "新邮箱"
            tvNewMsgHint.hint = "请输入新邮箱"
            if (SaveUserMsg.email.isNotEmpty()) {
                tvOldMsgHint.text = SaveUserMsg.email
            } else {
                tvOldMsgHint.text = "无"
            }
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("修改信息")
            .setView(dialogView)
            .setPositiveButton("修改") { _, _ ->
                val inputNewMsg = etNewUserMsg.text.toString()

                if (inputNewMsg.isEmpty()) {
                    // 处理提交的评论
                    Toast.makeText(context, "请输入修改信息！", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                thread {
                    if (UserDao.updateUserMsg(
                            SaveUserMsg.userId,
                            selectType,
                            inputNewMsg
                        )
                    ) {
                        activity?.runOnUiThread {
                            Toast.makeText(context, "成功修改联系电话！", Toast.LENGTH_SHORT)
                                .show()
                        }

                    }
                    when (selectType) {
                        1 -> SaveUserMsg.phoneNumber = inputNewMsg
                        2 -> SaveUserMsg.address = inputNewMsg
                        3 -> SaveUserMsg.email = inputNewMsg
                    }
                }
            }
            .setNegativeButton("取消", null)
            .create()
        dialog.show()
    }

    //从SqlServer数据库中获取数据，并获取阿里云OSS云存储中的图片
    private fun initBookMsgFromSqlServer() {
        getBookAllMsg(object : BookQueryCallback {
            override fun onSuccess(bookList: ArrayList<Book>) {
                this@HomeFragment.bookList = bookList

                // 随机打乱 bookList
                this@HomeFragment.bookList.shuffle()

                Log.e("HomeFragment", "bookList.size before for : =${bookList.size}")

                // 用于跟踪已下载图片的计数器
                val totalBooks = this@HomeFragment.bookList.size
                var downloadedImages = 0

                for (book in this@HomeFragment.bookList) {
                    val bookImageFileName = "${book.bookId}_${book.bookName}.jpg"
                    ConnectAlibabaOssToImage.getImage(
                        context!!,
                        bookImageFileName,
                        object : ConnectAlibabaOssToImage.ImageCallback {
                            @SuppressLint("NotifyDataSetChanged")
                            override fun onImageLoaded(bitmap: Bitmap?) {
                                BookListImageBitmap.addBookImage(bookImageFileName, bitmap!!)
                                Log.e(
                                    "HomeFragment", "BookListImageBitmap size" +
                                            "${BookListImageBitmap.getBookListImageBitmap().size}"
                                )
                                downloadedImages++
                                checkAllImagesDownloaded(totalBooks, downloadedImages)

                            }

                            override fun onError() {
                                TODO("Not yet implemented")
                            }

                        })
                }

                Log.e("HomeFragment", "bookList.size after for : =${bookList.size}")

            }

            override fun onFailure(exception: Exception) {
                exception.printStackTrace()
                activity?.runOnUiThread {
                    Toast.makeText(context, "更新图书数据失败！", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    // 检查是否所有图片都已下载完成
    private fun checkAllImagesDownloaded(totalBooks: Int, downloadedImages: Int) {
        if (downloadedImages == totalBooks) {
            //requireActivity().runOnUiThread {
            updateBookList(this@HomeFragment.bookList)
            Log.e("HomeFragment", "onImageLoaded: 所有图片加载完成")
            //}
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateBookList(newBookList: ArrayList<Book>) {
        activity?.runOnUiThread {
            Log.e("HomeFragment", "updateBookList: ${newBookList.size}")
            //bookList.clear()
            for (book in bookList) {
                Log.e("HomeFragment update before", book.toString())
            }

            Toast.makeText(context, "获取图书数据成功！", Toast.LENGTH_SHORT).show()
            //bookList.addAll(newBookList)
            //bookList = newBookList

            for (book in bookList) {
                Log.e("HomeFragment update after", book.toString())
            }

            if (adapter == null) {
                Log.e("HomeFragment", "adapter is null")
            } else {
                Log.e("HomeFragment", "updateBookList: notifyDataSetChanged")
                adapter!!.updateData(bookList)
                adapter!!.notifyDataSetChanged()
            }
            // adapter?.notifyItemChanged(lastMsgIndex)

            //adapter?.notifyDataSetChanged()
            Log.e("HomeFragment", "updateBookList02: ${bookList.size}")
        }
    }


    private fun getBookAllMsg(callback: BookQueryCallback) {
        // 调用SQL Server数据库的代码
        val sb = StringBuilder()
        var bookList = ArrayList<Book>()
        thread {
            try {
                bookList.addAll(BookDao.queryAll())
                //获取数据库数据后执行->成功回调
                callback.onSuccess(bookList)
            } catch (e: Exception) {
                e.printStackTrace()
                callback.onFailure(e)
            }

            for (book in bookList) {
                sb.append("图书编号：" + book.bookId)
                sb.append("图书名称：" + book.bookName)
                sb.append("类别：" + book.category)
                sb.append("出版社：" + book.publisher)
                sb.append("作者：" + book.author)
                sb.append("价格：" + book.price)
                sb.append("库存：" + book.stock)
                sb.append("出版日期：" + book.publicationDate)
                sb.append("图书信息：" + book.bookInfo)
                sb.append("图书ISBN：" + book.isbn)
                sb.append("创建时间：" + book.creationTime)
                sb.append("\n")
            }

            //binding.tvShowMsg.text = Cno
            Log.e("TAG", "getMsgFromSqlServer: $sb")
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshBooks(adapter: BookCoverAdapter) {
        thread {
            Thread.sleep(500)
            activity?.runOnUiThread { //进入主线程修改视图；
                //initBooks()
                initBookMsgFromSqlServer()
                Log.e("HomeFragment", "refreshBooks")
                adapter.notifyDataSetChanged()
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }


    //onCreateOptionsMenu()方法中加载了toolbar.xml这个菜单文件
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //在onOptionsItemSelected()方法中处理各个按钮的点击事件。
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                binding.drawLayout.openDrawer(GravityCompat.START)
                return true
            }

            /*            R.id.backup -> {
                            return true
                        }*/

            R.id.searchBook -> {
                binding.SearchBookLineLayout.visibility = View.VISIBLE
                return true
            }

            /*            R.id.settings -> {
                            return true
                        }*/
        }
        return super.onOptionsItemSelected(item)
    }


    private fun getMsgFromSqlServer() {
        // 调用SQL Server数据库的代码
        val sb = StringBuilder()
        var SC = ArrayList<SC>()
        var Cno: String? = null
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

            /*            activity?.runOnUiThread {
                            binding.tvShowMsg.text = sb.toString()
                        }*/

            //binding.tvShowMsg.text = Cno
            Log.e("TAG", "getMsgFromSqlServer: " + sb.toString())
            Log.e("TAG", "getMsgFromSqlServer: " + Cno)
        }
    }

    interface BookQueryCallback {
        fun onSuccess(bookList: ArrayList<Book>)
        fun onFailure(exception: Exception)
    }
}