package com.pacetrace.app.lib

import kotlin.math.*
import kotlin.random.Random

object GeoUtil {
    fun randomPoint(lat: String, lng: String, radius: Float = 100f): Pair<String, String> {
        val angle = Random.nextFloat() * 2 * PI
        val r = sqrt(Random.nextFloat()) * radius
        val dx = r * cos(angle) / (111320f * cos(Math.toRadians(lat.toDouble())).toFloat())
        val dy = r * sin(angle) / 111320f
        return (lat.toFloat() + dy).toString() to (lng.toFloat() + dx).toString()
    }

    fun routeDistance(coords: List<Pair<Double, Double>>): Int {
        var d = 0.0
        for (i in 1 until coords.size) {
            val a = coords[i - 1]
            val b = coords[i]
            val dx = (b.second - a.second) * 111320.0 * cos(Math.toRadians((a.first + b.first) / 2))
            val dy = (b.first - a.first) * 111320.0
            d += sqrt(dx * dx + dy * dy)
        }
        return d.toInt()
    }

    fun buildTrack(coords: List<Pair<Double, Double>>, targetDist: Int): List<Pair<Double, Double>> {
        val fullD = routeDistance(coords)
        if (fullD <= 0) return coords
        val n = coords.size
        val start = Random.nextInt(n)
        val result = mutableListOf<Pair<Double, Double>>()
        var i = start
        while (routeDistance(result) < targetDist) {
            result.add(coords[i])
            i = (i + 1) % n
        }
        return result
    }
}
