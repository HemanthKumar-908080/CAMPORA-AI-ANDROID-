package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.model.MapNode
import com.example.data.model.PoiCategory
import com.example.data.navigation.CampusGraph
import com.example.ui.components.AudioVoiceInputFab
import com.example.ui.components.CampusMapCanvas
import com.example.ui.viewmodel.CampusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusMapScreen(
    viewModel: CampusViewModel,
    modifier: Modifier = Modifier
) {
    val nodes = CampusGraph.nodes
    val edges = CampusGraph.edges

    val selectedStartId by viewModel.selectedStartNodeId.collectAsState()
    val selectedEndId by viewModel.selectedEndNodeId.collectAsState()
    val categoryFilter by viewModel.selectedCategoryFilter.collectAsState()
    val pathResult by viewModel.pathResult.collectAsState()
    val selectedNodeDetail by viewModel.selectedNodeDetail.collectAsState()

    var showStartDropdown by remember { mutableStateOf(false) }
    var showEndDropdown by remember { mutableStateOf(false) }
    var showStepsExpanded by remember { mutableStateOf(false) }

    val startNode = nodes.find { it.id == selectedStartId } ?: nodes.first()
    val endNode = nodes.find { it.id == selectedEndId } ?: nodes.last()

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("campus_map_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            // --- TOP SELECTION CONTROLS ---
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Interactive 2D Campus Navigation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Start Location Picker Dropdown
                    ExposedDropdownMenuBox(
                        expanded = showStartDropdown,
                        onExpandedChange = { showStartDropdown = !showStartDropdown }
                    ) {
                        OutlinedTextField(
                            value = "Start: ${startNode.name}",
                            onValueChange = {},
                            readOnly = true,
                            leadingIcon = {
                                Icon(Icons.Filled.TripOrigin, contentDescription = null, tint = Color(0xFF10B981))
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showStartDropdown) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                                .fillMaxWidth()
                                .testTag("start_location_dropdown")
                        )

                        ExposedDropdownMenu(
                            expanded = showStartDropdown,
                            onDismissRequest = { showStartDropdown = false }
                        ) {
                            nodes.forEach { node ->
                                DropdownMenuItem(
                                    text = { Text("${node.name} (${node.category.displayName})") },
                                    onClick = {
                                        viewModel.setStartNode(node.id)
                                        showStartDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Destination Location Picker Dropdown
                    ExposedDropdownMenuBox(
                        expanded = showEndDropdown,
                        onExpandedChange = { showEndDropdown = !showEndDropdown }
                    ) {
                        OutlinedTextField(
                            value = "Destination: ${endNode.name}",
                            onValueChange = {},
                            readOnly = true,
                            leadingIcon = {
                                Icon(Icons.Filled.Place, contentDescription = null, tint = Color(0xFFEF4444))
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showEndDropdown) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                                .fillMaxWidth()
                                .testTag("end_location_dropdown")
                        )

                        ExposedDropdownMenu(
                            expanded = showEndDropdown,
                            onDismissRequest = { showEndDropdown = false }
                        ) {
                            nodes.forEach { node ->
                                DropdownMenuItem(
                                    text = { Text("${node.name} (${node.category.displayName})") },
                                    onClick = {
                                        viewModel.setEndNode(node.id)
                                        showEndDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category Filter Chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(PoiCategory.values()) { category ->
                            FilterChip(
                                selected = categoryFilter == category,
                                onClick = { viewModel.setCategoryFilter(category) },
                                label = { Text(category.displayName, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }

            // --- 2D MAP CANVAS COMPONENT ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                CampusMapCanvas(
                    nodes = nodes,
                    edges = edges,
                    pathResult = pathResult,
                    selectedStartNodeId = selectedStartId,
                    selectedEndNodeId = selectedEndId,
                    selectedCategoryFilter = categoryFilter,
                    onNodeSelected = { node -> viewModel.selectNodeDetail(node) }
                )
            }

            // --- SHORTEST PATH NAVIGATION RESULTS SUMMARY SHEET ---
            if (pathResult != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("path_summary_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.DirectionsWalk,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "${pathResult!!.totalDistanceMeters} meters walkway distance",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Estimated Walk Time: ~${pathResult!!.estimatedWalkMinutes} mins",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            TextButton(onClick = { showStepsExpanded = !showStepsExpanded }) {
                                Text(if (showStepsExpanded) "Hide Steps" else "View Steps")
                                Icon(
                                    imageVector = if (showStepsExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                    contentDescription = null
                                )
                            }
                        }

                        // Turn-by-Turn Steps Expansion
                        AnimatedVisibility(
                            visible = showStepsExpanded,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(top = 12.dp)
                                    .heightIn(max = 160.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Divider()
                                Spacer(modifier = Modifier.height(8.dp))
                                pathResult!!.turnByTurnSteps.forEachIndexed { idx, step ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = "${idx + 1}. ",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = step,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- HANDS-FREE VOICE MIC FAB ---
        AudioVoiceInputFab(
            onTranscriptReceived = { transcript -> viewModel.handleVoiceCommand(transcript) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 100.dp)
        )

        // --- BUILDING / POI DETAIL DIALOG ---
        if (selectedNodeDetail != null) {
            AlertDialog(
                onDismissRequest = { viewModel.selectNodeDetail(null) },
                icon = { Icon(Icons.Filled.Apartment, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                title = { Text(selectedNodeDetail!!.name, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Building: ${selectedNodeDetail!!.buildingName}", fontWeight = FontWeight.SemiBold)
                        Text("Category: ${selectedNodeDetail!!.category.displayName}", style = MaterialTheme.typography.bodySmall)
                        Text("Floor: ${selectedNodeDetail!!.floor}", style = MaterialTheme.typography.bodySmall)
                        Text("Timings: ${selectedNodeDetail!!.openHours}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(selectedNodeDetail!!.description, style = MaterialTheme.typography.bodyMedium)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.setEndNode(selectedNodeDetail!!.id)
                            viewModel.selectNodeDetail(null)
                        }
                    ) {
                        Icon(Icons.Filled.NearMe, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Navigate Here")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.selectNodeDetail(null) }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}
