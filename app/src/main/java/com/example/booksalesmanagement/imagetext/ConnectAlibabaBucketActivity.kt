package com.example.booksalesmanagement.imagetext

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.model.GetObjectRequest
import com.alibaba.sdk.android.oss.model.GetObjectResult
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.alibaba.sdk.android.oss.model.PutObjectResult
import com.example.booksalesmanagement.R
import com.example.booksalesmanagement.bean.Book
import com.example.booksalesmanagement.bean.SC
import com.example.booksalesmanagement.dao.BookDao
import com.example.booksalesmanagement.database.ConnectionSqlServer
import com.example.booksalesmanagement.databinding.ActivityConnectAlibabaBucketBinding
import java.lang.StringBuilder
import java.math.BigDecimal
import java.sql.Timestamp
import kotlin.concurrent.thread

class ConnectAlibabaBucketActivity : AppCompatActivity() {

    private var ossManager: OssManager? = null

    private var _binding: ActivityConnectAlibabaBucketBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageUri: Uri
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var cropImageLauncher: ActivityResultLauncher<Intent>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityConnectAlibabaBucketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化ActivityResultLauncher来启动相册应用
        initPickImageLauncher()

        // 初始化ActivityResultLauncher来启动裁剪应用
        initCropImageLauncher()

