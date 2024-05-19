package com.example.booksalesmanagement.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.booksalesmanagement.bean.SC
import com.example.booksalesmanagement.database.ConnectionSqlServer

/*
MutableLiveData是一种可变的LiveData，它的用法很简单，主要有3种读写数据的方法，分别是
getValue()、setValue()和postValue()方法。
    getValue()方法用于获取LiveData中包含的数据；
    setValue()方法用于给LiveData设置数据，但是只能在主线程中调用；
    postValue()方法用于在非主线程中给LiveData设置数据
 */

/*
get() 方法：在 Kotlin 中，如果你定义了一个属性，你可以使用 get() 方法来获取它的值。
当你访问 counter 属性时，实际上是调用了 get() 方法。在这个例子中，get() 方法返回了 _counter 属性。
* */

class HomeViewModel : ViewModel() {
/*    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    val text: LiveData<String> = _text*/

    val msgLiveData: LiveData<String>
        get() = _msgLiveData

    private val _msgLiveData = MutableLiveData<String>()

    fun fetchDataFromServer() {
        val sb = StringBuilder()
        var SC = ArrayList<SC>()
        try {
            ConnectionSqlServer.getConnection("lp")
            SC = ConnectionSqlServer.querySCMsg()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        for (sc in SC) {
            sb.append("学号：" + sc.Sno)
            sb.append("课程号：" + sc.Cno)
            sb.append("成绩：" + sc.Score)
            sb.append("\n")
        }

        //postValue()方法用于在非主线程中给LiveData设置数据
        _msgLiveData.postValue(sb.toString())
    }

}