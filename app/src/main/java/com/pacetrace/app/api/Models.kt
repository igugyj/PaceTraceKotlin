package com.pacetrace.app.api

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    val code: Int = 0,
    val msg: String = "",
    val response: Any? = null
)

data class OauthToken(
    val token: String = "",
    val refreshToken: String = ""
)

data class User(
    val userId: Int = 0,
    val studentId: Int = 0,
    val schoolId: Int = 0,
    val studentName: String = "",
    val schoolName: String = "",
    val gender: String = "",
    val oauthToken: OauthToken? = null,
    val classId: Int = 0,
    val className: String = "",
    val collegeCode: String = "",
    val collegeName: String = "",
    val majorCode: String = "",
    val majorName: String = "",
    val registerCode: String = "",
    val startSchool: Int = 0,
    val studentClass: Int = 0,
    val userVerifyStatus: String = "",
    val birthday: String = "",
    val idCardNo: String = "",
    val nationCode: String = "",
    val studentSource: String = "",
    val addrDetail: String = "",
    val mark: Int = 0,
    val grade: Int = 0,
    val gradeName: String = ""
)

data class RunStandard(
    val standardId: Int? = null,
    val schoolId: Int? = null,
    val boyOnceTimeMin: Int? = null,
    val boyOnceTimeMax: Int? = null,
    val boyOnceDistanceMin: Int? = null,
    val boyOnceDistanceMax: Int? = null,
    val boyAllRunDistance: Int? = null,
    val boyAllRunTime: Int? = null,
    val girlOnceTimeMin: Int? = null,
    val girlOnceTimeMax: Int? = null,
    val girlOnceDistanceMin: Int? = null,
    val girlOnceDistanceMax: Int? = null,
    val girlAllRunDistance: Int? = null,
    val girlAllRunTime: Int? = null,
    val firstSemesterDateStart: String? = null,
    val firstSemesterDateEnd: String? = null,
    val secondSemesterDateStart: String? = null,
    val secondSemesterDateEnd: String? = null,
    val vocalVerifyTime: Int? = null,
    val instanceSemester: String? = null,
    val semesterYear: String? = null,
    val boyRunSpeed: Int? = null,
    val girlRunSpeed: Int? = null,
    val boyMaxSpeed: Int? = null,
    val boyMinSpeed: Int? = null,
    val girlMaxSpeed: Int? = null,
    val girlMinSpeed: Int? = null,
    val overSpeedWarn: String? = null,
    val vocalType: String? = null,
    val vocalStartTime: String? = null,
    val vocalEndTime: String? = null,
    val effectiveRangeType: String? = null
)

data class RunInfo(
    val runValidDay: Int = 0,
    val runValidDistance: Int = 0,
    val speed: Int = 0,
    val showSpeed: String = ""
)

data class RunRecord(
    val recordId: Int = 0,
    val userId: Int = 0,
    val studentId: Int = 0,
    val schoolId: Int = 0,
    val yearSemester: Int = 0,
    val recordDate: String = "",
    val recordMonth: String = "",
    val runDistance: Int = 0,
    val runValidDistance: Int = 0,
    val runTime: Int = 0,
    val runValidTime: Int = 0,
    val runSpeed: Int = 0,
    val runCalorie: Int = 0,
    val runValidCalorie: Int = 0,
    val vocalStatus: String = "",
    val runStatus: String = "",
    val defeatedInfo: String = "",
    val createTime: String = "",
    val infoStatus: String = "",
    val runSpeedWarn: String = "",
    val defeatStudentRatio: Int = 0,
    val suspectedStatus: String = "",
    val rangeStatus: String = ""
)

data class SemesterRun(
    val runValidDay: Int = 0,
    val runValidDistance: Int = 0,
    val runCount: Int = 0
)

data class ClubProject(
    val itemId: Int = 0,
    val itemName: String = "",
    val joinNum: Int = 0
)

data class ClubActivity(
    @SerializedName("clubActivityId") val clubActivityId: Int = 0,
    val activityName: String = "",
    val addressDetail: String = "",
    val clubIntroduction: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val teacherName: String = "",
    val maxStudent: Int = 0,
    val signInStudent: Int = 0,
    val signStatus: String = "",
    val cancelSign: String = "",
    val fullActivity: String = "",
    val optionStatus: String = ""
)

data class MyClubActivity(
    @SerializedName("clubActivityId") val clubActivityId: Int = 0,
    val activityName: String = "",
    val activityStatus: String = "",
    val addressDetail: String = "",
    val clubIntroduction: String = "",
    val configurationTimeId: Int = 0,
    val signInStudent: Int = 0,
    val maxStudent: Int = 0,
    val teacherName: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val mmdd: String = "",
    val nextClubActivityId: Int? = null,
    val nextStartTime: String = "",
    val nextEndTime: String = "",
    val nextMmdd: String = "",
    val nextSignInStudent: Int = 0,
    val nextMaxStudent: Int = 0,
    val nextHaveActivity: String = "",
    val currentActivity: String = "",
    val cancelSign: String = "",
    val optionStatus: String = "",
    val signStatus: String = "",
    val clubType: String = "",
    val yearSemester: String = "",
    val signUpId: Int? = null,
    val activityItemId: Int? = null
)

data class SignInTf(
    val signStatus: String = "",
    val activityId: Int? = null,
    val longitude: String? = null,
    val latitude: String? = null,
    val address: String? = null,
    val signInStatus: String? = null,
    val signBackStatus: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val signInTime: String? = null,
    val activityType: String? = null,
    val continueTime: Int? = null,
    val signBackLimitTime: Int? = null,
    val activityName: String? = null
)

data class ClubRecord(
    val configurationId: Int = 0,
    val activityName: String = "",
    val teacherName: String = "",
    val weekDay: Int = 0,
    val yymmdd: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val signStatus: String = ""
)

data class SemesterClubActivity(
    val configurationId: Int = 0,
    val activityName: String = "",
    val addressDetail: String = "",
    val clubIntroduction: String = "",
    val teacherName: String = "",
    val teacherId: Int = 0,
    val studentNum: Int = 0,
    val joinStudentNum: Int = 0,
    val joinStatus: String = "",
    val weekDay: String = "",
    val startDay: String = "",
    val endDay: String = "",
    val startTime: String = "",
    val endTime: String = ""
)

data class HomeClub(
    val clubActivityId: String = "",
    val activityItemId: String = "",
    val itemName: String = "",
    val activityName: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val addressDetail: String = "",
    val maxStudent: String = "",
    val applyStudentCount: String = ""
)

data class MapData(
    val mapId: String = "",
    val mapName: String = "",
    val mapData: List<String> = emptyList()
)

data class RouteMap(
    val id: String,
    val name: String,
    val coords: List<Pair<Double, Double>>
)
