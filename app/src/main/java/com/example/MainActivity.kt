package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.NotificationEntity
import com.example.ui.components.NotificationBellButton
import com.example.ui.components.NotificationCenterSheet
import com.example.ui.navigation.Screen
import com.example.ui.screens.*
import com.example.ui.theme.CamporaTheme
import com.example.ui.viewmodel.CampusViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CampusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val unreadCount by viewModel.unreadCount.collectAsState(initial = 0)
            val notificationsList by viewModel.notifications.collectAsState(initial = emptyList())
            val isNotificationOpen by viewModel.isNotificationCenterOpen.collectAsState()

            val showTour by viewModel.showOnboardingTour.collectAsState()
            val voiceConfirmation by viewModel.voiceConfirmation.collectAsState()

            CamporaTheme(darkTheme = isDarkMode) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CamporaTopAppBar(
                            currentScreen = currentScreen,
                            unreadCount = unreadCount,
                            onOpenNotifications = { viewModel.setNotificationCenterOpen(true) },
                            onNavigateToScreen = { screen -> viewModel.navigateToScreen(screen) }
                        )
                    },
                    bottomBar = {
                        CamporaBottomNavigationBar(
                            currentScreen = currentScreen,
                            onSelectScreen = { screen -> viewModel.navigateToScreen(screen) }
                        )
                    },
                    floatingActionButton = {
                        // Global Mic FAB accessible from all screens
                        if (currentScreen != Screen.Assistant) {
                            com.example.ui.components.AudioVoiceInputFab(
                                onTranscriptReceived = { transcript ->
                                    viewModel.handleVoiceCommand(transcript)
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            Screen.Dashboard -> DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToScreen = { screen -> viewModel.navigateToScreen(screen) },
                                onNavigateToDestination = { nodeId -> viewModel.triggerDestinationNavigation(nodeId) }
                            )
                            Screen.Map -> CampusMapScreen(
                                viewModel = viewModel
                            )
                            Screen.Assistant -> AssistantScreen(
                                viewModel = viewModel,
                                onNavigateToDestination = { nodeId -> viewModel.triggerDestinationNavigation(nodeId) }
                            )
                            Screen.Notices -> NoticesScreen(
                                viewModel = viewModel,
                                onNavigateToVenue = { nodeId -> viewModel.triggerDestinationNavigation(nodeId) }
                            )
                            Screen.Timetable -> TimetableScreen(
                                viewModel = viewModel,
                                onNavigateToRoom = { nodeId -> viewModel.triggerDestinationNavigation(nodeId) }
                            )
                            Screen.Faculty -> FacultyScreen(
                                viewModel = viewModel,
                                onNavigateToOffice = { nodeId -> viewModel.triggerDestinationNavigation(nodeId) }
                            )
                            Screen.Emergency -> EmergencyScreen(
                                viewModel = viewModel
                            )
                            Screen.Settings -> SettingsScreen(
                                viewModel = viewModel
                            )
                        }
                    }

                    if (isNotificationOpen) {
                        NotificationCenterSheet(
                            notifications = notificationsList,
                            onDismiss = { viewModel.setNotificationCenterOpen(false) },
                            onMarkAsRead = { id -> viewModel.markNotificationAsRead(id) },
                            onMarkAllAsRead = { viewModel.markAllNotificationsAsRead() },
                            onSendTestAlert = { viewModel.sendDemoTestAlert() },
                            onNavigateToActionNode = { nodeId -> viewModel.triggerDestinationNavigation(nodeId) }
                        )
                    }

                    // --- ONBOARDING TOUR DIALOG (3 STEPS) ---
                    if (showTour) {
                        OnboardingTourDialog(
                            onDismiss = { viewModel.dismissOnboardingTour() }
                        )
                    }

                    // --- VOICE COMMAND CONFIRMATION DIALOG ("Did You Mean?") ---
                    voiceConfirmation?.let { data ->
                        VoiceConfirmationDialog(
                            data = data,
                            onConfirm = { viewModel.confirmVoiceCommand(data) },
                            onDismiss = { viewModel.dismissVoiceConfirmation() }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CamporaTopAppBar(
    currentScreen: Screen,
    unreadCount: Int,
    onOpenNotifications: () -> Unit,
    onNavigateToScreen: (Screen) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Row {
                Text(
                    text = "Campora AI",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = " • ${currentScreen.title}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        actions = {
            NotificationBellButton(
                unreadCount = unreadCount,
                onOpenNotificationCenter = onOpenNotifications
            )

            IconButton(
                onClick = { onNavigateToScreen(Screen.Emergency) },
                modifier = Modifier.testTag("top_bar_emergency_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Emergency,
                    contentDescription = "Emergency",
                    tint = MaterialTheme.colorScheme.error
                )
            }

            Box {
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Faculty Directory") },
                        leadingIcon = { Icon(Icons.Filled.People, contentDescription = null) },
                        onClick = {
                            onNavigateToScreen(Screen.Faculty)
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Emergency Contacts") },
                        leadingIcon = { Icon(Icons.Filled.PhoneInTalk, contentDescription = null) },
                        onClick = {
                            onNavigateToScreen(Screen.Emergency)
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Settings & Dark Mode") },
                        leadingIcon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                        onClick = {
                            onNavigateToScreen(Screen.Settings)
                            showMenu = false
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun CamporaBottomNavigationBar(
    currentScreen: Screen,
    onSelectScreen: (Screen) -> Unit
) {
    NavigationBar(
        modifier = Modifier.testTag("bottom_navigation_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Screen.bottomNavItems.forEach { screen ->
            screen?.let { targetScreen ->
                val isSelected = currentScreen == targetScreen

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onSelectScreen(targetScreen) },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) targetScreen.selectedIcon else targetScreen.unselectedIcon,
                            contentDescription = targetScreen.title
                        )
                    },
                    label = { Text(targetScreen.title) }
                )
            }
        }
    }
}

@Composable
private fun OnboardingTourDialog(
    onDismiss: () -> Unit
) {
    var tourStep by remember { mutableIntStateOf(1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (tourStep < 3) {
                        tourStep++
                    } else {
                        onDismiss()
                    }
                }
            ) {
                Text(if (tourStep < 3) "Next Step" else "Get Started")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Skip Tour")
            }
        },
        title = {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Explore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Welcome to Campora AI", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Step $tourStep of 3",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                when (tourStep) {
                    1 -> {
                        Text(
                            text = "🗺️ Interactive 2D Campus Navigation",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Explore buildings on an interactive canvas with clickable footprints. Calculate shortest walking paths with Dijkstra pathfinding, total distance, and estimated walk time.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    2 -> {
                        Text(
                            text = "🤖 AI Assistant & Hands-free Voice Input",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ask Gemini AI anything about campus rules, faculty cabins, or exam schedules. Use the global microphone FAB on any screen for hands-free voice commands.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    3 -> {
                        Text(
                            text = "🔔 Campus Push Alerts & Emergency",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Stay notified about weather advisories and exam seatings. Access 1-tap emergency contacts for campus security and 24/7 medical room.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun VoiceConfirmationDialog(
    data: CampusViewModel.VoiceConfirmationData,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirm & Execute")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.RecordVoiceOver,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Did You Mean?", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    text = "Spoken Command:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "\"${data.transcript}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Recognized Action:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val intentDesc = when (data.parsedIntent) {
                    "NAVIGATE" -> "Navigate to ${data.targetNodeName ?: "Location"} on 2D Campus Map"
                    "SHOW_EVENTS" -> "Open Campus Notices & TechFest Events"
                    "SHOW_TIMETABLE" -> "Open Class Timetable & Schedule"
                    "EMERGENCY" -> "Open Emergency Contacts & Security Hotlines"
                    else -> "Query Gemini Campus Assistant"
                }

                Text(
                    text = intentDesc,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}
