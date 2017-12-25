package com.eland.android.eoas.Util

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

import android.os.Environment.MEDIA_MOUNTED

/**
 * Created by liu.wenbin on 15/12/7.
 */
object FileUtil {

    private val EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE"
    private val TAG = "EOAS"
    /*
    * 获取缓存图片文件目录
    */
    fun getCacheDirectory(context: Context, preferExternal: Boolean, dirName: String): File {
        var appCacheDir: File? = null
        if (preferExternal && MEDIA_MOUNTED == Environment.getExternalStorageState() && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context, dirName)
        }
        if (appCacheDir == null) {
            appCacheDir = context.cacheDir
        }
        if (appCacheDir == null) {
            val cacheDirPath = "/data/data/" + context.packageName + "/cache/"
            ConsoleUtil.i(TAG, "Can't define system cache directory! '%s' will be used." + cacheDirPath)
            appCacheDir = File(cacheDirPath)
        }
        return appCacheDir
    }

    /*
    * 获取扩展目录
    */
    private fun getExternalCacheDir(context: Context, dirName: String): File? {
        val appCacheDir2 = File(File(Environment.getExternalStorageDirectory(), context.packageName), "cache")
        val appCacheDir = File(appCacheDir2, dirName)
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                ConsoleUtil.i(TAG, "Unable to create external cache directory")
                return null
            }
            try {
                File(appCacheDir, ".nomedia").createNewFile()
            } catch (e: IOException) {
                ConsoleUtil.i(TAG, "Can't create \".nomedia\" file in application external cache directory")
            }

        }
        return appCacheDir
    }

    private fun hasExternalStoragePermission(context: Context): Boolean {
        val perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION)
        return perm == PackageManager.PERMISSION_GRANTED
    }

    /**
     * @param bitmap
     * @return
     * 保存到sd卡
     */
    fun saveToSdCard(bitmap: Bitmap, context: Context, fileName: String): String {
        val files = FileUtil.getCacheDirectory(context, true, fileName).toString() + ".jpg"
        val file = File(files)
        try {
            val out = FileOutputStream(file)
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush()
                out.close()
            }
        } catch (e: FileNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return file.absolutePath
    }

    /**
     * @param srcPath
     * @return
     * 压缩图片
     */
    fun compressImageFromFile(srcPath: String): Bitmap {
        val newOpts = BitmapFactory.Options()
        newOpts.inJustDecodeBounds = true//只读边,不读内容
        var bitmap = BitmapFactory.decodeFile(srcPath, newOpts)

        newOpts.inJustDecodeBounds = false
        val w = newOpts.outWidth
        val h = newOpts.outHeight
        val hh = 800f//800
        val ww = 480f//480
        var be = 1
        if (w > h && w > ww) {
            be = (newOpts.outWidth / ww).toInt()
        } else if (w < h && h > hh) {
            be = (newOpts.outHeight / hh).toInt()
        }
        if (be <= 0)
            be = 1
        newOpts.inSampleSize = be//设置采样率

        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888//该模式是默认的,可不设
        newOpts.inPurgeable = true// 同时设置才会有效
        newOpts.inInputShareable = true//。当系统内存不够时候图片自动被回收

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts)
        return bitmap
    }
}
