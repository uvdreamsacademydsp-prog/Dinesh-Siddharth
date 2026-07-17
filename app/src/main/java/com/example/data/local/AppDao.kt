package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // --- Grocery Queries ---
    @Query("SELECT * FROM grocery_items ORDER BY timestamp DESC")
    fun getAllGroceryFlow(): Flow<List<GroceryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrocery(item: GroceryItem)

    @Update
    suspend fun updateGrocery(item: GroceryItem)

    @Query("DELETE FROM grocery_items WHERE id = :id")
    suspend fun deleteGrocery(id: Int)

    @Query("DELETE FROM grocery_items WHERE isChecked = 1")
    suspend fun clearCompletedGroceries()

    // --- Flashcard Queries ---
    @Query("SELECT * FROM study_flashcards ORDER BY timestamp DESC")
    fun getAllFlashcardsFlow(): Flow<List<Flashcard>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: Flashcard)

    @Update
    suspend fun updateFlashcard(flashcard: Flashcard)

    @Query("DELETE FROM study_flashcards WHERE id = :id")
    suspend fun deleteFlashcard(id: Int)

    // --- Exam countdown Queries ---
    @Query("SELECT * FROM exam_countdowns ORDER BY timestamp DESC")
    fun getAllExamsFlow(): Flow<List<ExamCountdown>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamCountdown)

    @Query("DELETE FROM exam_countdowns WHERE id = :id")
    suspend fun deleteExam(id: Int)

    // --- Study Note / Doc Queries ---
    @Query("SELECT * FROM study_notes ORDER BY timestamp DESC")
    fun getAllNotesFlow(): Flow<List<StudyNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: StudyNote)

    @Query("DELETE FROM study_notes WHERE id = :id")
    suspend fun deleteNote(id: Int)

    // --- Chat Messages Queries ---
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatFlow(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatHistory()

    // --- QR Attendance Sessions Queries ---
    @Query("SELECT * FROM attendance_sessions ORDER BY timestamp DESC")
    fun getAllSessionsFlow(): Flow<List<AttendanceSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: AttendanceSession): Long

    @Query("DELETE FROM attendance_sessions WHERE id = :id")
    suspend fun deleteSession(id: Int)

    @Query("SELECT * FROM attendance_sessions WHERE sessionCode = :code LIMIT 1")
    suspend fun getSessionByCode(code: String): AttendanceSession?

    // --- QR Attendance Records Queries ---
    @Query("SELECT * FROM attendance_records ORDER BY scanTimestamp DESC")
    fun getAllRecordsFlow(): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE sessionId = :sessionId ORDER BY scanTimestamp DESC")
    fun getRecordsForSessionFlow(sessionId: Int): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE sessionId = :sessionId ORDER BY scanTimestamp DESC")
    suspend fun getRecordsForSession(sessionId: Int): List<AttendanceRecord>

    @Query("SELECT * FROM attendance_records WHERE sessionId = :sessionId AND studentEmail = :email LIMIT 1")
    suspend fun getRecordBySessionAndStudent(sessionId: Int, email: String): AttendanceRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: AttendanceRecord)

    @Query("DELETE FROM attendance_records WHERE id = :id")
    suspend fun deleteRecord(id: Int)
}
