package com.pacetrace.app.lib

import android.content.Context
import com.google.gson.Gson
import com.pacetrace.app.api.MapData
import com.pacetrace.app.api.RouteMap

object MapsUtil {
    private var _allMaps: List<RouteMap>? = null

    fun loadMaps(context: Context): List<RouteMap> {
        if (_allMaps != null) return _allMaps!!

        val gson = Gson()
        val items = mutableListOf<RouteMap>()

        val rawIds = try {
            val fields = com.pacetrace.app.R.raw::class.java.fields
            fields.map { it.name to it.getInt(null) }
        } catch (_: Exception) {
            emptyList()
        }

        for ((name, id) in rawIds) {
            if (!name.endsWith("_json")) continue
            try {
                val inputStream = context.resources.openRawResource(id)
                val text = inputStream.bufferedReader().use { it.readText() }
                val data = gson.fromJson(text, MapData::class.java)
                val pts = data.mapData.mapNotNull { p ->
                    val parts = p.split(",")
                    if (parts.size == 2) {
                        val lng = parts[0].toDoubleOrNull()
                        val lat = parts[1].toDoubleOrNull()
                        if (lat != null && lng != null) Pair(lat, lng) else null
                    } else null
                }
                if (pts.isNotEmpty()) {
                    items.add(RouteMap(
                        id = data.mapId.ifEmpty { name },
                        name = data.mapName.ifEmpty { name },
                        coords = pts
                    ))
                }
            } catch (_: Exception) {}
        }

        _allMaps = items.sortedBy { it.id }
        return _allMaps!!
    }
}
