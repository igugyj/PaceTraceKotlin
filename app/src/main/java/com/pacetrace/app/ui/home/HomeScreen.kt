package com.pacetrace.app.ui.home

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
import com.pacetrace.app.api.*
import com.pacetrace.app.api.ClubApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var runInfo by remember { mutableStateOf<Map<String, Any?>?>(null) }
    var records by remember { mutableStateOf<List<Any?>?>(null) }
    var hotClubs by remember { mutableStateOf<List<Any?>?>(null) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullToRefreshState()

    val loadData: suspend () -> Unit = {
        runInfo = RunApi.getRunInfo()
        val recResp = RunApi.getRunRecords(1, 10)
        records = recResp["response"] as? List<Any?>
        val hcResp = ClubApi.getHomeClub()
        hotClubs = hcResp["response"] as? List<Any?>
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

    val rd = runInfo?.get("response") as? Map<String, Any?> ?: emptyMap()

    Box(Modifier.nestedScroll(pullRefreshState.nestedScrollConnection).fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("运动概览", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("跑步天数: ${rd["runValidDay"] ?: 0}", style = MaterialTheme.typography.bodyLarge)
            Text("总距离(米): ${rd["runValidDistance"] ?: 0}", style = MaterialTheme.typography.bodyLarge)
            Text("配速: ${rd["showSpeed"] ?: "-"}", style = MaterialTheme.typography.bodyLarge)
        }

        item {
            Spacer(Modifier.height(8.dp))
            Text("最近跑步", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        val rl = records ?: emptyList()
        if (rl.isEmpty()) {
            item { Text("暂无记录", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            items(rl.take(5)) { r ->
                val rm = r as? Map<String, Any?> ?: return@items
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            "${rm["recordDate"] ?: ""}  ${rm["defeatedInfo"] ?: ""}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "${rm["runValidDistance"]}m | ${rm["runValidTime"]}min | ${rm["runSpeed"]}m/min",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            Text("俱乐部热门", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        val hl = hotClubs ?: emptyList()
        if (hl.isEmpty()) {
            item { Text("暂无热门活动", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            items(hl) { a ->
                val am = a as? Map<String, Any?> ?: return@items
                val full = (am["applyStudentCount"] as? Number)?.toInt() ?: 0 >= (am["maxStudent"] as? Number)?.toInt() ?: 1
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(am["activityName"] as? String ?: "", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(8.dp))
                            if (full) {
                                Surface(color = MaterialTheme.colorScheme.error.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small) {
                                    Text("已满", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                                }
                            } else {
                                Surface(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small) {
                                    Text("可报", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                }
                            }
                        }
                        Text(
                            "${am["startTime"]} | ${am["addressDetail"]} | ${am["applyStudentCount"]}/${am["maxStudent"]}",
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

