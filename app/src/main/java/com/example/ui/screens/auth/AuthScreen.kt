package com.example.ui.screens.auth

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.data.repository.PodoRepository
import com.example.ui.components.PodoBrandLogo
import com.example.ui.theme.HealthTealPrimary
import com.example.ui.theme.PodoBlue
import com.example.ui.theme.PodoGreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    repository: PodoRepository,
    onNavigateToPatientHome: () -> Unit,
    onNavigateToDoctorDashboard: () -> Unit,
    onNavigateToEDevlet: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedRole by remember { mutableStateOf(UserRole.PATIENT) }
    var showHealthIdSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .testTag("auth_screen"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. HEADER: Artistic Brush Brand Display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 3.dp,
                    border = BorderStroke(1.dp, Color(0xFFE0F2F1)),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 18.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        PodoBrandLogo(
                            size = 88.dp,
                            showSubtext = true,
                            isDarkText = true
                        )
                    }
                }

                Text(
                    text = "Secure, HIPAA-Compliant Diabetic Foot Health\n& Podiatric Clinical Assistant",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color(0xFF546E7A),
                    textAlign = TextAlign.Center
                )
            }

            // 2. MODERN CAPSULE ROLE TOGGLE
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Your Role to Continue",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color(0xFF78909C),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Capsule Pill Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0xFFECEFF1))
                        .padding(5.dp)
                        .testTag("role_selector_pill")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Patient Pill
                        val isPatient = selectedRole == UserRole.PATIENT
                        val patientBg by animateColorAsState(
                            targetValue = if (isPatient) HealthTealPrimary else Color.Transparent,
                            animationSpec = tween(250),
                            label = "patient_bg"
                        )
                        val patientText by animateColorAsState(
                            targetValue = if (isPatient) Color.White else Color(0xFF455A64),
                            animationSpec = tween(250),
                            label = "patient_text"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .background(patientBg)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    selectedRole = UserRole.PATIENT
                                }
                                .testTag("role_select_patient"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = patientText,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Patient",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    ),
                                    color = patientText
                                )
                            }
                        }

                        // Podiatrist / DPM Specialist Pill
                        val isDoctor = selectedRole == UserRole.PODOLOGIST
                        val doctorBg by animateColorAsState(
                            targetValue = if (isDoctor) PodoBlue else Color.Transparent,
                            animationSpec = tween(250),
                            label = "doctor_bg"
                        )
                        val doctorText by animateColorAsState(
                            targetValue = if (isDoctor) Color.White else Color(0xFF455A64),
                            animationSpec = tween(250),
                            label = "doctor_text"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1.25f)
                                .height(50.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .background(doctorBg)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    selectedRole = UserRole.PODOLOGIST
                                }
                                .testTag("role_select_doctor"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MedicalServices,
                                    contentDescription = null,
                                    tint = doctorText,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Podiatrist (DPM)",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    ),
                                    color = doctorText
                                )
                            }
                        }
                    }
                }
            }

            // 3. ONE-CLICK SECURE HEALTH AUTHENTICATION BUTTON
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        showHealthIdSheet = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp),
                            spotColor = Color(0xFF0277BD).copy(alpha = 0.35f)
                        )
                        .testTag("edevlet_magic_login_button"),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0277BD), // US Health Blue
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // USA Health Emblem
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🇺🇸",
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = if (selectedRole == UserRole.PATIENT) "Continue with US Health ID" else "Sign in with NPI Registry",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = Color.White
                            )
                            Text(
                                text = if (selectedRole == UserRole.PATIENT) "SMART on FHIR • 1-Click Verification" else "NPPES / ABPM Board Verification",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.5.sp
                                ),
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Trust Badges Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TrustChip(
                        icon = Icons.Default.Shield,
                        text = "HIPAA Compliant"
                    )
                    TrustChip(
                        icon = Icons.Default.Lock,
                        text = "256-Bit Encrypted"
                    )
                    TrustChip(
                        icon = Icons.Default.VerifiedUser,
                        text = "NPI Verified"
                    )
                }
            }

            // Bottom Disclaimer
            Text(
                text = "Compliant with US Department of Health & Human Services (HHS) & HIPAA security guidelines.",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                ),
                color = Color(0xFF90A4AE),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
        }
    }

    // 4. CONSENT BOTTOM SHEET SIMULATION
    if (showHealthIdSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showHealthIdSheet = false
            },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .width(44.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFCFD8DC))
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
                    .testTag("edevlet_consent_sheet")
            ) {
                // Official US Health Header Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0277BD))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🇺🇸",
                                fontSize = 22.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "US Department of Health & Human Services",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Text(
                                text = "Digital Health Identity Gateway",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp
                                ),
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Permission Consent Prompt
                Text(
                    text = "Health Record & Identity Authorization",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = HealthTealPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                val consentText = if (selectedRole == UserRole.PATIENT) {
                    "PodoAssist platform is requesting authorization via SMART on FHIR to securely access your Name, Age, Diabetes Profile, and Primary Foot Health records. Do you approve?"
                } else {
                    "PodoAssist platform is requesting authorization via NPPES NPI Registry to verify your Board Certification, DPM License, and Clinical Practice Credentials. Do you approve?"
                }

                Text(
                    text = consentText,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 22.sp
                    ),
                    color = Color(0xFF37474F)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Verified Identity Preview Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = PodoGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (selectedRole == UserRole.PATIENT) "Verified Patient Profile" else "Verified Provider Profile",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PodoGreen
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (selectedRole == UserRole.PATIENT) {
                            Text(
                                text = "👤 Sarah Jenkins (SSN: XXX-XX-4891)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Color(0xFF1B5E20)
                            )
                            Text(
                                text = "🩸 Type 2 Diabetes • Age: 64 • A Positive (A+)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF2E7D32)
                            )
                        } else {
                            Text(
                                text = "👨‍⚕️ Dr. Michael Ross, DPM (NPI: 1942857103)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Color(0xFF1B5E20)
                            )
                            Text(
                                text = "📜 ABPM Board Certified • NY State Medical Board",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons: Approve vs Cancel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                showHealthIdSheet = false
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("edevlet_cancel_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = Color(0xFF546E7A)
                        )
                    }

                    Button(
                        onClick = {
                            showHealthIdSheet = false
                            if (selectedRole == UserRole.PATIENT) {
                                repository.loginPatient("Sarah Jenkins", "(555) 234-5678")
                                onNavigateToPatientHome()
                            } else {
                                repository.loginDoctor("NPI-1942857103", "ABPM-CERT-2024")
                                onNavigateToDoctorDashboard()
                            }
                        },
                        modifier = Modifier
                            .weight(1.4f)
                            .height(52.dp)
                            .testTag("edevlet_confirm_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HealthTealPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Approve & Start",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrustChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFECEFF1))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HealthTealPrimary,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.5.sp
            ),
            color = Color(0xFF37474F)
        )
    }
}
