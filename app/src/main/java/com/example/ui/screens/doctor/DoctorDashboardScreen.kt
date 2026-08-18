package com.example.ui.screens.doctor

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MessageSender
import com.example.data.model.PatientSummary
import com.example.data.model.RiskCategory
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
import com.example.data.repository.PodoRepository
import com.example.ui.components.PodoTopBar
import com.example.ui.screens.chat.ChatMessageBubble
import com.example.ui.theme.DoctorPurple
import com.example.ui.theme.DoctorPurpleContainer
import com.example.ui.theme.DoctorPurpleLight
import com.example.ui.theme.HealthTealPrimary
import com.example.ui.theme.PodoOrange
import com.example.ui.theme.PodoSoftMint
import com.example.ui.theme.PodoTeal
import com.example.ui.theme.PodoTealContainer
import com.example.ui.theme.PodoTealDark
import com.example.ui.theme.PodoTealLight
import com.example.ui.theme.PodoTealPrimary
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun DoctorDashboardScreen(
    repository: PodoRepository,
    onSwitchRole: () -> Unit,
    onNavigateToEDevlet: () -> Unit = {}
) {
    val doctorProfile by repository.doctorProfile.collectAsState()
    val patientRoster by repository.patientRoster.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabTitles = listOf(
        "👥 Patients (${patientRoster.size})",
        "💬 Direct Chat",
        "📋 Clinical Record",
        "🏥 Practice Profile"
    )

    if (!doctorProfile.isVerified && doctorProfile.verificationStatus == VerificationStatus.PENDING_APPROVAL) {
        PendingApprovalScreen(
            repository = repository,
            onNavigateToEDevlet = onNavigateToEDevlet,
            onSwitchToPatient = onSwitchRole
        )
        return
    }

    Scaffold(
        topBar = {
            PodoTopBar(
                title = "Podiatrist Clinical Portal",
                subtitle = "${doctorProfile.fullName} • ${patientRoster.size} Active Patients",
                currentRole = UserRole.PODOLOGIST,
                onSwitchRoleClick = onSwitchRole
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Verified Badge Banner
            if (doctorProfile.isVerified) {
                Surface(
                    color = PodoSoftMint,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Verified Specialist",
                            tint = PodoTeal,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "State Board & US Health ID Verified Podiatrist (NPI: ${doctorProfile.diplomaRegistryNo})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PodoTealDark
                        )
                    }
                }
            }

            // Segmented Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = DoctorPurple,
                edgePadding = 12.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = DoctorPurple,
                        height = 3.5.dp
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp,
                                color = if (selectedTabIndex == index) DoctorPurple else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> DoctorPatientRosterTab(
                    repository = repository,
                    onOpenChatForPatient = {
                        selectedTabIndex = 1
                    },
                    onOpenFileForPatient = {
                        selectedTabIndex = 2
                    }
                )
                1 -> DoctorPatientChatTab(repository)
                2 -> DoctorPatientFileTab(repository)
                3 -> DoctorClinicProfileTab(repository, onNavigateToEDevlet)
            }
        }
    }
}

