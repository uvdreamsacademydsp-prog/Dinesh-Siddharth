package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.*
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.UiState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onLogout: () -> Unit
) {
    val currentUser by viewModel.userState.collectAsStateWithLifecycle()
    var selectedScreenTab by remember { mutableStateOf(3) } // 0: Study Mode, 1: Grocery Mode, 2: AI Chatbot, 3: Attendance

    // Navigation and Logout handling
    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            onLogout()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.mana_choice_logo),
                                contentDescription = "Logo Thumbnail",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(androidx.compose.foundation.shape.CircleShape)
                            )
                        }

                        Column {
                            Text(
                                text = "Mana Choice",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 19.sp,
                                modifier = Modifier.testTag("app_main_title")
                            )
                            Text(
                                text = currentUser?.displayName?.let { "Hello, $it" } ?: "Companion",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Log Out",
                            tint = Color(0xFFF43F5E)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1E293B),
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationBarItem(
                    selected = selectedScreenTab == 0,
                    onClick = { selectedScreenTab = 0 },
                    icon = {
                        Icon(
                            imageVector = if (selectedScreenTab == 0) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                            contentDescription = "Study tab"
                        )
                    },
                    label = { Text("Study Prep") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF3B82F6),
                        selectedTextColor = Color(0xFF3B82F6),
                        indicatorColor = Color(0x223B82F6),
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("nav_study_mode_tab")
                )

                NavigationBarItem(
                    selected = selectedScreenTab == 1,
                    onClick = { selectedScreenTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (selectedScreenTab == 1) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                            contentDescription = "Grocery tab"
                        )
                    },
                    label = { Text("Groceries") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF10B981),
                        selectedTextColor = Color(0xFF10B981),
                        indicatorColor = Color(0x2210B981),
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("nav_grocery_mode_tab")
                )

                NavigationBarItem(
                    selected = selectedScreenTab == 2,
                    onClick = { selectedScreenTab = 2 },
                    icon = {
                        Icon(
                            imageVector = if (selectedScreenTab == 2) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                            contentDescription = "Chatbot tab"
                        )
                    },
                    label = { Text("AI Tutor") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF8B5CF6),
                        selectedTextColor = Color(0xFF8B5CF6),
                        indicatorColor = Color(0x228B5CF6),
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("nav_ai_chat_tab")
                )

                NavigationBarItem(
                    selected = selectedScreenTab == 3,
                    onClick = { selectedScreenTab = 3 },
                    icon = {
                        Icon(
                            imageVector = if (selectedScreenTab == 3) Icons.Filled.QrCodeScanner else Icons.Outlined.QrCodeScanner,
                            contentDescription = "Attendance tab"
                        )
                    },
                    label = { Text("Attendance") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFEC4899),
                        selectedTextColor = Color(0xFFEC4899),
                        indicatorColor = Color(0x22EC4899),
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("nav_attendance_tab")
                )

                NavigationBarItem(
                    selected = selectedScreenTab == 4,
                    onClick = { selectedScreenTab = 4 },
                    icon = {
                        Icon(
                            imageVector = if (selectedScreenTab == 4) Icons.Filled.RecordVoiceOver else Icons.Outlined.RecordVoiceOver,
                            contentDescription = "Spoken English tab"
                        )
                    },
                    label = { Text("Spoken AI") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF8B5CF6),
                        selectedTextColor = Color(0xFF8B5CF6),
                        indicatorColor = Color(0x228B5CF6),
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("nav_spoken_english_tab")
                )

                NavigationBarItem(
                    selected = selectedScreenTab == 5,
                    onClick = { selectedScreenTab = 5 },
                    icon = {
                        Icon(
                            imageVector = if (selectedScreenTab == 5) Icons.Filled.Fastfood else Icons.Outlined.Fastfood,
                            contentDescription = "Food Delivery tab"
                        )
                    },
                    label = { Text("Food Express") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFF43F5E),
                        selectedTextColor = Color(0xFFF43F5E),
                        indicatorColor = Color(0x22F43F5E),
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.testTag("nav_food_delivery_tab")
                )
            }
        },
        containerColor = Color(0xFF0F172A)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedScreenTab) {
                0 -> StudyTabContent(viewModel)
                1 -> GroceryTabContent(viewModel)
                2 -> ChatTabContent(viewModel)
                3 -> AttendanceTabContent(viewModel)
                4 -> SpokenEnglishTabContent(viewModel)
                5 -> FoodDeliveryTabContent(viewModel)
            }
        }
    }
}

