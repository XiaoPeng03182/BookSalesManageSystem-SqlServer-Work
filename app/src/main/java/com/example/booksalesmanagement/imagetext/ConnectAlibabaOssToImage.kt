package com.example.booksalesmanagement.imagetext

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.model.GetObjectRequest
import com.alibaba.sdk.android.oss.model.GetObjectResult
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.alibaba.sdk.android.oss.model.PutObjectResult

object ConnectAlibabaOssToImage {
    //获取图片的异步回调
    interface ImageCallback {
        fun onImageLoaded(bitmap: Bitmap?)
        fun onError()
    }
    //上传图片的异步回调
    interface UploadCallback {
        fun onUploadSuccess()
        fun onUploadFailure()
    }

    /**
     * @param context
     * @param bookImageFileName 保存在OSS中的文件名字
     * @param imageFilePath 图片的url
     * @param callback 图片成功上传的回调
     */
    //上传图片
    fun sendImage(context: Context, bookImageFileName: String, imageFilePath:String, callback: UploadCallback) {
        var ossManager: OssManager? = null

        ossManager = OssManager.Builder(context)
            .bucketName("bookcoverimage")
            .accessKeyId("LTAI5tShWwnv8iwW6fYpXaUg")
            .accessKeySecret("aCdE7DpKSm9fiHTmqgKw3F28T6hHuo")
            .endPoint("http://oss-cn-chengdu.aliyuncs.com")
            .objectKey("bookImages/$bookImageFileName")
            .localFilePath(imageFilePath)
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
                //异步执行上传成功回调
                ossManager.cancelPushOrGet()
                callback.onUploadSuccess()
            }

            override fun onFailure(
                request: PutObjectRequest?,
                clientException: ClientException?,
                serviceException: ServiceException?
            ) {
                // 上传失败处理
                Log.e("TAG", "上传失败")
                callback.onUploadFailure()
            }
        })

        //执行上传任务
        ossManager?.push()
    }


    /**
     * @param context
     * @param bookImageFileName 保存在OSS中的文件名字
     * @param callback 图片成功上传的回调
     */
    //获取图书图片
    fun getImage(context:Context, bookImageFileName: String, callback: ImageCallback) {
        var ossManager: OssManager? = null
        ossManager = OssManager.Builder(context)
            .bucketName("bookcoverimage")
            .accessKeyId("LTAI5tShWwnv8iwW6fYpXaUg")
            .accessKeySecret("aCdE7DpKSm9fiHTmqgKw3F28T6hHuo")
            .endPoint("http://oss-cn-chengdu.aliyuncs.com")
            .objectKey("bookImages/$bookImageFileName")
            .build()

        ossManager?.setGetStateListener(object : OssManager.OnGetStateListener {
            override fun onSuccess(request: GetObjectRequest?, result: GetObjectResult?) {
                Log.e("TAG", "下载成功")
                val bitmap = ossManager.downImageBitmap
                //执行异步回调，对返回的bitmap进行处理
                callback.onImageLoaded(bitmap)
                ossManager.cancelPushOrGet()
            }

            override fun onFailure(
                request: GetObjectRequest?,
                clientException: ClientException?,
                serviceException: ServiceException?
            ) {
                callback.onError()
            }
        })
        //开始下载任务
        ossManager?.getObject()
    }

}