// -------------------------------------------------------------
// 1. Multi-Patient Roster & Triage Queue Tab
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DoctorPatientRosterTab(
    repository: PodoRepository,
    onOpenChatForPatient: () -> Unit,
    onOpenFileForPatient: () -> Unit
) {
    val patientRoster by repository.patientRoster.collectAsState()
    val broadcastAnnouncements by repository.broadcastAnnouncements.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var selectedFilter by remember { mutableStateOf(RiskCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var showBroadcastDialog by remember { mutableStateOf(false) }
    var showAddPatientDialog by remember { mutableStateOf(false) }
    var showCodeDialog by remember { mutableStateOf(false) }
    var currentInviteCode by remember { mutableStateOf("") }

    val filteredPatients = patientRoster.filter { patient ->
        val matchesCategory = (selectedFilter == RiskCategory.ALL || patient.riskCategory == selectedFilter)
        val matchesSearch = searchQuery.isBlank() ||
                patient.fullName.contains(searchQuery, ignoreCase = true) ||
                patient.diabetesStatus.contains(searchQuery, ignoreCase = true) ||
                patient.footRiskLevel.contains(searchQuery, ignoreCase = true) ||
                patient.tcKimlikNo.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    val criticalCount = patientRoster.count { it.riskCategory == RiskCategory.CRITICAL_ALERT }
    val moderateCount = patientRoster.count { it.riskCategory == RiskCategory.MODERATE_RISK }
    val postOpCount = patientRoster.count { it.riskCategory == RiskCategory.POST_OP_BRACING }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 14.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Quick Cohort Actions & Metrics Bar
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DoctorPurpleContainer),
                    border = BorderStroke(1.dp, DoctorPurple.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Clinical Patient Cohort",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DoctorPurple
                                )
                                Text(
                                    text = "${patientRoster.size} Registered Patients • $criticalCount Critical Ulcer Alerts",
                                    fontSize = 12.5.sp,
                                    color = Color(0xFF4A148C)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { showBroadcastDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = DoctorPurple),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Broadcast", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick Patient Connect Button
                        OutlinedButton(
                            onClick = {
                                currentInviteCode = repository.generatePatientSyncCode()
                                showCodeDialog = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, DoctorPurple)
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = null, tint = DoctorPurple, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Patient Sync Code (QR / Code)", color = DoctorPurple, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search patient by name, condition, or SSN...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("patient_roster_search"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DoctorPurple,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    singleLine = true
                )
            }

            // Triage Risk Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedFilter == RiskCategory.ALL,
                            onClick = { selectedFilter = RiskCategory.ALL },
                            label = { Text("All Patients (${patientRoster.size})") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = DoctorPurple, selectedLabelColor = Color.White)
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter == RiskCategory.CRITICAL_ALERT,
                            onClick = { selectedFilter = RiskCategory.CRITICAL_ALERT },
                            label = { Text("🚨 High Risk / Ulcers ($criticalCount)") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFD32F2F), selectedLabelColor = Color.White)
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter == RiskCategory.MODERATE_RISK,
                            onClick = { selectedFilter = RiskCategory.MODERATE_RISK },
                            label = { Text("⚠️ Moderate ($moderateCount)") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = WarningAmber, selectedLabelColor = Color.White)
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedFilter == RiskCategory.POST_OP_BRACING,
                            onClick = { selectedFilter = RiskCategory.POST_OP_BRACING },
                            label = { Text("🦶 3TO Nail Bracing ($postOpCount)") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PodoTeal, selectedLabelColor = Color.White)
                        )
                    }
                }
            }

            // Broadcast Announcements Preview Card
            if (broadcastAnnouncements.isNotEmpty()) {
                item {
                    val latestBroadcast = broadcastAnnouncements.first()
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                        border = BorderStroke(1.dp, Color(0xFFFFB300))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Latest Broadcast: ${latestBroadcast.title}",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFBF360C)
                                )
                                Text(
                                    text = "Sent to ${latestBroadcast.recipientCount} patients • ${latestBroadcast.formattedDate}",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF795548)
                                )
                            }
                        }
                    }
                }
            }

            // Patients List
            if (filteredPatients.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Groups, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No patients found matching criteria", fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(filteredPatients, key = { it.id }) { patient ->
                    PatientRosterCard(
                        patient = patient,
                        onOpenChat = {
                            repository.selectPatient(patient.id)
                            onOpenChatForPatient()
                        },
                        onOpenFile = {
                            repository.selectPatient(patient.id)
                            onOpenFileForPatient()
                        },
                        onCallPatient = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${patient.phone}"))
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }

        // Floating Action Button to Add / Onboard Patient
        FloatingActionButton(
            onClick = { showAddPatientDialog = true },
            containerColor = DoctorPurple,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_patient_fab")
        ) {
            Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = "Add Patient")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Patient", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Dialog: Broadcast Message to Cohort
    if (showBroadcastDialog) {
        BroadcastComposerDialog(
            patientCount = patientRoster.size,
            onDismiss = { showBroadcastDialog = false },
            onSendBroadcast = { title, msg, targetCategory ->
                repository.broadcastAnnouncement(title, msg, targetCategory)
                showBroadcastDialog = false
            }
        )
    }

    // Dialog: Generate Patient Sync Code / QR
    if (showCodeDialog) {
        PatientSyncCodeDialog(
            code = currentInviteCode,
            onDismiss = { showCodeDialog = false }
        )
    }

    // Dialog: Manual Patient Onboarding
    if (showAddPatientDialog) {
        AddPatientDialog(
            onDismiss = { showAddPatientDialog = false },
            onAddPatient = { newPatient ->
                repository.addNewPatient(newPatient)
                showAddPatientDialog = false
            }
        )
    }
}

