package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmergencyContact
import com.example.ui.theme.StatusUrgent
import com.example.ui.viewmodel.CampusViewModel

@Composable
fun EmergencyScreen(
    viewModel: CampusViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = viewModel.getRepository()
    val emergencyContacts = repository.emergencyContacts

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
            .testTag("emergency_screen")
    ) {
        // --- TOP RED EMERGENCY HEADER BANNER ---
        Card(
            colors = CardDefaults.cardColors(containerColor = StatusUrgent),
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Emergency,
                            contentDescription = null,
                            tint = StatusUrgent,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "VTHT Campus Emergency Helplines",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Vel Tech High Tech Avadi | 24/7 Security & Medical Desk",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // SOS SHARE LOCATION BUTTON
                Button(
                    onClick = { shareSosLocation(context) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sos_share_location_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.ShareLocation,
                        contentDescription = null,
                        tint = StatusUrgent
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share VTHT Emergency SOS & GPS Coordinates",
                        fontWeight = FontWeight.Bold,
                        color = StatusUrgent
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // DISCLAIMER BANNER
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Disclaimer: One-tap tel: links connect directly to VTHT college office and emergency services. Verify on campus.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- CONTACTS LIST ---
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(emergencyContacts, key = { it.id }) { contact ->
                EmergencyContactCard(
                    contact = contact,
                    onCall = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phoneNumber.replace("-", "").replace(" ", "")}"))
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
private fun EmergencyContactCard(
    contact: EmergencyContact,
    onCall: () -> Unit
) {
    val cardBg = if (contact.isPrimaryRedCard) {
        StatusUrgent.copy(alpha = 0.12f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = if (contact.isPrimaryRedCard) StatusUrgent else MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = contact.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = contact.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = contact.phoneNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (contact.isPrimaryRedCard) StatusUrgent else MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "📍 ${contact.locationHint}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onCall,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (contact.isPrimaryRedCard) StatusUrgent else MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.PhoneInTalk, contentDescription = "Call ${contact.title}")
            }
        }
    }
}

private fun shareSosLocation(context: Context) {
    val sosMsg = """
        🚨 EMERGENCY SOS ALERT — Vel Tech High Tech Campus 🚨
        Location: Vel Tech High Tech Dr. Rangarajan Dr. Sakunthala Engineering College
        Address: No. 60, Avadi-Vel Tech Road, Vel Nagar, Avadi, Chennai – 600062
        GPS Coordinates: 13.187792° N, 80.106545° E
        Main Office: 044-26840181
        Please send immediate security / medical response team!
    """.trimIndent()

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, sosMsg)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "Share VTHT Emergency SOS")
    context.startActivity(shareIntent)
}
