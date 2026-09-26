package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.NotificationEntity
import com.example.data.model.*
import com.example.data.navigation.CampusGraph
import com.example.data.repository.CampusRepository
import com.example.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CampusViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CampusRepository(application)

    // --- NAVIGATION STATE ---
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // --- MAP & PATHFINDING STATE ---
    private val _selectedStartNodeId = MutableStateFlow<String>("node_gate")
    val selectedStartNodeId: StateFlow<String> = _selectedStartNodeId.asStateFlow()

    private val _selectedEndNodeId = MutableStateFlow<String>("node_library")
    val selectedEndNodeId: StateFlow<String> = _selectedEndNodeId.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<PoiCategory>(PoiCategory.ALL)
    val selectedCategoryFilter: StateFlow<PoiCategory> = _selectedCategoryFilter.asStateFlow()

    private val _pathResult = MutableStateFlow<PathResult?>(null)
    val pathResult: StateFlow<PathResult?> = _pathResult.asStateFlow()

    private val _selectedNodeDetail = MutableStateFlow<MapNode?>(null)
    val selectedNodeDetail: StateFlow<MapNode?> = _selectedNodeDetail.asStateFlow()

    // --- CHAT ASSISTANT STATE ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = ChatMessage.Sender.AI_ASSISTANT,
                text = "Hello! I am Campora AI, your personalized campus intelligence assistant. Ask me about buildings, faculty, library timings, events, or say 'Take me to Library' for map navigation!",
                relatedLocationId = "node_gate",
                relatedLocationName = "Main Gate Entrance"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAssistantLoading = MutableStateFlow(false)
    val isAssistantLoading: StateFlow<Boolean> = _isAssistantLoading.asStateFlow()

    // --- NOTICES & EVENTS STATE ---
    private val _selectedNoticeTab = MutableStateFlow(0) // 0 = Events, 1 = Announcements
    val selectedNoticeTab: StateFlow<Int> = _selectedNoticeTab.asStateFlow()

    private val _selectedEventCategory = MutableStateFlow<EventCategory>(EventCategory.ALL)
    val selectedEventCategory: StateFlow<EventCategory> = _selectedEventCategory.asStateFlow()

    private val _noticesSearchQuery = MutableStateFlow("")
    val noticesSearchQuery: StateFlow<String> = _noticesSearchQuery.asStateFlow()

    // --- TIMETABLE STATE ---
    private val _timetableDept = MutableStateFlow("CSE_AIML")
    val timetableDept: StateFlow<String> = _timetableDept.asStateFlow()

    private val _timetableSem = MutableStateFlow(3)
    val timetableSem: StateFlow<Int> = _timetableSem.asStateFlow()

    private val _timetableSec = MutableStateFlow("A")
    val timetableSec: StateFlow<String> = _timetableSec.asStateFlow()

    private val _selectedDay = MutableStateFlow("MON")
    val selectedDay: StateFlow<String> = _selectedDay.asStateFlow()

    // --- FACULTY STATE ---
    private val _facultyQuery = MutableStateFlow("")
    val facultyQuery: StateFlow<String> = _facultyQuery.asStateFlow()

    // --- SETTINGS STATE ---
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // --- NOTIFICATIONS & ALERTS ---
    val notifications = repository.notifications
    val unreadCount = repository.unreadNotificationCount

    private val _isNotificationCenterOpen = MutableStateFlow(false)
    val isNotificationCenterOpen: StateFlow<Boolean> = _isNotificationCenterOpen.asStateFlow()

    // --- VOICE & TTS STATE ---
    private val _isTtsEnabled = MutableStateFlow(true)
    val isTtsEnabled: StateFlow<Boolean> = _isTtsEnabled.asStateFlow()

    data class VoiceConfirmationData(
        val transcript: String,
        val parsedIntent: String,
        val targetNodeId: String?,
        val targetNodeName: String?
    )
    private val _voiceConfirmation = MutableStateFlow<VoiceConfirmationData?>(null)
    val voiceConfirmation: StateFlow<VoiceConfirmationData?> = _voiceConfirmation.asStateFlow()

    private var ttsEngine: android.speech.tts.TextToSpeech? = null

    // --- ONBOARDING TOUR STATE ---
    private val _showOnboardingTour = MutableStateFlow(true)
    val showOnboardingTour: StateFlow<Boolean> = _showOnboardingTour.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialNotificationIfNeeded()
            computePath()
        }
        try {
            ttsEngine = android.speech.tts.TextToSpeech(application) { status ->
                if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                    ttsEngine?.language = java.util.Locale.US
                }
            }
        } catch (_: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        ttsEngine?.stop()
        ttsEngine?.shutdown()
    }

    fun toggleTts() {
        _isTtsEnabled.value = !_isTtsEnabled.value
    }

    fun dismissOnboardingTour() {
        _showOnboardingTour.value = false
    }

    fun replayOnboardingTour() {
        _showOnboardingTour.value = true
    }

    fun dismissVoiceConfirmation() {
        _voiceConfirmation.value = null
    }

    fun confirmVoiceCommand(data: VoiceConfirmationData) {
        val intent = data.parsedIntent
        val nodeId = data.targetNodeId

        _voiceConfirmation.value = null

        when {
            nodeId != null -> {
                _selectedEndNodeId.value = nodeId
                computePath()
                _currentScreen.value = Screen.Map
            }
            intent == "SHOW_EVENTS" -> _currentScreen.value = Screen.Notices
            intent == "SHOW_TIMETABLE" -> _currentScreen.value = Screen.Timetable
            intent == "EMERGENCY" -> _currentScreen.value = Screen.Emergency
            else -> {
                sendUserChatMessage(data.transcript)
            }
        }
    }

    fun navigateToScreen(screen: Screen) {
        _currentScreen.value = screen
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setStartNode(nodeId: String) {
        _selectedStartNodeId.value = nodeId
        computePath()
    }

    fun setEndNode(nodeId: String) {
        _selectedEndNodeId.value = nodeId
        computePath()
    }

    fun setCategoryFilter(category: PoiCategory) {
        _selectedCategoryFilter.value = category
    }

    fun selectNodeDetail(node: MapNode?) {
        _selectedNodeDetail.value = node
    }

    fun computePath() {
        val start = _selectedStartNodeId.value
        val end = _selectedEndNodeId.value
        _pathResult.value = repository.computePath(start, end)
    }

    fun setNoticeTab(tabIndex: Int) {
        _selectedNoticeTab.value = tabIndex
    }

    fun setEventCategory(category: EventCategory) {
        _selectedEventCategory.value = category
    }

    fun setNoticesQuery(query: String) {
        _noticesSearchQuery.value = query
    }

    fun setTimetableFilter(dept: String, sem: Int, sec: String) {
        _timetableDept.value = dept
        _timetableSem.value = sem
        _timetableSec.value = sec
    }

    fun setSelectedDay(day: String) {
        _selectedDay.value = day
    }

    fun setFacultyQuery(query: String) {
        _facultyQuery.value = query
    }

    fun setNotificationCenterOpen(open: Boolean) {
        _isNotificationCenterOpen.value = open
    }

    fun sendUserChatMessage(prompt: String) {
        if (prompt.isBlank()) return

        val userMsg = ChatMessage(sender = ChatMessage.Sender.USER, text = prompt)
        val currentList = _chatMessages.value
        _chatMessages.value = currentList + userMsg
        _isAssistantLoading.value = true

        viewModelScope.launch {
            val responseText = repository.queryGeminiAssistant(prompt, currentList)
            _isAssistantLoading.value = false

            val detectedNode = detectNodeFromText(prompt) ?: detectNodeFromText(responseText)

            val aiMsg = ChatMessage(
                sender = ChatMessage.Sender.AI_ASSISTANT,
                text = responseText,
                relatedLocationId = detectedNode?.id,
                relatedLocationName = detectedNode?.name
            )
            _chatMessages.value = _chatMessages.value + aiMsg

            // Read aloud if TTS is enabled
            if (_isTtsEnabled.value) {
                try {
                    ttsEngine?.speak(
                        responseText.take(250),
                        android.speech.tts.TextToSpeech.QUEUE_FLUSH,
                        null,
                        "CamporaTts"
                    )
                } catch (_: Exception) {}
            }
        }
    }

    fun handleVoiceCommand(transcript: String) {
        val lower = transcript.lowercase()
        val targetNode = detectNodeFromText(transcript)

        val parsedIntent = when {
            targetNode != null && (lower.contains("navigate") || lower.contains("take me") || lower.contains("where is") || lower.contains("path") || lower.contains("go to")) -> "NAVIGATE"
            lower.contains("event") || lower.contains("notice") || lower.contains("announcement") || lower.contains("fest") -> "SHOW_EVENTS"
            lower.contains("security") || lower.contains("doctor") || lower.contains("emergency") || lower.contains("call") || lower.contains("help") -> "EMERGENCY"
            lower.contains("timetable") || lower.contains("class") || lower.contains("schedule") -> "SHOW_TIMETABLE"
            else -> "ASK"
        }

        // Trigger confirmation dialog with transcript + intent
        _voiceConfirmation.value = VoiceConfirmationData(
            transcript = transcript,
            parsedIntent = parsedIntent,
            targetNodeId = targetNode?.id,
            targetNodeName = targetNode?.name
        )
    }

    fun triggerDestinationNavigation(nodeId: String) {
        _selectedEndNodeId.value = nodeId
        computePath()
        _currentScreen.value = Screen.Map
    }

    fun sendDemoTestAlert() {
        viewModelScope.launch {
            val demoAlerts = listOf(
                Pair("Weather Advisory Notice", "Heavy rain expected — all classes after 2:00 PM shift online today."),
                Pair("Mid-Sem Exam Tickets Out", "Hall tickets for CSE & ECE students are now available at Admin Block A."),
                Pair("Placement Drive Update", "Infosys PPT session moved to Kalam Seminar Hall at 3:00 PM.")
            )
            val randomAlert = demoAlerts.random()
            repository.insertDemoAlert(
                title = randomAlert.first,
                message = randomAlert.second,
                type = "URGENT",
                actionNodeId = "node_admin"
            )
        }
    }

    fun markNotificationAsRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    private fun detectNodeFromText(text: String): MapNode? {
        val lower = text.lowercase()
        if (lower.contains("j202") || lower.contains("j 202")) return CampusGraph.getNodeById("node_j202")
        if (lower.contains("l203") || lower.contains("l 203")) return CampusGraph.getNodeById("node_l203")
        return CampusGraph.nodes.find { node ->
            lower.contains(node.name.lowercase()) ||
            lower.contains(node.buildingName.lowercase()) ||
            node.name.lowercase().split(" ").any { word -> word.length > 3 && lower.contains(word) }
        }
    }

    fun getRepository(): CampusRepository = repository
}
