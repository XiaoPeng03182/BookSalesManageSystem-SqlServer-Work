package com.example.booksalesmanagement.fragment.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.adapter.BookCoverItemBean
import com.example.booksalesmanagement.adapter.bookcover.BookCoverAdapter
import com.example.booksalesmanagement.bean.SC
import com.example.booksalesmanagement.database.ConnectionSqlServer
import com.example.booksalesmanagement.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import java.lang.StringBuilder
import kotlin.concurrent.thread

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    val books = mutableListOf(
        BookCoverItemBean("Apple", R.drawable.apple, 90.0), BookCoverItemBean(
            "Banana",
            R.drawable.banana, 99.9
        ), BookCoverItemBean("Orange", R.drawable.orange, 85.1), BookCoverItemBean(
            "Watermelon",
            R.drawable.watermelon, 122.1
        ), BookCoverItemBean("Pear", R.drawable.pear, 85.1), BookCoverItemBean(
            "Grape",
            R.drawable.grape, 122.1
        ), BookCoverItemBean("Pineapple", R.drawable.pineapple, 85.1), BookCoverItemBean(
            "Strawberry",
            R.drawable.strawberry, 122.1
        ), BookCoverItemBean("Cherry", R.drawable.cherry, 85.1), BookCoverItemBean(
            "Mango",
            R.drawable.mango, 122.1
        )
    )

    val bookList = ArrayList<BookCoverItemBean>()

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //如果VieModel中变量的值发生变化，会触发观察者中的方法，从而更新UI界面
        homeViewModel.msgLiveData.observe(viewLifecycleOwner) {
            //binding.tvShowMsg.text = it
        }

        /*
        启用 Fragment 的选项菜单：在 onCreate 或 onCreateView 中调用 setHasOptionsMenu(true)。
         */
        // 告知 Fragment 它有选项菜单
        setHasOptionsMenu(true)

        //setSupportActionBar(binding.toolbar)
        // 设置 Toolbar
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.toolbar)

        /*        binding.btnGetMsg.setOnClickListener {
                    //getMsgFromSqlServer()
                    homeViewModel.fetchDataFromServer()
                }*/

        //初始化图书信息
        initBooks()

        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.layoutManager = layoutManager
        val adapter = BookCoverAdapter(requireContext(), bookList)
        binding.recyclerView.adapter = adapter

        //设置toolbar上的呼出左侧drawerLayout的导航图标
        (activity as? AppCompatActivity)?.supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        //设置drawerLayout的副页的菜单选项监听
        binding.navView.setCheckedItem(R.id.navCall)  //设置默认选中
        binding.navView.setNavigationItemSelectedListener {item->
            when (item.itemId) {
                R.id.navCall -> {
                    Toast.makeText(requireContext(), "navCall", Toast.LENGTH_SHORT).show()
                    // Handle the home action
                }

                R.id.navFriends -> {
                    Toast.makeText(requireContext(), "navFriends", Toast.LENGTH_SHORT).show()
                    // Handle the backup action
                }

                R.id.navLocation -> {
                    Toast.makeText(requireContext(), "navLocation", Toast.LENGTH_SHORT).show()
                    // Handle the delete action
                }

                R.id.navMail -> {
                    Toast.makeText(requireContext(), "navMail", Toast.LENGTH_SHORT).show()
                    // Handle the settings action
                }

                R.id.navTask -> {
                    Toast.makeText(requireContext(), "navTask", Toast.LENGTH_SHORT).show()
                    // Handle the help action
                }

                else -> {
                    // Handle other cases if needed
                }
            }


            binding.drawLayout.closeDrawers()  //关闭drawerLayout的副页
            true
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
            refreshBooks(adapter)
        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun refreshBooks(adapter: BookCoverAdapter) {
        thread {
            Thread.sleep(500)
            activity?.runOnUiThread { //进入主线程修改视图；
                initBooks()
                adapter.notifyDataSetChanged()
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun initBooks() {
        bookList.clear()
        repeat(50) {
            val index = (0 until books.size).random()
            bookList.add(books[index])
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
                Toast.makeText(requireContext(), "You clicked home", Toast.LENGTH_SHORT).show()
                binding.drawLayout.openDrawer(GravityCompat.START)
                return true
            }

            R.id.backup -> {
                Toast.makeText(requireContext(), "You clicked Backup", Toast.LENGTH_SHORT).show()
                return true
            }

            R.id.delete -> {
                Toast.makeText(requireContext(), "You clicked Delete", Toast.LENGTH_SHORT).show()
                return true
            }

            R.id.settings -> {
                Toast.makeText(requireContext(), "You clicked Settings", Toast.LENGTH_SHORT).show()
                return true
            }
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
}