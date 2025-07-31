/*
package com.example.booksalesmanagement.imagetext;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

public class ConnectAlibabaBucket {
    // 第一个参数：在【RAM访问控制】创建用户时分配的accessKeyId
// 第二个参数：在【RAM访问控制】创建用户时分配的accessKeySecret
    OSSCredentialProvider ossCredentialProvider = new OSSPlainTextAKSKCredentialProvider("xxx", "xxx");
    private Context mContext;
    // 第一个参数：上下文
    // 第二个参数：在OSS控制台创建好Bucket后，会有一个EndPoint(地域节点)，比如我这里的节点是：http://oss-cn-shanghai.aliyuncs.com
    // 第三个参数：OSSCredentialProvider
    OSS oss = new OSSClient(mContext.getApplicationContext(), "http://oss-cn-shanghai.aliyuncs.com", ossCredentialProvider);
    // 第二个参数：可以是一个文件路径：比如你在image-header创建了一个文件夹为images,那第二个参数传images/img.jpg
    // 第三个参数：是文件的本地路径，比如我这里在本地路径中是姨丈图片，路径是sdcard/img/img.jpg
    PutObjectRequest put = new PutObjectRequest("image-header", "img.jpg", "sdcard/img/img.jpg");
    // 异步上传时可以设置进度回调。
    put.setProgressCallback((request, currentSize, totalSize) -> {
        Log.d("currentSize = " + currentSize, "totalSize = " + totalSize);
    });
    // 此处调用异步上传方法
    OSSAsyncTask ossAsyncTask= oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
        @Override
        public void onSuccess(PutObjectRequest request, PutObjectResult result) {
            Log.d("PutObject", "UploadSuccess");
            Log.d("ETag", result.getETag());
            Log.d("RequestId", result.getRequestId());
        }

        @Override
        public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
            // 请求异常。
            if (clientException != null) {
                // 本地异常，如网络异常等。
                Log.e(clientException.getMessage());
            }
            if (serviceException != null) {
                // 服务异常。
                Log.e("ErrorCode", serviceException.getErrorCode());
                Log.e("RequestId", serviceException.getRequestId());
                Log.e("HostId", serviceException.getHostId());
                Log.e("RawMessage", serviceException.getRawMessage());

            }
        }
    });


}
*/
