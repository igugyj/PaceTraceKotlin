package com.pacetrace.app.ui.run

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pacetrace.app.api.AppContext
import com.pacetrace.app.api.RunApi
import com.pacetrace.app.lib.GeoUtil
import com.pacetrace.app.lib.MapsUtil
import com.pacetrace.app.PaceTraceApp
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    var runInfo by remember { mutableStateOf<Map<String, Any?>?>(null) }
    var std by remember { mutableStateOf<Map<String, Any?>?>(null) }
    var semInfo by remember { mutableStateOf<Map<String, Any?>?>(null) }
    var records by remember { mutableStateOf<List<Any?>?>(null) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullToRefreshState()

    val loadData: suspend () -> Unit = {
        runInfo = RunApi.getRunInfo()
        val stdResp = RunApi.getRunStandard()
        std = stdResp["response"] as? Map<String, Any?>
        val semester = (std?.get("semesterYear") as? String) ?: ""
        if (semester.isNotEmpty()) {
            val semResp = RunApi.getRunSemesterInfo(yearSemester = semester)
            semInfo = semResp["response"] as? Map<String, Any?>
        }
        val recResp = RunApi.getRunRecords(1, 10)
        records = recResp["response"] as? List<Any?>
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

    val maps = remember { MapsUtil.loadMaps(context) }
    val stdData = std ?: emptyMap()
    val gender = if (AppContext.user.gender == "2") "girl" else "boy"
    val onceMin = (stdData["${gender}OnceDistanceMin"] as? Number)?.toInt() ?: 1000
    val onceMax = (stdData["${gender}OnceDistanceMax"] as? Number)?.toInt() ?: 5000
    val totalDist = (stdData["${gender}AllRunDistance"] as? Number)?.toInt() ?: 80000
    val totalTimes = (stdData["${gender}AllRunTime"] as? Number)?.toInt() ?: 24
    val semester = (stdData["semesterYear"] as? String) ?: ""

    val semData = semInfo ?: emptyMap()
    val curDist = (semData["runValidDistance"] as? Number)?.toInt() ?: 0
    val curDays = (semData["runValidDay"] as? Number)?.toInt() ?: 0

    val initDist = Random.nextInt(onceMin, onceMax + 1)
    val initPace = Random.nextFloat() * 3f + 4f
    val initDuration = maxOf(1, minOf(180, (initDist * 0.06f / initPace).toInt()))

    var selectedMap by remember(maps) { mutableStateOf(maps.getOrNull(0)) }
    var distance by remember { mutableStateOf(initDist) }
    var duration by remember { mutableStateOf(initDuration) }
    var submitting by remember { mutableStateOf(false) }
    var resultMsg by remember { mutableStateOf<String?>(null) }

    Box(Modifier.nestedScroll(pullRefreshState.nestedScrollConnection).fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("跑步", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("学校标准", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Text("单次 ${onceMin}-${onceMax}米")
                        Text("学期 ${totalDist / 1000}公里/${totalTimes}次")
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("学期进度", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    val distPct = if (totalDist > 0) (curDist.toFloat() / totalDist).coerceAtMost(1f) else 0f
                    val daysPct = if (totalTimes > 0) (curDays.toFloat() / totalTimes).coerceAtMost(1f) else 0f
                    Text("已完成距离: ${curDist}米 (${(distPct * 100).toInt()}%)")
                    LinearProgressIndicator(progress = { distPct }, modifier = Modifier.fillMaxWidth().height(6.dp))
                    Spacer(Modifier.height(4.dp))
                    Text("已完成次数: ${curDays}次 (${(daysPct * 100).toInt()}%)")
                    LinearProgressIndicator(progress = { daysPct }, modifier = Modifier.fillMaxWidth().height(6.dp))
                }
            }
        }

        if (maps.isNotEmpty()) {
            val currentMap = selectedMap ?: maps[0]
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text("提交跑步记录", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))

                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                            OutlinedTextField(
                                value = currentMap.name,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("选择路线") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                maps.forEach { m ->
                                    DropdownMenuItem(
                                        text = { Text(m.name) },
                                        onClick = { selectedMap = m; expanded = false }
                                    )
                                }
                            }
                        }

                        val fullDist = GeoUtil.routeDistance(currentMap.coords)
                        Text("路线全长约 ${fullDist} 米", style = MaterialTheme.typography.bodySmall)

                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = distance.toString(),
                                onValueChange = { distance = it.toIntOrNull() ?: distance },
                                label = { Text("距离(米)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = duration.toString(),
                                onValueChange = { duration = it.toIntOrNull() ?: duration },
                                label = { Text("时长(分钟)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        if (duration > 0) {
                            val speedKmh = (distance.toFloat() / (duration * 60f)) * 3.6f
                            if (speedKmh > 0) {
                                Text("配速约 ${"%.1f".format(speedKmh)} km/h",
                                    color = if (speedKmh > 12) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        if (resultMsg != null) {
                            Text(resultMsg!!, color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(4.dp))
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    submitting = true
                                    resultMsg = null
                                    try {
                                        val sm = selectedMap ?: return@launch
                                        val track = GeoUtil.buildTrack(sm.coords, distance)
                                        val tsBase = System.currentTimeMillis()
                                        val interval = ((duration * 60 * 1000L) / track.size.coerceAtLeast(1)).toInt()
                                        val ptsStr = track.mapIndexed { i, p ->
                                            "${p.second}-${p.first}-${tsBase + i * interval.toLong()}-${Random.nextInt(5, 10)}"
                                        }
                                        val json = com.google.gson.Gson().toJson(ptsStr)
                                        val resp = RunApi.saveRunRecordV2(
                                            distance = distance,
                                            time = duration,
                                            trackPoints = json,
                                            vocalStatus = "1",
                                            recordDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                            yearSemester = semester
                                        )
                                        val code = (resp["code"] as? Number)?.toInt()
                                        if (code == 10000) {
                                            resultMsg = "提交成功!"
                                            runInfo = RunApi.getRunInfo()
                                            val recResp2 = RunApi.getRunRecords(1, 10)
                                            records = recResp2["response"] as? List<Any?>
                                        } else {
                                            resultMsg = "提交失败: ${resp["msg"]}"
                                        }
                                    } catch (e: Exception) {
                                        resultMsg = "错误: ${e.message}"
                                    } finally {
                                        submitting = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !submitting
                        ) {
                            if (submitting) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            else Text("提交跑步记录")
                        }
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            Text("跑步记录", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        val rl = records ?: emptyList()
        if (rl.isEmpty()) {
            item { Text("暂无记录", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            items(rl.size) { idx ->
                val r = rl[idx] as? Map<String, Any?> ?: return@items
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            "${r["recordDate"] ?: ""}  ${r["defeatedInfo"] ?: ""}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "${r["runValidDistance"]}m | ${r["runValidTime"]}min | ${r["runSpeed"]}m/min",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        }
        PullToRefreshContainer(state = pullRefreshState, modifier = Modifier.align(Alignment.TopCenter))
    }
}

