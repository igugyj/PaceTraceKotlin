package com.pacetrace.app.ui.profile

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
import com.pacetrace.app.api.AuthApi
import com.pacetrace.app.api.RunApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val user = AppContext.user
    var runInfo by remember { mutableStateOf<Map<String, Any?>?>(null) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullToRefreshState()

    val loadData: suspend () -> Unit = {
        runInfo = RunApi.getRunInfo()
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

    Box(Modifier.nestedScroll(pullRefreshState.nestedScrollConnection).fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(user.studentName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("${user.schoolName} | ${user.className} | 学号 ${user.registerCode}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        item {
            Text("运动统计", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        if (loading) {
            item {
                Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        } else {
            val rd = runInfo?.get("response") as? Map<String, Any?> ?: emptyMap()
            item {
                Text("有效天数: ${rd["runValidDay"] ?: 0}", style = MaterialTheme.typography.bodyLarge)
            }
            item {
                Text("总距离(米): ${rd["runValidDistance"] ?: 0}", style = MaterialTheme.typography.bodyLarge)
            }
            item {
                Text("配速: ${rd["showSpeed"] ?: "-"}", style = MaterialTheme.typography.bodyLarge)
            }
        }
        /*
        item {
            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))
            Text("行迹 PaceTrace", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("校园跑管理工具", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        */

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("授权协议", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("CC BY-NC 4.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium)
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("合理使用声明", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("1. 本软件仅供个人学习、研究使用",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("2. 使用者应遵守所在学校的校园跑相关规定",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("3. 开发者不对因使用本软件产生的任何后果承担责任",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("4. 本软件不收集、上传任何用户个人信息",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("5. 所有数据仅存储在用户本地设备",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("致谢", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("maps from yanyaoli/byerun-web (CC BY-NC 4.0)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("core idea from jysafe.cn",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("coding by opencode (DeepSeek)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("and YOU",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        }
        PullToRefreshContainer(state = pullRefreshState, modifier = Modifier.align(Alignment.TopCenter))
    }
}
