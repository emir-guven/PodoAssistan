package com.example.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.PodoRepository
import com.example.ui.components.PodoTopBar
import com.example.ui.theme.PodoTealPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    repository: PodoRepository,
    onNavigateBack: () -> Unit
) {
    val currentProfile by repository.userProfile.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var fullName by remember { mutableStateOf(currentProfile.fullName) }
    var ageText by remember { mutableStateOf(currentProfile.age.toString()) }
    var bloodType by remember { mutableStateOf(currentProfile.bloodType) }
    var diabetesStatus by remember { mutableStateOf(currentProfile.diabetesStatus) }
    var chronicDiseasesText by remember {
        mutableStateOf(currentProfile.chronicDiseases.joinToString("\n"))
    }
    var regularMedicationsText by remember {
        mutableStateOf(currentProfile.regularMedications.joinToString("\n"))
    }
    var emergencyContactName by remember { mutableStateOf(currentProfile.emergencyContactName) }
    var emergencyContactPhone by remember { mutableStateOf(currentProfile.emergencyContactPhone) }
    var address by remember { mutableStateOf(currentProfile.address) }

    Scaffold(
        topBar = {
            PodoTopBar(
                title = "Edit Clinical Profile",
                subtitle = "Update Health & Demographics",
                onBackClick = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .imePadding()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("edit_profile_form"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "Medical History & Emergency Demographics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                AccessibleFormField(
                    label = "Full Legal Name",
                    value = fullName,
                    onValueChange = { fullName = it },
                    testTag = "input_full_name"
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AccessibleFormField(
                        label = "Age",
                        value = ageText,
                        onValueChange = { ageText = it },
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.weight(1f),
                        testTag = "input_age"
                    )

                    AccessibleFormField(
                        label = "Blood Type",
                        value = bloodType,
                        onValueChange = { bloodType = it },
                        modifier = Modifier.weight(1.5f),
                        testTag = "input_blood_type"
                    )
                }
            }

            item {
                AccessibleFormField(
                    label = "Diabetes Diagnosis & Duration",
                    value = diabetesStatus,
                    onValueChange = { diabetesStatus = it },
                    testTag = "input_diabetes_status"
                )
            }

            item {
                AccessibleFormField(
                    label = "Chronic Conditions (One per line)",
                    value = chronicDiseasesText,
                    onValueChange = { chronicDiseasesText = it },
                    minLines = 3,
                    testTag = "input_chronic_diseases"
                )
            }

            item {
                AccessibleFormField(
                    label = "Active Medications (One per line)",
                    value = regularMedicationsText,
                    onValueChange = { regularMedicationsText = it },
                    minLines = 3,
                    testTag = "input_medications"
                )
            }

            item {
                AccessibleFormField(
                    label = "Emergency Contact Name & Relation",
                    value = emergencyContactName,
                    onValueChange = { emergencyContactName = it },
                    testTag = "input_emergency_name"
                )
            }

            item {
                AccessibleFormField(
                    label = "Emergency Contact Phone Number",
                    value = emergencyContactPhone,
                    onValueChange = { emergencyContactPhone = it },
                    keyboardType = KeyboardType.Phone,
                    testTag = "input_emergency_phone"
                )
            }

            item {
                AccessibleFormField(
                    label = "Home Address",
                    value = address,
                    onValueChange = { address = it },
                    minLines = 2,
                    testTag = "input_address"
                )
            }

            item {
                Button(
                    onClick = {
                        val parsedAge = ageText.toIntOrNull() ?: currentProfile.age
                        val updatedProfile = currentProfile.copy(
                            fullName = fullName.trim(),
                            age = parsedAge,
                            bloodType = bloodType.trim(),
                            diabetesStatus = diabetesStatus.trim(),
                            chronicDiseases = chronicDiseasesText.split("\n")
                                .map { it.trim() }
                                .filter { it.isNotBlank() },
                            regularMedications = regularMedicationsText.split("\n")
                                .map { it.trim() }
                                .filter { it.isNotBlank() },
                            emergencyContactName = emergencyContactName.trim(),
                            emergencyContactPhone = emergencyContactPhone.trim(),
                            address = address.trim()
                        )
                        repository.updatePatientProfile(updatedProfile)

                        scope.launch {
                            snackbarHostState.showSnackbar("Profile updated successfully in health record.")
                            delay(400)
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("button_save_profile"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PodoTealPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save Changes",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun AccessibleFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1,
    testTag: String
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            shape = RoundedCornerShape(14.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            minLines = minLines,
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PodoTealPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}
