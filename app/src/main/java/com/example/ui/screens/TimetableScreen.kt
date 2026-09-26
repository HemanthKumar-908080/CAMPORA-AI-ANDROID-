package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TimetableSlot
import com.example.ui.viewmodel.CampusViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableScreen(
    viewModel: CampusViewModel,
    onNavigateToRoom: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val repository = viewModel.getRepository()
    val allSlots = repository.timetables

    val selectedDept by viewModel.timetableDept.collectAsState()
    val selectedSem by viewModel.timetableSem.collectAsState()
    val selectedSec by viewModel.timetableSec.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()

    val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }

    val days = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT")
    val depts = listOf("CSE_AIML", "CSE", "AI&DS", "IT", "ECE", "Mech", "Civil", "Biotech", "Chemical")
    val sems = listOf(1, 2, 3, 4, 5, 6, 7, 8)
    val secs = listOf("A", "B")

    val slotsForDay = allSlots.filter { slot ->
        slot.department == selectedDept &&
        slot.semester == selectedSem &&
        slot.section == selectedSec &&
        slot.dayOfWeek == selectedDay
    }.ifEmpty {
        allSlots.filter { it.dayOfWeek == selectedDay }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
            .testTag("timetable_screen")
    ) {
        // --- HEADER BAR & DEPT SELECTOR ---
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "VTHT Class Timetable",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Dept Dropdown
                    var showDeptMenu by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = showDeptMenu,
                        onExpandedChange = { showDeptMenu = !showDeptMenu },
                        modifier = Modifier.weight(1.2f)
                    ) {
                        OutlinedTextField(
                            value = selectedDept,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Dept") },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                        )
                        ExposedDropdownMenu(
                            expanded = showDeptMenu,
                            onDismissRequest = { showDeptMenu = false }
                        ) {
                            depts.forEach { d ->
                                DropdownMenuItem(
                                    text = { Text(d) },
                                    onClick = {
                                        viewModel.setTimetableFilter(d, selectedSem, selectedSec)
                                        showDeptMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Sem Dropdown
                    var showSemMenu by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = showSemMenu,
                        onExpandedChange = { showSemMenu = !showSemMenu },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = "Sem $selectedSem",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Sem") },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                        )
                        ExposedDropdownMenu(
                            expanded = showSemMenu,
                            onDismissRequest = { showSemMenu = false }
                        ) {
                            sems.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text("Sem $s") },
                                    onClick = {
                                        viewModel.setTimetableFilter(selectedDept, s, selectedSec)
                                        showSemMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Sec Dropdown
                    var showSecMenu by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = showSecMenu,
                        onExpandedChange = { showSecMenu = !showSecMenu },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = "Sec $selectedSec",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Sec") },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                        )
                        ExposedDropdownMenu(
                            expanded = showSecMenu,
                            onDismissRequest = { showSecMenu = false }
                        ) {
                            secs.forEach { sec ->
                                DropdownMenuItem(
                                    text = { Text("Sec $sec") },
                                    onClick = {
                                        viewModel.setTimetableFilter(selectedDept, selectedSem, sec)
                                        showSecMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- DAY OF WEEK SELECTOR ---
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(days) { day ->
                FilterChip(
                    selected = selectedDay == day,
                    onClick = { viewModel.setSelectedDay(day) },
                    label = { Text(day, fontWeight = FontWeight.Bold) },
                    leadingIcon = {
                        if (selectedDay == day) {
                            Icon(Icons.Filled.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- SLOTS LIST ---
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(slotsForDay, key = { it.id }) { slot ->
                val isActiveNow = slot.startHour == currentHour

                TimetableSlotCard(
                    slot = slot,
                    isActiveNow = isActiveNow,
                    onNavigateToRoom = {
                        if (slot.targetNodeId != null) {
                            onNavigateToRoom(slot.targetNodeId)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun TimetableSlotCard(
    slot: TimetableSlot,
    isActiveNow: Boolean,
    onNavigateToRoom: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isActiveNow) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isActiveNow) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF10B981), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LIVE NOW",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = slot.timeSlot,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = slot.subjectCode,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = slot.subjectName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Faculty: ${slot.facultyName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.DoorFront,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = slot.roomNumber,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (slot.targetNodeId != null) {
                    Button(
                        onClick = onNavigateToRoom,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.NearMe,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Navigate Class", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
