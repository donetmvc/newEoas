package com.eland.android.eoas.Model

import java.io.Serializable

/**
 * Created by liu.wenbin on 15/11/27.
 */
class LoginInfo : Serializable {

    var userId: String? = null
    var password: String? = null
    var userName: String? = null
    var imei: String? = null
    var email: String? = null
    var cellNo: String? = null
    var url: String? = null
    var backCellNo: String? = null
    var backName: String? = null
}


data class UserInfoData(
        val userId: String,
        val password: String,
        val userName: String,
        val imei: String,
        val email: String,
        val cellNo: String,
        val url: String,
        val backCellNo: String,
        val backName: String
)
