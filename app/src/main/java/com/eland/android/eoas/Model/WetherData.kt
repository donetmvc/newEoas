package com.eland.android.eoas.Model

import java.util.*

/**
 * Created by liuwenbin on 2017/12/28.
 * 虽然青春不在，但不能自我放逐.
 */
data class WetherData(
        val location: Location,
        val current: Current,
        val forecast: ForeCast
)


//"name": "Beijing",
//"region": "Beijing",
//"country": "China",
//"lat": 39.93,
//"lon": 116.39,
//"tz_id": "Asia/Shanghai",
//"localtime_epoch": 1514448845,
//"localtime": "2017-12-28 16:14"
data class Location(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val tz_id: String,
    val localtime_epoch: Int,
    val localtime: String
)


//"last_updated_epoch": 1514448019,
//"last_updated": "2017-12-28 16:00",
//"temp_c": 3,
//"temp_f": 37.4,
//"is_day": 1,
//"condition": {
//    "text": "Sunny",
//    "icon": "//cdn.apixu.com/weather/64x64/day/113.png",
//    "code": 1000
//},
//"wind_mph": 0,
//"wind_kph": 0,
//"wind_degree": 102,
//"wind_dir": "ESE",
//"pressure_mb": 1026,
//"pressure_in": 30.8,
//"precip_mm": 0,
//"precip_in": 0,
//"humidity": 56,
//"cloud": 0,
//"feelslike_c": 3,
//"feelslike_f": 37.4,
//"vis_km": 7,
//"vis_miles": 4
data class Current(
        val last_updated_epoch: Int,
        val last_updated: String,
        val temp_c: Double,
        val temp_f: Double,
        val is_day: Int,
        val condition: Condition,
        val wind_mph: Double,
        val wind_kph: Double,
        val wind_degree: Double,
        val wind_dir: String,
        val pressure_mb: Double,
        val pressure_in: Double,
        val precip_mm: Double,
        val precip_in: Double,
        val humidity: Int,
        val cloud: Int,
        val feelslike_c: Double,
        val feelslike_f: Double,
        val vis_km: Int,
        val vis_miles: Int
)

data class Condition(
        val text: String,
        val icon: String,
        val code: Int
)

data class ForeCast(
        val forecastday: List<ForeCastDay>
)

data class ForeCastDay(
        val date: String,
        val date_epoch: String,
        val day: Day,
        val astro: Astro
        //val hour: List<Hour>
)


//"maxtemp_c": 7.1,
//"maxtemp_f": 44.8,
//"mintemp_c": 3.7,
//"mintemp_f": 38.7,
//"avgtemp_c": 3.1,
//"avgtemp_f": 37.6,
//"maxwind_mph": 2.9,
//"maxwind_kph": 4.7,
//"totalprecip_mm": 0,
//"totalprecip_in": 0,
//"avgvis_km": 20,
//"avgvis_miles": 12,
//"avghumidity": 51,
//"condition": {
//    "text": "Partly cloudy",
//    "icon": "//cdn.apixu.com/weather/64x64/day/116.png",
//    "code": 1003
//},
//"uv": 1.4
data class Day(
        val maxtemp_c: Double,
        val maxtemp_f: Double,
        val mintemp_c: Double,

        val mintemp_f: Double,
        val avgtemp_c: Double,
        val avgtemp_f: Double,

        val maxwind_mph: Double,
        val maxwind_kph: Double,
        val totalprecip_mm: Double,
        val totalprecip_in: Double,
        val avgvis_km: Double,
        val avgvis_miles: Double,
        val avghumidity: Double,
        val condition: Condition,
        val uv: Double
)


//"sunrise": "07:36 AM",
//"sunset": "04:57 PM",
//"moonrise": "01:22 PM",
//"moonset": "01:34 AM"
data class Astro(
        val sunrise: String,
        val sunset: String,
        val moonrise: String,
        val moonset: String
)


//"time_epoch": 1514390400,
//"time": "2017-12-28 00:00",
//"temp_c": -0.2,
//"temp_f": 31.6,
//"is_day": 0,
//"condition": {
//    "text": "Partly cloudy",
//    "icon": "//cdn.apixu.com/weather/64x64/night/116.png",
//    "code": 1003
//},
//"wind_mph": 2.9,
//"wind_kph": 4.7,
//"wind_degree": 6,
//"wind_dir": "N",
//"pressure_mb": 1026,
//"pressure_in": 30.8,
//"precip_mm": 0,
//"precip_in": 0,
//"humidity": 48,
//"cloud": 34,
//"feelslike_c": -1.6,
//"feelslike_f": 29.1,
//"windchill_c": -1.6,
//"windchill_f": 29.1,
//"heatindex_c": -0.2,
//"heatindex_f": 31.6,
//"dewpoint_c": -9.8,
//"dewpoint_f": 14.4,
//"will_it_rain": 0,
//"chance_of_rain": "0",
//"will_it_snow": 0,
//"chance_of_snow": "0",
//"vis_km": 10,
//"vis_miles": 6
//data class Hour(
//
//)