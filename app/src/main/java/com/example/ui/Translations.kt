package com.example.ui

import java.util.Locale

object Translations {

    /**
     * وظيفة ذكية لجلب اللغة الافتراضية للجهاز عند أول تشغيل
     */
    fun getSystemLanguage(): AppLanguage {
        val systemLang = Locale.getDefault().language
        return when (systemLang) {
            "ar" -> AppLanguage.ARABIC
            else -> AppLanguage.ENGLISH // لغات أخرى مثل الفرنسية ستفتح بالإنجليزية افتراضياً
        }
    }

    /**
     * المحرك الرئيسي لجلب النصوص بناءً على المفتاح واللغة المختارة
     */
    fun getString(key: String, lang: AppLanguage): String {
        val isAr = lang == AppLanguage.ARABIC

        return when (key) {
            // القائمة الجانبية (Sidebar)
            "app_name" -> if (isAr) "درع التلفزيون" else "TV SHIELD"
            "dashboard" -> if (isAr) "لوحة التحكم" else "Dashboard"
            "ram_booster" -> if (isAr) "مسرع التربو" else "Turbo Boost"
            "storage_cleaner" -> if (isAr) "منظف المساحة" else "Cleaner"
            "network_tools" -> if (isAr) "الشبكة" else "Network"
            "scheduler" -> if (isAr) "المجدول الآلي" else "Scheduler"
            "history" -> if (isAr) "سجل التنظيف" else "History"
            "about" -> if (isAr) "عن الجهاز" else "Blueprint"

            // لوحة الصحة (HUD)
            "system_health" -> if (isAr) "حالة النظام" else "System Health"
            "largest_app" -> if (isAr) "أكبر تطبيق" else "Largest App"
            "boost" -> if (isAr) "تسريع" else "Boost"
            "restart" -> if (isAr) "إعادة تشغيل" else "Restart"

            // بطاقات الإحصائيات (Status Cards)
            "memory" -> if (isAr) "الذاكرة (RAM)" else "Memory"
            "storage" -> if (isAr) "التخزين" else "Storage"
            "free" -> if (isAr) "متوفر" else "Free"
            "mb" -> if (isAr) "ميجابايت" else "MB"
            "gb" -> if (isAr) "جيجابايت" else "GB"

            // رسائل النظام
            "cleaning_msg" -> if (isAr) "جاري تحسين الأداء..." else "Optimizing performance..."
            "done_msg" -> if (isAr) "اكتمل التنظيف!" else "Cleaning Completed!"

            else -> key // في حال عدم وجود المفتاح، يتم عرض الكلمة الأصلية
        }
    }
}