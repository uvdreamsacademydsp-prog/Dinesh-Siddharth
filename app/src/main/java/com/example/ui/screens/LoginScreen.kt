package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AppViewModel

data class ServicePreviewInfo(
    val title: String,
    val teluguTitle: String,
    val description: String,
    val teluguDesc: String,
    val colors: List<Color>
)

@Composable
fun LoginScreen(viewModel: AppViewModel, onLoginSuccess: () -> Unit) {
    val user by viewModel.userState.collectAsState()
    val authError by viewModel.authError.collectAsState()

    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Service Preview tab state: 0=Education, 1=Grocery, 2=Food Delivery
    var selectedServicePreview by remember { mutableStateOf(0) }

    LaunchedEffect(user) {
        if (user != null) {
            onLoginSuccess()
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF0F172A)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
                    .widthIn(max = 480.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Header Display (using Custom Branding Logo)
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.mana_choice_logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(androidx.compose.foundation.shape.CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isLoginMode) "Welcome to Mana Choice" else "Create a New Account",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Your premium companion app for education, groceries, and dining",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // ----------------------------------------------------
                // 3 SERVICE AREAS PREVIEW GRID (MANDATORY REQUIREMENT)
                // ----------------------------------------------------
                Text(
                    text = "OUR 3 CORE SERVICES / మా సేవలు:",
                    color = Color(0xFF38BDF8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 1. Education
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedServicePreview = 0 },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedServicePreview == 0) Color(0x333B82F6) else Color(0xFF1E293B)
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (selectedServicePreview == 0) Color(0xFF3B82F6) else Color(0xFF334155)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = "Education",
                                tint = if (selectedServicePreview == 0) Color(0xFF60A5FA) else Color(0xFF94A3B8),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "1. Education",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "విద్య",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    // 2. Grocery
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedServicePreview = 1 },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedServicePreview == 1) Color(0x3310B981) else Color(0xFF1E293B)
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (selectedServicePreview == 1) Color(0xFF10B981) else Color(0xFF334155)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Grocery",
                                tint = if (selectedServicePreview == 1) Color(0xFF34D399) else Color(0xFF94A3B8),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "2. Grocery",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "గ్రాసరీస్",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    // 3. Food Delivery
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedServicePreview = 2 },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedServicePreview == 2) Color(0x33F43F5E) else Color(0xFF1E293B)
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (selectedServicePreview == 2) Color(0xFFF43F5E) else Color(0xFF334155)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fastfood,
                                contentDescription = "Food Delivery",
                                tint = if (selectedServicePreview == 2) Color(0xFFFB7185) else Color(0xFF94A3B8),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "3. Food Delivery",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "ఆహార పంపిణీ",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Interactive Dynamic Service Showcase Card (Telugu + English)
                AnimatedContent(
                    targetState = selectedServicePreview
                ) { targetState ->
                    val info = when (targetState) {
                        0 -> ServicePreviewInfo(
                            title = "Interactive Education & Spoken AI",
                            teluguTitle = "విద్య & స్పోకెన్ ఇంగ్లీష్ సాధన",
                            description = "Ace exams with flashcards & countdowns. Practice spoken English fluency with specialized bilingual Telugu-to-English AI reviews.",
                            teluguDesc = "పోటీ పరీక్షల తయారీ, కౌంట్ డౌన్ మరియు తెలుగు సహాయంతో సులువుగా ఇంగ్లీష్ మాట్లాడటం నేర్చుకునే అద్భుతమైన AI కోచ్ సేవలు.",
                            colors = listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6))
                        )
                        1 -> ServicePreviewInfo(
                            title = "Smart Shopping & Grocery Lists",
                            teluguTitle = "సరుకులు & స్మార్ట్ గ్రాసరీ మేనేజర్",
                            description = "Maintain lists offline, track household item stock, check barcode metadata, and sync budgets easily with cloud backup.",
                            teluguDesc = "ఇంటికి కావలసిన నిత్యావసర వస్తువుల జాబితా, బడ్జెట్ గణనలు మరియు బార్ కోడ్ స్కానింగ్ సదుపాయాలు.",
                            colors = listOf(Color(0xFF10B981), Color(0xFF059669))
                        )
                        else -> ServicePreviewInfo(
                            title = "Premium Cafe & Express Food Delivery",
                            teluguTitle = "ఆహార విక్రయాలు & ఫుడ్ డెలివరీ",
                            description = "Explore customized dining options! Order hot beverages like tea or meals and generate dynamic recipes using AI.",
                            teluguDesc = "వేడి వేడి టిఫిన్లు, రుచికరమైన భోజనం మరియు కాఫీ-టీలను నేరుగా ఆర్డర్ చేసి డెలివరీ పొందే కస్టమైజ్డ్ ఫుడ్ సదుపాయాలు.",
                            colors = listOf(Color(0xFFF43F5E), Color(0xFFE11D48))
                        )
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Brush.horizontalGradient(colors = info.colors))
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "Feature info",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = info.title,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        fontSize = 13.sp
                                    )
                                }
                                Text(
                                    text = info.teluguTitle,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = info.description,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                                Text(
                                    text = info.teluguDesc,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    fontWeight = FontWeight.Light
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Error Card
                AnimatedVisibility(
                    visible = authError != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0x33EF4444)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error icon",
                                tint = Color(0xFFEF4444)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = authError ?: "",
                                color = Color(0xFFFCA5A5),
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Input Credentials Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Email Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address", color = Color(0xFF94A3B8)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF3B82F6),
                                unfocusedBorderColor = Color(0xFF475569)
                            ),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email",
                                    tint = Color(0xFF64748B)
                                )
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("email_input")
                        )

                        // Password Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password", color = Color(0xFF94A3B8)) },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF3B82F6),
                                unfocusedBorderColor = Color(0xFF475569)
                            ),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Password",
                                    tint = Color(0xFF64748B)
                                )
                            },
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                val description = if (passwordVisible) "Hide password" else "Show password"
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(imageVector = image, contentDescription = description, tint = Color(0xFF64748B))
                                }
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("password_input")
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                if (isLoginMode) {
                                    viewModel.loginWithEmail(email, password)
                                } else {
                                    viewModel.signUpWithEmail(email, password)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("login_submit_button")
                        ) {
                            Text(
                                text = if (isLoginMode) "Sign In" else "Register Account",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Toggle Mode Click
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isLoginMode) "Don't have an account? " else "Already have an account? ",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp
                    )
                    Text(
                        text = if (isLoginMode) "Register" else "Login",
                        color = Color(0xFF60A5FA),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .clickable {
                                isLoginMode = !isLoginMode
                            }
                            .testTag("mode_toggle")
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Divider(color = Color(0xFF334155), thickness = 1.dp)

                Spacer(modifier = Modifier.height(16.dp))

                // Firebase/Google Google Sign-In Simulation Button
                OutlinedButton(
                    onClick = { viewModel.loginWithGoogle() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("google_login_button")
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TravelExplore,
                            contentDescription = "Google Icon",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Continue with Google",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Firebase Auth and Cloud Sync can be configured with google-services.json details in production settings.",
                    color = Color(0xFF475569),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

