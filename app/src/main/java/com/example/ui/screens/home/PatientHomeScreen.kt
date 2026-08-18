package com.example.ui.screens.home

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.data.repository.PodoRepository
import com.example.ui.components.EmergencyConfirmDialog
import com.example.ui.components.ModernGridDashboardCard
import com.example.ui.components.PodoBrandIconBadge
import com.example.ui.components.PodoTopBar
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedContainer
import com.example.ui.theme.EmergencyRedDark
import com.example.ui.theme.PodoBlueDark
import com.example.ui.theme.PodoBlueLight
import com.example.ui.theme.PodoBluePrimary
import com.example.ui.theme.PodoSoftMint
import com.example.ui.theme.PodoSoftMintDark
import com.example.ui.theme.PodoTealContainer
import com.example.ui.theme.PodoTealDark
import com.example.ui.theme.PodoTealLight
import com.example.ui.theme.PodoTealPrimary
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer
import com.example.ui.util.callPhone

@Composable
fun PatientHomeScreen(
    repository: PodoRepository,
    onNavigateToDoctor: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToEducation: () -> Unit,
    onNavigateToDiabeticFoot: () -> Unit,
    onNavigateToAiBot: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onSwitchRole: () -> Unit
) {
    val userProfile by repository.userProfile.collectAsState()
    val doctorProfile by repository.doctorProfile.collectAsState()
    val streakInfo by repository.streakInfo.collectAsState()
    val badges by repository.badges.collectAsState()
    val context = LocalContext.current
    var showEmergencyDialog by remember { mutableStateOf(false) }

    if (showEmergencyDialog) {
        EmergencyConfirmDialog(
            isOpen = showEmergencyDialog,
            onDismiss = { showEmergencyDialog = false }
        )
    }

    Scaffold(
        topBar = {
            PodoTopBar(
                title = "PodoAssist",
                subtitle = "Foot Health & Podiatry Care",
                currentRole = UserRole.PATIENT,
                onSwitchRoleClick = onSwitchRole
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("patient_home_grid"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Welcome & Diabetic Foot Status Hero Card
            item(span = { GridItemSpan(2) }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("patient_status_hero_card"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = PodoTealLight
                    ),
                    border = BorderStroke(1.2.dp, PodoTealPrimary.copy(alpha = 0.35f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PodoBrandIconBadge(size = 44.dp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Welcome,",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = PodoTealDark
                                    )
                                    Text(
                                        text = userProfile.fullName.ifBlank { "Patient Member" },
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF0C3D34)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = PodoSoftMint,
                                border = BorderStroke(1.dp, PodoSoftMintDark.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(PodoSoftMintDark, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Patient Mode",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PodoSoftMintDark
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Diabetic Status & Foot Health Alert
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = WarningAmberContainer,
                            border = BorderStroke(1.dp, WarningAmber.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = WarningAmber,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = userProfile.diabetesStatus.ifBlank { "Diabetic Foot Daily Care" },
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFBF360C)
                                    )
                                    Text(
                                        text = userProfile.footRiskLevel.ifBlank { "Daily Routine Self-Check Recommended" },
                                        fontSize = 12.sp,
                                        color = Color(0xFF8C2700)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Gamification Streak Hero Card
            item(span = { GridItemSpan(2) }) {
                com.example.ui.components.StreakHeroCard(streakInfo = streakInfo)
            }

            // 3. Badges / Achievements Carousel
            item(span = { GridItemSpan(2) }) {
                com.example.ui.components.AchievementsSection(badges = badges)
            }

            // 4. Diabetic Foot Daily Checklist Card
            item {
                ModernGridDashboardCard(
                    title = "Diabetic Tracker",
                    subtitle = "Daily Foot Inspection Checklist",
                    icon = Icons.Default.Checklist,
                    iconColor = PodoTealDark,
                    iconBgColor = PodoTealContainer,
                    badgeText = "Essential",
                    onClick = onNavigateToDiabeticFoot,
                    testTag = "card_diabetic_foot"
                )
            }

            // 5. AI Podiatry Chatbot Card
            item {
                ModernGridDashboardCard(
                    title = "AI Podiatrist",
                    subtitle = "Clinical Foot Care Advisor",
                    icon = Icons.Default.AutoAwesome,
                    iconColor = Color(0xFF6A1B9A),
                    iconBgColor = Color(0xFFF3E5F5),
                    badgeText = "AI 24/7",
                    badgeColor = Color(0xFF6A1B9A),
                    onClick = onNavigateToAiBot,
                    testTag = "card_ai_bot"
                )
            }

            // 6. DPM Locator & Map Card
            item {
                ModernGridDashboardCard(
                    title = "Find Podiatrist",
                    subtitle = "Clinics & Surgical Centers",
                    icon = Icons.Default.Map,
                    iconColor = PodoBluePrimary,
                    iconBgColor = PodoBlueLight,
                    badgeText = "USA DPM",
                    badgeColor = PodoBluePrimary,
                    onClick = onNavigateToMap,
                    testTag = "card_map_clinics"
                )
            }

            // 7. Podiatry Education & Wiki Card
            item {
                ModernGridDashboardCard(
                    title = "Podiatry Wiki",
                    subtitle = "Care Guides, Nails & Exercises",
                    icon = Icons.Default.MenuBook,
                    iconColor = Color(0xFF2E7D32),
                    iconBgColor = PodoSoftMint,
                    badgeText = "Guides",
                    badgeColor = Color(0xFF2E7D32),
                    onClick = onNavigateToEducation,
                    testTag = "card_education_wiki"
                )
            }

            // 8. My DPM Doctor Card
            item {
                ModernGridDashboardCard(
                    title = "My Doctor",
                    subtitle = doctorProfile.fullName,
                    icon = Icons.Default.MedicalServices,
                    iconColor = PodoTealDark,
                    iconBgColor = PodoTealContainer,
                    badgeText = "Clinic",
                    badgeColor = PodoTealPrimary,
                    onClick = onNavigateToDoctor,
                    testTag = "card_my_doctor"
                )
            }

            // 9. Health Profile Card
            item {
                ModernGridDashboardCard(
                    title = "Health Profile",
                    subtitle = "Medications, History & EHR",
                    icon = Icons.Default.Person,
                    iconColor = Color(0xFF37474F),
                    iconBgColor = Color(0xFFECEFF1),
                    badgeText = "HIPAA",
                    badgeColor = Color(0xFF455A64),
                    onClick = onNavigateToProfile,
                    testTag = "card_my_profile"
                )
            }

            // 10. Direct Doctor Telehealth Messaging Banner
            item(span = { GridItemSpan(2) }) {
                Card(
                    onClick = onNavigateToChat,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_doctor_chat_banner"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PodoBlueLight),
                    border = BorderStroke(1.2.dp, PodoBluePrimary.copy(alpha = 0.35f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = PodoBluePrimary,
                            modifier = Modifier.size(50.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MedicalServices,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Direct Telehealth Message",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PodoBlueDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Encrypted podiatric clinical chat with ${doctorProfile.fullName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = PodoBlueDark.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // 11. Emergency 911 Call Section
            item(span = { GridItemSpan(2) }) {
                EmergencyCardSection(
                    onEmergencyClick = { showEmergencyDialog = true },
                    emergencyContactName = userProfile.emergencyContactName,
                    emergencyContactPhone = userProfile.emergencyContactPhone,
                    onCallContact = { callPhone(context, userProfile.emergencyContactPhone) }
                )
            }

            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun EmergencyCardSection(
    onEmergencyClick: () -> Unit,
    emergencyContactName: String,
    emergencyContactPhone: String,
    onCallContact: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("emergency_section_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = EmergencyRedContainer
        ),
        border = BorderStroke(1.5.dp, EmergencyRed.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = EmergencyRed,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CrisisAlert,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Emergency Health Alert",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmergencyRedDark
                    )
                    Text(
                        text = "For sudden wounds, severe bleeding, or acute infections",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF8C2700)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 911 Emergency Button
            Button(
                onClick = onEmergencyClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("button_call_112_emergency"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmergencyRed,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Call 911 Emergency Service",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Emergency Family Contact Button
            Surface(
                onClick = onCallContact,
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                border = BorderStroke(1.dp, EmergencyRed.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Call Emergency Contact: $emergencyContactName",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmergencyRedDark
                        )
                        Text(
                            text = emergencyContactPhone,
                            fontSize = 12.sp,
                            color = Color(0xFF5D4037)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = EmergencyRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