// ==========================================
// 1. STUDY TAB CONTENT
// ==========================================
@Composable
fun StudyTabContent(viewModel: AppViewModel) {
    val countdowns by viewModel.examCountdowns.collectAsStateWithLifecycle()
    val flashcards by viewModel.studyFlashcards.collectAsStateWithLifecycle()
    val studyNotes by viewModel.studyNotes.collectAsStateWithLifecycle()

    var showAddExamDialog by remember { mutableStateOf(false) }
    var newExamName by remember { mutableStateOf("") }
    var newExamDate by remember { mutableStateOf("") } // YYYY-MM-DD format

    var showAddCardDialog by remember { mutableStateOf(false) }
    var newCardQ by remember { mutableStateOf("") }
    var newCardA by remember { mutableStateOf("") }
    var newCardSubject by remember { mutableStateOf("") }

    var showAddNoteDialog by remember { mutableStateOf(false) }
    var newNoteTitle by remember { mutableStateOf("") }
    var newNoteContent by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Exam Countdown Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Exam Countdowns",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Keep track of upcoming deadlines",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
                IconButton(
                    onClick = { showAddExamDialog = true },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0x333B82F6))
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add countdown", tint = Color(0xFF60A5FA))
                }
            }
        }

        // Horizontal Exams Countdowns List
        item {
            if (countdowns.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No exam targets config. Tap '+' to schedule first!", color = Color(0xFF475569), fontSize = 13.sp)
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    countdowns.take(3).forEach { exam ->
                        // Simple days left calculation
                        val parsedDays = remember(exam.dateString) {
                            try {
                                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val future = sdf.parse(exam.dateString)
                                val today = Date()
                                val diff = future.time - today.time
                                if (diff < 0) 0 else (diff / (1000 * 60 * 60 * 24)).toInt()
                            } catch (e: Exception) {
                                12
                            }
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            modifier = Modifier
                                .weight(1f)
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                        ) {
                            Box {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = exam.eventName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = exam.dateString,
                                        fontSize = 10.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "$parsedDays Days Left",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (parsedDays < 7) Color(0xFFF43F5E) else Color(0xFF60A5FA),
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.deleteExamCountdown(exam.id) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(24.dp)
                                        .padding(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Delete",
                                        tint = Color(0x6694A3B8),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // QUICK DOCS/PDF UPLOAD SIMULATION SECTION
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0x1A8B5CF6)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0x448B5CF6), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Study Material & Documents",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Add reference notes for your AI Tutor to parse",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = { showAddNoteDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("pdf_upload_simulation_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.UploadFile, contentDescription = "Upload note", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Doc", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Active Reference Docs List (if configured)
        if (studyNotes.isNotEmpty()) {
            item {
                Text(
                    text = "Reference Notes (${studyNotes.size})",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Bold
                )
            }
            items(studyNotes) { note ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Description, contentDescription = "Note Paper", tint = Color(0xFF8B5CF6), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(note.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(note.content, color = Color(0xFF64748B), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    IconButton(onClick = { viewModel.deleteStudyNote(note.id) }) {
                        Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete Doc", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // Study Flashcards Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Practice Flashcards",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Tap to review question & answer cards",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
                IconButton(
                    onClick = { showAddCardDialog = true },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0x333B82F6))
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Flashcard", tint = Color(0xFF60A5FA))
                }
            }
        }

        // Grid-like list of flashcards
        if (flashcards.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No practice cards created. Tap '+' to write questions!", color = Color(0xFF475569), fontSize = 13.sp)
                }
            }
        } else {
            items(flashcards) { card ->
                var revealed by remember { mutableStateOf(false) }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (revealed) Color(0xFF0F172A) else Color(0xFF1E293B)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { revealed = !revealed }
                        .border(1.dp, if (revealed) Color(0xFF8B5CF6) else Color(0xFF334155), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AssistChip(
                                onClick = {},
                                label = { Text(card.subject, color = Color(0xFF8B5CF6), fontSize = 10.sp) },
                                colors = AssistChipDefaults.assistChipColors(containerColor = Color(0x1F8B5CF6))
                            )
                            IconButton(onClick = { viewModel.deleteStudyFlashcard(card.id) }, modifier = Modifier.size(24.dp)) {
                                Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete Q", tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Crossfade(targetState = revealed) { isRevealed ->
                            if (isRevealed) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("ANSWER:", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(card.answer, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("QUESTION:", color = Color(0xFF3B82F6), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(card.question, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (revealed) "Tap to hide answer" else "Tap to flip & reveal response",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Light,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    // Exam Dialog Formulation
    if (showAddExamDialog) {
        AlertDialog(
            onDismissRequest = { showAddExamDialog = false },
            title = { Text("Schedule Exam countdown", color = Color.White) },
            containerColor = Color(0xFF1E293B),
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newExamName,
                        onValueChange = { newExamName = it },
                        label = { Text("Exam / Event Name", color = Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newExamDate,
                        onValueChange = { newExamDate = it },
                        label = { Text("Date (YYYY-MM-DD)", color = Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newExamName.isNotBlank() && newExamDate.isNotBlank()) {
                            viewModel.addExamCountdown(newExamName, newExamDate)
                            newExamName = ""
                            newExamDate = ""
                            showAddExamDialog = false
                        }
                    }
                ) {
                    Text("Add Schedule")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddExamDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Flashcard Dialog Formulation
    if (showAddCardDialog) {
        AlertDialog(
            onDismissRequest = { showAddCardDialog = false },
            title = { Text("Create Practice Flashcard", color = Color.White) },
            containerColor = Color(0xFF1E293B),
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newCardSubject,
                        onValueChange = { newCardSubject = it },
                        label = { Text("Subject / Module Tag", color = Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newCardQ,
                        onValueChange = { newCardQ = it },
                        label = { Text("Question text", color = Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        maxLines = 3
                    )
                    OutlinedTextField(
                        value = newCardA,
                        onValueChange = { newCardA = it },
                        label = { Text("Correct Answer", color = Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCardQ.isNotBlank() && newCardA.isNotBlank() && newCardSubject.isNotBlank()) {
                            viewModel.addStudyFlashcard(newCardQ, newCardA, newCardSubject)
                            newCardQ = ""
                            newCardA = ""
                            newCardSubject = ""
                            showAddCardDialog = false
                        }
                    }
                ) {
                    Text("Add Card")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCardDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Note Dialog Formulation
    if (showAddNoteDialog) {
        AlertDialog(
            onDismissRequest = { showAddNoteDialog = false },
            title = { Text("Add Study Note / PDF Mock context", color = Color.White) },
            containerColor = Color(0xFF1E293B),
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newNoteTitle,
                        onValueChange = { newNoteTitle = it },
                        label = { Text("Document Title", color = Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newNoteContent,
                        onValueChange = { newNoteContent = it },
                        label = { Text("Contents / Key Text details", color = Color(0xFF94A3B8)) },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        minLines = 4,
                        maxLines = 6
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newNoteTitle.isNotBlank() && newNoteContent.isNotBlank()) {
                            viewModel.addStudyNote(newNoteTitle, newNoteContent)
                            newNoteTitle = ""
                            newNoteContent = ""
                            showAddNoteDialog = false
                        }
                    }
                ) {
                    Text("Save Document")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddNoteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ==========================================
// 2. GROCERY TAB CONTENT
// ==========================================
@Composable
fun GroceryTabContent(viewModel: AppViewModel) {
    val groceries by viewModel.groceryItems.collectAsStateWithLifecycle()
    val recipeState by viewModel.recipeState.collectAsStateWithLifecycle()

    var inputGroceryName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Kitchen") }
    var showRecipeDialog by remember { mutableStateOf(false) }

    val categories = listOf("Kitchen", "Produce", "Dairy", "Spices", "Snacks")

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Smart Grocery Log",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Telugu keyboard entry supported (e.g. పాలు, ఆలుగడ్డలు)",
                        fontSize = 11.sp,
                        color = Color(0xFF10B981)
                    )
                }
                TextButton(onClick = { viewModel.clearPurchasedGroceries() }) {
                    Text("Clear Done", color = Color(0xFFEF4444), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bilingual Add Items Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputGroceryName,
                            onValueChange = { inputGroceryName = it },
                            placeholder = { Text("Tomato / టమాటో", color = Color(0x7794A3B8), fontSize = 14.sp) },
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("grocery_item_input")
                        )

                        // VOICE logging Simulation Button
                        IconButton(
                            onClick = {
                                // Simulate Voice typing translating Telugu groceries
                                val sampleVoiceItems = listOf("Carrot / క్యారెట్", "Milk / పాలు", "Rice / బియ్యం", "Onions / ఉల్లిపాయలు")
                                inputGroceryName = sampleVoiceItems.random()
                            },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0x2210B981)),
                            modifier = Modifier.testTag("voice_input_simulation_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Mic, contentDescription = "Voice Log simulation", tint = Color(0xFF34D399))
                        }

                        Button(
                            onClick = {
                                if (inputGroceryName.isNotBlank()) {
                                    viewModel.addGroceryItem(inputGroceryName, selectedCategory)
                                    inputGroceryName = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("add_grocery_button")
                        ) {
                            Text("Add", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Horizontal chips selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        categories.forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF10B981),
                                    selectedLabelColor = Color.White,
                                    labelColor = Color(0xFF94A3B8)
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grocery Lazy List with Checkboxes
            if (groceries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.ShoppingBasket, contentDescription = "Empty Basket", tint = Color(0xFF475569), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("List is empty! Add ingredients above.", color = Color(0xFF64748B), fontSize = 13.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(groceries) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E293B))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Checkbox(
                                    checked = item.isChecked,
                                    onCheckedChange = { viewModel.toggleGroceryItem(item) },
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF10B981)),
                                    modifier = Modifier.testTag("grocery_checkbox_${item.id}")
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = item.name,
                                        color = if (item.isChecked) Color(0xFF64748B) else Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(item.category, color = Color(0xFF475569), fontSize = 10.sp)
                                }
                            }
                            IconButton(onClick = { viewModel.deleteGroceryItem(item.id) }) {
                                Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete Item", tint = Color(0x66EF4444), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // FLOATING ACTION ENGINE BUTTON: RECIPE MATCHER (Checks items and asks Gemini for Chef meals)
        ExtendedFloatingActionButton(
            text = { Text("Recipe Match", fontWeight = FontWeight.Bold, color = Color.White) },
            icon = { Icon(imageVector = Icons.Default.RestaurantMenu, contentDescription = "AI Recipe Matcher", tint = Color.White) },
            onClick = {
                viewModel.matchRecipeFromGroceries()
                showRecipeDialog = true
            },
            containerColor = Color(0xFF10B981),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("recipe_matcher_btn")
        )
    }

    // Recipe Matcher AI Response Dialog
    if (showRecipeDialog) {
        AlertDialog(
            onDismissRequest = {
                showRecipeDialog = false
                viewModel.resetRecipeState()
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI Stars", tint = Color(0xFF10B981))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chef Mana Suggested Recipe", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF1E293B),
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp)
                ) {
                    when (val state = recipeState) {
                        is UiState.Idle -> {
                            Text("Chef is checking what items are unchecked...", color = Color(0xFF94A3B8))
                        }
                        is UiState.Loading -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = Color(0xFF10B981))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Cooking dynamic instructions with Gemini...", color = Color.White, fontSize = 12.sp)
                            }
                        }
                        is UiState.Success -> {
                            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                item {
                                    Text(
                                        text = state.data,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp,
                                        modifier = Modifier.testTag("recipe_output_text")
                                    )
                                }
                            }
                        }
                        is UiState.Error -> {
                            Text("Error matching groceries: ${state.message}", color = Color(0xFFEF4444))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRecipeDialog = false
                        viewModel.resetRecipeState()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("Got It!")
                }
            }
        )
    }
}

// ==========================================
// 3. COOP AI TUTOR CHAT TAB CONTENT
// ==========================================
@Composable
fun ChatTabContent(viewModel: AppViewModel) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val notes by viewModel.studyNotes.collectAsStateWithLifecycle()
    val activeNote by viewModel.selectedNote.collectAsStateWithLifecycle()
    val chatState by viewModel.tutorChatState.collectAsStateWithLifecycle()

    var inputChatQuery by remember { mutableStateOf("") }
    var showDocSelector by remember { mutableStateOf(false) }
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    // Automatically scroll to the bottom when messages list size increases
    LaunchedEffect(messages.size, chatState) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val isWideScreen = maxWidth > 600.dp
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 800.dp)
                .align(Alignment.Center)
        ) {
            // Chat Header with document reference selection capability
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        // AI Avatar Status indicator
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8B5CF6).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Android,
                                contentDescription = "Tutor Icon",
                                tint = Color(0xFFA78BFA),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        
                        Column {
                            Text(
                                text = "Mana Study Tutor",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (activeNote != null) "Active note context: ${activeNote!!.title}" else "Unanchored Tutor Chat (General prep)",
                                fontSize = 11.sp,
                                color = if (activeNote != null) Color(0xFF10B981) else Color(0xFF94A3B8),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.testTag("active_doc_indicator")
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Attach study note dialog button
                        IconButton(
                            onClick = { showDocSelector = true },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0x1F8B5CF6)),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Attachment,
                                contentDescription = "Select context doc",
                                tint = Color(0xFFA78BFA),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        // Clear chat history
                        IconButton(
                            onClick = { viewModel.clearAllChatHistory() },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0x1AEF4444)),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear Chat",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Scrollable chat message area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F172A))
                    .padding(12.dp)
            ) {
                if (messages.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "Tutor Logo",
                                tint = Color(0xFF8B5CF6),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "How can I help you study today?",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Bind a saved document context to get tailored study summaries, auto-generated mock quizzes, or conceptual prep questions.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = "Or start instantly with suggestions:",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Quick action chips (horizontally scrollable or wrapped flow)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val quickPrompts = listOf(
                                    "📝 Summarize my active note" to "Summarize the key takeaways and major themes of my active study notes.",
                                    "❓ Create a 5-question Quiz" to "Please generate a 5-question mock exam quiz based on my study notes.",
                                    "💡 Core Definitions & Terms" to "Outline the most critical definitions and terms from this document.",
                                    "🧠 Explain like I'm five" to "Explain the main subject of my active study notes in a simple, intuitive way with an analogy."
                                )
                                quickPrompts.forEach { (label, promptText) ->
                                    Card(
                                        modifier = Modifier
                                            .clickable {
                                                viewModel.sendChatMessage(promptText)
                                            }
                                            .widthIn(max = 320.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = label,
                                            color = Color(0xFFA78BFA),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    androidx.compose.foundation.lazy.LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(messages) { msg ->
                            val isUser = msg.role == "user"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
                                verticalAlignment = Alignment.Top
                            ) {
                                if (!isUser) {
                                    // Small custom bot avatar icon for AI response
                                    Box(
                                        modifier = Modifier
                                            .padding(end = 8.dp, top = 4.dp)
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF8B5CF6).copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Android,
                                            contentDescription = "Bot Avatar",
                                            tint = Color(0xFFA78BFA),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 16.dp,
                                                topEnd = 16.dp,
                                                bottomStart = if (isUser) 16.dp else 4.dp,
                                                bottomEnd = if (isUser) 4.dp else 16.dp
                                            )
                                        )
                                        .background(
                                            if (isUser) {
                                                Brush.linearGradient(
                                                    colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                                                )
                                            } else {
                                                Brush.linearGradient(
                                                    colors = listOf(Color(0xFF1E293B), Color(0xFF1E293B))
                                                )
                                            }
                                        )
                                        .padding(horizontal = 14.dp, vertical = 12.dp)
                                        .widthIn(max = if (isWideScreen) 500.dp else 280.dp)
                                ) {
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = if (isUser) "Student" else "Mana Study Tutor",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isUser) Color(0xE6FFFFFF) else Color(0xFFA78BFA)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = msg.text,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            lineHeight = 20.sp
                                        )
                                    }
                                }

                                if (isUser) {
                                    // Small custom student avatar icon
                                    Box(
                                        modifier = Modifier
                                            .padding(start = 8.dp, top = 4.dp)
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF3B82F6).copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "User Avatar",
                                            tint = Color(0xFF60A5FA),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Loading animation bubble
                        if (chatState is UiState.Loading) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(end = 8.dp, top = 4.dp)
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF8B5CF6).copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Android,
                                            contentDescription = "Bot Avatar Loading",
                                            tint = Color(0xFFA78BFA),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp))
                                            .background(Color(0xFF1E293B))
                                            .padding(horizontal = 14.dp, vertical = 12.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(14.dp),
                                                strokeWidth = 2.dp,
                                                color = Color(0xFF8B5CF6)
                                            )
                                            Text(
                                                text = "Analyzing notes and synthesizing explanation...",
                                                color = Color(0xFF94A3B8),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick micro chips above input (shows when chat history is not empty to suggest next queries)
            if (messages.isNotEmpty() && chatState !is UiState.Loading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val followUps = listOf("📝 Quiz Me", "💡 Summarize", "🔬 Simpler Analogy")
                    followUps.forEach { label ->
                        SuggestionChip(
                            onClick = {
                                val fullText = when(label) {
                                    "📝 Quiz Me" -> "Generate a quick practice question to test my understanding on this topic."
                                    "💡 Summarize" -> "Give me a bulleted summary of this topic."
                                    else -> "Can you explain that last point again with a very simple real-world analogy?"
                                }
                                viewModel.sendChatMessage(fullText)
                            },
                            label = { Text(label, fontSize = 11.sp, color = Color(0xFFA78BFA)) },
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFF1E293B))
                        )
                    }
                }
            }

            // TextInput row for Chat queries
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputChatQuery,
                    onValueChange = { inputChatQuery = it },
                    placeholder = { 
                        Text(
                            text = if (activeNote != null) "Ask about '${activeNote!!.title}'..." else "Ask general questions...", 
                            color = Color(0x6694A3B8), 
                            fontSize = 13.sp
                        ) 
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF1E293B),
                        unfocusedContainerColor = Color(0xFF0F172A),
                        focusedBorderColor = Color(0xFF8B5CF6),
                        unfocusedBorderColor = Color(0xFF334155)
                    ),
                    singleLine = false,
                    maxLines = 3,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_textfield"),
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = {
                        if (inputChatQuery.isNotBlank()) {
                            viewModel.sendChatMessage(inputChatQuery)
                            inputChatQuery = ""
                        }
                    },
                    enabled = chatState !is UiState.Loading && inputChatQuery.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B5CF6),
                        disabledContainerColor = Color(0xFF475569)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("send_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send, 
                        contentDescription = "Send text", 
                        tint = if (inputChatQuery.isNotBlank()) Color.White else Color(0xFF94A3B8)
                    )
                }
            }
        }
    }

    // Document Anchor Selector Popup
    if (showDocSelector) {
        AlertDialog(
            onDismissRequest = { showDocSelector = false },
            title = { 
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Attachment, contentDescription = null, tint = Color(0xFFA78BFA))
                    Text("Select Document Anchor", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF1E293B),
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Anchor the AI's intelligence to one of your custom study documents to enable contextual summarizations and quiz generators.", 
                        color = Color(0xFF94A3B8), 
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))

                    // Clear Context Selector Option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (activeNote == null) Color(0x338B5CF6) else Color.Transparent)
                            .clickable {
                                viewModel.selectNoteForContext(null)
                                showDocSelector = false
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = activeNote == null, 
                            onClick = {
                                viewModel.selectNoteForContext(null)
                                showDocSelector = false
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF8B5CF6))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("No anchor (general assistant)", color = Color.White, fontSize = 14.sp)
                    }

                    if (notes.isEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                        ) {
                            Text(
                                text = "No documents saved. Add some in 'Study Prep' screen first!", 
                                color = Color(0xFF94A3B8), 
                                fontSize = 12.sp, 
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        notes.forEach { note ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (activeNote?.id == note.id) Color(0x338B5CF6) else Color.Transparent)
                                    .clickable {
                                        viewModel.selectNoteForContext(note)
                                        showDocSelector = false
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = activeNote?.id == note.id, 
                                    onClick = {
                                        viewModel.selectNoteForContext(note)
                                        showDocSelector = false
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF8B5CF6))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(note.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDocSelector = false }) {
                    Text("Dismiss", color = Color(0xFF60A5FA))
                }
            }
        )
    }
}
