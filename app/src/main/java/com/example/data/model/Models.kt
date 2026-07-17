package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grocery_items")
data class GroceryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val isChecked: Boolean = false,
    val category: String = "Other",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_flashcards")
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val question: String,
    val answer: String,
    val subject: String,
    val known: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "exam_countdowns")
data class ExamCountdown(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventName: String,
    val dateString: String, // e.g. "2026-07-15"
    val daysLeft: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "study_notes")
data class StudyNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val fileUriString: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val role: String, // "user" or "model"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "attendance_sessions")
data class AttendanceSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val dateString: String, // e.g. "2026-07-16"
    val sessionCode: String, // QR code string e.g. "SESSION_MATH_101_UUID"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "attendance_records")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionId: Int,
    val studentName: String,
    val studentEmail: String,
    val scanTimestamp: Long = System.currentTimeMillis(),
    val status: String = "Present", // "Present", "Late", "Absent"
    val deviceModel: String? = null
)

