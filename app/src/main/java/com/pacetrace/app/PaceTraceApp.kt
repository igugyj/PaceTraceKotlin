package com.pacetrace.app

import android.app.Application
import android.os.Process
import com.pacetrace.app.api.AppContext
import java.io.FileWriter

class PaceTraceApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContext.init(this)

        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            try {
                val file = filesDir.resolve("crash.log")
                FileWriter(file, true).use { w ->
                    w.appendLine("=== ${java.util.Date()} ===")
                    e.printStackTrace(java.io.PrintWriter(w))
                    w.appendLine()
                }
            } catch (_: Exception) {}
            Process.killProcess(Process.myPid())
        }
    }
}
