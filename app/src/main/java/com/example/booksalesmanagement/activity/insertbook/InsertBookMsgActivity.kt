package com.example.booksalesmanagement.activity.insertbook

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.booksalesmanagement.bean.Book
import com.example.booksalesmanagement.dao.BookDao
import com.example.booksalesmanagement.databinding.ActivityInsertBookMsgBinding
import com.example.booksalesmanagement.utils.ConnectAlibabaOssToImage
import com.example.booksalesmanagement.utils.ImageUtils
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InsertBookMsgActivity : AppCompatActivity() {
    private var _binding: ActivityInsertBookMsgBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageUri: Uri
    private var croppedBitmap: Bitmap? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var cropImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityInsertBookMsgBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化ActivityResultLauncher来启动相册应用
        initPickImageLauncher()

        // 初始化ActivityResultLauncher来启动裁剪应用
        initCropImageLauncher()


        binding.btnInsertBookImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnCommitInsertBook.setOnClickListener {
            //添加图书信息
            commitInsertBook()
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun commitInsertBook() {
        //如果未给图书添加图片信息
        if (!(::imageUri.isInitialized)) {
            Toast.makeText(this,"请选择图片",Toast.LENGTH_SHORT).show()
            return
        }

        // 从输入框中获取数据
        val bookId = binding.etBookId.text.toString().toInt()
        val bookName = binding.etBookName.text.toString()
        val author = binding.etBookAuthor.text.toString()
        val price = BigDecimal(binding.etBookPrice.text.toString())
        val publisher = binding.etPublisher.text.toString()
        // 获取出版时间的字符串，并转换为 Timestamp 对象
        val publicationDateStr = binding.etPublisherTime.text.toString()
        val publicationDate = BookDao.parseDate(publicationDateStr)!!
        //val publicationDate = Timestamp(System.currentTimeMillis())
        val stock = binding.etInventory.text.toString().toInt()
        val bookInfo = binding.etBookInfo.text.toString()
        val isbn = binding.etIsbn.text.toString()
        val category = binding.etBookCategory.text.toString()

        // 创建 Book 对象并赋值
        val book = Book(
            bookId = bookId,
            bookName = bookName,
            publisher = publisher,
            price = price,
            author = author,
            stock = stock,
            publicationDate = publicationDate,
            bookInfo = bookInfo,
            isbn = isbn,
            category = category
        )

        /*            val imageFilePath =
                        FileUtils.saveDrawableToInternalStorage(this, R.drawable.banana, "pear.jpg")
                    if (imageFilePath != null) {
                        // 图片保存成功，filePath 是保存的文件路径
                    } else {
                        // 图片保存失败
                    }*/
        //将Uri转换为文件路径
        val imageFilePath = ImageUtils.getPathFromUri(this, imageUri)

        val bookImageName =
            "${book.bookId}_${book.bookName}.jpg"

        // 调用上传图片的方法
        ConnectAlibabaOssToImage.sendImage(
            this,
            bookImageName,
            imageFilePath,
            object : ConnectAlibabaOssToImage.UploadCallback {
                override fun onUploadSuccess() {
                    if (BookDao.insertBook(book)) {
                        runOnUiThread {
                            Toast.makeText(
                                this@InsertBookMsgActivity,
                                "图书上传并插入成功",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onUploadFailure() {
                    TODO("Not yet implemented")
                }
            })
    }


    private fun initPickImageLauncher() {
        // 初始化ActivityResultLauncher来启动相册应用
        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
                result?.let {
                    imageUri = it
                    startCropImage()
                }
            }
    }

    private fun initCropImageLauncher() {
        // 初始化ActivityResultLauncher来启动裁剪应用
        cropImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val extras = result.data!!.extras //这行代码获取了从裁剪应用返回的 Intent 中的额外数据，通常包含了裁剪后的图片数据
                    croppedBitmap = extras?.getParcelable("data")!!
                    imageUri = bitmapToUri(croppedBitmap)

                    if (croppedBitmap != null) {
                        // 处理裁剪后的 Bitmap 数据
                        // 在这里你可以将 croppedBitmap 设置到 ImageView 中进行预览等操作
                        runOnUiThread {
                            binding.btnInsertBookImage.setImageBitmap(croppedBitmap)
                        }
                    }


                    //将Uri转换为文件路径
                    val imageFilePath = ImageUtils.getPathFromUri(this, imageUri)

/*                    ConnectAlibabaOssToImage.sendImage(this, "pear", imageFilePath,
                        object : ConnectAlibabaOssToImage.UploadCallback {
                            override fun onUploadSuccess() {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@InsertBookMsgActivity,
                                        "上传成功",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onUploadFailure() {
                                TODO("Not yet implemented")
                            }
                        })*/
                }
            }
    }

    private fun bitmapToUri(bitmap: Bitmap?): Uri {
        val context = applicationContext
        val imagesDir =
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            put(MediaStore.Images.Media.DISPLAY_NAME, "image_$timeStamp.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        val imageUri = resolver.insert(imagesDir, contentValues)!!
        imageUri?.let {
            val outputStream = resolver.openOutputStream(it)
            outputStream?.use { stream ->
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            }
        }
        return imageUri
    }

    private fun startCropImage() {
        imageUri.let { uri ->
            val cropIntent = Intent("com.android.camera.action.CROP").apply {
                setDataAndType(uri, "image/*") //设置了要裁剪的图片的数据和类型。uri 是图片的 Uri 地址，"image/*" 表示裁剪的是图片
                putExtra("scale", true);// 设置缩放
                putExtra(
                    "scaleUpIfNeeded",
                    true
                );// 去黑边 ;设置了是否在裁剪时扩展图像以适应裁剪框。设置为 true 表示在需要时扩展图像以适应裁剪框
                putExtra("circleCrop", true);//字面意思是圆形的裁剪框。但是目测也需要手机的支持。
                putExtra("crop", "true") //这行代码启用了裁剪功能
                putExtra("aspectX", 1) //这两行代码设置了裁剪框的宽高比。在这里，它们都被设置为 1，表示裁剪框是正方形的
                putExtra("aspectY", 1)
                putExtra("outputX", 450) // 期望的裁剪输出图片宽度
                putExtra("outputY", 450) // 期望的裁剪输出图片高度
                putExtra("return-data", true) //设置了裁剪后的数据是否返回到调用者。设置为 true 表示裁剪后的图片数据会返回到调用者
            }
            cropImageLauncher.launch(cropIntent)
        }
    }
}