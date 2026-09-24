package com.example.ui

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*

@Composable
fun TvDashboard(viewModel: TvViewModel) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val stats by viewModel.systemStats.collectAsStateWithLifecycle()

    // التعامل مع زر الرجوع في الريموت
    BackHandler(enabled = currentTab != TvTab.DASHBOARD) {
        viewModel.selectTab(TvTab.DASHBOARD)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A0A))) {
        Row(modifier = Modifier.fillMaxSize()) {

            // --- القائمة الجانبية (Sidebar) ---
            TvSidebar(
                selectedTab = currentTab,
                onTabSelected = { viewModel.selectTab(it) },
                modifier = Modifier
                    .width(240.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF121212))
                    .padding(vertical = 16.dp, horizontal = 12.dp)
            )

            // --- محتوى الصفحات ---
            Box(modifier = Modifier.weight(1f).padding(24.dp)) {
                when (currentTab) {
                    TvTab.DASHBOARD -> DashboardContent(viewModel, stats, lang, context)
                    TvTab.OPTIMIZER -> OptimizerContent(viewModel)
                    TvTab.STORAGE -> StorageContent(viewModel, context)
                    TvTab.NETWORK -> NetworkContent(viewModel)
                    TvTab.SCHEDULER -> Text("Maintenance Scheduler coming soon", color = Color.White)
                    TvTab.REPORTS -> Text("Cleaning History coming soon", color = Color.White)
                    else -> Text("Section Under Construction", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun DashboardContent(viewModel: TvViewModel, stats: RealSystemStats, lang: AppLanguage, context: Context) {
    val isArabic = lang == AppLanguage.ARABIC

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = if (isArabic) "درع التلفزيون الذكي" else "Smart TV Shield",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        item {
            // --- المربع البنفسجي الساكن (HUD) - مستوحى من PC Manager ---
            Card(
                modifier = Modifier.fillMaxWidth().height(180.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF6200EA)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // عرض النسبة المئوية للصحة بشكل ساكن
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) "حالة النظام" else "System Health",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${stats.healthScore}%",
                            color = Color.White,
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // إحصائيات سريعة وأزرار التحكم
                    Column(
                        modifier = Modifier.weight(1.2f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = if (isArabic) "أكبر تطبيق: ${stats.topApp}" else "Largest App: ${stats.topApp}",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // زر التسريع (Boost)
                            TvDashboardButton(
                                text = if (isArabic) "تسريع" else "Boost",
                                icon = Icons.Default.PlayArrow,
                                color = Color.White.copy(alpha = 0.2f),
                                onClick = { viewModel.runTurboBoost() }
                            )
                            // زر إعادة التشغيل (Restart)
                            TvDashboardButton(
                                text = if (isArabic) "إعادة تشغيل" else "Restart",
                                icon = Icons.Default.Refresh,
                                color = Color.Red.copy(alpha = 0.4f),
                                onClick = { viewModel.openSystemSettings(context, "RESTART") }
                            )
                        }
                    }
                }
            }
        }

        item {
            // صف البطاقات السريعة (RAM & Storage)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatusCard(
                    title = if (isArabic) "الذاكرة (RAM)" else "Memory",
                    value = "${stats.availRamBytes / 1024 / 1024} MB Free",
                    icon = Icons.Default.Build,
                    color = Color(0xFF00B0FF),
                    modifier = Modifier.weight(1f)
                )
                StatusCard(
                    title = if (isArabic) "التخزين" else "Storage",
                    value = "${stats.availStorageBytes / 1024 / 1024 / 1024} GB Free",
                    icon = Icons.Default.Delete,
                    color = Color(0xFFFFAB00),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TvSidebar(selectedTab: TvTab, onTabSelected: (TvTab) -> Unit, modifier: Modifier) {
    val menuItems = listOf(
        TvTabItem(TvTab.DASHBOARD, "Dashboard", Icons.Default.Home),
        TvTabItem(TvTab.OPTIMIZER, "Turbo Boost", Icons.Default.Build),
        TvTabItem(TvTab.STORAGE, "Cleaner", Icons.Default.Delete),
        TvTabItem(TvTab.NETWORK, "Network", Icons.Default.Refresh),
        TvTabItem(TvTab.SCHEDULER, "Scheduler", Icons.Default.DateRange),
        TvTabItem(TvTab.REPORTS, "History", Icons.AutoMirrored.Filled.List),
        TvTabItem(TvTab.ABOUT, "Blueprint", Icons.Default.Info)
    )

    Column(modifier = modifier) {
        Text("TV SHIELD", color = Color(0xFF6200EA), fontWeight = FontWeight.Black, fontSize = 20.sp, modifier = Modifier.padding(8.dp))
        Spacer(modifier = Modifier.height(20.dp))

        menuItems.forEach { item ->
            var isFocused by remember { mutableStateOf(false) }
            val isSelected = selectedTab == item.tab

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) Color(0xFF6200EA).copy(alpha = 0.2f) else if (isFocused) Color.White.copy(alpha = 0.05f) else Color.Transparent)
                    .onFocusChanged { isFocused = it.isFocused }
                    .clickable { onTabSelected(item.tab) }
                    .focusable()
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(item.icon, null, tint = if (isSelected || isFocused) Color(0xFF6200EA) else Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(item.title, color = if (isSelected || isFocused) Color.White else Color.Gray, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun TvDashboardButton(text: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    Surface(
        onClick = onClick,
        modifier = Modifier.height(36.dp).onFocusChanged { focused = it.isFocused },
        color = if (focused) Color.White else color,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = if (focused) Color.Black else Color.White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, color = if (focused) Color.Black else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StatusCard(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = Color.Gray, fontSize = 12.sp)
            Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable fun OptimizerContent(vm: TvViewModel) { Column { Text("Booster Engine Active", color = Color.White); Button(onClick = { vm.runTurboBoost() }) { Text("Run Boost") } } }
@Composable fun StorageContent(vm: TvViewModel, context: Context) { Column { Text("Storage Manager", color = Color.White); Button(onClick = { vm.openSystemSettings(context, "STORAGE") }) { Text("Clean Storage") } } }
@Composable fun NetworkContent(vm: TvViewModel) { Column { Text("Network Health", color = Color.White); Button(onClick = { vm.updateStats() }) { Text("Refresh Network") } } }

data class TvTabItem(val tab: TvTab, val title: String, val icon: ImageVector)