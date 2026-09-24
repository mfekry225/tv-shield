package com.example.data

import kotlinx.coroutines.flow.Flow

class OptimizationRepository(private val dao: OptimizationDao) {

    val allReports: Flow<List<OptimizationReport>> = dao.getAllReports()

    val allScheduledTasks: Flow<List<ScheduledTask>> = dao.getAllScheduledTasks()

    suspend fun insertReport(report: OptimizationReport) {
        dao.insertReport(report)
    }

    suspend fun clearAllReports() {
        dao.clearAllReports()
    }

    suspend fun deleteReportById(id: Int) {
        dao.deleteReportById(id)
    }

    suspend fun insertScheduledTask(task: ScheduledTask) {
        dao.insertScheduledTask(task)
    }

    suspend fun updateScheduledTask(task: ScheduledTask) {
        dao.updateScheduledTask(task)
    }

    suspend fun deleteScheduledTaskById(id: Int) {
        dao.deleteScheduledTaskById(id)
    }
}
