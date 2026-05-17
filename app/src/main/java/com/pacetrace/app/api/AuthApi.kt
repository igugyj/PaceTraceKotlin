package com.pacetrace.app.api

object AuthApi {
    suspend fun login(phone: String, password: String): Map<String, Any?> {
        val pwdHashed = md5(password.trim())
        val body = mapOf(
            "appVersions" to "1.8.5",
            "brand" to "Xiaomi",
            "deviceToken" to "",
            "deviceType" to "1",
            "mobileType" to "Mi 10",
            "password" to pwdHashed,
            "sysVersions" to "12",
            "userPhone" to phone.trim()
        )
        val resp = ApiClient.post("v1/auth/login/password", body)
        if ((resp["code"] as? Number)?.toInt() in listOf(200, 10000) && resp["response"] != null) {
            val responseMap = resp["response"] as? Map<String, Any?>
            if (responseMap != null) {
                AppContext.saveUser(responseMap)
            }
        }
        return resp
    }

    suspend fun loginByToken(): Map<String, Any?> {
        val resp = ApiClient.get("v1/auth/login/token")
        if ((resp["code"] as? Number)?.toInt() in listOf(200, 10000) && resp["response"] != null) {
            val responseMap = resp["response"] as? Map<String, Any?>
            if (responseMap != null) {
                AppContext.saveUser(responseMap)
            }
        }
        return resp
    }

    suspend fun getUserInfo(): Map<String, Any?> {
        return ApiClient.get("v1/auth/query/token")
    }

    fun logout() {
        AppContext.clear()
    }

    suspend fun getSchools(): Map<String, Any?> {
        return ApiClient.get("v1/school/getSchoolSingleInfoList")
    }

    suspend fun getStudentSchoolName(studentName: String, registerCode: String): Map<String, Any?> {
        return ApiClient.get("v1/auth/getStudentSchoolName", mapOf(
            "studentName" to studentName,
            "registerCode" to registerCode
        ))
    }

    suspend fun sendSmsRegister(phone: String): Map<String, Any?> {
        return ApiClient.get("v1/auth/sendSmsForRegister", mapOf("phoneNum" to phone))
    }

    suspend fun sendSmsReset(phone: String): Map<String, Any?> {
        return ApiClient.get("v1/auth/sendSmsForPassWord", mapOf("phoneNum" to phone))
    }

    suspend fun changePassword(body: Map<String, Any?>): Map<String, Any?> {
        return ApiClient.post("v1/auth/update/password", body)
    }

    suspend fun changePhone(body: Map<String, Any?>): Map<String, Any?> {
        return ApiClient.post("v1/auth/update/phone", body)
    }

    suspend fun updateUserInfo(params: Map<String, Any?>): Map<String, Any?> {
        return ApiClient.post("v1/auth/update/user/info", params)
    }

    private fun md5(s: String): String {
        val digest = java.security.MessageDigest.getInstance("MD5")
        val bytes = digest.digest(s.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
