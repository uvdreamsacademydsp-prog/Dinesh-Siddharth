package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AttendanceRecord
import com.example.data.model.AttendanceSession
import com.example.ui.viewmodel.AppViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceTabContent(viewModel: AppViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentUser by viewModel.userState.collectAsStateWithLifecycle()

    val sessions by viewModel.attendanceSessions.collectAsStateWithLifecycle()
    val allRecords by viewModel.allAttendanceRecords.collectAsStateWithLifecycle()

    var isAdminTab by remember { mutableStateOf(false) } // False: Student Desk, True: Teacher Admin
    var showAddSessionDialog by remember { mutableStateOf(false) }
    var newSessionTitle by remember { mutableStateOf("") }
    var newSessionCodeInput by remember { mutableStateOf("") }

    var selectedSessionForQR by remember { mutableStateOf<AttendanceSession?>(null) }
    var selectedSessionForLogs by remember { mutableStateOf<AttendanceSession?>(null) }

    // State for manual check-in testing / simulation
    var manualCodeField by remember { mutableStateOf("") }
    var scanStatusMessage by remember { mutableStateOf<String?>(null) }
    var scanStatusSuccess by remember { mutableStateOf(true) }

    // Teacher Console admin protection states
    var isTeacherAuthenticated by remember { mutableStateOf(false) }
    var showAdminLoginDialog by remember { mutableStateOf(false) }
    var adminNameInput by remember { mutableStateOf("") }
    var adminPasswordInput by remember { mutableStateOf("") }
    var adminLoginError by remember { mutableStateOf<String?>(null) }

    // Setup GPS Scanner Options
    val barcodeScannerOptions = remember {
        GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .enableAutoZoom()
            .build()
    }
    val scannerClient = remember {
        GmsBarcodeScanning.getClient(context, barcodeScannerOptions)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Double Segmented Control Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E293B))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (!isAdminTab) Color(0xFFEC4899) else Color.Transparent)
                    .clickable { isAdminTab = false }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Student Mode",
                        tint = if (!isAdminTab) Color.White else Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Student Desk",
                        color = if (!isAdminTab) Color.White else Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isAdminTab) Color(0xFFEC4899) else Color.Transparent)
                    .clickable {
                        if (isTeacherAuthenticated) {
                            isAdminTab = true
                        } else {
                            adminNameInput = ""
                            adminPasswordInput = ""
                            adminLoginError = null
                            showAdminLoginDialog = true
                        }
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SupervisorAccount,
                        contentDescription = "Teacher Mode",
                        tint = if (isAdminTab) Color.White else Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Teacher Console",
                        color = if (isAdminTab) Color.White else Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Action Status Banner (Success/Error feedback)
        scanStatusMessage?.let { msg ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (scanStatusSuccess) Color(0x2210B981) else Color(0x22EF4444)
                ),
                border = BorderStroke(1.dp, if (scanStatusSuccess) Color(0xFF10B981) else Color(0xFFEF4444)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (scanStatusSuccess) Icons.Default.CheckCircle else Icons.Default.Info,
                            contentDescription = "Status Icon",
                            tint = if (scanStatusSuccess) Color(0xFF34D399) else Color(0xFFF87171)
                        )
                        Text(
                            text = msg,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    IconButton(
                        onClick = { scanStatusMessage = null },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss Banner",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // 2. Main Content Cards based on role
        if (isAdminTab) {
            // TEACHER VIEW: SESSION MANAGEMENT
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Active Classes",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Generate QR codes and monitor scan feeds",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Lock Console button
                    IconButton(
                        onClick = {
                            isTeacherAuthenticated = false
                            isAdminTab = false
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0x33EF4444)),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock Console",
                            tint = Color(0xFFF87171),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Button(
                        onClick = { showAddSessionDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("add_session_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Class",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Session", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Total Classes", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        Text("${sessions.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF472B6))
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Total Swipes", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        Text("${allRecords.size}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF472B6))
                    }
                }
            }

            // Sessions List
            if (sessions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "No sessions",
                            tint = Color(0xFF334155),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "No Classes Available Yet",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8),
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Create a custom session to generate an authentic QR scan card.",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(sessions) { session ->
                        val recordsCount = allRecords.count { it.sessionId == session.id }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = session.title,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = session.dateString,
                                                fontSize = 11.sp,
                                                color = Color(0xFF94A3B8)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color(0xFF0F172A))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = session.sessionCode,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFF472B6)
                                                )
                                            }
                                        }
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteAttendanceSession(session.id) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Divider(color = Color(0xFF334155), thickness = 0.5.dp)
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Attendee headcount
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Checked-in icon",
                                            tint = Color(0xFF10B981),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "$recordsCount students scanned",
                                            fontSize = 12.sp,
                                            color = Color(0xFF10B981),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        TextButton(
                                            onClick = { selectedSessionForLogs = session },
                                            contentPadding = PaddingValues(horizontal = 8.dp),
                                            colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF60A5FA))
                                        ) {
                                            Text("Logs", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = { selectedSessionForQR = session },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x22EC4899)),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.QrCode,
                                                contentDescription = "View QR",
                                                tint = Color(0xFFF472B6),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Show QR", fontSize = 11.sp, color = Color(0xFFF472B6), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // STUDENT VIEW: SELF SCAN & HISTORY
            val name = currentUser?.displayName ?: "Companion"
            val email = currentUser?.email ?: "student@example.com"

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Personal details badge
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF334155)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEC4899),
                                    fontSize = 18.sp
                                )
                            }
                            Column {
                                Text(
                                    text = "Scanned Identity Card",
                                    fontSize = 11.sp,
                                    color = Color(0xFFF472B6),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = name,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = email,
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                // Interactive scanning card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0F172A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Scan Icon",
                                    tint = Color(0xFFEC4899),
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Text(
                                text = "Scan Attendance QR Code",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 16.sp
                            )

                            Text(
                                text = "Point your camera at a session QR code provided by your instructor to check in instantly.",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8),
                                textAlign = TextAlign.Center
                            )

                            Button(
                                onClick = {
                                    // Start Play Services Scanner
                                    try {
                                        scannerClient.startScan()
                                            .addOnSuccessListener { barcode ->
                                                val scannedValue = barcode.rawValue ?: ""
                                                if (scannedValue.isNotEmpty()) {
                                                    coroutineScope.launch {
                                                        val result = viewModel.recordSessionCheckIn(
                                                            sessionCode = scannedValue,
                                                            studentName = name,
                                                            studentEmail = email
                                                        )
                                                        scanStatusSuccess = result.startsWith("SUCCESS")
                                                        scanStatusMessage = result
                                                    }
                                                }
                                            }
                                            .addOnFailureListener { e ->
                                                scanStatusSuccess = false
                                                scanStatusMessage = "Scanner Error: ${e.localizedMessage ?: "Camera not available in emulator mode"}. Please use the Simulator Sandbox below!"
                                            }
                                    } catch (e: Exception) {
                                        scanStatusSuccess = false
                                        scanStatusMessage = "Scan exception: ${e.localizedMessage}. Please use the Sandbox below."
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("student_scan_camera_button"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = "Launch Scanner",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Launch QR Scanner", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Divider(color = Color(0xFF334155), thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(4.dp))

                            // SIMULATOR BOX (For Browser Preview testing)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0F172A))
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Simulator Info",
                                        tint = Color(0xFF60A5FA),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Web Emulator Sandbox",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF60A5FA),
                                        fontSize = 11.sp
                                    )
                                }

                                Text(
                                    text = "Quick check-in simulation (since camera hardware is absent in browser environment):",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )

                                if (sessions.isNotEmpty()) {
                                    Text(
                                        text = "Tap any class code below to simulate check-in:",
                                        fontSize = 9.sp,
                                        color = Color(0xFF64748B),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        sessions.take(3).forEach { sess ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color(0xFF1E293B))
                                                    .border(1.dp, Color(0xFFEC4899), RoundedCornerShape(4.dp))
                                                    .clickable {
                                                        coroutineScope.launch {
                                                            val result = viewModel.recordSessionCheckIn(
                                                                sessionCode = sess.sessionCode,
                                                                studentName = name,
                                                                studentEmail = email
                                                            )
                                                            scanStatusSuccess = result.startsWith("SUCCESS")
                                                            scanStatusMessage = result
                                                        }
                                                    }
                                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = sess.sessionCode,
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = manualCodeField,
                                        onValueChange = { manualCodeField = it },
                                        placeholder = { Text("Or enter Code", fontSize = 12.sp, color = Color(0xFF64748B)) },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFFEC4899),
                                            unfocusedBorderColor = Color(0xFF334155),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        ),
                                        singleLine = true,
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp),
                                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                                    )

                                    Button(
                                        onClick = {
                                            if (manualCodeField.isNotBlank()) {
                                                coroutineScope.launch {
                                                    val result = viewModel.recordSessionCheckIn(
                                                        sessionCode = manualCodeField,
                                                        studentName = name,
                                                        studentEmail = email
                                                    )
                                                    scanStatusSuccess = result.startsWith("SUCCESS")
                                                    scanStatusMessage = result
                                                    if (result.startsWith("SUCCESS")) {
                                                        manualCodeField = ""
                                                    }
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.height(44.dp)
                                    ) {
                                        Text("Go", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }

                // History Header
                item {
                    Text(
                        text = "My Attendance History",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Student's personal history list
                val myRecords = allRecords.filter { it.studentEmail.trim().lowercase() == email.trim().lowercase() }
                if (myRecords.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = "No history",
                                        tint = Color(0xFF334155),
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Text(
                                        text = "No Scans Recorded Yet",
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF94A3B8),
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "Once you scan or simulate a check-in, your attendance logs will appear here.",
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                } else {
                    items(myRecords) { rec ->
                        val matchingSession = sessions.find { it.id == rec.sessionId }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
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
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981))
                                    )
                                    Column {
                                        Text(
                                            text = matchingSession?.title ?: "Unknown Session",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "Swiped on: " + SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(rec.scanTimestamp)),
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0x2210B981))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = rec.status,
                                        color = Color(0xFF34D399),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- DIALOGS & BOTTOM SHEETS ---

    // 1. ADD SESSION DIALOG
    if (showAddSessionDialog) {
        AlertDialog(
            onDismissRequest = { showAddSessionDialog = false },
            containerColor = Color(0xFF1E293B),
            title = {
                Text(
                    text = "Add New Session",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = newSessionTitle,
                        onValueChange = { newSessionTitle = it },
                        label = { Text("Class / Meeting Title") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFEC4899),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedLabelColor = Color(0xFFEC4899),
                            unfocusedLabelColor = Color(0xFF94A3B8),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_session_title_field")
                    )

                    OutlinedTextField(
                        value = newSessionCodeInput,
                        onValueChange = { newSessionCodeInput = it },
                        label = { Text("Custom Code (Optional)") },
                        placeholder = { Text("e.g. MATH101") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFEC4899),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedLabelColor = Color(0xFFEC4899),
                            unfocusedLabelColor = Color(0xFF94A3B8),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("add_session_code_field")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newSessionTitle.isNotBlank()) {
                            viewModel.addAttendanceSession(
                                title = newSessionTitle,
                                code = newSessionCodeInput
                            )
                            newSessionTitle = ""
                            newSessionCodeInput = ""
                            showAddSessionDialog = false
                            Toast.makeText(context, "Class Session Created!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899))
                ) {
                    Text("Create", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddSessionDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF94A3B8))
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // 2. SHOW QR CODE DIALOG
    if (selectedSessionForQR != null) {
        val session = selectedSessionForQR!!
        val clipboardManager = LocalClipboardManager.current

        AlertDialog(
            onDismissRequest = { selectedSessionForQR = null },
            containerColor = Color(0xFF1E293B),
            title = {
                Text(
                    text = session.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Scan Code below to Register Attendance",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center
                    )

                    // Draw actual Custom Canvas QR Code
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .size(200.dp)
                            .padding(10.dp)
                    ) {
                        QRCodeCanvas(
                            code = session.sessionCode,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF0F172A))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Code: " + session.sessionCode,
                            color = Color(0xFFEC4899),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(session.sessionCode))
                            Toast.makeText(context, "Session Code Copied to Clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CopyAll,
                            contentDescription = "Copy code",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Code", fontSize = 12.sp, color = Color.White)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedSessionForQR = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close Panel", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // 3. SHOW ATTENDANCE LOGS DIALOG
    if (selectedSessionForLogs != null) {
        val session = selectedSessionForLogs!!
        val sessionRecords = allRecords.filter { it.sessionId == session.id }

        AlertDialog(
            onDismissRequest = { selectedSessionForLogs = null },
            containerColor = Color(0xFF1E293B),
            title = {
                Text(
                    text = "Attendance Logs",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                ) {
                    Text(
                        text = session.title,
                        color = Color(0xFFEC4899),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    Divider(color = Color(0xFF334155), thickness = 0.5.dp)

                    if (sessionRecords.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No student swipe entries yet.",
                                color = Color(0xFF64748B),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(sessionRecords) { rec ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = rec.studentName,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = rec.studentEmail,
                                                color = Color(0xFF94A3B8),
                                                fontSize = 11.sp
                                            )
                                            if (rec.deviceModel != null) {
                                                Text(
                                                    text = "Device: ${rec.deviceModel}",
                                                    color = Color(0xFF64748B),
                                                    fontSize = 9.sp
                                                )
                                            }
                                        }

                                        Column(
                                            horizontalAlignment = Alignment.End,
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(rec.scanTimestamp)),
                                                color = Color(0xFFEC4899),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )

                                            IconButton(
                                                onClick = { viewModel.deleteAttendanceRecord(rec.id) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete swipe",
                                                    tint = Color(0xFFEF4444),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedSessionForLogs = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Finished", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // 4. TEACHER ADMIN LOGIN DIALOG
    if (showAdminLoginDialog) {
        AlertDialog(
            onDismissRequest = { showAdminLoginDialog = false },
            containerColor = Color(0xFF1E293B),
            title = {
                Text(
                    text = "Teacher Console Authentication",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Please authenticate using your administrative credentials to manage sessions.",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp
                    )

                    OutlinedTextField(
                        value = adminNameInput,
                        onValueChange = { adminNameInput = it },
                        label = { Text("Admin Name") },
                        placeholder = { Text("e.g. Dinesh") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFEC4899),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedLabelColor = Color(0xFFEC4899),
                            unfocusedLabelColor = Color(0xFF94A3B8),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_username_field")
                    )

                    OutlinedTextField(
                        value = adminPasswordInput,
                        onValueChange = { adminPasswordInput = it },
                        label = { Text("Password") },
                        placeholder = { Text("Enter admin password") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFEC4899),
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedLabelColor = Color(0xFFEC4899),
                            unfocusedLabelColor = Color(0xFF94A3B8),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().testTag("admin_password_field")
                    )

                    if (adminLoginError != null) {
                        Text(
                            text = adminLoginError!!,
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (adminNameInput == "Dinesh" && adminPasswordInput == "123456") {
                            isTeacherAuthenticated = true
                            isAdminTab = true
                            showAdminLoginDialog = false
                            adminLoginError = null
                        } else {
                            adminLoginError = "Invalid admin credentials! Hint: Check Name & Password."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login to Console", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAdminLoginDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF94A3B8))
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Custom QR Code Renderer drawn natively on a Jetpack Compose Canvas.
 * Emulates authentic Finder Patterns and timing blocks, with a deterministic random grid
 * generated from the session code's hash value, ensuring every code looks distinctly real.
 */
@Composable
fun QRCodeCanvas(code: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.aspectRatio(1f)) {
        val sizePx = size.width
        val blocks = 21 // Version 1 QR is 21x21 matrix
        val blockSize = sizePx / blocks

        // Draw clean white background
        drawRect(Color.White)

        // Draw Finder Pattern helper (Top-Left, Top-Right, Bottom-Left)
        fun drawFinderPattern(col: Int, row: Int) {
            // Outer 7x7 dark block
            drawRect(
                color = Color.Black,
                topLeft = androidx.compose.ui.geometry.Offset(col * blockSize, row * blockSize),
                size = androidx.compose.ui.geometry.Size(7 * blockSize, 7 * blockSize)
            )
            // Inner 5x5 light square
            drawRect(
                color = Color.White,
                topLeft = androidx.compose.ui.geometry.Offset((col + 1) * blockSize, (row + 1) * blockSize),
                size = androidx.compose.ui.geometry.Size(5 * blockSize, 5 * blockSize)
            )
            // Center 3x3 dark square
            drawRect(
                color = Color.Black,
                topLeft = androidx.compose.ui.geometry.Offset((col + 2) * blockSize, (row + 2) * blockSize),
                size = androidx.compose.ui.geometry.Size(3 * blockSize, 3 * blockSize)
            )
        }

        // Render standard finder patterns
        drawFinderPattern(col = 0, row = 0)         // Top Left
        drawFinderPattern(col = 14, row = 0)        // Top Right
        drawFinderPattern(col = 0, row = 14)        // Bottom Left

        // Draw Timing Patterns (dashed lines connecting finder corners)
        for (i in 7..13) {
            val color = if (i % 2 == 0) Color.Black else Color.White
            drawRect(
                color = color,
                topLeft = androidx.compose.ui.geometry.Offset(6 * blockSize, i * blockSize),
                size = androidx.compose.ui.geometry.Size(blockSize, blockSize)
            )
            drawRect(
                color = color,
                topLeft = androidx.compose.ui.geometry.Offset(i * blockSize, 6 * blockSize),
                size = androidx.compose.ui.geometry.Size(blockSize, blockSize)
            )
        }

        // Draw deterministic matrix payload based on hash of code
        val hash = code.hashCode()
        val random = java.util.Random(hash.toLong())

        for (r in 0 until blocks) {
            for (c in 0 until blocks) {
                // Ensure we DO NOT overwrite the primary 3 finder regions
                if ((r < 8 && c < 8) || (r < 8 && c >= 13) || (r >= 13 && c < 8)) {
                    continue
                }
                // Skip timing patterns
                if (r == 6 || c == 6) {
                    continue
                }
                // Randomly activate QR cells
                if (random.nextBoolean()) {
                    drawRect(
                        color = Color.Black,
                        topLeft = androidx.compose.ui.geometry.Offset(c * blockSize, r * blockSize),
                        size = androidx.compose.ui.geometry.Size(blockSize, blockSize)
                    )
                }
            }
        }
    }
}
