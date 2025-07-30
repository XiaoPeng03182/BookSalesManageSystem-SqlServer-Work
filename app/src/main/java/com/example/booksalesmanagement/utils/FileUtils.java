package com.example.booksalesmanagement.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {



    public static String saveDrawableToInternalStorage(Context context, int drawableId, String filename) {
        // 1. 将 drawable 资源加载为 Drawable 对象
        Drawable drawable = context.getDrawable(drawableId);
        if (drawable == null) {
            return null; // 资源加载失败
        }

        // 2. 将 Drawable 对象转换为 Bitmap 对象
        Bitmap bitmap = drawableToBitmap(drawable);

        // 3. 将 Bitmap 对象保存到应用的文件目录中
        String filePath = saveBitmapToInternalStorage(context, bitmap, filename);

        return filePath;
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        // 如果不是 BitmapDrawable，则创建一个新的 Bitmap 对象来绘制 Drawable
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private static String saveBitmapToInternalStorage(Context context, Bitmap bitmap, String filename) {
        // 获取应用的内部文件目录
        File directory = context.getApplicationContext().getDir("images", Context.MODE_PRIVATE);
        // 创建一个文件对象，指定文件名和保存路径
        File file = new File(directory, filename);

        FileOutputStream fos = null;
        try {
            // 创建文件输出流，将 Bitmap 写入文件
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); // 将 Bitmap 压缩为 PNG 格式，质量为 100%
            fos.flush();
            return file.getAbsolutePath(); // 返回保存的文件路径
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
