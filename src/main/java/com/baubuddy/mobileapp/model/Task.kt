package com.baubuddy.mobileapp.model

data class Task(
    val task: String,
    val title: String,
    val description: String?,
    val sort: Int,
    val wageType: String,
    val BusinessUnitKey: String?,
    val businessUnit: String,
    val parentTaskID: String,
    val preplanningBoardQuickSelect: String,
    val colorCode: String,
    val workingTime: String?,
    val isAvailableInTimeTrackingKioskMode: Boolean
)
