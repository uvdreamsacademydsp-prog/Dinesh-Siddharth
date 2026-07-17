package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface UiState<out T> {
    object Idle : UiState<Nothing>
    object Loading : UiState<Nothing>
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

data class AuthUser(
    val email: String,
    val displayName: String,
    val isGoogleUser: Boolean = false
)

class AppViewModel(private val repository: AppRepository) : ViewModel() {

    // --- Authentication State Flow ---
    private val _userState = MutableStateFlow<AuthUser?>(null)
    val userState: StateFlow<AuthUser?> = _userState.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // --- Database Flow Streams ---
    val groceryItems: StateFlow<List<GroceryItem>> = repository.allGroceries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyFlashcards: StateFlow<List<Flashcard>> = repository.allFlashcards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val examCountdowns: StateFlow<List<ExamCountdown>> = repository.allExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyNotes: StateFlow<List<StudyNote>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessage>> = repository.chatHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val attendanceSessions: StateFlow<List<AttendanceSession>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttendanceRecords: StateFlow<List<AttendanceRecord>> = repository.allAttendanceRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Active Document Context for the Chatbot ---
    private val _selectedNote = MutableStateFlow<StudyNote?>(null)
    val selectedNote: StateFlow<StudyNote?> = _selectedNote.asStateFlow()

    // --- AI Asynchronous Call States ---
    private val _recipeState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val recipeState: StateFlow<UiState<String>> = _recipeState.asStateFlow()

    private val _tutorChatState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val tutorChatState: StateFlow<UiState<String>> = _tutorChatState.asStateFlow()

    init {
        // Hydrate some default exams and flashcards if empty, to ensure instant beautiful UX on first run
        viewModelScope.launch {
            repository.allExams.first().let { currentExams ->
                if (currentExams.isEmpty()) {
                    repository.addExam(ExamCountdown(eventName = "Semester Final Exams", dateString = "2026-07-15"))
                    repository.addExam(ExamCountdown(eventName = "UPSC Prelims Mock", dateString = "2026-06-25"))
                }
            }
            repository.allFlashcards.first().let { currentFlashcards ->
                if (currentFlashcards.isEmpty()) {
                    repository.addFlashcard(Flashcard(question = "What is Mana Choice?", answer = "An elegant, dual-purpose helper combining AI Study Prep and Smart Grocery Management.", subject = "General UI"))
                    repository.addFlashcard(Flashcard(question = "How to trigger Recipe suggestions in Telugu/English?", answer = "Ensure groceries are added to the list, then tap the 'Recipe Match' floating engine action.", subject = "Groceries"))
                }
            }
            repository.allNotes.first().let { currentNotes ->
                if (currentNotes.isEmpty()) {
                    repository.addNote(StudyNote(title = "General Study Tips", content = "1. Active recall with flashcards improves retention.\n2. Space out reviews over multiple days.\n3. Mana Choice dual setup keeps pantry and brain energized!"))
                }
            }
            repository.allSessions.first().let { currentSessions ->
                if (currentSessions.isEmpty()) {
                    val sId1 = repository.createSession(AttendanceSession(title = "Android Mobile Development - Unit 1", dateString = "2026-07-16", sessionCode = "SESSION_AND_U1"))
                    val sId2 = repository.createSession(AttendanceSession(title = "UPSC General Seminar", dateString = "2026-07-20", sessionCode = "SESSION_UPSC_GEN"))
                    
                    // Add dummy records for instant high-fidelity UX
                    repository.recordAttendance(AttendanceRecord(sessionId = sId1.toInt(), studentName = "John Doe", studentEmail = "john.doe@example.com", status = "Present", deviceModel = "Pixel 8 Pro"))
                    repository.recordAttendance(AttendanceRecord(sessionId = sId1.toInt(), studentName = "Rama Rao", studentEmail = "ram@example.com", status = "Present", deviceModel = "Samsung Galaxy S24"))
                    repository.recordAttendance(AttendanceRecord(sessionId = sId2.toInt(), studentName = "John Doe", studentEmail = "john.doe@example.com", status = "Present", deviceModel = "Pixel 8 Pro"))
                }
            }
        }
    }

    // --- Authentication Actions ---
    fun loginWithEmail(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authError.value = "Enter valid email and password"
            return
        }
        if (password.length < 6) {
            _authError.value = "Password must be at least 6 characters"
            return
        }
        _authError.value = null
        // Client interface supports simple local validation mimicking Firebase core logic
        _userState.value = AuthUser(email = email, displayName = email.split("@").first().capitalize())
    }

    fun signUpWithEmail(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty() || password.length < 6) {
            _authError.value = "Valid credentials required (Password min 6 chars)"
            return
        }
        _authError.value = null
        _userState.value = AuthUser(email = email, displayName = email.split("@").first().capitalize())
    }

    fun loginWithGoogle(accountName: String = "uvdreamsacademydsp@gmail.com") {
        _authError.value = null
        _userState.value = AuthUser(email = accountName, displayName = "UV Dreams", isGoogleUser = true)
    }

    fun logout() {
        _userState.value = null
        _authError.value = null
    }

    // --- Grocery Actions ---
    fun addGroceryItem(name: String, category: String = "Kitchen") {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.addGrocery(GroceryItem(name = name.trim(), category = category))
        }
    }

    fun toggleGroceryItem(item: GroceryItem) {
        viewModelScope.launch {
            repository.updateGrocery(item.copy(isChecked = !item.isChecked))
        }
    }

    fun deleteGroceryItem(id: Int) {
        viewModelScope.launch {
            repository.deleteGrocery(id)
        }
    }

    fun clearPurchasedGroceries() {
        viewModelScope.launch {
            repository.clearCompletedGroceries()
        }
    }

    // --- Exam and Flashcard Actions ---
    fun addExamCountdown(name: String, dateString: String) {
        if (name.isBlank() || dateString.isBlank()) return
        viewModelScope.launch {
            repository.addExam(ExamCountdown(eventName = name.trim(), dateString = dateString))
        }
    }

    fun deleteExamCountdown(id: Int) {
        viewModelScope.launch {
            repository.deleteExam(id)
        }
    }

    fun addStudyFlashcard(q: String, a: String, sub: String) {
        if (q.isBlank() || a.isBlank() || sub.isBlank()) return
        viewModelScope.launch {
            repository.addFlashcard(Flashcard(question = q.trim(), answer = a.trim(), subject = sub.trim()))
        }
    }

    fun toggleFlashcardKnowledge(card: Flashcard) {
        viewModelScope.launch {
            repository.updateFlashcard(card.copy(known = !card.known))
        }
    }

    fun deleteStudyFlashcard(id: Int) {
        viewModelScope.launch {
            repository.deleteFlashcard(id)
        }
    }

    // --- Study Material Notes Actions ---
    fun addStudyNote(title: String, content: String) {
        if (title.isBlank() || content.isBlank()) return
        viewModelScope.launch {
            repository.addNote(StudyNote(title = title.trim(), content = content.trim()))
        }
    }

    fun selectNoteForContext(note: StudyNote?) {
        _selectedNote.value = note
    }

    fun deleteStudyNote(id: Int) {
        viewModelScope.launch {
            repository.deleteNote(id)
            if (_selectedNote.value?.id == id) {
                _selectedNote.value = null
            }
        }
    }

    // --- Chef AI Recipe Execution ---
    fun matchRecipeFromGroceries() {
        _recipeState.value = UiState.Loading
        viewModelScope.launch {
            val list = groceryItems.value
            val responseText = repository.generateRecipeFromGroceries(list)
            _recipeState.value = UiState.Success(responseText)
        }
    }

    fun resetRecipeState() {
        _recipeState.value = UiState.Idle
    }

    // --- Study Chat GPT/Gemini AI Execution ---
    fun sendChatMessage(userText: String) {
        if (userText.trim().isEmpty()) return
        
        viewModelScope.launch {
            // Save user message to database history
            val userMsg = ChatMessage(role = "user", text = userText.trim())
            repository.addChatMessage(userMsg)
            
            _tutorChatState.value = UiState.Loading
            
            // Build Context string if note is selected
            val contextData = _selectedNote.value?.let {
                "Document Title: ${it.title}\nDocument Content: ${it.content}"
            } ?: ""
            
            // Fetch updated history
            val currentHistory = chatMessages.value
            
            val replyText = repository.studyChatWithAI(currentHistory, contextData, userText.trim())
            
            // Save model response to database history
            val modelMsg = ChatMessage(role = "model", text = replyText)
            repository.addChatMessage(modelMsg)
            
            _tutorChatState.value = UiState.Success(replyText)
        }
    }

    fun clearAllChatHistory() {
        viewModelScope.launch {
            repository.clearChat()
            _tutorChatState.value = UiState.Idle
        }
    }

    // --- Spoken English AI Practice ---
    private val _spokenEnglishFeedback = MutableStateFlow<UiState<String>>(UiState.Idle)
    val spokenEnglishFeedback: StateFlow<UiState<String>> = _spokenEnglishFeedback.asStateFlow()

    fun evaluateSpokenEnglish(taskTitle: String, taskScenario: String, studentAnswer: String) {
        viewModelScope.launch {
            _spokenEnglishFeedback.value = UiState.Loading
            val feedback = repository.evaluateSpokenEnglishTask(taskTitle, taskScenario, studentAnswer)
            _spokenEnglishFeedback.value = UiState.Success(feedback)
        }
    }

    fun resetSpokenEnglishFeedback() {
        _spokenEnglishFeedback.value = UiState.Idle
    }

    // --- QR Attendance Actions ---
    fun addAttendanceSession(title: String, code: String = "") {
        if (title.isBlank()) return
        val finalCode = if (code.trim().isEmpty()) {
            "ATT_SESSION_" + System.currentTimeMillis().toString().takeLast(6)
        } else {
            code.trim().uppercase()
        }
        viewModelScope.launch {
            repository.createSession(
                AttendanceSession(
                    title = title.trim(),
                    dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                    sessionCode = finalCode
                )
            )
        }
    }

    fun deleteAttendanceSession(id: Int) {
        viewModelScope.launch {
            repository.deleteSession(id)
        }
    }

    fun deleteAttendanceRecord(id: Int) {
        viewModelScope.launch {
            repository.deleteAttendanceRecord(id)
        }
    }

    suspend fun recordSessionCheckIn(sessionCode: String, studentName: String, studentEmail: String, status: String = "Present"): String {
        if (sessionCode.isBlank()) return "Invalid QR Code format."
        val trimmedCode = sessionCode.trim()
        val session = repository.getSessionByCode(trimmedCode.uppercase()) 
            ?: repository.getSessionByCode(trimmedCode)
            ?: return "Attendance Session Not Found for code: $trimmedCode"

        val existing = repository.getRecordBySessionAndStudent(session.id, studentEmail.trim().lowercase())
        if (existing != null) {
            return "Already Checked In for session: '${session.title}'."
        }

        val record = AttendanceRecord(
            sessionId = session.id,
            studentName = studentName.trim(),
            studentEmail = studentEmail.trim().lowercase(),
            status = status,
            deviceModel = android.os.Build.MODEL
        )
        repository.recordAttendance(record)
        return "SUCCESS: Checked in for '${session.title}'!"
    }
}

class AppViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
