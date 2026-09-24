package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OptimizationDao {

    // Reports Queries
    @Query("SELECT * FROM optimization_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<OptimizationReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: OptimizationReport)

    @Query("DELETE FROM optimization_reports")
    suspend fun clearAllReports()

    @Query("DELETE FROM optimization_reports WHERE id = :id")
    suspend fun deleteReportById(id: Int)

    // Scheduled Tasks Queries
    @Query("SELECT * FROM scheduled_tasks ORDER BY timeOfDay ASC")
    fun getAllScheduledTasks(): Flow<List<ScheduledTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduledTask(task: ScheduledTask)

    @Query("DELETE FROM scheduled_tasks WHERE id = :id")
    suspend fun deleteScheduledTaskById(id: Int)

    @Update
    suspend fun updateScheduledTask(task: ScheduledTask)
}
