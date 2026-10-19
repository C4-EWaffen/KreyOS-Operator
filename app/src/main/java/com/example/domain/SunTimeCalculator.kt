package com.example.domain

import java.util.Calendar
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.tan

data class SunTimes(
    val sunriseStr: String,
    val sunsetStr: String,
    val isDaylight: Boolean,
    val statusText: String
)

object SunTimeCalculator {

    fun calculate(lat: Double, lon: Double): SunTimes {
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val nowMinutes = currentHour * 60 + currentMinute

        val sunriseMin = getSunEventMinute(lat, lon, dayOfYear, isSunrise = true)
        val sunsetMin = getSunEventMinute(lat, lon, dayOfYear, isSunrise = false)

        val srH = sunriseMin / 60
        val srM = sunriseMin % 60
        val ssH = sunsetMin / 60
        val ssM = sunsetMin % 60

        val srFormatted = "%02d:%02d".format(srH, srM)
        val ssFormatted = "%02d:%02d".format(ssH, ssM)

        val isDay = nowMinutes in sunriseMin until sunsetMin
        val status = if (isDay) {
            val diff = sunsetMin - nowMinutes
            val h = diff / 60
            val m = diff % 60
            "Tag · noch ${h}h ${m}m Tageslicht"
        } else {
            val diff = if (nowMinutes < sunriseMin) {
                sunriseMin - nowMinutes
            } else {
                (1440 - nowMinutes) + sunriseMin
            }
            val h = diff / 60
            val m = diff % 60
            "Nacht · hell in ${h}h ${m}m"
        }

        return SunTimes(
            sunriseStr = srFormatted,
            sunsetStr = ssFormatted,
            isDaylight = isDay,
            statusText = status
        )
    }

    private fun getSunEventMinute(
        lat: Double,
        lon: Double,
        dayOfYear: Int,
        isSunrise: Boolean
    ): Int {
        val rad = Math.PI / 180.0
        val zenith = 90.833

        val n = round(dayOfYear + (if (isSunrise) 0.0009 else 0.5))
        val lngHour = lon / 15.0
        val t = if (isSunrise) n + ((6.0 - lngHour) / 24.0) else n + ((18.0 - lngHour) / 24.0)

        val meanAnomaly = (0.9856 * t) - 3.289
        var trueLongitude = meanAnomaly + (1.916 * sin(meanAnomaly * rad)) +
                (0.020 * sin(2.0 * meanAnomaly * rad)) + 282.634
        trueLongitude = (trueLongitude % 360.0 + 360.0) % 360.0

        var rightAscension = atan(0.91764 * tan(trueLongitude * rad)) / rad
        rightAscension = (rightAscension % 360.0 + 360.0) % 360.0

        val lQuadrant = floor(trueLongitude / 90.0) * 90.0
        val raQuadrant = floor(rightAscension / 90.0) * 90.0
        rightAscension += (lQuadrant - raQuadrant)
        rightAscension /= 15.0

        val sinDec = 0.39782 * sin(trueLongitude * rad)
        val cosDec = cos(asin(sinDec))

        val cosH = (cos(zenith * rad) - (sinDec * sin(lat * rad))) / (cosDec * cos(lat * rad))
        if (cosH > 1.0 || cosH < -1.0) {
            // Polar day or night fallback
            return if (isSunrise) 360 else 1080
        }

        val hourAngle = if (isSunrise) 360.0 - (acos(cosH) / rad) else (acos(cosH) / rad)
        val h = hourAngle / 15.0

        val localMeanTime = h + rightAscension - (0.06571 * t) - 6.622
        val universalTime = (localMeanTime - lngHour % 24.0 + 24.0) % 24.0

        // Timezone offset for Central European Time (CET/CEST approx +1 or +2)
        val tzOffset = 2.0
        val localHours = (universalTime + tzOffset + 24.0) % 24.0

        val hours = floor(localHours).toInt()
        val minutes = floor((localHours - hours) * 60.0).toInt()
        return (hours * 60 + minutes).coerceIn(0, 1439)
    }
}
