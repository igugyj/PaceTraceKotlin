package com.pacetrace.app.api

object ClubApi {
    suspend fun getClubProjects(studentId: Int? = null, schoolId: Int? = null): Map<String, Any?> {
        val params = mutableMapOf<String, Any?>()
        if (studentId != null) params["studentId"] = studentId
        else if (AppContext.user.studentId != 0) params["studentId"] = AppContext.user.studentId
        if (schoolId != null) params["schoolId"] = schoolId
        else if (AppContext.user.schoolId != 0) params["schoolId"] = AppContext.user.schoolId
        return ApiClient.get("v1/clubactivity/getMyClubItemList", params)
    }

    suspend fun getActivityList(
        queryTime: String? = null,
        studentId: Int? = null,
        schoolId: Int? = null,
        activityItemId: Int? = null,
        page: Int = 1,
        size: Int = 15
    ): Map<String, Any?> {
        val params = mutableMapOf<String, Any?>("pageNo" to page, "pageSize" to size)
        if (queryTime != null) params["queryTime"] = queryTime
        if (studentId != null) params["studentId"] = studentId
        else if (AppContext.user.studentId != 0) params["studentId"] = AppContext.user.studentId
        if (schoolId != null) params["schoolId"] = schoolId
        else if (AppContext.user.schoolId != 0) params["schoolId"] = AppContext.user.schoolId
        if (activityItemId != null) params["activityItemId"] = activityItemId
        return ApiClient.get("v1/clubactivity/queryActivityList", params)
    }

    suspend fun joinActivity(studentId: Int? = null, activityId: Int = 0): Map<String, Any?> {
        val sid = studentId ?: AppContext.user.studentId
        return ApiClient.get("v1/clubactivity/joinClubActivity", mapOf(
            "studentId" to sid, "activityId" to activityId
        ))
    }

    suspend fun cancelActivity(studentId: Int? = null, activityId: Int = 0): Map<String, Any?> {
        val sid = studentId ?: AppContext.user.studentId
        return ApiClient.get("v1/clubactivity/cancelActivity", mapOf(
            "studentId" to sid, "activityId" to activityId
        ))
    }

    suspend fun getMyActivities(studentId: Int? = null, page: Int = 1, size: Int = 15): Map<String, Any?> {
        val sid = studentId ?: AppContext.user.studentId
        return ApiClient.get("v1/clubactivity/queryMyActivityList", mapOf(
            "studentId" to sid, "pageNo" to page, "pageSize" to size
        ))
    }

    suspend fun getClubRecords(studentId: Int? = null, page: Int = 1, size: Int = 10): Map<String, Any?> {
        val sid = studentId ?: AppContext.user.studentId
        return ApiClient.get("v1/clubactivity/getStudentClubRecord", mapOf(
            "studentId" to sid, "pageNo" to page, "pageSize" to size
        ))
    }

    suspend fun getClubBanner(schoolId: Int? = null): Map<String, Any?> {
        val sid = schoolId ?: AppContext.user.schoolId
        return ApiClient.get("v1/banner/querySchoolBannerChickenSoup", mapOf("schoolId" to sid))
    }

    suspend fun joinOrCancelSemester(configId: Int, type: String): Map<String, Any?> {
        return ApiClient.get("v1/clubactivity/joinOrCancelSchoolSemesterActivity", mapOf(
            "configurationId" to configId, "type" to type
        ))
    }

    suspend fun getSemesterActivities(
        weekDay: String? = null,
        activityItemId: Int? = null,
        page: Int = 1,
        size: Int = 15
    ): Map<String, Any?> {
        val params = mutableMapOf<String, Any?>("pageNo" to page, "pageSize" to size)
        if (weekDay != null) params["weekDay"] = weekDay
        if (activityItemId != null) params["activityItemId"] = activityItemId
        return ApiClient.get("v1/clubactivity/querySemesterClubActivity", params)
    }

    suspend fun getMySemesterActivities(): Map<String, Any?> {
        return ApiClient.get("v1/clubactivity/queryMySemesterClubActivity")
    }

    suspend fun getSignInTf(studentId: Int? = null): Map<String, Any?> {
        val sid = studentId ?: AppContext.user.studentId
        return ApiClient.get("v1/clubactivity/getSignInTf", mapOf("studentId" to sid))
    }

    suspend fun signInOrBack(
        activityId: Int,
        latitude: String,
        longitude: String,
        signType: String = "1",
        studentId: Int? = null
    ): Map<String, Any?> {
        val sid = studentId ?: AppContext.user.studentId
        return ApiClient.post("v1/clubactivity/signInOrSignBack", mapOf(
            "activityId" to activityId,
            "latitude" to latitude,
            "longitude" to longitude,
            "signType" to signType,
            "studentId" to sid
        ))
    }

    suspend fun signApply(
        activityId: Int,
        reason: String,
        applyType: String = "1",
        latitude: String = "",
        longitude: String = "",
        pic1: String = "", pic2: String = "", pic3: String = "",
        studentId: Int? = null, schoolId: Int? = null
    ): Map<String, Any?> {
        val sid = studentId ?: AppContext.user.studentId
        val scid = schoolId ?: AppContext.user.schoolId
        return ApiClient.post("v1/clubactivity/signApply", mapOf(
            "activityId" to activityId,
            "applyType" to applyType,
            "latitude" to latitude,
            "longitude" to longitude,
            "pic1" to pic1, "pic2" to pic2, "pic3" to pic3,
            "reason" to reason,
            "schoolId" to scid,
            "studentId" to sid
        ))
    }

    suspend fun getHomeClub(): Map<String, Any?> {
        return ApiClient.get("v1/clubactivity/querySchoolActivityTopThree")
    }

    suspend fun getApplyIntroduce(): Map<String, Any?> {
        return ApiClient.get("v1/clubactivity/getApplyIntroduce")
    }

    suspend fun countValidSignUp(studentId: Int? = null): Map<String, Any?> {
        val params = mutableMapOf<String, Any?>()
        if (studentId != null) params["studentId"] = studentId
        else if (AppContext.user.studentId != 0) params["studentId"] = AppContext.user.studentId
        return ApiClient.get("v1/clubactivity/countValidSignUp", params)
    }
}
