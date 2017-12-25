package com.eland.android.eoas.Model

import java.io.Serializable

/**
 * Created by Administrator on 2016/2/24.
 */
class WetherInfo : Serializable {
    var sd: String? = null
    var temperature: String? = null
    var temperature_time: String? = null
    var weather: String? = null
    var weather_code: String? = null
    var weather_pic: String? = null
    var wind_direction: String? = null
    var wind_power: String? = null
    var hightTemp: String? = null
    var lowTemp: String? = null
}
