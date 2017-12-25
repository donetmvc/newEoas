package com.eland.android.eoas.Model

/**
 * Created by liuwenbin on 2017/12/7.
 * 虽然青春不在，但不能自我放逐.
 */

data class ApplyListDto(
        val processStateCode: String,
        val vacationTypeName: String,
        val vacationDays: String,
        val vacationPeriod: String,
        val remarks: String,
        val vacationNo: String
)