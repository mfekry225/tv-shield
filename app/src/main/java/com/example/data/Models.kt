package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "optimization_reports")
data class OptimizationReport(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val freedMemoryBytes: Long,
    val freedStorageBytes: Long,
    val previousScore: Int,
    val newScore: Int,
    val summaryAr: String,
    val summaryEn: String
)

@Entity(tableName = "scheduled_tasks")
data class ScheduledTask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timeOfDay: String, // e.g., "03:00"
    val isRepeatDaily: Boolean = true,
    val isEnabled: Boolean = true,
    val cleanJunk: Boolean = true,
    val releaseRam: Boolean = true,
    val stopBackgroundProcesses: Boolean = true
)
