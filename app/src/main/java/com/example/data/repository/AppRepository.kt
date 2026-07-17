package com.example.data.repository

import com.example.BuildConfig
import com.example.data.local.AppDao
import com.example.data.model.*
import com.example.data.remote.Content
import com.example.data.remote.GenerateContentRequest
import com.example.data.remote.Part
import com.example.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {

    // --- Local DB delegates ---
    val allGroceries: Flow<List<GroceryItem>> = appDao.getAllGroceryFlow()
    val allFlashcards: Flow<List<Flashcard>> = appDao.getAllFlashcardsFlow()
    val allExams: Flow<List<ExamCountdown>> = appDao.getAllExamsFlow()
    val allNotes: Flow<List<StudyNote>> = appDao.getAllNotesFlow()
    val chatHistory: Flow<List<ChatMessage>> = appDao.getAllChatFlow()

    // Grocery actions
    suspend fun addGrocery(item: GroceryItem) = appDao.insertGrocery(item)
    suspend fun updateGrocery(item: GroceryItem) = appDao.updateGrocery(item)
    suspend fun deleteGrocery(id: Int) = appDao.deleteGrocery(id)
    suspend fun clearCompletedGroceries() = appDao.clearCompletedGroceries()

    // Flashcard actions
    suspend fun addFlashcard(card: Flashcard) = appDao.insertFlashcard(card)
    suspend fun updateFlashcard(card: Flashcard) = appDao.updateFlashcard(card)
    suspend fun deleteFlashcard(id: Int) = appDao.deleteFlashcard(id)

    // Exam countdown actions
    suspend fun addExam(exam: ExamCountdown) = appDao.insertExam(exam)
    suspend fun deleteExam(id: Int) = appDao.deleteExam(id)

    // Study notes actions
    suspend fun addNote(note: StudyNote) = appDao.insertNote(note)
    suspend fun deleteNote(id: Int) = appDao.deleteNote(id)

    // Chat actions
    suspend fun addChatMessage(msg: ChatMessage) = appDao.insertChatMessage(msg)
    suspend fun clearChat() = appDao.clearChatHistory()

    // --- Gemini Generative Requests ---
    suspend fun generateRecipeFromGroceries(groceries: List<GroceryItem>): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "Please configure your GEMINI_API_KEY in the Google AI Studio Secrets Panel to use the AI Recipe Matcher."
        }

        val uncheckedGroceries = groceries.filter { !it.isChecked }
        if (uncheckedGroceries.isEmpty()) {
            return "Your shopping list is empty or all items are checked out! Please add some groceries to match delicious recipes."
        }

        val groceryString = uncheckedGroceries.map { it.name }.joinToString(", ")
        val prompt = """
            Based on these grocery items in the pantry/shopping list: $groceryString.
            Suggest a quick and healthy meal recipe that utilizes these ingredients.
            
            Style rules:
            - Give it a creative, localized name (incorporate Telugu cuisine terms where fitting).
            - List major ingredients and standard steps in short, modern, bulleted markdown.
            - Provide a brief tip in both English and Telugu script (తెలుగు) explaining the nutritional value.
        """.trimIndent()

        return try {
            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                systemInstruction = Content(parts = listOf(Part(text = "You are 'Chef Mana', a highly skilled master chef specializing in Indian & fusion cooking. You write concise, delightful guides.")))
            )
            val response = RetrofitClient.geminiService.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "Chef couldn't compose a custom recipe suggestion. Intentionally refine your list items and tap again!"
        } catch (e: Exception) {
            "Recipe Matching failed: ${e.localizedMessage}. Verify your internet connection and API key configuration."
        }
    }

    suspend fun studyChatWithAI(history: List<ChatMessage>, docContext: String, userMessage: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "Please configure your GEMINI_API_KEY in the Google AI Studio Secrets Panel to consult with the AI Tutor."
        }

        val requestContents = mutableListOf<Content>()
        
        // Let the AI understand study documents if any are selected/uploaded
        val systemMessageText = if (docContext.isNotEmpty()) {
            "You are 'Mana Choice AI Tutor', an expert study assistant. The student has uploaded/selected this study material: \n=== STUDY NOTES COMPILATION ===\n$docContext\n===============================\nHelp the student understand this text. Respond in clear, digestible language. Use friendly explanations with Telugu translation summaries for critical terms where appropriate."
        } else {
            "You are 'Mana Choice AI Tutor', an expert exam prep coach. Help the student summarize notes, practice mock quiz questions, and prepare for targets. Be encouraging, concise, and professional. Telugu-English bilingual study friendly."
        }

        // Add history (last 10 messages for context)
        history.takeLast(10).forEach { msg ->
            requestContents.add(Content(parts = listOf(Part(text = msg.text))))
        }
        
        // Add current user prompt
        requestContents.add(Content(parts = listOf(Part(text = userMessage))))

        return try {
            val request = GenerateContentRequest(
                contents = requestContents,
                systemInstruction = Content(parts = listOf(Part(text = systemMessageText)))
            )
            val response = RetrofitClient.geminiService.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "Tutor couldn't generate a text response. Please try rephrasing your conceptual inquiry!"
        } catch (e: Exception) {
            "AI Study Tutor Error: ${e.localizedMessage}. Verify network and secrets settings."
        }
    }

    // --- QR Attendance Sessions & Records ---
    val allSessions: Flow<List<AttendanceSession>> = appDao.getAllSessionsFlow()
    val allAttendanceRecords: Flow<List<AttendanceRecord>> = appDao.getAllRecordsFlow()

    fun getRecordsForSessionFlow(sessionId: Int): Flow<List<AttendanceRecord>> = appDao.getRecordsForSessionFlow(sessionId)
    suspend fun getRecordsForSession(sessionId: Int): List<AttendanceRecord> = appDao.getRecordsForSession(sessionId)
    suspend fun createSession(session: AttendanceSession): Long = appDao.insertSession(session)
    suspend fun deleteSession(id: Int) = appDao.deleteSession(id)
    suspend fun getSessionByCode(code: String): AttendanceSession? = appDao.getSessionByCode(code)
    suspend fun getRecordBySessionAndStudent(sessionId: Int, email: String): AttendanceRecord? = appDao.getRecordBySessionAndStudent(sessionId, email)
    suspend fun recordAttendance(record: AttendanceRecord) = appDao.insertRecord(record)
    suspend fun deleteAttendanceRecord(id: Int) = appDao.deleteRecord(id)

    // --- Spoken English AI Practice Trainer ---
    suspend fun evaluateSpokenEnglishTask(taskTitle: String, taskScenario: String, studentAnswer: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "Please configure your GEMINI_API_KEY in the Google AI Studio Secrets Panel to analyze Spoken English task."
        }

        val prompt = """
            You are 'Mana Spoken English AI Trainer'.
            Evaluate the following student's response for a Spoken English Practice Challenge.
            
            Task Title: $taskTitle
            Scenario/Context: $taskScenario
            Student's Response: "$studentAnswer"
            
            Please analyze this response and provide:
            1. **Overall Grade / Score**: Out of 10 (e.g. 8.5/10) with encouraging remarks.
            2. **Grammar & Sentence Structure**: Highlight any grammatical mistakes, word order issues, or prepositions that need adjustment, and explain why.
            3. **Vocabulary & Natural Expression**: Suggest more polished, professional, or native-sounding phrases that the student could use in this context.
            4. **Telugu Help / translation (తెలుగు)**: Provide a summary of how to say the correct expressions in clean Telugu so the student can easily understand.
            
            Write the output in beautiful, encouraging, and clear markdown. Keep formatting extremely neat and readable with clear headers.
        """.trimIndent()

        return try {
            val request = GenerateContentRequest(
                contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                systemInstruction = Content(parts = listOf(Part(text = "You are a professional, warm, bilingual Spoken English coach helping Indian students master spoken English fluency.")))
            )
            val response = RetrofitClient.geminiService.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "AI Coach could not generate feedback. Please re-submit your response."
        } catch (e: Exception) {
            "AI English Trainer Error: ${e.localizedMessage}. Please verify your connection."
        }
    }
}
