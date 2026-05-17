package com.pacetrace.app.api

object RunApi {
    suspend fun getRunInfo(): Map<String, Any?> {
        return ApiClient.get("v1/unirun/query/student/run/info")
    }

    suspend fun getRunStandard(schoolId: Int? = null): Map<String, Any?> {
        val params = mutableMapOf<String, Any?>()
        if (schoolId != null) params["schoolId"] = schoolId
        else if (AppContext.user.schoolId != 0) params["schoolId"] = AppContext.user.schoolId
        return ApiClient.get("v1/unirun/query/runStandard", params)
    }

    suspend fun getSchoolArea(schoolId: Int? = null): Map<String, Any?> {
        val sid = schoolId ?: AppContext.user.schoolId
        return ApiClient.get("v1/unirun/querySchoolBound", mapOf("schoolId" to sid))
    }

    suspend fun getRunRecords(page: Int = 1, size: Int = 10): Map<String, Any?> {
        return ApiClient.get("v1/unirun/query/run/record", mapOf(
            "pageNum" to page,
            "pageSize" to size
        ))
    }

    suspend fun getRunRecordsByUser(
        userId: Int? = null,
        yearSemester: Int? = null,
        page: Int = 1,
        size: Int = 10
    ): Map<String, Any?> {
        val params = mutableMapOf("pageNum" to page, "pageSize" to size)
        if (userId != null) params["userId"] = userId
        else if (AppContext.user.userId != 0) params["userId"] = AppContext.user.userId
        if (yearSemester != null) params["yearSemester"] = yearSemester
        return ApiClient.get("v1/unirun/query/run/record", params)
    }

    suspend fun getRunSemesterInfo(
        userId: Int? = null,
        yearSemester: String? = null
    ): Map<String, Any?> {
        val params = mutableMapOf<String, Any?>()
        if (userId != null) params["userId"] = userId
        else if (AppContext.user.userId != 0) params["userId"] = AppContext.user.userId
        if (yearSemester != null) params["yearSemester"] = yearSemester
        return ApiClient.get("v1/unirun/query/runInfo", params)
    }

    suspend fun getOnceRunRecord(recordId: Int, studentId: Int? = null): Map<String, Any?> {
        val sid = studentId ?: AppContext.user.studentId
        return ApiClient.get("v1/unirun/query/student/record/info", mapOf(
            "recordId" to recordId,
            "studentId" to sid
        ))
    }

    suspend fun getRecordTrack(recordId: Int): Map<String, Any?> {
        return ApiClient.get("v1/unirun/query/student/record/track", mapOf("recordId" to recordId))
    }

    suspend fun startRun(studentId: Int? = null): Map<String, Any?> {
        val sid = studentId ?: AppContext.user.studentId
        return ApiClient.get("v1/push/startRun", mapOf("studentId" to sid))
    }

    suspend fun saveRunRecordV2(
        userId: Int? = null,
        distance: Int = 0,
        time: Int = 0,
        trackPoints: String = "",
        realityTrack: String = "",
        innerSchool: String = "1",
        yearSemester: String = "",
        recordDate: String = "",
        againRunStatus: String = "",
        againRunTime: Int = 0,
        distanceTimeStatus: String = "1",
        vocalStatus: String = ""
    ): Map<String, Any?> {
        val uid = userId ?: AppContext.user.userId
        return ApiClient.post("v1/unirun/save/run/record/new", mapOf(
            "againRunStatus" to againRunStatus,
            "againRunTime" to againRunTime,
            "appVersions" to "1.8.5",
            "brand" to "Xiaomi",
            "mobileType" to "Mi 10",
            "sysVersions" to "12",
            "trackPoints" to trackPoints,
            "distanceTimeStatus" to distanceTimeStatus,
            "innerSchool" to innerSchool,
            "runDistance" to distance,
            "runTime" to time,
            "userId" to uid,
            "vocalStatus" to vocalStatus,
            "yearSemester" to yearSemester,
            "recordDate" to recordDate,
            "realityTrackPoints" to realityTrack
        ))
    }

    suspend fun getRank(orderType: String = "distance"): Map<String, Any?> {
        return ApiClient.get("v1/unirun/querySchoolYearSemesterOrder", mapOf("orderType" to orderType))
    }

    suspend fun getHomeRank(): Map<String, Any?> {
        return ApiClient.get("v1/unirun/queryStudentRunOrder")
    }

    suspend fun getBannerList(schoolId: Int? = null): Map<String, Any?> {
        val sid = schoolId ?: AppContext.user.schoolId
        return ApiClient.get("v1/banner/getAllBannerBySchoolId", mapOf("schoolId" to sid))
    }

    suspend fun getSportsScore(): Map<String, Any?> {
        return ApiClient.get("v1/sports/class/getStudentSportsScoreDetail")
    }

    suspend fun getSemesterDetail(): Map<String, Any?> {
        return ApiClient.get("v1/sports/class/student/semester/detail")
    }

    suspend fun getSemesterScore(): Map<String, Any?> {
        return ApiClient.get("v1/sports/class/student/semester/score")
    }
}