        binding.btnInsertBook.setOnClickListener {
            // 将Uri转换为文件路径
  //val imageFilePath = ImageUtils.getPathFromUri(this, imageUri)

           val imageFilePath =
                FileUtils.saveDrawableToInternalStorage(this, R.drawable.banana, "pear.jpg")
            if (imageFilePath != null) {
                // 图片保存成功，filePath 是保存的文件路径
            } else {
                // 图片保存失败
            }
            ConnectAlibabaOssToImage.sendImage(
                this,
                "6百年孤独",
                imageFilePath,
                object : ConnectAlibabaOssToImage.UploadCallback {
                    override fun onUploadSuccess() {
                        val book = Book(6, "百年孤独",112, BigDecimal("857.99"),
                            "马尔克斯",211, Timestamp(System.currentTimeMillis())
                        )
                       if (BookDao.insertBook(book)) {
                           runOnUiThread {
                               Toast.makeText(
                                   this@ConnectAlibabaBucketActivity,
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

        binding.button.setOnClickListener {
            getBookAllMsg()
        }

        binding.btnSend.setOnClickListener {
            //sendImage()
            // 启动相册应用
            pickImageLauncher.launch("image/*")

            /*  // 将Uri转换为文件路径
              val imageFilePath = ImageUtils.getPathFromUri(this, imageUri)

  *//*            val imageFilePath =
                FileUtils.saveDrawableToInternalStorage(this, R.drawable.pear, "pear.jpg")
            if (imageFilePath != null) {
                // 图片保存成功，filePath 是保存的文件路径
            } else {
                // 图片保存失败
            }*//*
            ConnectAlibabaOssToImage.sendImage(
                this,
                "pear",
                imageFilePath,
                object : ConnectAlibabaOssToImage.UploadCallback {
                    override fun onUploadSuccess() {
                        runOnUiThread {
                            Toast.makeText(
                                this@ConnectAlibabaBucketActivity,
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

        binding.btnGet.setOnClickListener {
            //getImage("img3.jpg")
            ConnectAlibabaOssToImage.getImage(
                this,
                "pear",
                object : ConnectAlibabaOssToImage.ImageCallback {
                    override fun onImageLoaded(bitmap: Bitmap?) {
                        runOnUiThread {
                            Toast.makeText(
                                this@ConnectAlibabaBucketActivity,
                                "下载成功",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.imageView.setImageBitmap(bitmap)
                        }
                    }

                    override fun onError() {
                        TODO("Not yet implemented")
                    }

                })
        }

    }

    private fun getBookAllMsg() {
        // 调用SQL Server数据库的代码
        val sb = StringBuilder()
        var bookList = ArrayList<Book>()
        thread {
            try {
                bookList = BookDao.queryAll()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            for (book in bookList) {
                sb.append("图书编号：" + book.bookId)
                sb.append("图书名称：" + book.bookName)
                sb.append("出版社编号：" + book.publisherId)
                sb.append("作者：" + book.author)
                sb.append("价格：" + book.price)
                sb.append("库存：" + book.inventory)
                sb.append("出版日期：" + book.publicationDate)
                sb.append("\n")
            }

            runOnUiThread {
                binding.textView.text = sb.toString()
            }
            //binding.tvShowMsg.text = Cno
            Log.e("TAG", "getMsgFromSqlServer: " + sb.toString())
        }
    }

    private fun initCropImageLauncher() {
        // 初始化ActivityResultLauncher来启动裁剪应用
        cropImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val extras = result.data!!.extras //这行代码获取了从裁剪应用返回的 Intent 中的额外数据，通常包含了裁剪后的图片数据
                    val croppedBitmap = extras?.getParcelable<Bitmap>("data")
                    imageUri = bitmapToUri(croppedBitmap)
                    //将Uri转换为文件路径
                    val imageFilePath = ImageUtils.getPathFromUri(this, imageUri)

                    ConnectAlibabaOssToImage.sendImage(this, "pear", imageFilePath,
                        object : ConnectAlibabaOssToImage.UploadCallback {
                            override fun onUploadSuccess() {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@ConnectAlibabaBucketActivity,
                                        "上传成功",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onUploadFailure() {
                                TODO("Not yet implemented")
                            }
                        })
                }
            }
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

    private fun bitmapToUri(bitmap: Bitmap?): Uri {
        val context = applicationContext
        val imagesDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            TODO("VERSION.SDK_INT < Q")
        }
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "cropped_image.jpg")
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

    private fun getImage(bookImage: String) {
        ossManager = OssManager.Builder(this)
            .bucketName("bookcoverimage")
            .accessKeyId("LTAI5tShWwnv8iwW6fYpXaUg")
            .accessKeySecret("aCdE7DpKSm9fiHTmqgKw3F28T6hHuo")
            .endPoint("http://oss-cn-chengdu.aliyuncs.com")
            .objectKey("images/$bookImage")
            .build()

        var getBitmap: Bitmap? = null

        ossManager?.setGetStateListener(object : OssManager.OnGetStateListener {
            override fun onSuccess(request: GetObjectRequest?, result: GetObjectResult?) {
                // 下载成功处理
                Log.e("TAG", "下载成功")
                // 将临时文件加载为 Bitmap 对象
                getBitmap = ossManager?.downImageBitmap

                /*                // 在 UI 线程上更新 ImageView
                                runOnUiThread {
                                    binding.imageView.setImageBitmap(getBitmap)
                                    ossManager?.cancelPushOrGet()
                                }*/

            }

            override fun onFailure(
                request: GetObjectRequest?,
                clientException: ClientException?,
                serviceException: ServiceException?
            ) {

            }
        })

        ossManager?.getObject()


        /*        val getBitMap = ossManager?.downImageBitmap
                binding.imageView.setImageBitmap(getBitMap)*/
    }

    @SuppressLint("ResourceType")
    private fun sendImage() {
        // 获取资源 ID
        val resourceId = R.drawable._pear

        /*        // 将资源转换为 InputStream
                val inputStream = resources.openRawResource(resourceId).toString()

                val imagePath =
                    "android.resource://$packageName/drawable-xxhdpi/_pear.jpg"

                //String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.builder_video_five;
                // 设置图片的Url地址
                val videoUrl =
                    "https://typroamarkdown.oss-cn-chengdu.aliyuncs.com/image-20230721115005028.png" // 替换为你的网络视频地址*/

        val filePath = FileUtils.saveDrawableToInternalStorage(this, R.drawable.pear, "pear.jpg")
        if (filePath != null) {
            // 图片保存成功，filePath 是保存的文件路径
        } else {
            // 图片保存失败
        }


        ossManager = OssManager.Builder(this)
            .bucketName("bookcoverimage")
            .accessKeyId("LTAI5tShWwnv8iwW6fYpXaUg")
            .accessKeySecret("aCdE7DpKSm9fiHTmqgKw3F28T6hHuo")
            .endPoint("http://oss-cn-chengdu.aliyuncs.com")
            .objectKey("images/img3.jpg")
            .localFilePath(filePath)
            .build()

        //设置上传进度和状态监听器（可选）
        ossManager?.setPushProgressListener(object : OssManager.OnPushProgressListener {
            override fun onProgress(
                request: PutObjectRequest?,
                currentSize: Long,
                totalSize: Long
            ) {
                // 处理上传进度

            }
        })

        ossManager?.setPushStateListener(object : OssManager.OnPushStateListener {
            override fun onSuccess(request: PutObjectRequest?, result: PutObjectResult?) {
                // 上传成功处理
                Log.e("TAG", "上传成功")
                ossManager?.cancelPushOrGet()
            }

            override fun onFailure(
                request: PutObjectRequest?,
                clientException: ClientException?,
                serviceException: ServiceException?
            ) {
                // 上传失败处理

            }
        })

// 执行上传任务
        ossManager?.push()


    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}