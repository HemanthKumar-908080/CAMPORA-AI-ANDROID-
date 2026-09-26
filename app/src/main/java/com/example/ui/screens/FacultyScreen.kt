package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FacultyMember
import com.example.ui.viewmodel.CampusViewModel

@Composable
fun FacultyScreen(
    viewModel: CampusViewModel,
    onNavigateToOffice: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = viewModel.getRepository()
    val facultyList = repository.facultyList
    val searchQuery by viewModel.facultyQuery.collectAsState()

    var selectedSchoolFilter by remember { mutableStateOf("ALL") }

    val filteredList = facultyList.filter { f ->
        val matchesSearch = searchQuery.isBlank() ||
            f.name.contains(searchQuery, ignoreCase = true) ||
            f.department.contains(searchQuery, ignoreCase = true) ||
            f.subjectsHandled.any { sub -> sub.contains(searchQuery, ignoreCase = true) }

        val matchesSchool = when (selectedSchoolFilter) {
            "COMPUTING" -> f.department.contains("Computer", true) || f.department.contains("AI", true) || f.department.contains("Information", true)
            "ELECTRONICS" -> f.department.contains("Electronics", true)
            "BIOTECH_CHEM" -> f.department.contains("Biotech", true) || f.department.contains("Chemical", true)
            "CIVIL_MECH" -> f.department.contains("Civil", true) || f.department.contains("Mechanical", true)
            "MANAGEMENT_SH" -> f.department.contains("Humanities", true) || f.department.contains("MBA", true)
            else -> true
        }

        matchesSearch && matchesSchool
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
            .testTag("faculty_screen")
    ) {
        // --- SEARCH BAR & SCHOOL FILTER HEADER ---
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "VTHT Faculty & HOD Directory",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Vel Tech High Tech Dr. Rangarajan Dr. Sakunthala Engg College",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setFacultyQuery(it) },
                    placeholder = { Text("Search professor, HOD, department...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("faculty_search_field")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // School Filter Chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedSchoolFilter == "ALL",
                            onClick = { selectedSchoolFilter = "ALL" },
                            label = { Text("All Schools") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedSchoolFilter == "COMPUTING",
                            onClick = { selectedSchoolFilter = "COMPUTING" },
                            label = { Text("School of Computing") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedSchoolFilter == "ELECTRONICS",
                            onClick = { selectedSchoolFilter = "ELECTRONICS" },
                            label = { Text("School of Electronics") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedSchoolFilter == "BIOTECH_CHEM",
                            onClick = { selectedSchoolFilter = "BIOTECH_CHEM" },
                            label = { Text("Biotech & Chemical") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedSchoolFilter == "CIVIL_MECH",
                            onClick = { selectedSchoolFilter = "CIVIL_MECH" },
                            label = { Text("Civil & Mechanical") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedSchoolFilter == "MANAGEMENT_SH",
                            onClick = { selectedSchoolFilter = "MANAGEMENT_SH" },
                            label = { Text("MBA & S&H") }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- FACULTY LIST ---
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredList, key = { it.id }) { faculty ->
                FacultyCardItem(
                    faculty = faculty,
                    onCallFaculty = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${faculty.phone}"))
                        context.startActivity(intent)
                    },
                    onNavigateToOffice = {
                        if (faculty.targetNodeId != null) {
                            onNavigateToOffice(faculty.targetNodeId)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun FacultyCardItem(
    faculty: FacultyMember,
    onCallFaculty: () -> Unit,
    onNavigateToOffice: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = faculty.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = faculty.designation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = faculty.department,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onCallFaculty,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Call,
                        contentDescription = "Call ${faculty.name}",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Room,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = faculty.cabinRoom,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = faculty.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Office Hours: ${faculty.officeHours}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Subjects: ${faculty.subjectsHandled.joinToString(", ")}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (faculty.targetNodeId != null) {
                Button(
                    onClick = onNavigateToOffice,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.NearMe,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Navigate to Cabin / Office", fontSize = 12.sp)
                }
            }
        }
    }
}
