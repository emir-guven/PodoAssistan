package com.example.ui.screens.map

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.PodologyClinic
import com.example.data.repository.PodoRepository
import com.example.ui.components.PodoTopBar
import com.example.ui.util.callPhone
import com.example.ui.theme.PodoBlueDark
import com.example.ui.theme.PodoBlueLight
import com.example.ui.theme.PodoBluePrimary
import com.example.ui.theme.PodoTealDark
import com.example.ui.theme.PodoTealLight
import com.example.ui.theme.PodoTealPrimary

@Composable
fun MapScreen(
    repository: PodoRepository,
    onNavigateBack: () -> Unit
) {
    val clinics by repository.clinics.collectAsState()
    val context = LocalContext.current
    var selectedCityFilter by remember { mutableStateOf("All") }
    var selectedClinic by remember { mutableStateOf<PodologyClinic?>(clinics.firstOrNull()) }

    val cities = listOf("All", "New York", "Boston", "Chicago", "Los Angeles", "Houston")

    val filteredClinics = if (selectedCityFilter == "All") {
        clinics
    } else {
        clinics.filter { it.city.equals(selectedCityFilter, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            PodoTopBar(
                title = "Podiatrist Locator & Map",
                subtitle = "Nationwide DPM Podiatry Centers",
                onBackClick = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // City Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                cities.forEach { city ->
                    FilterChip(
                        selected = selectedCityFilter == city,
                        onClick = { selectedCityFilter = city },
                        label = {
                            Text(
                                text = city,
                                fontWeight = if (selectedCityFilter == city) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PodoTealPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Interactive Map Visual Preview Canvas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("interactive_map_canvas"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE5F1F4)),
                border = BorderStroke(1.2.dp, PodoTealPrimary.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFD4E9ED))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.9f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Map,
                                        contentDescription = null,
                                        tint = PodoTealPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${filteredClinics.size} Clinics Shown",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PodoTealDark
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    selectedClinic?.let { openGoogleMaps(context, it.address) }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PodoBluePrimary,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Directions,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Directions", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Map Markers Interactive Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            filteredClinics.forEach { clinic ->
                                val isSelected = selectedClinic?.id == clinic.id
                                Surface(
                                    onClick = { selectedClinic = clinic },
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) PodoTealPrimary else Color.White,
                                    border = BorderStroke(1.5.dp, if (isSelected) Color.White else PodoTealPrimary),
                                    shadowElevation = 4.dp
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else Color(0xFFD32F2F),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                text = clinic.name,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else Color(0xFF1B2A27),
                                                maxLines = 1
                                            )
                                            Text(
                                                text = "${clinic.district}, ${clinic.city}",
                                                fontSize = 10.sp,
                                                color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color(0xFF526360)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Clinic Cards List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("clinics_list"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredClinics, key = { it.id }) { clinic ->
                    val isSelected = selectedClinic?.id == clinic.id
                    ClinicDetailCard(
                        clinic = clinic,
                        isSelected = isSelected,
                        onSelect = { selectedClinic = clinic },
                        onGetDirections = { openGoogleMaps(context, clinic.address) },
                        onCall = { callPhone(context, clinic.phone) }
                    )
                }
            }
        }
    }
}

@Composable
fun ClinicDetailCard(
    clinic: PodologyClinic,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onGetDirections: () -> Unit,
    onCall: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("clinic_card_${clinic.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PodoTealLight.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) PodoTealPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PodoBlueLight
                    ) {
                        Text(
                            text = clinic.type,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PodoBlueDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = clinic.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = clinic.rating.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Podiatrist: ${clinic.podologistName}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = PodoTealDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "📍 ${clinic.address}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Services Tags
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                clinic.services.forEach { service ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = service,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onGetDirections,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PodoBluePrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Get Directions", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                }

                OutlinedButton(
                    onClick = onCall,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, PodoTealPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = PodoTealPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call Clinic", fontWeight = FontWeight.Bold, color = PodoTealPrimary, fontSize = 13.5.sp)
                }
            }
        }
    }
}

fun openGoogleMaps(context: Context, address: String) {
    try {
        val mapUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${Uri.encode(address)}")
        val intent = Intent(Intent.ACTION_VIEW, mapUri)
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}
