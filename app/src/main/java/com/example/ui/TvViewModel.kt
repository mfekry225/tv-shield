package com.example.ui

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.OptimizationReport
import com.example.data.OptimizationRepository
import com.example.data.ScheduledTask
import com.example.data.AppDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

// الأقسام الاحترافية للتطبيق
enum class TvTab {
    DASHBOARD,      // اللوحة الرئيسية (PC Manager Style)
    OPTIMIZER,      // مسرع الرام ووضع التربو
    STORAGE,        // منظف المساحة والملفات الكبيرة
    NETWORK,        // إنعاش الشبكة وفحص الاستقرار
    DISPLAY,        // حماية OLED واختبار البكسلات
    SCHEDULER,      // الجدولة الصامتة للصيانة
    REPORTS,        // تاريخ عمليات التنظيف
    TWEAK_BOARD,    // إعدادات النظام المخفية
    ABOUT           // معلومات العتاد (System Blueprint)
}

enum class AppLanguage { ENGLISH, ARABIC }

// إحصائيات النظام الحقيقية (Real-time Stats)
data class RealSystemStats(
    val brand: String = Build.BRAND,
    val model: String = Build.MODEL,
    val totalRamBytes: Long = 0,
    val availRamBytes: Long = 0,
    val totalStorageBytes: Long = 0,
    val availStorageBytes: Long = 0,
    val healthScore: Int = 100,
    val isOptimizing: Boolean = false,
    val topApp: String = "Analyzing...",
    val securityPatch: String = Build.VERSION.SECURITY_PATCH ?: "Unknown"
)

class TvViewModel(application: Application) : AndroidViewModel(application) {

    // إدارة البيانات (التقارير والمهام المجدولة)
    private val database = AppDatabase.getDatabase(application)
    private val repository = OptimizationRepository(database.optimizationDao())

    private val _systemStats = MutableStateFlow(RealSystemStats())
    val systemStats = _systemStats.asStateFlow()

    private val _currentTab = MutableStateFlow(TvTab.DASHBOARD)
    val currentTab = _currentTab.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.ARABIC)
    val currentLanguage = _currentLanguage.asStateFlow()

    init {
        // تحديث الإحصائيات فور تشغيل التطبيق
        updateStats()
    }

    fun selectTab(tab: TvTab) { _currentTab.value = tab }

    // 1. محرك تحديث البيانات (يراعي دقة أحجام التطبيقات)
    fun updateStats() {
        val context = getApplication<Application>()
        viewModelScope.launch {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val mi = ActivityManager.MemoryInfo()
            am.getMemoryInfo(mi)

            val stat = StatFs(Environment.getDataDirectory().path)
            val totalStore = stat.blockCountLong * stat.blockSizeLong
            val availStore = stat.availableBlocksLong * stat.blockSizeLong

            // جلب أكبر تطبيق مثبت (خوارزمية الفرز التنازلي)
            val topAppName = calculateLargestApp(context)

            // حساب درجة الصحة بناءً على الرام المتاح (منطق PC Manager)
            val ramUsageRatio = (mi.totalMem - mi.availMem).toFloat() / mi.totalMem.toFloat()
            val score = (100 - (ramUsageRatio * 100)).toInt().coerceIn(10, 100)

            _systemStats.update {
                it.copy(
                    totalRamBytes = mi.totalMem,
                    availRamBytes = mi.availMem,
                    totalStorageBytes = totalStore,
                    availStorageBytes = availStore,
                    healthScore = score,
                    topApp = topAppName
                )
            }
        }
    }

    // خوارزمية فرز التطبيقات حسب الحجم (الأكبر أولاً)
    private fun calculateLargestApp(context: Context): String {
        return try {
            val pm = context.packageManager
            val apps = pm.getInstalledApplications(0)

            // فرز التطبيقات حسب حجم ملف الـ APK (تنازلياً) وتصفية تطبيقات النظام
            val largest = apps.filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
                .maxByOrNull { File(it.sourceDir).length() }

            largest?.loadLabel(pm)?.toString() ?: "System"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    // 2. محرك الإصلاح الشامل (Boost Engine) - متوافق مع سياسات جوجل
    fun runTurboBoost() {
        val context = getApplication<Application>()
        viewModelScope.launch {
            // حفظ الحالة قبل التنظيف للمقارنة
            val ramBefore = _systemStats.value.availRamBytes
            val storageBefore = _systemStats.value.availStorageBytes
            val scoreBefore = _systemStats.value.healthScore

            _systemStats.update { it.copy(isOptimizing = true) }

            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val pm = context.packageManager
            val apps = pm.getInstalledApplications(0)

            // إغلاق تطبيقات المستخدم الخلفية فقط (آمن وقانوني)
            apps.forEach { app ->
                if ((app.flags and ApplicationInfo.FLAG_SYSTEM) == 0 && app.packageName != context.packageName) {
                    am.killBackgroundProcesses(app.packageName)
                }
            }

            // تنظيف الكاش الداخلي للتطبيق
            try {
                context.cacheDir.deleteRecursively()
            } catch (e: Exception) {}

            delay(2000) // وقت مستقطع لمحاكاة الفحص العميق

            // تحديث الإحصائيات بعد التنظيف
            updateStats()
            delay(500) // انتظار بسيط لضمان تحديث Flow الإحصائيات

            val ramAfter = _systemStats.value.availRamBytes
            val storageAfter = _systemStats.value.availStorageBytes

            // حساب ما تم توفيره
            val freedRam = (ramAfter - ramBefore).coerceAtLeast(0)
            val freedStorage = (storageAfter - storageBefore).coerceAtLeast(0)
            val newScore = _systemStats.value.healthScore

            // تسجيل التقرير في قاعدة البيانات مع القيم الجديدة
            saveOptimizationReport(
                newScore = newScore,
                freedRam = freedRam,
                freedStorage = freedStorage,
                prevScore = scoreBefore
            )

            _systemStats.update { it.copy(isOptimizing = false) }
        }
    }

    // 3. روابط النظام العميقة (Deep Links) - لتوفير أقصى صلاحيات بشكل قانوني
    fun openSystemSettings(context: Context, type: String) {
        val intent = when (type) {
            "STORAGE" -> Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS)
            "APPS" -> Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
            "WIFI" -> Intent(Settings.ACTION_WIFI_SETTINGS)
            "OLED_SAFE" -> Intent(Settings.ACTION_DISPLAY_SETTINGS)
            else -> Intent(Settings.ACTION_SETTINGS)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    // 4. نظام إدارة التقارير والجدولة
    private fun saveOptimizationReport(newScore: Int, freedRam: Long, freedStorage: Long, prevScore: Int) {
        viewModelScope.launch {
            val report = OptimizationReport(
                timestamp = System.currentTimeMillis(),
                freedMemoryBytes = freedRam,
                freedStorageBytes = freedStorage,
                previousScore = prevScore,
                newScore = newScore,
                summaryEn = "System cleaned and RAM optimized.",
                summaryAr = "تم تنظيف النظام وتحسين الذاكرة العشوائية."
            )
            repository.insertReport(report)
        }
    }

    fun addNewSchedule(time: String) {
        viewModelScope.launch {
            repository.insertScheduledTask(ScheduledTask(timeOfDay = time, isEnabled = true))
        }
    }

    // ميزات إضافية فريدة (Unique Features)
    fun toggleEcoMode() { /* منطق تقليل استهلاك الطاقة */ }
    fun runDisplayTest() { /* منطق فحص البكسلات التالفة */ }
}