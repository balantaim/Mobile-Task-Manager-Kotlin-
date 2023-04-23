package com.baubuddy.application.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "task") var task: String,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "sort") var sort: Int,
    @ColumnInfo(name = "wageType") var wageType: String,
    @ColumnInfo(name = "BusinessUnitKey") var BusinessUnitKey: String?,
    @ColumnInfo(name = "businessUnit") var businessUnit: String,
    @ColumnInfo(name = "parentTaskID") var parentTaskID: String,
    @ColumnInfo(name = "preplanningBoardQuickSelect") var preplanningBoardQuickSelect: String,
    @ColumnInfo(name = "colorCode") var colorCode: String,
    @ColumnInfo(name = "workingTime") var workingTime: String?,
    @ColumnInfo(name = "isAvailableInTimeTrackingKioskMode") var isAvailableInTimeTrackingKioskMode: Boolean
)
