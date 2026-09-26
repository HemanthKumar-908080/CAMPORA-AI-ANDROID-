package com.example.data.model

import androidx.compose.ui.graphics.vector.ImageVector

enum class PoiCategory(val displayName: String) {
    ALL("All POIs"),
    CLASSROOM("Classrooms"),
    LAB("Labs"),
    OFFICE("Offices"),
    CANTEEN("Canteen"),
    LIBRARY("Library"),
    HOSTEL("Hostel"),
    AUDITORIUM("Auditorium"),
    PARKING("Parking"),
    RESTROOM("Restrooms"),
    MEDICAL("Medical / First Aid")
}

data class MapNode(
    val id: String,
    val name: String,
    val category: PoiCategory,
    val floor: String = "Ground Floor",
    val x: Float, // Relative campus canvas coordinate (0 to 1000)
    val y: Float, // Relative campus canvas coordinate (0 to 1000)
    val description: String,
    val buildingName: String,
    val openHours: String = "8:00 AM - 6:00 PM",
    val isMainLandmark: Boolean = false
)

data class MapEdge(
    val id: String,
    val nodeA: String,
    val nodeB: String,
    val distanceMeters: Int,
    val walkwayLabel: String = "Walkway"
)

data class PathResult(
    val pathNodes: List<MapNode>,
    val totalDistanceMeters: Int,
    val estimatedWalkMinutes: Int,
    val turnByTurnSteps: List<String>
)

enum class PriorityLevel {
    NORMAL,
    IMPORTANT,
    URGENT
}

enum class EventCategory {
    ALL,
    ACADEMIC,
    SPORTS,
    CULTURAL,
    PLACEMENT
}

data class NoticeEvent(
    val id: String,
    val title: String,
    val description: String,
    val dateStr: String,
    val timeStr: String,
    val venue: String,
    val category: EventCategory,
    val isFeatured: Boolean = false,
    val targetNodeId: String? = null
)

data class NoticeAnnouncement(
    val id: String,
    val title: String,
    val content: String,
    val priority: PriorityLevel,
    val dateStr: String,
    val department: String = "General Admin"
)

data class TimetableSlot(
    val id: String,
    val department: String, // CSE, ECE, ME, CIVIL, CSE_AIML
    val semester: Int,      // 1 to 8
    val section: String,    // A, B
    val dayOfWeek: String,  // MON, TUE, WED, THU, FRI, SAT
    val timeSlot: String,   // e.g. "08:15 AM - 09:05 AM"
    val startHour: Int,     // 24hr format start hour for live highlighting
    val startMinute: Int = 0,
    val endHour: Int = startHour + 1,
    val endMinute: Int = 0,
    val periodNumber: Int = 0, // 1 to 8, or 0 for BREAK/LUNCH
    val isBreakOrLunch: Boolean = false,
    val subjectCode: String,
    val subjectName: String,
    val facultyName: String,
    val roomNumber: String,
    val targetNodeId: String? = null,
    val activityCode: String? = null
)

data class FacultyMember(
    val id: String,
    val name: String,
    val department: String,
    val designation: String,
    val cabinRoom: String,
    val email: String,
    val phone: String,
    val officeHours: String,
    val subjectsHandled: List<String>,
    val targetNodeId: String? = null
)

data class EmergencyContact(
    val id: String,
    val title: String,
    val category: String,
    val phoneNumber: String,
    val locationHint: String,
    val isPrimaryRedCard: Boolean = false
)

data class CampusKnowledge(
    val id: String,
    val keywords: List<String>,
    val question: String,
    val answer: String,
    val relatedNodeId: String? = null
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: Sender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val relatedLocationId: String? = null,
    val relatedLocationName: String? = null
) {
    enum class Sender { USER, AI_ASSISTANT }
}