@Composable
fun PatientRosterCard(
    patient: PatientSummary,
    onOpenChat: () -> Unit,
    onOpenFile: () -> Unit,
    onCallPatient: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenFile() }
            .testTag("patient_card_${patient.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.2.dp,
            if (patient.riskCategory == RiskCategory.CRITICAL_ALERT) Color(0xFFEF5350)
            else MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Avatar, Name, Risk Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(patient.avatarColorHex),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = patient.fullName.take(2).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = patient.fullName,
                            fontSize = 16.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (patient.unreadMessagesCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = CircleShape,
                                color = DoctorPurple,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${patient.unreadMessagesCount}",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        text = "${patient.age} yrs • ${patient.diabetesStatus}",
                        fontSize = 12.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Risk Category Indicator Chip
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when (patient.riskCategory) {
                    RiskCategory.CRITICAL_ALERT -> Color(0xFFFFEBEE)
                    RiskCategory.MODERATE_RISK -> WarningAmberContainer
                    RiskCategory.POST_OP_BRACING -> PodoTealContainer
                    else -> PodoSoftMint
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (patient.riskCategory) {
                            RiskCategory.CRITICAL_ALERT -> Icons.Default.Warning
                            RiskCategory.MODERATE_RISK -> Icons.Default.Warning
                            else -> Icons.Default.Check
                        },
                        contentDescription = null,
                        tint = when (patient.riskCategory) {
                            RiskCategory.CRITICAL_ALERT -> Color(0xFFD32F2F)
                            RiskCategory.MODERATE_RISK -> Color(0xFFE65100)
                            else -> PodoTealDark
                        },
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${patient.footRiskLevel} • Last Check: ${patient.lastInspectionDate}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when (patient.riskCategory) {
                            RiskCategory.CRITICAL_ALERT -> Color(0xFFC62828)
                            RiskCategory.MODERATE_RISK -> Color(0xFFBF360C)
                            else -> PodoTealDark
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (patient.activeTreatmentPlan.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "🎯 Treatment: ${patient.activeTreatmentPlan}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenFile,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clinical File", fontSize = 12.5.sp)
                }

                Button(
                    onClick = onOpenChat,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = DoctorPurple),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Direct Chat", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = onCallPatient,
                    modifier = Modifier
                        .size(38.dp)
                        .background(PodoSoftMint, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.Phone, contentDescription = "Call Patient", tint = PodoTeal, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. Direct Patient Chat Tab with Active Patient Selector
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorPatientChatTab(repository: PodoRepository) {
    val patientRoster by repository.patientRoster.collectAsState()
    val selectedPatientId by repository.selectedPatientId.collectAsState()
    val doctorProfile by repository.doctorProfile.collectAsState()
    var doctorReplyText by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val activePatient = patientRoster.find { it.id == selectedPatientId } ?: patientRoster.firstOrNull()

    if (activePatient == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    tint = DoctorPurple.copy(alpha = 0.5f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Patient Consultations Yet",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Direct patient chat channels will appear here once patients connect to your clinical cohort.",
                    fontSize = 13.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
        return
    }

    val messages = repository.getMessagesForPatient(activePatient.id)

    Column(modifier = Modifier.fillMaxSize()) {
        // Patient Selector Dropdown Header
        Surface(
            color = DoctorPurpleLight,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(activePatient.avatarColorHex),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = activePatient.fullName.take(2).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .clickable { isDropdownExpanded = true }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = activePatient.fullName,
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = DoctorPurple
                            )
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                        }
                        Text(
                            text = "${activePatient.diabetesStatus} • ${activePatient.footRiskLevel}",
                            fontSize = 11.5.sp,
                            color = Color(0xFF4A148C),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        patientRoster.forEach { patient ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(patient.fullName, fontWeight = FontWeight.Bold)
                                        Text(patient.footRiskLevel, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                },
                                onClick = {
                                    repository.selectPatient(patient.id)
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Quick Clinical Suggestion Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable {
                        doctorReplyText = "Please keep your foot elevated, avoid unassisted weight bearing, and come in for wound inspection."
                    }
                ) {
                    Text("Offload & Elevate 🦶", fontSize = 12.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                }
            }
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable {
                        doctorReplyText = "Apply 20% urea cream to heels nightly. Never apply lotion between toes."
                    }
                ) {
                    Text("Urea Moisturizer 💧", fontSize = 12.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                }
            }
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable {
                        doctorReplyText = "Your 3TO Onyfix nail brace appointment is confirmed for this week."
                    }
                ) {
                    Text("Nail Brace Follow-up 🩺", fontSize = 12.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                }
            }
        }

        // Messages List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("doctor_patient_chat_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                ChatMessageBubble(message = message, doctorName = doctorProfile.fullName)
            }
        }

        // Doctor Reply Box
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = doctorReplyText,
                    onValueChange = { doctorReplyText = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("doctor_reply_input"),
                    placeholder = {
                        Text(
                            text = "Write response to ${activePatient.fullName}...",
                            fontSize = 14.5.sp
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DoctorPurple,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(10.dp))

                IconButton(
                    onClick = {
                        if (doctorReplyText.isNotBlank()) {
                            repository.sendDoctorMessageToPatient(activePatient.id, doctorReplyText, MessageSender.DOCTOR)
                            doctorReplyText = ""
                        }
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .background(DoctorPurple, RoundedCornerShape(16.dp))
                        .testTag("doctor_send_reply_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Reply",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. Clinical File & History Tab with Editable Progress Notes
// -------------------------------------------------------------
@Composable
fun DoctorPatientFileTab(repository: PodoRepository) {
    val patientRoster by repository.patientRoster.collectAsState()
    val selectedPatientId by repository.selectedPatientId.collectAsState()
    val diabeticHistory by repository.diabeticHistory.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val activePatient = patientRoster.find { it.id == selectedPatientId } ?: patientRoster.firstOrNull()

    if (activePatient == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = DoctorPurple.copy(alpha = 0.5f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Patient Records Available",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Clinical progress charts, diabetic assessments, and EHR notes will appear here once patients are enrolled in your clinic.",
                    fontSize = 13.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
        return
    }

    var clinicalNotes by remember(activePatient.id) { mutableStateOf(activePatient.podologistNotes) }
    var treatmentPlan by remember(activePatient.id) { mutableStateOf(activePatient.activeTreatmentPlan) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .testTag("doctor_patient_file_list"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Patient Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(activePatient.avatarColorHex),
                                modifier = Modifier.size(52.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = activePatient.fullName.take(2).uppercase(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = activePatient.fullName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${activePatient.age} Years Old • Blood Type: ${activePatient.bloodType}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "SSN: ${activePatient.tcKimlikNo} • Phone: ${activePatient.phone}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = when (activePatient.riskCategory) {
                                RiskCategory.CRITICAL_ALERT -> Color(0xFFFFEBEE)
                                else -> WarningAmberContainer
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (activePatient.riskCategory == RiskCategory.CRITICAL_ALERT) Color(0xFFD32F2F) else WarningAmber,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = activePatient.footRiskLevel,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activePatient.riskCategory == RiskCategory.CRITICAL_ALERT) Color(0xFFC62828) else Color(0xFFBF360C)
                                )
                            }
                        }
                    }
                }
            }

            // Chronic Conditions
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Chronic Conditions & Neuropathy History",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PodoTealDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        activePatient.chronicDiseases.forEach { disease ->
                            Text(
                                text = "• $disease",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Active Medications
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Medication, contentDescription = null, tint = DoctorPurple, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Active Daily Medications",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DoctorPurple
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        activePatient.regularMedications.forEach { med ->
                            Text(
                                text = "💊 $med",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Podologist Progress Notes Editor
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PodoTealLight),
                    border = BorderStroke(1.dp, PodoTealPrimary.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = PodoTealDark, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Podiatric Clinical Progress Notes & Treatment Plan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PodoTealDark
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = clinicalNotes,
                            onValueChange = { clinicalNotes = it },
                            label = { Text("Clinical Notes (Visible in Patient Chart)") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PodoTealDark,
                                unfocusedBorderColor = PodoTealPrimary.copy(alpha = 0.5f)
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = treatmentPlan,
                            onValueChange = { treatmentPlan = it },
                            label = { Text("Active Treatment & Offloading Protocol") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PodoTealDark,
                                unfocusedBorderColor = PodoTealPrimary.copy(alpha = 0.5f)
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                repository.updatePatientNotes(activePatient.id, clinicalNotes, treatmentPlan)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Clinical notes updated for ${activePatient.fullName}.")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = PodoTealDark),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Clinical Notes", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Latest Daily Foot Check
            if (diabeticHistory.isNotEmpty()) {
                item {
                    val lastCheck = diabeticHistory.first()
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Latest Diabetic Foot Self-Check (${lastCheck.dateFormatted})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Score: ${lastCheck.scoreText} • ${lastCheck.riskLevel}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = WarningAmber
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = lastCheck.doctorAdvice,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

// -------------------------------------------------------------
// 4. Clinical Practice Profile Tab
// -------------------------------------------------------------
@Composable
fun DoctorClinicProfileTab(
    repository: PodoRepository,
    onNavigateToEDevlet: () -> Unit = {}
) {
    val doctor by repository.doctorProfile.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var fullName by remember { mutableStateOf(doctor.fullName) }
    var title by remember { mutableStateOf(doctor.title) }
    var hospital by remember { mutableStateOf(doctor.hospital) }
    var clinicAddress by remember { mutableStateOf(doctor.clinicAddress) }
    var phone by remember { mutableStateOf(doctor.phone) }
    var workingHours by remember { mutableStateOf(doctor.workingHours) }
    var about by remember { mutableStateOf(doctor.about) }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            repository.uploadDoctorDiploma(it.toString())
            scope.launch {
                snackbarHostState.showSnackbar("Medical credential uploaded successfully.")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .testTag("doctor_clinic_profile_form"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Verification / Diploma Status Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = if (doctor.isVerified) PodoSoftMint else Color(0xFFFFF3E0)),
                    border = BorderStroke(1.5.dp, if (doctor.isVerified) PodoTeal else PodoOrange)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (doctor.isVerified) Icons.Default.VerifiedUser else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (doctor.isVerified) PodoTeal else PodoOrange,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (doctor.isVerified) "Certified & Board-Verified Podiatrist" else "Verification Pending",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (doctor.isVerified) PodoTealDark else Color(0xFFBF360C)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (doctor.isVerified)
                                "National Provider Identifier (NPI): ${doctor.diplomaRegistryNo}\nUS Health ID verification is active."
                            else
                                "Your medical diploma / board certificate is awaiting clinical review.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(
                                onClick = { filePicker.launch("image/*") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.FileUpload, contentDescription = null, tint = PodoTeal, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Upload Credentials", fontSize = 13.sp, color = PodoTeal)
                            }

                            if (!doctor.isVerified) {
                                Button(
                                    onClick = onNavigateToEDevlet,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                                ) {
                                    Text("Verify Health ID 🇺🇸", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Clinical Practice & Contact Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name & Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Clinical Specialty") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = hospital,
                    onValueChange = { hospital = it },
                    label = { Text("Hospital / Podiatry Center Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = clinicAddress,
                    onValueChange = { clinicAddress = it },
                    label = { Text("Clinic Physical Address") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Clinic Appointment Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = workingHours,
                    onValueChange = { workingHours = it },
                    label = { Text("Office Hours") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
            }

            item {
                OutlinedTextField(
                    value = about,
                    onValueChange = { about = it },
                    label = { Text("About & Clinical Practice Services") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )
            }

            item {
                Button(
                    onClick = {
                        val updated = doctor.copy(
                            fullName = fullName.trim(),
                            title = title.trim(),
                            hospital = hospital.trim(),
                            clinicAddress = clinicAddress.trim(),
                            phone = phone.trim(),
                            workingHours = workingHours.trim(),
                            about = about.trim()
                        )
                        repository.updateDoctorProfile(updated)
                        scope.launch {
                            snackbarHostState.showSnackbar("Practice profile updated successfully.")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DoctorPurple, contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Practice Information", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

// -------------------------------------------------------------
// Helper Dialogs: Broadcast Composer, QR Code, Add Patient
// -------------------------------------------------------------
@Composable
fun BroadcastComposerDialog(
    patientCount: Int,
    onDismiss: () -> Unit,
    onSendBroadcast: (title: String, message: String, target: RiskCategory) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var targetCategory by remember { mutableStateOf(RiskCategory.ALL) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Campaign, contentDescription = null, tint = DoctorPurple)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Broadcast Clinical Alert", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Send a secure clinical alert or seasonal foot care guideline to your patients simultaneously.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Announcement Subject / Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Clinical Alert Content") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Text("Target Group:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = targetCategory == RiskCategory.ALL,
                        onClick = { targetCategory = RiskCategory.ALL },
                        label = { Text("All ($patientCount)") }
                    )
                    FilterChip(
                        selected = targetCategory == RiskCategory.CRITICAL_ALERT,
                        onClick = { targetCategory = RiskCategory.CRITICAL_ALERT },
                        label = { Text("🚨 High-Risk Only") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && message.isNotBlank()) {
                        onSendBroadcast(title, message, targetCategory)
                    }
                },
                enabled = title.isNotBlank() && message.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = DoctorPurple)
            ) {
                Text("Send Broadcast")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun PatientSyncCodeDialog(
    code: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Patient Sync & QR Connect", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Share this 6-digit sync code or QR code with your patient to securely link their PodoAssist mobile chart to your clinical portal.",
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DoctorPurpleContainer,
                    border = BorderStroke(2.dp, DoctorPurple),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = code,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 4.sp,
                        color = DoctorPurple,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Expires in 24 hours • HIPAA Compliant Patient Pairing",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = DoctorPurple)
            ) {
                Text("Done")
            }
        }
    )
}

@Composable
fun AddPatientDialog(
    onDismiss: () -> Unit,
    onAddPatient: (PatientSummary) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("60") }
    var phone by remember { mutableStateOf("") }
    var ssn by remember { mutableStateOf("") }
    var diabetesStatus by remember { mutableStateOf("Type 2 Diabetes") }
    var riskCategory by remember { mutableStateOf(RiskCategory.MODERATE_RISK) }
    var treatmentPlan by remember { mutableStateOf("Daily foot inspection & routine DPM follow-up") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Onboard New Patient", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Patient Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text("Age") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone") },
                        modifier = Modifier.weight(2f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                OutlinedTextField(
                    value = ssn,
                    onValueChange = { ssn = it },
                    label = { Text("SSN / Health ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = diabetesStatus,
                    onValueChange = { diabetesStatus = it },
                    label = { Text("Diabetes Condition") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = treatmentPlan,
                    onValueChange = { treatmentPlan = it },
                    label = { Text("Initial Treatment Plan") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank()) {
                        val newPatient = PatientSummary(
                            id = "patient_${UUID.randomUUID().toString().take(6)}",
                            fullName = fullName.trim(),
                            age = age.toIntOrNull() ?: 60,
                            phone = if (phone.isNotBlank()) phone.trim() else "(555) 000-0000",
                            tcKimlikNo = if (ssn.isNotBlank()) ssn.trim() else "SSN-XXX-XX-0000",
                            bloodType = "O Positive (O+)",
                            diabetesStatus = diabetesStatus.trim(),
                            footRiskLevel = when (riskCategory) {
                                RiskCategory.CRITICAL_ALERT -> "High Risk (Ulcer / Charcot Alert)"
                                RiskCategory.MODERATE_RISK -> "Moderate Risk (Regular Care)"
                                RiskCategory.POST_OP_BRACING -> "Low Risk (3TO Nail Bracing)"
                                else -> "Routine Care"
                            },
                            riskCategory = riskCategory,
                            lastInspectionDate = "Just added",
                            activeTreatmentPlan = treatmentPlan.trim(),
                            avatarColorHex = 0xFF5E35B1
                        )
                        onAddPatient(newPatient)
                    }
                },
                enabled = fullName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = DoctorPurple)
            ) {
                Text("Add Patient")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
