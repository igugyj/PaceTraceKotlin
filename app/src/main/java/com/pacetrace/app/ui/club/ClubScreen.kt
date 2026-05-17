package com.pacetrace.app.ui.club

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pacetrace.app.api.ClubApi
import com.pacetrace.app.lib.GeoUtil
import com.pacetrace.app.lib.TimeUtil
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubScreen() {
    var semesterActs by remember { mutableStateOf<List<Any?>?>(null) }
    var clubTypes by remember { mutableStateOf<List<Any?>?>(null) }
    var myActs by remember { mutableStateOf<List<Any?>?>(null) }
    var signTf by remember { mutableStateOf<Map<String, Any?>?>(null) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullToRefreshState()

    var queryLoading by remember { mutableStateOf(false) }
    var queryDone by remember { mutableStateOf(false) }
    var queriedActivities by remember { mutableStateOf<List<Any?>?>(null) }
    var selectedTypeName by remember { mutableStateOf("") }
    var selectedDateLabel by remember { mutableStateOf("") }
    var typeExpanded by remember { mutableStateOf(false) }
    var dateExpanded by remember { mutableStateOf(false) }

    val loadData: suspend () -> Unit = {
        val saResp = ClubApi.getSemesterActivities()
        semesterActs = saResp["response"] as? List<Any?>
        val ctResp = ClubApi.getClubProjects()
        clubTypes = ctResp["response"] as? List<Any?>
        val maResp = ClubApi.getMyActivities()
        myActs = maResp["response"] as? List<Any?>
        val stResp = ClubApi.getSignInTf()
        signTf = stResp["response"] as? Map<String, Any?>
    }

    LaunchedEffect(Unit) {
        try {
            loadData()
        } catch (_: Exception) {} finally {
            loading = false
        }
    }

    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            try {
                loadData()
            } catch (_: Exception) {}
            pullRefreshState.endRefresh()
        }
    }

    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val signData = signTf ?: emptyMap()
    val curAid = (signData["activityId"] as? Number)?.toInt()
    val tfLat = signData["latitude"] as? String
    val tfLng = signData["longitude"] as? String
    val myIdSet = (myActs ?: emptyList()).mapNotNull {
        ((it as? Map<String, Any?>)?.get("clubActivityId") as? Number)?.toInt()
    }.toSet()

    val weekdayCn = remember { listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日") }
    val dateOptions = remember {
        val opts = mutableListOf<Pair<String, String>>()
        val now = LocalDate.now()
        for (i in 0 until 14) {
            val d = now.plusDays(i.toLong())
            if (d.dayOfWeek.value > 5) continue
            opts.add(
                "${d.format(DateTimeFormatter.ISO_LOCAL_DATE)} ${weekdayCn[d.dayOfWeek.value - 1]}" to
                d.format(DateTimeFormatter.ISO_LOCAL_DATE)
            )
        }
        opts
    }
    val typeOptions = remember(clubTypes) {
        (clubTypes ?: emptyList()).mapNotNull { t ->
            val tm = t as? Map<String, Any?> ?: return@mapNotNull null
            val id = (tm["itemId"] as? Number)?.toInt() ?: return@mapNotNull null
            val name = tm["itemName"] as? String ?: return@mapNotNull null
            name to id
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize().nestedScroll(pullRefreshState.nestedScrollConnection)
    ) { padding ->
        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ── Section 1: 学期项目 ──
                item {
                    Text(
                        "学期项目",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                val semList = semesterActs ?: emptyList()
                if (semList.isEmpty()) {
                    item {
                        Text(
                            "暂无学期项目",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(semList) { s ->
                        val sm = s as? Map<String, Any?> ?: return@items
                        val joined = sm["joinStatus"] == "1"
                        val cid = (sm["configurationId"] as? Number)?.toInt() ?: return@items
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        sm["activityName"] as? String ?: "",
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Surface(
                                        color = if (joined) MaterialTheme.colorScheme.secondary.copy(
                                            alpha = 0.2f
                                        ) else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = MaterialTheme.shapes.small
                                    ) {
                                        Text(
                                            if (joined) "已加入" else "未加入",
                                            modifier = Modifier.padding(
                                                horizontal = 6.dp,
                                                vertical = 2.dp
                                            ),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (joined) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "${sm["addressDetail"]} | ${sm["weekDay"]} ${sm["startTime"]}-${sm["endTime"]} | 人数 ${sm["joinStudentNum"]}/${sm["studentNum"]}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(8.dp))
                                if (!joined) {
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                try {
                                                    val r = ClubApi.joinOrCancelSemester(cid, "add")
                                                    if ((r["code"] as? Number)?.toInt() == 10000) {
                                                        snackbarHostState.showSnackbar("加入成功")
                                                        val saResp = ClubApi.getSemesterActivities()
                                                        semesterActs =
                                                            saResp["response"] as? List<Any?>
                                                    } else {
                                                        snackbarHostState.showSnackbar("加入失败: ${r["msg"] ?: "未知错误"}")
                                                    }
                                                } catch (e: Exception) {
                                                    snackbarHostState.showSnackbar("加入失败: ${e.message}")
                                                }
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) { Text("加入") }
                                } else {
                                    OutlinedButton(
                                        onClick = {
                                            scope.launch {
                                                try {
                                                    val r =
                                                        ClubApi.joinOrCancelSemester(cid, "remove")
                                                    if ((r["code"] as? Number)?.toInt() == 10000) {
                                                        snackbarHostState.showSnackbar("已退出")
                                                        val saResp = ClubApi.getSemesterActivities()
                                                        semesterActs =
                                                            saResp["response"] as? List<Any?>
                                                    } else {
                                                        snackbarHostState.showSnackbar("退出失败: ${r["msg"] ?: "未知错误"}")
                                                    }
                                                } catch (e: Exception) {
                                                    snackbarHostState.showSnackbar("退出失败: ${e.message}")
                                                }
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) { Text("退出") }
                                }
                            }
                        }
                    }
                }

                // ── Section 2: 报名活动 ──
                if (typeOptions.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(4.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "报名活动",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            ExposedDropdownMenuBox(
                                expanded = typeExpanded,
                                onExpandedChange = { typeExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = selectedTypeName.ifEmpty { typeOptions.first().first },
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("项目类型") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            typeExpanded
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth().menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = typeExpanded,
                                    onDismissRequest = { typeExpanded = false }
                                ) {
                                    typeOptions.forEach { (name, _) ->
                                        DropdownMenuItem(
                                            text = { Text(name) },
                                            onClick = {
                                                selectedTypeName = name
                                                typeExpanded = false
                                                queryDone = false
                                                queriedActivities = null
                                            }
                                        )
                                    }
                                }
                            }

                            if (dateOptions.isNotEmpty()) {
                                ExposedDropdownMenuBox(
                                    expanded = dateExpanded,
                                    onExpandedChange = { dateExpanded = it }
                                ) {
                                    OutlinedTextField(
                                        value = selectedDateLabel.ifEmpty { dateOptions.first().first },
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("日期") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                dateExpanded
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth().menuAnchor()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = dateExpanded,
                                        onDismissRequest = { dateExpanded = false }
                                    ) {
                                        dateOptions.forEach { (label, _) ->
                                            DropdownMenuItem(
                                                text = { Text(label) },
                                                onClick = {
                                                    selectedDateLabel = label
                                                    dateExpanded = false
                                                    queryDone = false
                                                    queriedActivities = null
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    val typeEntry =
                                        typeOptions.find { it.first == selectedTypeName }
                                            ?: typeOptions.firstOrNull()
                                    val dateEntry =
                                        dateOptions.find { it.first == selectedDateLabel }
                                            ?: dateOptions.firstOrNull()
                                    if (typeEntry == null) {
                                        scope.launch { snackbarHostState.showSnackbar("请选择项目类型") }
                                        return@Button
                                    }
                                    scope.launch {
                                        queryLoading = true
                                        queryDone = false
                                        try {
                                            val r = ClubApi.getActivityList(
                                                queryTime = dateEntry?.second,
                                                activityItemId = typeEntry.second,
                                                page = 1, size = 50
                                            )
                                            queriedActivities = r["response"] as? List<Any?>
                                            queryDone = true
                                        } catch (e: Exception) {
                                            snackbarHostState.showSnackbar("查询失败: ${e.message}")
                                        } finally {
                                            queryLoading = false
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !queryLoading
                            ) {
                                if (queryLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("查询活动")
                                }
                            }
                        }
                    }

                    val actList = queriedActivities
                    if (actList != null) {
                        if (actList.isEmpty()) {
                            item {
                                Text(
                                    "该日期暂无活动，请换一天试试",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            items(actList) { a ->
                                val am = a as? Map<String, Any?> ?: return@items
                                val aid = (am["clubActivityId"] as? Number)?.toInt() ?: return@items
                                val full = ((am["signInStudent"] as? Number)?.toInt()
                                    ?: 0) >= ((am["maxStudent"] as? Number)?.toInt() ?: 1)
                                val already = aid in myIdSet

                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                am["activityName"] as? String ?: "",
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            val (tagText, tagColor) = when {
                                                full -> "已满" to MaterialTheme.colorScheme.error
                                                already -> "已报名" to MaterialTheme.colorScheme.secondary
                                                else -> "可报" to MaterialTheme.colorScheme.primary
                                            }
                                            Surface(
                                                color = tagColor.copy(alpha = 0.15f),
                                                shape = MaterialTheme.shapes.small
                                            ) {
                                                Text(
                                                    tagText,
                                                    modifier = Modifier.padding(
                                                        horizontal = 6.dp,
                                                        vertical = 2.dp
                                                    ),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = tagColor
                                                )
                                            }
                                        }
                                        Spacer(Modifier.height(4.dp))
                                        val intro = am["clubIntroduction"] as? String ?: ""
                                        Text(
                                            "${am["addressDetail"]} | ${am["teacherName"] ?: ""} | ${am["startTime"]}-${am["endTime"]}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            "已报 ${am["signInStudent"]}/${am["maxStudent"]} 人${if (intro.isNotEmpty()) " | $intro" else ""}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        if (!full && !already) {
                                            Button(
                                                onClick = {
                                                    scope.launch {
                                                        try {
                                                            val r =
                                                                ClubApi.joinActivity(activityId = aid)
                                                            val respData = r["response"]
                                                            val code =
                                                                (r["code"] as? Number)?.toInt()
                                                            var ok = code == 10000
                                                            if (respData is Map<*, *>) {
                                                                ok =
                                                                    ok && (respData["status"] as? String) != "0"
                                                            }
                                                            if (ok) {
                                                                snackbarHostState.showSnackbar("报名成功")
                                                                val maResp =
                                                                    ClubApi.getMyActivities()
                                                                myActs =
                                                                    maResp["response"] as? List<Any?>
                                                            } else {
                                                                val msg =
                                                                    if (respData is Map<*, *>) (respData["message"] as? String)
                                                                        ?: "" else ""
                                                                snackbarHostState.showSnackbar("报名失败: ${msg.ifEmpty { r["msg"] as? String ?: "未知错误" }}")
                                                            }
                                                        } catch (e: Exception) {
                                                            snackbarHostState.showSnackbar("报名失败: ${e.message}")
                                                        }
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            ) { Text("报名") }
                                        }
                                        if (already) {
                                            OutlinedButton(
                                                onClick = {
                                                    scope.launch {
                                                        try {
                                                            val r =
                                                                ClubApi.cancelActivity(activityId = aid)
                                                            val respData = r["response"]
                                                            val code =
                                                                (r["code"] as? Number)?.toInt()
                                                            var ok = code == 10000
                                                            if (respData is Map<*, *>) {
                                                                ok =
                                                                    ok && (respData["status"] as? String) != "0"
                                                            }
                                                            if (ok) {
                                                                snackbarHostState.showSnackbar("已取消报名")
                                                                val maResp =
                                                                    ClubApi.getMyActivities()
                                                                myActs =
                                                                    maResp["response"] as? List<Any?>
                                                            } else {
                                                                val msg =
                                                                    if (respData is Map<*, *>) (respData["message"] as? String)
                                                                        ?: "" else ""
                                                                snackbarHostState.showSnackbar("取消失败: ${msg.ifEmpty { r["msg"] as? String ?: "未知错误" }}")
                                                            }
                                                        } catch (e: Exception) {
                                                            snackbarHostState.showSnackbar("取消失败: ${e.message}")
                                                        }
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            ) { Text("取消报名") }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ── Section 3: 我的活动 ──
                item {
                    Spacer(Modifier.height(4.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "我的活动",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                val myList = myActs ?: emptyList()
                if (myList.isEmpty()) {
                    item {
                        Text(
                            "暂无报名记录",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    val now = LocalDateTime.now()
                    val upcoming = myList.filter { a ->
                        val am = a as? Map<String, Any?> ?: return@filter false
                        val (_, endDt) = TimeUtil.getActivityWindow(
                            am["mmdd"] as? String,
                            am["startTime"] as? String,
                            am["endTime"] as? String
                        )
                        endDt == null || endDt.isAfter(now)
                    }
                    val history = myList.filter { a ->
                        val am = a as? Map<String, Any?> ?: return@filter false
                        val (_, endDt) = TimeUtil.getActivityWindow(
                            am["mmdd"] as? String,
                            am["startTime"] as? String,
                            am["endTime"] as? String
                        )
                        endDt != null && !endDt.isAfter(now)
                    }

                    if (upcoming.isEmpty() && history.isEmpty()) {
                        item {
                            Text(
                                "暂无报名记录",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (upcoming.isNotEmpty()) {
                        item {
                            Text(
                                "待签到 / 待签退",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(upcoming) { a ->
                            val am = a as? Map<String, Any?> ?: return@items
                            val aid = (am["clubActivityId"] as? Number)?.toInt() ?: return@items
                            val isSigned = aid == curAid && signData["signStatus"] == "1"
                            val label = if (isSigned) "签退" else "签到"
                            val signType = if (isSigned) "2" else "1"

                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            am["activityName"] as? String ?: "",
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Surface(
                                            color = if (isSigned) MaterialTheme.colorScheme.tertiary.copy(
                                                alpha = 0.2f
                                            ) else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                            shape = MaterialTheme.shapes.small
                                        ) {
                                            Text(
                                                if (isSigned) "待签退" else "待签到",
                                                modifier = Modifier.padding(
                                                    horizontal = 6.dp,
                                                    vertical = 2.dp
                                                ),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isSigned) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "${am["mmdd"]} ${am["startTime"]}-${am["endTime"]} | ${am["addressDetail"]}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        if (tfLat != null && tfLng != null) {
                                            Button(
                                                onClick = {
                                                    scope.launch {
                                                        try {
                                                            val (latR, lngR) = GeoUtil.randomPoint(
                                                                tfLat,
                                                                tfLng,
                                                                100f
                                                            )
                                                            val r = ClubApi.signInOrBack(
                                                                aid,
                                                                latR,
                                                                lngR,
                                                                signType
                                                            )
                                                            val code =
                                                                (r["code"] as? Number)?.toInt()
                                                            val msg =
                                                                if (code == 10000) "${label}成功" else "${label}失败: ${r["msg"] ?: "未知错误"}"
                                                            snackbarHostState.showSnackbar(msg)
                                                            if (code == 10000) {
                                                                val stResp = ClubApi.getSignInTf()
                                                                signTf =
                                                                    stResp["response"] as? Map<String, Any?>
                                                                val maResp =
                                                                    ClubApi.getMyActivities()
                                                                myActs =
                                                                    maResp["response"] as? List<Any?>
                                                            }
                                                        } catch (e: Exception) {
                                                            snackbarHostState.showSnackbar("${label}失败: ${e.message}")
                                                        }
                                                    }
                                                },
                                                modifier = Modifier.weight(1f)
                                            ) { Text(label) }
                                        } else {
                                            OutlinedButton(
                                                onClick = {},
                                                modifier = Modifier.weight(1f),
                                                enabled = false
                                            ) { Text(label) }
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                scope.launch {
                                                    try {
                                                        val r =
                                                            ClubApi.cancelActivity(activityId = aid)
                                                        val respData = r["response"]
                                                        val code = (r["code"] as? Number)?.toInt()
                                                        var ok = code == 10000
                                                        if (respData is Map<*, *>) {
                                                            ok =
                                                                ok && (respData["status"] as? String) != "0"
                                                        }
                                                        if (ok) {
                                                            snackbarHostState.showSnackbar("已取消报名")
                                                            val maResp = ClubApi.getMyActivities()
                                                            myActs =
                                                                maResp["response"] as? List<Any?>
                                                        } else {
                                                            val msg =
                                                                if (respData is Map<*, *>) (respData["message"] as? String)
                                                                    ?: "" else ""
                                                            snackbarHostState.showSnackbar("取消失败: ${msg.ifEmpty { r["msg"] as? String ?: "未知错误" }}")
                                                        }
                                                    } catch (e: Exception) {
                                                        snackbarHostState.showSnackbar("取消失败: ${e.message}")
                                                    }
                                                }
                                            },
                                            modifier = Modifier.weight(1f)
                                        ) { Text("取消报名") }
                                    }
                                }
                            }
                        }
                    }

                    if (history.isNotEmpty()) {
                        item {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "历史记录 (${history.size} 条)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(history) { a ->
                            val am = a as? Map<String, Any?> ?: return@items
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(
                                        am["activityName"] as? String ?: "",
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "${am["mmdd"]} ${am["startTime"]}-${am["endTime"]} | ${am["addressDetail"]}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
            PullToRefreshContainer(state = pullRefreshState, modifier = Modifier.align(Alignment.TopCenter))
        }
    }
}
