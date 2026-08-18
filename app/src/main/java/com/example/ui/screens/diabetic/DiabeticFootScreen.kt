package com.example.ui.screens.diabetic

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.PodoRepository
import com.example.ui.components.PodoTopBar
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedContainer
import com.example.ui.theme.PodoSoftMint
import com.example.ui.theme.PodoSoftMintDark
import com.example.ui.theme.PodoTealDark
import com.example.ui.theme.PodoTealLight
import com.example.ui.theme.PodoTealPrimary
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer
import kotlinx.coroutines.launch

@Composable
fun DiabeticFootScreen(
    repository: PodoRepository,
    onNavigateBack: () -> Unit
) {
    val history by repository.diabeticHistory.collectAsState()
    val questions = repository.diabeticQuestions
    var checkedYesIds by remember { mutableStateOf(setOf<String>()) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            PodoTopBar(
                title = "Diabetic Foot Tracker",
                subtitle = "Daily Inspection Checklist & Risk Score",
                onBackClick = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("diabetic_foot_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PodoTealLight),
                    border = BorderStroke(1.2.dp, PodoTealPrimary.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "🔍 Nightly Foot Inspection Routine",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PodoTealDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Please inspect both feet under good lighting each evening. Check the boxes below if you observe any of the following symptoms.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PodoTealDark.copy(alpha = 0.85f),
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // Realtime Risk Status Indicator Card
            item {
                val yesCount = checkedYesIds.size
                val (riskBg, riskText, riskDesc) = when {
                    yesCount == 0 -> Triple(
                        PodoSoftMint,
                        "Low Risk (Optimal)",
                        "No risk indicators marked today. Excellent condition!"
                    )
                    yesCount in 1..2 -> Triple(
                        WarningAmberContainer,
                        "Moderate Risk (Monitoring Required)",
                        "$yesCount symptom(s) observed. Keep feet dry, wear non-binding socks, and monitor closely."
                    )
                    else -> Triple(
                        EmergencyRedContainer,
                        "High Risk (Consult Podiatrist)",
                        "$yesCount risk factors noted! Promptly consult your DPM podiatrist or provider."
                    )
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = riskBg),
                    border = BorderStroke(1.5.dp, if (yesCount >= 3) EmergencyRed else if (yesCount > 0) WarningAmber else PodoSoftMintDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (yesCount >= 3) EmergencyRed else if (yesCount > 0) WarningAmber else PodoSoftMintDark,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (yesCount > 0) Icons.Default.Warning else Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Current Status: $riskText",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (yesCount >= 3) EmergencyRed else if (yesCount > 0) Color(0xFFBF360C) else PodoSoftMintDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = riskDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Questions Checklist
            items(questions, key = { it.id }) { q ->
                val isChecked = checkedYesIds.contains(q.id)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            checkedYesIds = if (isChecked) {
                                checkedYesIds - q.id
                            } else {
                                checkedYesIds + q.id
                            }
                        }
                        .testTag("diabetic_question_${q.id}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isChecked) WarningAmberContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        1.2.dp,
                        if (isChecked) WarningAmber else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                checkedYesIds = if (checked) {
                                    checkedYesIds + q.id
                                } else {
                                    checkedYesIds - q.id
                                }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = WarningAmber,
                                checkmarkColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = q.category,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (q.isCritical) EmergencyRed else PodoTealPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = q.question,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = q.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        repository.submitDiabeticCheck(checkedYesIds)
                        scope.launch {
                            snackbarHostState.showSnackbar("Daily inspection log successfully saved to your EHR record.")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("submit_diabetic_check_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PodoTealPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save & Evaluate Daily Log",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Past History Section
            if (history.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = PodoTealDark,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Historical Inspection Logs",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PodoTealDark
                        )
                    }
                }

                items(history, key = { it.id }) { record ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = record.dateFormatted,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (record.checkedYesCount > 0) WarningAmberContainer else PodoSoftMint
                                ) {
                                    Text(
                                        text = record.riskLevel,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (record.checkedYesCount > 0) Color(0xFFBF360C) else PodoSoftMintDark,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = record.doctorAdvice,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}
