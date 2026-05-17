package com.pacetrace.app.api

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

object AppContext {
    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    var user: User = User()
    var runStandard: RunStandard = RunStandard()

    fun init(context: Context) {
        prefs = context.getSharedPreferences("pacetrace", Context.MODE_PRIVATE)
        load()
    }

    private fun load() {
        val userJson = prefs.getString("user", null)
        if (userJson != null) {
            try {
                user = gson.fromJson(userJson, User::class.java)
                user.oauthToken?.let {
                    ApiClient.setToken(it.token)
                }
            } catch (_: Exception) {}
        }
    }

    fun saveUser(userDict: Map<String, Any?>) {
        val json = gson.toJson(userDict)
        val u: User = gson.fromJson(json, User::class.java)
        user = u
        user.oauthToken?.let {
            ApiClient.setToken(it.token)
        }
        prefs.edit().putString("user", json).apply()
    }

    fun clear() {
        user = User()
        runStandard = RunStandard()
        ApiClient.setToken("")
        prefs.edit().clear().apply()
    }
}
