package com.eland.android.eoas.Util

import android.content.Context
import android.util.Log

import com.eland.android.eoas.Model.Constant
import com.eland.android.eoas.Model.LoginInfo
import com.eland.android.eoas.Model.RegAutoInfo

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.ArrayList

/**
 * Created by liu.wenbin on 15/12/7.
 */
object CacheInfoUtil {

    private val TAG = "EOAS"

    //自动出勤与否缓存
    fun saveIsRegAuto(context: Context, data: ArrayList<RegAutoInfo>) {
        DataCache<RegAutoInfo>().saveGlobal(context, data, Constant.EOAS_REGAUTO)
    }

    //判断是否自动设置出勤
    fun loadIsRegAuto(context: Context, userId: String): Boolean? {
        val list = DataCache<RegAutoInfo>().loadGlobal(context, Constant.EOAS_REGAUTO)

        return list.indices.any { (list[it].userId == userId || list[it].imei == userId) && list[it].isRegAuto == "TRUE" }
    }

    //联系人缓存
    fun saveContact(context: Context, data: ArrayList<LoginInfo>) {
        DataCache<LoginInfo>().saveGlobal(context, data, Constant.EOAS_CONTACT)
    }

    //读取联系人
    fun loadContact(context: Context): ArrayList<LoginInfo> {
        return DataCache<LoginInfo>().loadGlobal(context, Constant.EOAS_CONTACT)
    }

    //保存是否接收push设置
    fun saveIsReceive(context: Context, data: ArrayList<RegAutoInfo>) {
        DataCache<RegAutoInfo>().saveGlobal(context, data, Constant.EOAS_RECEIVE)
    }

    fun loadIsReceive(context: Context, userId: String): Boolean? {
        val list = DataCache<RegAutoInfo>().loadGlobal(context, Constant.EOAS_RECEIVE)

        return list.indices.any { list[it].userId == userId && list[it].isReceive == "TRUE" }
    }

    internal class DataCache<T> {
        fun save(ctx: Context, data: ArrayList<T>, name: String) {
            save(ctx, data, name, "")
        }

        fun saveGlobal(ctx: Context, data: ArrayList<T>, name: String) {
            save(ctx, data, name, Constant.EOAS_CACHE)
        }

        private fun save(ctx: Context?, data: ArrayList<T>, name: String,
                         folder: String) {
            if (ctx == null) {
                return
            }
            val file = if (folder.isNotEmpty()) {
                val fileDir = File(ctx.filesDir, folder)
                ConsoleUtil.i(TAG, "----------创建缓存:-------------" + fileDir)
                if (!fileDir.exists() || !fileDir.isDirectory) {
                    fileDir.mkdir()
                }
                File(fileDir, name)
            } else {
                File(ctx.filesDir, name)
            }
            if (file.exists()) {
                file.delete()
            }
            try {
                val oos = ObjectOutputStream(
                        FileOutputStream(file))
                oos.writeObject(data)
                oos.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun load(ctx: Context, name: String): ArrayList<T> {
            return load(ctx, name, "")
        }

        fun loadGlobal(ctx: Context, name: String): ArrayList<T> {
            return load(ctx, name, Constant.EOAS_CACHE)
        }

        private fun load(ctx: Context, name: String, folder: String): ArrayList<T> {
            var data: ArrayList<T>? = null

            val file = if (!folder.isEmpty()) {
                val fileDir = File(ctx.filesDir, folder)
                if (!fileDir.exists() || !fileDir.isDirectory) {
                    fileDir.mkdir()
                }
                File(fileDir, name)
            } else {
                File(ctx.filesDir, name)
            }
            if (file.exists()) {
                try {
                    ConsoleUtil.i(TAG, "----------读取缓存-------------")
                    val ois = ObjectInputStream(FileInputStream(file))
                    data = ois.readObject() as ArrayList<T>
                    ois.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            if (data == null) {
                data = ArrayList()
            }
            return data
        }
    }
}
