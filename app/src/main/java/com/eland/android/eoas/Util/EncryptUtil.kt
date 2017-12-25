package com.eland.android.eoas.Util

import java.security.MessageDigest
import kotlin.experimental.and

/**
 * Created by liu.wenbin on 15/11/10.
 */
object EncryptUtil {

    /**
     * @param decript 要加密的字符串
     * @return 加密的字符串
     * MD5加密
     */
    fun MD5(decript: String): String? {
        val hexDigits = charArrayOf(// 用来将字节转换成 16 进制表示的字符
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
        try {
            val strTemp = decript.toByteArray()
            val mdTemp = MessageDigest.getInstance("MD5")
            mdTemp.update(strTemp)
            val tmp = mdTemp.digest() // MD5 的计算结果是一个 128 位的长整数，
            // 用字节表示就是 16 个字节
            val strs = CharArray(16 * 2) // 每个字节用 16 进制表示的话，使用两个字符，
            // 所以表示成 16 进制需要 32 个字符
            var k = 0 // 表示转换结果中对应的字符位置
            for (i in 0..15) { // 从第一个字节开始，对 MD5 的每一个字节
                // 转换成 16 进制字符的转换
                val byte0 = tmp[i] // 取第 i 个字节
                strs[k++] = hexDigits[(240 and tmp[i].toInt()).ushr(4)] // 取字节中高 4 位的数字转换,
                // >>> 为逻辑右移，将符号位一起右移
                strs[k++] = hexDigits[15 and tmp[i].toInt()] // 取字节中低 4 位的数字转换
            }
            return String(strs).toUpperCase() // 换后的结果转换为字符串
        } catch (e: Exception) {
            return null
        }

    }

    private val DIGITS_LOWER = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
    private val DIGITS_UPPER = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

    fun encodeHex(data: ByteArray, toLowerCase: Boolean = true): CharArray {
        return encodeHex(
                data,
                if (toLowerCase) DIGITS_LOWER else DIGITS_UPPER
        )
    }

    fun encodeHex(data: ByteArray, toDigits: CharArray): CharArray {
        val l = data.size
        val out = CharArray(l shl 1)
        var i = 0
        var j = 0
        while (i < l) {
            out[j++] = toDigits[(240 and data[i].toInt()).ushr(4)]
            out[j++] = toDigits[15 and data[i].toInt()]
            i++
        }
        return out
    }
}
