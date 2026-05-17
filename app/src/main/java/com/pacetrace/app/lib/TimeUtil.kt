package com.pacetrace.app.lib

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object TimeUtil {
    fun parseTime(t: String?): LocalDateTime? {
        if (t.isNullOrEmpty()) return null

        val fullFormats = listOf(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        )
        for (fmt in fullFormats) {
            try {
                return LocalDateTime.parse(t, fmt)
            } catch (_: DateTimeParseException) {}
        }
        try {
            val time = LocalTime.parse(t, DateTimeFormatter.ofPattern("HH:mm"))
            return LocalDateTime.of(LocalDate.now(), time)
        } catch (_: DateTimeParseException) {
            return null
        }
    }

    fun parseActivityTime(mmdd: String?, timeStr: String?): LocalDateTime? {
        if (timeStr.isNullOrEmpty()) return null

        val fullFormats = listOf(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        )
        for (fmt in fullFormats) {
            try {
                return LocalDateTime.parse(timeStr, fmt)
            } catch (_: DateTimeParseException) {}
        }
        try {
            val t = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"))
            if (mmdd.isNullOrEmpty()) return null
            val parts = mmdd.split("-")
            if (parts.size != 2) return null
            val month = parts[0].toIntOrNull() ?: return null
            val day = parts[1].toIntOrNull() ?: return null
            val year = LocalDate.now().year
            return LocalDateTime.of(year, month, day, t.hour, t.minute)
        } catch (_: Exception) {
            return null
        }
    }

    fun getActivityWindow(mmdd: String?, startStr: String?, endStr: String?): Pair<LocalDateTime?, LocalDateTime?> {
        val startDt = parseActivityTime(mmdd, startStr)
        var endDt = parseActivityTime(mmdd, endStr)
        if (startDt != null && endDt != null && endDt.isBefore(startDt)) {
            endDt = endDt.plusDays(1)
        }
        return startDt to endDt
    }
}
