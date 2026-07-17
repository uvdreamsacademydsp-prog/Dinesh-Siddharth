package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.viewmodel.AppViewModel
import com.example.ui.viewmodel.UiState

data class EnglishTask(
    val id: Int,
    val title: String,
    val description: String,
    val scenario: String,
    val hints: List<String>,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpokenEnglishTabContent(viewModel: AppViewModel) {
    val feedbackState by viewModel.spokenEnglishFeedback.collectAsStateWithLifecycle()

    val tasks = remember {
        listOf(
            EnglishTask(
                id = 1,
                title = "Job Interview Practice",
                description = "Practice introducing yourself and explaining why you are the best candidate for the position.",
                scenario = "An interviewer asks you in Telugu context: 'Why should we hire you for this role? What unique strengths do you bring?'",
                hints = listOf("I have strong problem-solving skills...", "In my previous experiences, I learned...", "I am eager to contribute to..."),
                icon = Icons.Default.BusinessCenter
            ),
            EnglishTask(
                id = 2,
                title = "Daily Conversations (నిత్య సంభాషణలు)",
                description = "Learn how to translate standard Telugu daily sentences to natural English.",
                scenario = "Translate and practice saying this: 'నేను రేపు పొద్దున్నే ఆఫీసుకి వెళ్ళాలి, తొందరగా నిద్రలేవాలి.' (I have to go to the office early tomorrow morning, I need to wake up early.)",
                hints = listOf("I have to go to the office tomorrow early morning...", "I need to wake up early...", "I must sleep soon..."),
                icon = Icons.Default.ChatBubbleOutline
            ),
            EnglishTask(
                id = 3,
                title = "Ordering Food at a Cafe (క్యాంటీన్ ఆర్డర్)",
                description = "Simulate ordering drinks or meals politely with specific customized preferences.",
                scenario = "You want to order tea but need to speak in English: 'నాకు పంచదార లేకుండా ఒక కప్పు వేడి అల్లం టీ మరియు స్యాండ్‌విచ్ ఇవ్వండి.' (Give me a cup of hot ginger tea without sugar and a sandwich.)",
                hints = listOf("I would like to order a cup of hot ginger tea without sugar...", "Could I get that with less sugar?", "Please make it toasted."),
                icon = Icons.Default.LocalCafe
            ),
            EnglishTask(
                id = 4,
                title = "Asking/Giving Directions (దారులు చెప్పడం)",
                description = "Learn how to politely help lost travelers or inquire about transit routes.",
                scenario = "A foreigner asks you for directions to the railway station. Translate and say: 'ఇక్కడి నుండి నేరుగా వెళ్లి మొదటి ఎడమ మలుపు తీసుకోండి.' (Go straight from here and take the first left.)",
                hints = listOf("Go straight from here...", "Take the first left turn...", "It is just opposite the..."),
                icon = Icons.Default.Directions
            ),
            EnglishTask(
                id = 5,
                title = "Grammar Sandbox & Bilingual Improver",
                description = "Type any sentence you want to verify. AI will optimize its grammar, flow, and natural phrasing with Telugu details.",
                scenario = "Type a custom sentence you are unsure of. AI will rewrite it elegantly, explain errors, and provide the Telugu context.",
                hints = listOf("Type freely: 'He do not know English...' or 'Since yesterday I am working...'", "Great for daily check-ups!"),
                icon = Icons.Default.AutoAwesome
            )
        )
    }

    var selectedTask by remember { mutableStateOf<EnglishTask?>(null) }
    var studentAnswerText by remember { mutableStateOf("") }

    var revealedCardIndices by remember { mutableStateOf(setOf<Int>()) }
    val quickCards = remember {
        listOf(
            "నేను నీ కోసం ఎదురుచూస్తున్నాను" to "I have been waiting for you.",
            "నువ్వు ఎప్పుడు వస్తావు?" to "When will you arrive?",
            "దయచేసి కొంచెం నెమ్మదిగా మాట్లాడండి" to "Please speak a bit more slowly.",
            "నేను దీనిని అర్థం చేసుకోగలను" to "I can understand this.",
            "మళ్ళీ రేపు కలుద్దాం" to "See you again tomorrow!",
            "అది ఎక్కడ ఉందో మీకు తెలుసా?" to "Do you know where it is?",
            "అలా అనడానికి వీల్లేదు" to "You shouldn't say that."
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = "Spoken English",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Spoken English AI Coach",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Select customized speaking challenges and receive comprehensive bilingual Telugu-to-English AI reviews.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        if (selectedTask == null) {
            // Task selection view - combined single outer LazyColumn to avoid nested scroll issues
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Scenarios Header
                item {
                    Text(
                        text = "Select practice scenarios / సాధన అంశాలు:",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Render tasks
                items(tasks) { task ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedTask = task
                                studentAnswerText = ""
                                viewModel.resetSpokenEnglishFeedback()
                            }
                            .testTag("english_task_item_${task.id}"),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0F172A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = task.icon,
                                    contentDescription = task.title,
                                    tint = Color(0xFFF472B6),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.title,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = task.description,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 12.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Open challenge",
                                tint = Color(0xFF475569)
                            )
                        }
                    }
                }

                // Telugu-to-English Quick Study deck Header
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "💡 Interactive Cards / నిత్యం వాడే వాక్యాలు:",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEC4899),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    Text(
                        text = "Tap on any Telugu card to reveal its beautiful Spoken English translation instantly!",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Render horizontal scrollable Quick Study cards
                item {
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        items(quickCards.size) { index ->
                            val card = quickCards[index]
                            val isRevealed = revealedCardIndices.contains(index)
                            Card(
                                modifier = Modifier
                                    .width(220.dp)
                                    .height(115.dp)
                                    .clickable {
                                        revealedCardIndices = if (isRevealed) {
                                            revealedCardIndices - index
                                        } else {
                                            revealedCardIndices + index
                                        }
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isRevealed) Color(0x228B5CF6) else Color(0xFF1E293B)
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isRevealed) Color(0xFF8B5CF6) else Color(0xFF334155)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = "Telugu (తెలుగు):",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = card.first,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            lineHeight = 16.sp
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                if (isRevealed) Color(0xFF8B5CF6) else Color(0xFF475569),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .padding(vertical = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (isRevealed) card.second else "Tap to Reveal English",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Active task details & feedback area
            val task = selectedTask!!

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Task brief card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = task.icon,
                                        contentDescription = task.title,
                                        tint = Color(0xFFEC4899),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = task.title,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 15.sp
                                    )
                                }

                                TextButton(
                                    onClick = { selectedTask = null },
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF64748B))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.size(14.dp))
                                        Text("All Tasks", fontSize = 12.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "SCENARIO:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFFEC4899)
                            )
                            Text(
                                text = task.scenario,
                                color = Color.White,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "SUGGESTED WORDS / HINTS:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFF60A5FA)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            task.hints.forEach { hint ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(vertical = 1.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF60A5FA))
                                    )
                                    Text(text = hint, color = Color(0xFF94A3B8), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // Answer form card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Your Spoken/Written Answer:",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )

                            OutlinedTextField(
                                value = studentAnswerText,
                                onValueChange = { studentAnswerText = it },
                                placeholder = { Text("Draft your response in English...", color = Color(0xFF64748B), fontSize = 13.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFEC4899),
                                    unfocusedBorderColor = Color(0xFF334155),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                minLines = 3,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("spoken_english_input_field")
                            )

                            Button(
                                onClick = {
                                    if (studentAnswerText.isNotBlank()) {
                                        viewModel.evaluateSpokenEnglish(task.title, task.scenario, studentAnswerText)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("submit_spoken_english_button"),
                                shape = RoundedCornerShape(8.dp),
                                enabled = studentAnswerText.isNotBlank() && feedbackState !is UiState.Loading
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "Evaluate",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text("Analyze with AI English Coach", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                // Loading or Feedback Response view
                item {
                    when (val state = feedbackState) {
                        is UiState.Loading -> {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    CircularProgressIndicator(color = Color(0xFFEC4899))
                                    Text(
                                        text = "AI Coach is evaluating your sentence grammar & pronunciation flow...",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                        is UiState.Success -> {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0x228B5CF6)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8B5CF6)),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = "Success",
                                            tint = Color(0xFFA78BFA),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "Coach Feedback Report",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = state.data,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        lineHeight = 20.sp
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))
                                    Button(
                                        onClick = {
                                            studentAnswerText = ""
                                            viewModel.resetSpokenEnglishFeedback()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Text("Try Again / Revise", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                        is UiState.Error -> {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0x22EF4444)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Error: " + state.message,
                                    color = Color(0xFFF87171),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                        else -> {
                            // Idle
                        }
                    }
                }
            }
        }
    }
}
