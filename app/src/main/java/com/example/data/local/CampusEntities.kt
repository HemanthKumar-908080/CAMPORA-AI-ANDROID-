package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val type: String, // URGENT, EVENT, TIMETABLE, GENERAL
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val actionNodeId: String? = null
)

@Entity(tableName = "saved_notices")
data class SavedNoticeEntity(
    @PrimaryKey val noticeId: String,
    val title: String,
    val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_preferences")
data class UserPreferenceEntity(
    @PrimaryKey val key: String,
    val value: String
)
