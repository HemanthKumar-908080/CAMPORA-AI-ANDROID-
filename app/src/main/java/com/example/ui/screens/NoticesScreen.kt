package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EventCategory
import com.example.data.model.NoticeAnnouncement
import com.example.data.model.NoticeEvent
import com.example.data.model.PriorityLevel
import com.example.ui.theme.StatusImportant
import com.example.ui.theme.StatusNormal
import com.example.ui.theme.StatusUrgent
import com.example.ui.viewmodel.CampusViewModel

@Composable
fun NoticesScreen(
    viewModel: CampusViewModel,
    onNavigateToVenue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val repository = viewModel.getRepository()
    val events = repository.events
    val announcements = repository.announcements

    val selectedTab by viewModel.selectedNoticeTab.collectAsState()
    val selectedCategory by viewModel.selectedEventCategory.collectAsState()
    val searchQuery by viewModel.noticesSearchQuery.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
            .testTag("notices_screen")
    ) {
        // --- TOP TAB BAR ---
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { viewModel.setNoticeTab(0) },
                text = { Text("Upcoming Events (${events.size})", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { viewModel.setNoticeTab(1) },
                text = { Text("Announcements (${announcements.size})", fontWeight = FontWeight.Bold) }
            )
        }

        // --- SEARCH BAR & FILTERS ---
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setNoticesQuery(it) },
                placeholder = { Text("Search title, venue, content...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("notices_search_field")
            )

            if (selectedTab == 0) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(EventCategory.values()) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { viewModel.setEventCategory(category) },
                            label = { Text(category.name, fontSize = 12.sp) }
                        )
                    }
                }
            }
        }

        // --- LIST CONTENT ---
        if (selectedTab == 0) {
            val filteredEvents = events.filter { event ->
                (selectedCategory == EventCategory.ALL || event.category == selectedCategory) &&
                (searchQuery.isBlank() || event.title.contains(searchQuery, ignoreCase = true) || event.venue.contains(searchQuery, ignoreCase = true))
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredEvents, key = { it.id }) { event ->
                    EventCardItem(
                        event = event,
                        onNavigateToVenue = {
                            if (event.targetNodeId != null) {
                                onNavigateToVenue(event.targetNodeId)
                            }
                        }
                    )
                }
            }
        } else {
            val filteredAnnouncements = announcements.filter { anc ->
                searchQuery.isBlank() || anc.title.contains(searchQuery, ignoreCase = true) || anc.content.contains(searchQuery, ignoreCase = true)
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredAnnouncements, key = { it.id }) { announcement ->
                    AnnouncementCardItem(announcement = announcement)
                }
            }
        }
    }
}

@Composable
private fun EventCardItem(
    event: NoticeEvent,
    onNavigateToVenue: () -> Unit
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = event.category.name,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = event.dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = event.description,
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
                        imageVector = Icons.Filled.Place,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.venue,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (event.targetNodeId != null) {
                    Button(
                        onClick = onNavigateToVenue,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.NearMe,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Navigate Venue", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AnnouncementCardItem(announcement: NoticeAnnouncement) {
    val (priorityColor, priorityText) = when (announcement.priority) {
        PriorityLevel.URGENT -> Pair(StatusUrgent, "URGENT")
        PriorityLevel.IMPORTANT -> Pair(StatusImportant, "IMPORTANT")
        PriorityLevel.NORMAL -> Pair(StatusNormal, "ANNOUNCEMENT")
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = priorityColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = priorityText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = priorityColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = announcement.dateStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = announcement.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = announcement.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Issued by: ${announcement.department}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
