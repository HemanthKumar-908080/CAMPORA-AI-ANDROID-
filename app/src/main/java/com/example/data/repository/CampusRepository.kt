package com.example.data.repository

import android.content.Context
import com.example.data.local.CampusDatabase
import com.example.data.local.NotificationEntity
import com.example.data.model.*
import com.example.data.navigation.CampusGraph
import com.example.data.navigation.DijkstraPathfinder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class CampusRepository(context: Context) {

    private val db = CampusDatabase.getDatabase(context)
    private val dao = db.campusDao()

    val notifications: Flow<List<NotificationEntity>> = dao.getAllNotifications()
    val unreadNotificationCount: Flow<Int> = dao.getUnreadNotificationCount()

    // --- VEL TECH HIGH TECH SEEDED EVENTS ---
    val events: List<NoticeEvent> = listOf(
        NoticeEvent(
            id = "ev1",
            title = "Induction Programme AY 2026–27 for First Year Students",
            description = "Welcome orientation for 1st year UG & PG students with address by Principal Dr. E. Kamalanaban & Dean Academics Dr. V.R. Ravi.",
            dateStr = "Aug 16, 2026",
            timeStr = "09:30 AM - 01:00 PM",
            venue = "Auditorium (1000 Capacity)",
            category = EventCategory.ACADEMIC,
            isFeatured = true,
            targetNodeId = "node_auditorium"
        ),
        NoticeEvent(
            id = "ev2",
            title = "Placement Training – Company Specific (Neopat Assessment)",
            description = "720-hour Placement Enhancement Program & Neopat online mock aptitude tests for CSE, IT, ECE, & Mech final year students.",
            dateStr = "Aug 18, 2026",
            timeStr = "08:30 AM - 05:00 PM",
            venue = "Training & Placement Cell (Dean: Dr. R. Suresh)",
            category = EventCategory.PLACEMENT,
            isFeatured = true,
            targetNodeId = "node_placements"
        ),
        NoticeEvent(
            id = "ev3",
            title = "Vel Tech Runs for Her – Marathon 2026",
            description = "Annual campus marathon promoting women empowerment & fitness, organized by Women Empowerment Cell (Convener: Dr. M. Malleswari).",
            dateStr = "Aug 23, 2026",
            timeStr = "06:00 AM - 09:30 AM",
            venue = "Open Air Auditorium & Main Gate Route",
            category = EventCategory.SPORTS,
            isFeatured = true,
            targetNodeId = "node_open_air"
        ),
        NoticeEvent(
            id = "ev4",
            title = "Codeathon & Aptimind Monthly Coding Contest",
            description = "Inter-department competitive programming sprint organized by School of Computing in AI & ML GPU Workstation Lab.",
            dateStr = "Aug 27, 2026",
            timeStr = "02:00 PM - 05:30 PM",
            venue = "AI & ML Lab (GPU Workstations)",
            category = EventCategory.ACADEMIC,
            isFeatured = false,
            targetNodeId = "node_aiml_lab"
        ),
        NoticeEvent(
            id = "ev5",
            title = "NBA Review Visit – CSE & ECE Departments",
            description = "National Board of Accreditation peer team review visit for Autonomous Accreditation extension.",
            dateStr = "Sep 02, 2026",
            timeStr = "09:00 AM - 05:00 PM",
            venue = "Academic Block A & Block B",
            category = EventCategory.ACADEMIC,
            isFeatured = false,
            targetNodeId = "node_block_a"
        ),
        NoticeEvent(
            id = "ev6",
            title = "Annual VTHT Inter-School Sports Tournament",
            description = "Cricket tournament, football finals, basketball matches, and track sprint events.",
            dateStr = "Sep 10, 2026",
            timeStr = "07:00 AM - 06:00 PM",
            venue = "Sports Complex & Gym",
            category = EventCategory.SPORTS,
            isFeatured = false,
            targetNodeId = "node_sports"
        )
    )

    // --- VEL TECH HIGH TECH SEEDED ANNOUNCEMENTS ---
    val announcements: List<NoticeAnnouncement> = listOf(
        NoticeAnnouncement(
            id = "anc1",
            title = "Heavy Rain Advisory - Afternoon Classes Shift Online",
            content = "Due to heavy rain alert across Avadi-Chennai region, afternoon lab sessions switch to online mode. College buses leave at 4:00 PM.",
            priority = PriorityLevel.URGENT,
            dateStr = "Today, 11:30 AM",
            department = "Principal Office (Dr. E. Kamalanaban)"
        ),
        NoticeAnnouncement(
            id = "anc2",
            title = "Autonomous End-Semester Exam Schedule Released (TNEA 1122)",
            content = "Detailed timetable for November/December 2026 Autonomous Examinations is posted on student portal & Exam Cell notice board.",
            priority = PriorityLevel.IMPORTANT,
            dateStr = "Yesterday",
            department = "Examination Cell"
        ),
        NoticeAnnouncement(
            id = "anc3",
            title = "360+ Recruiter Drive Registration Open",
            content = "Final year students must register on Neopat portal for upcoming IT & Core engineering campus drives with 720 hrs training completion certificate.",
            priority = PriorityLevel.IMPORTANT,
            dateStr = "Aug 06, 2026",
            department = "Training & Placement Cell (Dr. R. Suresh)"
        ),
        NoticeAnnouncement(
            id = "anc4",
            title = "Central Library Extended Hours During Mid-Sems",
            content = "Central Library (85,000+ volumes & digital lab) will remain open till 10:00 PM on weekdays for student exam preparation.",
            priority = PriorityLevel.NORMAL,
            dateStr = "Aug 04, 2026",
            department = "Central Library Complex"
        )
    )

    // --- VEL TECH HIGH TECH TIMETABLE (CSE AIML II-A, Sem III, Odd 2026-27) ---
    val timetables: List<TimetableSlot> = listOf(
        // MONDAY
        TimetableSlot("t_mon_h1", "CSE_AIML", 3, "A", "MON", "08:15 AM - 09:05 AM", 8, 15, 9, 5, 1, false, "25ML35T", "Foundations of Artificial Intelligence (FAI)", "Dr. Manoj Kumar D S", "Hall J202, Block A", "node_j202"),
        TimetableSlot("t_mon_h2", "CSE_AIML", 3, "A", "MON", "09:05 AM - 09:55 AM", 9, 5, 9, 55, 2, false, "25HML34T", "Data Structures using Python (DSP)", "Mrs. J Mary Hanna Priyadharshini", "Hall J202, Block A", "node_j202"),
        TimetableSlot("t_mon_brk", "CSE_AIML", 3, "A", "MON", "09:55 AM - 10:10 AM", 9, 55, 10, 10, 0, true, "BREAK", "Morning Tea Break", "Campus Cafeteria", "Hall J202", "node_j202"),
        TimetableSlot("t_mon_h3", "CSE_AIML", 3, "A", "MON", "10:10 AM - 11:00 AM", 10, 10, 11, 0, 3, false, "MP I", "Mini Project I", "Dr. Manoj Kumar D S", "Lab L203, Block A", "node_l203", "MP I"),
        TimetableSlot("t_mon_h4", "CSE_AIML", 3, "A", "MON", "11:00 AM - 11:50 AM", 11, 0, 11, 50, 4, false, "MP I", "Mini Project I", "Dr. Manoj Kumar D S", "Lab L203, Block A", "node_l203", "MP I"),
        TimetableSlot("t_mon_h5", "CSE_AIML", 3, "A", "MON", "11:50 AM - 12:35 PM", 11, 50, 12, 35, 5, false, "SEM", "Self-Learning & Seminar", "Mrs. J Mary Hanna Priyadharshini", "Hall J202, Block A", "node_j202"),
        TimetableSlot("t_mon_lnch", "CSE_AIML", 3, "A", "MON", "12:35 PM - 01:15 PM", 12, 35, 13, 15, 0, true, "LUNCH", "Lunch Break", "VTHT Canteen", "VTHT Canteen", "node_cafeteria"),
        TimetableSlot("t_mon_h6", "CSE_AIML", 3, "A", "MON", "01:15 PM - 02:00 PM", 13, 15, 14, 0, 6, false, "L1", "Industry Readiness (P&T)", "Dr. R. Suresh & Placement Team", "Hall J202, Block A", "node_j202", "L1"),
        TimetableSlot("t_mon_h7", "CSE_AIML", 3, "A", "MON", "02:00 PM - 02:45 PM", 14, 0, 14, 45, 7, false, "25MA05IT", "Linear Algebra for Data Science (LADS)", "Dr. Siva Kumar T", "Hall J202, Block A", "node_j202"),
        TimetableSlot("t_mon_h8", "CSE_AIML", 3, "A", "MON", "02:45 PM - 03:30 PM", 14, 45, 15, 30, 8, false, "25ML33IT", "Introduction to Data Science (IDSL)", "Mrs. J Mary Hanna Priyadharshini", "Hall J202, Block A", "node_j202"),

        // TUESDAY
        TimetableSlot("t_tue_h1", "CSE_AIML", 3, "A", "TUE", "08:15 AM - 09:05 AM", 8, 15, 9, 5, 1, false, "25ML33IT", "Introduction to Data Science Lab (IDSL)", "Mrs. J Mary Hanna Priyadharshini", "Lab L203, Block A", "node_l203"),
        TimetableSlot("t_tue_h2", "CSE_AIML", 3, "A", "TUE", "09:05 AM - 09:55 AM", 9, 5, 9, 55, 2, false, "25ML33IT", "Introduction to Data Science Lab (IDSL)", "Mrs. J Mary Hanna Priyadharshini", "Lab L203, Block A", "node_l203"),
        TimetableSlot("t_tue_brk", "CSE_AIML", 3, "A", "TUE", "09:55 AM - 10:10 AM", 9, 55, 10, 10, 0, true, "BREAK", "Morning Tea Break", "Campus Cafeteria", "Hall J202", "node_j202"),
        TimetableSlot("t_tue_h3", "CSE_AIML", 3, "A", "TUE", "10:10 AM - 11:00 AM", 10, 10, 11, 0, 3, false, "25HML34T", "Data Structures using Python (DSP)", "Mrs. J Mary Hanna Priyadharshini", "Hall J202, Block A", "node_j202"),
        TimetableSlot("t_tue_h4", "CSE_AIML", 3, "A", "TUE", "11:00 AM - 11:50 AM", 11, 0, 11, 50, 4, false, "Q1", "Coding Skills (Aptimind)", "School of Computing Team", "Hall J202, Block A", "node_j202", "Q1"),
        TimetableSlot("t_tue_h5", "CSE_AIML", 3, "A", "TUE", "11:50 AM - 12:35 PM", 11, 50, 12, 35, 5, false, "25MA05IT", "Linear Algebra for Data Science (LADS)", "Dr. Siva Kumar T", "Hall J202, Block A", "node_j202"),
        TimetableSlot("t_tue_lnch", "CSE_AIML", 3, "A", "TUE", "12:35 PM - 01:15 PM", 12, 35, 13, 15, 0, true, "LUNCH", "Lunch Break", "VTHT Canteen", "VTHT Canteen", "node_cafeteria"),
        TimetableSlot("t_tue_h6", "CSE_AIML", 3, "A", "TUE", "01:15 PM - 02:00 PM", 13, 15, 14, 0, 6, false, "25ML35T", "Foundations of Artificial Intelligence (FAI)", "Dr. Manoj Kumar D S", "Hall J202, Block A", "node_j202"),
        TimetableSlot("t_tue_h7", "CSE_AIML", 3, "A", "TUE", "02:00 PM - 02:45 PM", 14, 0, 14, 45, 7, false, "25HML34T", "Data Structures using Python (DSP)", "Mrs. J Mary Hanna Priyadharshini", "Hall J202, Block A", "node_j202"),
        TimetableSlot("t_tue_h8", "CSE_AIML", 3, "A", "TUE", "02:45 PM - 03:30 PM", 14, 45, 15, 30, 8, false, "25HCS32T", "Object Oriented Programming using Java (OOPS)", "Mrs. Noorul Julaiha A G", "Hall J202, Block A", "node_j202"),

        // WEDNESDAY
        TimetableSlot("t_wed_h1", "CSE_AIML", 3, "A", "WED", "08:15 AM - 09:05 AM", 8, 15, 9, 5, 1, false, "25HCS32T", "Object Oriented Programming using Java (OOPS)", "Mrs. Noorul Julaiha A G", "Hall J202, Block A", "node_j202"),
        TimetableSlot("t_wed_h2", "CSE_AIML", 3, "A", "WED", "09:05 AM - 09:55 AM", 9, 5, 9, 55, 2, false, "25HML38P", "Data Structures Python Lab (DSPL)", "Mrs. J Mary Hanna Priyadharshini & Mr. Sanjay Raj R", "Hall J202, Block A", "node_j202"),
        TimetableSlot("t_wed_brk", "CSE_AIML", 3, "A", "WED", "09:55 AM - 10:10 AM", 9, 55, 10, 10, 0, true, "BREAK", "Morning Tea Break", "Campus Cafeteria", "Hall J202", "node_j202"),
        TimetableSlot("t_wed_h3", "CSE_AIML", 3, "A", "WED", "10:10 AM - 11:00 AM", 10, 10, 11, 0, 3, false, "25HML38P", "Data Structures Python Lab (DSPL)", "Mrs. J Mary Hanna Priyadharshini & Mr. Sanjay Raj R", "Lab L203, Block A", "node_l203"),
        TimetableSlot("t_wed_h4", "CSE_AIML", 3, "A", "WED", "11:00 AM - 11:50 AM", 11, 0, 11, 50, 4, false, "25HML38P", "Data Structures Python Lab (DSPL)", "Mrs. J Mary Hanna Priyadharshini & Mr. Sanjay Raj R", "Lab L203, Block A", "node_l203"),
        TimetableSlot("t_wed_h5", "CSE_AIML", 3, "A", "WED", "11:50 AM - 12:35 PM", 11, 50, 12, 35, 5, false, "25HML38P", "Data Structures Python Lab (DSPL)", "Mrs. J Mary Hanna Priyadharshini & Mr. Sanjay Raj R", "Lab L203, Block A", "node_l203"),
        TimetableSlot("t_wed_lnch", "CSE_AIML", 3, "A", "WED", "12:35 PM - 01:15 PM", 12, 35, 13, 15, 0, true, "LUNCH", "Lunch Break", "VTHT Canteen", "VTHT Canteen", "node_cafeteria"),
        TimetableSlot("t_wed_h6", "CSE_AIML", 3, "A", "WED", "01:15 PM - 02:00 PM", 13, 15, 14, 0, 6, false, "25MA05IT", "Linear Algebra for Data Science (LADS)", "Dr. Siva Kumar T", "Hall J202, Block A", "node_j202"),
        TimetableSlot("t_wed_h7", "CSE_AIML", 3, "A", "WED", "02:00 PM - 02:45 PM", 14, 0, 14, 45, 7, false, "25MA05IT", "Linear Algebra for Data Science (LADS)", "Dr. Siva Kumar T", "Hall J202, Block A", "node_j202"),
        TimetableSlot("t_wed_h8", "CSE_AIML", 3, "A", "WED", "02:45 PM - 03:30 PM", 14, 45, 15, 30, 8, false, "25ML35T", "Foundations of Artificial Intelligence (FAI)", "Dr. Manoj Kumar D S", "Hall J202, Block A", "node_j202"),

        // THURSDAY
        TimetableSlot("t_thu_h1", "CSE_AIML", 3, "A", "THU", "08:15 AM - 09:05 AM", 8, 15, 9, 5, 1, false, "25HML34T", "Data Structures using Python (DSP)", "Mrs. J Mary Hanna Priyadharshini", "Hall J202, Block A", "node_j202"),
        TimetableSlot("t_thu_h2", "CSE_AIML", 3, "A", "THU", "09:05 AM - 09:55 AM", 9, 5, 9, 55, 2, false, "25HCS32T", "Object Oriented Programming using Java (OOPS)", "Mrs. Noorul Julaiha A G", "Hall J202, Block A", "node_j202"),
        TimetableSlot("t_thu_brk", "CSE_AIML", 3, "A", "THU", "09:55 AM - 10:10 AM", 9, 55, 10, 10, 0, true, "BREAK", "Morning Tea Break", "Campus Cafeteria", "Hall J202", "node_j202"),
        TimetableSlot("t_thu_h3", "CSE_AIML", 3, "A", "THU", "10:10 AM - 11:00 AM", 10, 10, 11, 0, 3, false, "MP I", "Mini Project I", "Dr. Manoj Kumar D S", "Lab L203, Block A", "node_l203", "MP I"),
        TimetableSlot("t_thu_h4", "CSE_AIML", 3, "A", "THU", "11:00 AM - 11:50 AM", 11, 0, 11, 50, 4, false, "MP I", "Mini Project I", "Dr. Manoj Kumar D S", "Lab L203, Block A", "node_l203", "MP I"),
        TimetableSlot("t_thu_h5", "CSE_AIML", 3, "A", "THU", "11:50 AM - 12:35 PM", 11, 50, 12, 35, 5, false, "MP I", "Mini Project I", "Dr. Manoj Kumar D S", "Lab L203, Block A", "node_l203", "MP I"),
        TimetableSlot("t_thu_lnch", "CSE_AIML", 3, "A", "THU", "12:35 PM - 01:15 PM", 12, 35, 13, 15, 0, true, "LUNCH", "Lunch Break", "VTHT Canteen", "VTHT Canteen", "node_cafeteria"),
        TimetableSlot("t_thu_h6", "CSE_AIML", 3, "A", "THU", "01:15 PM - 02:00 PM", 13, 15, 14, 0, 6, false, "CSD 1", "Communication Skills Development", "Science & Humanities Dept", "Hall J202, Block A", "node_j202", "CSD 1"),
        TimetableSlot("t_thu_h7", "CSE_AIML", 3, "A", "THU", "02:00 PM - 02:45 PM", 14, 0, 14, 45, 7, false, "CSD 1", "Communication Skills Development", "Science & Humanities Dept", "Hall J202, Block A", "node_j202", "CSD 1"),
        TimetableSlot("t_thu_h8", "CSE_AIML", 3, "A", "THU", "02:45 PM - 03:30 PM", 14, 45, 15, 30, 8, false, "25HCS32T", "Object Oriented Programming using Java (OOPS)", "Mrs. Noorul Julaiha A G", "Hall J202, Block A", "node_j202"),

        // FRIDAY
        TimetableSlot("t_fri_h1", "CSE_AIML", 3, "A", "FRI", "08:15 AM - 09:05 AM", 8, 15, 9, 5, 1, false, "25MA05IT", "Linear Algebra for Data Science (LADS)", "Dr. Siva Kumar T", "Hall J202, Block A", "node_j202"),
        TimetableSlot("t_fri_h2", "CSE_AIML", 3, "A", "FRI", "09:05 AM - 09:55 AM", 9, 5, 9, 55, 2, false, "25HCS37P", "OOP Java Lab (OOPSL)", "Mr. P Lokesh & Mrs. S. Kavitha Rani", "Hall J202, Block A", "node_j202"),
        TimetableSlot("t_fri_brk", "CSE_AIML", 3, "A", "FRI", "09:55 AM - 10:10 AM", 9, 55, 10, 10, 0, true, "BREAK", "Morning Tea Break", "Campus Cafeteria", "Hall J202", "node_j202"),
        TimetableSlot("t_fri_h3", "CSE_AIML", 3, "A", "FRI", "10:10 AM - 11:00 AM", 10, 10, 11, 0, 3, false, "25HCS37P", "OOP Java Lab (OOPSL)", "Mr. P Lokesh & Mrs. S. Kavitha Rani", "Lab L203, Block A", "node_l203"),
        TimetableSlot("t_fri_h4", "CSE_AIML", 3, "A", "FRI", "11:00 AM - 11:50 AM", 11, 0, 11, 50, 4, false, "25HCS37P", "OOP Java Lab (OOPSL)", "Mr. P Lokesh & Mrs. S. Kavitha Rani", "Lab L203, Block A", "node_l203"),
        TimetableSlot("t_fri_h5", "CSE_AIML", 3, "A", "FRI", "11:50 AM - 12:35 PM", 11, 50, 12, 35, 5, false, "25HCS37P", "OOP Java Lab (OOPSL)", "Mr. P Lokesh & Mrs. S. Kavitha Rani", "Lab L203, Block A", "node_l203"),
        TimetableSlot("t_fri_lnch", "CSE_AIML", 3, "A", "FRI", "12:35 PM - 01:15 PM", 12, 35, 13, 15, 0, true, "LUNCH", "Lunch Break", "VTHT Canteen", "VTHT Canteen", "node_cafeteria"),
        TimetableSlot("t_fri_h6", "CSE_AIML", 3, "A", "FRI", "01:15 PM - 02:00 PM", 13, 15, 14, 0, 6, false, "25ML35T", "Foundations of Artificial Intelligence (FAI)", "Dr. Manoj Kumar D S", "Hall J202, Block A", "node_j202"),
        TimetableSlot("t_fri_h7", "CSE_AIML", 3, "A", "FRI", "02:00 PM - 02:45 PM", 14, 0, 14, 45, 7, false, "25ML33IT", "Introduction to Data Science (IDSL)", "Mrs. J Mary Hanna Priyadharshini", "Hall J202, Block A", "node_j202"),
        TimetableSlot("t_fri_h8", "CSE_AIML", 3, "A", "FRI", "02:45 PM - 03:30 PM", 14, 45, 15, 30, 8, false, "LIB/SPORTS", "Library / Sports (Alt Weeks)", "Central Library & Sports Complex", "Central Library", "node_library")
    )

    // --- VEL TECH HIGH TECH FACULTY DIRECTORY & HODs ---
    val facultyList: List<FacultyMember> = listOf(
        FacultyMember(
            id = "f_incharge",
            name = "Mrs. J Mary Hanna Priyadharshini",
            department = "CSE (AI & ML)",
            designation = "Class In-Charge & Assistant Professor",
            cabinRoom = "Cabin A-205, Academic Block A",
            email = "maryhanna@velhightech.com",
            phone = "044-26840181",
            officeHours = "Mon - Fri: 09:30 AM - 04:30 PM",
            subjectsHandled = listOf("Data Structures using Python (DSP)", "Introduction to Data Science (IDSL)", "Data Structures Python Lab (DSPL)"),
            targetNodeId = "node_j202"
        ),
        FacultyMember(
            id = "f2",
            name = "Dr. Manoj Kumar D S",
            department = "CSE (AI & ML)",
            designation = "HOD & Associate Professor",
            cabinRoom = "Cabin A-308, Academic Block A",
            email = "hodaiml@velhightech.com",
            phone = "044-26840181",
            officeHours = "Mon - Fri: 10:00 AM - 12:00 PM",
            subjectsHandled = listOf("Foundations of Artificial Intelligence (FAI)", "Mini Project I", "Deep Learning"),
            targetNodeId = "node_l203"
        ),
        FacultyMember(
            id = "f_noorul",
            name = "Mrs. Noorul Julaiha A G",
            department = "CSE (AI & ML)",
            designation = "Assistant Professor",
            cabinRoom = "Cabin A-207, Academic Block A",
            email = "nooruljulaiha@velhightech.com",
            phone = "044-26840181",
            officeHours = "Mon - Fri: 02:00 PM - 04:00 PM",
            subjectsHandled = listOf("Object Oriented Programming using Java (OOPS)", "Java Programming Lab"),
            targetNodeId = "node_j202"
        ),
        FacultyMember(
            id = "f_sivakumar",
            name = "Dr. Siva Kumar T",
            department = "Mathematics / Science & Humanities",
            designation = "Assistant Professor",
            cabinRoom = "Cabin Admin Annexe 1st Floor",
            email = "sivakumar@velhightech.com",
            phone = "044-26840181",
            officeHours = "Mon - Fri: 11:00 AM - 01:00 PM",
            subjectsHandled = listOf("Linear Algebra for Data Science (LADS)", "Transform Calculus"),
            targetNodeId = "node_admin"
        ),
        FacultyMember(
            id = "f_lokesh",
            name = "Mr. P Lokesh",
            department = "CSE (AI & ML)",
            designation = "Assistant Professor",
            cabinRoom = "Cabin A-208, Academic Block A",
            email = "lokeshp@velhightech.com",
            phone = "044-26840181",
            officeHours = "Mon - Fri: 01:30 PM - 03:30 PM",
            subjectsHandled = listOf("OOP Java Lab (OOPSL)", "Object Oriented Software Engg"),
            targetNodeId = "node_l203"
        ),
        FacultyMember(
            id = "f_kavitha",
            name = "Mrs. S. Kavitha Rani",
            department = "CSE (AI & ML)",
            designation = "Assistant Professor",
            cabinRoom = "Cabin A-209, Academic Block A",
            email = "kavitharani@velhightech.com",
            phone = "044-26840181",
            officeHours = "Mon - Fri: 10:00 AM - 12:00 PM",
            subjectsHandled = listOf("OOP Java Lab (OOPSL)", "Database Security"),
            targetNodeId = "node_l203"
        ),
        FacultyMember(
            id = "f_sanjay",
            name = "Mr. Sanjay Raj R",
            department = "CSE (AI & ML)",
            designation = "Assistant Professor",
            cabinRoom = "Cabin A-210, Academic Block A",
            email = "sanjayraj@velhightech.com",
            phone = "044-26840181",
            officeHours = "Mon - Fri: 02:00 PM - 04:00 PM",
            subjectsHandled = listOf("Data Structures Python Lab (DSPL)", "Python AI Toolkits"),
            targetNodeId = "node_l203"
        ),
        FacultyMember(
            id = "f1",
            name = "Dr. S. Durgadevi",
            department = "Computer Science & Engineering",
            designation = "HOD & Professor",
            cabinRoom = "Cabin A-302, Academic Block A",
            email = "hodcse@velhightech.com",
            phone = "044-26840181",
            officeHours = "Mon - Fri: 02:00 PM - 04:30 PM",
            subjectsHandled = listOf("Data Structures", "Cloud Computing", "Distributed Systems"),
            targetNodeId = "node_block_a"
        ),
        FacultyMember(
            id = "f2",
            name = "Dr. M.S. Manoj Kumar",
            department = "CSE (AI & ML)",
            designation = "HOD & Associate Professor",
            cabinRoom = "Cabin A-308, Academic Block A",
            email = "hodaiml@velhightech.com",
            phone = "044-26840181",
            officeHours = "Mon - Fri: 10:00 AM - 12:00 PM",
            subjectsHandled = listOf("Deep Learning", "Artificial Intelligence", "Neural Networks"),
            targetNodeId = "node_aiml_lab"
        ),
        FacultyMember(
            id = "f3",
            name = "Dr. Manoj Kumar D S",
            department = "AI & Data Science",
            designation = "HOD & Professor",
            cabinRoom = "Cabin A-312, Academic Block A",
            email = "hodaids@velhightech.com",
            phone = "044-26840181",
            officeHours = "Mon - Fri: 03:00 PM - 05:00 PM",
            subjectsHandled = listOf("Big Data Analytics", "Machine Learning", "Data Mining"),
            targetNodeId = "node_block_a"
        ),
        FacultyMember(
            id = "f4",
            name = "Dr. M. Malleswari",
            department = "Information Technology",
            designation = "HOD & Professor (Convener - Women Empowerment Cell)",
            cabinRoom = "Cabin A-204, Academic Block A",
            email = "hodit@velhightech.com",
            phone = "044-26840181",
            officeHours = "Mon - Fri: 11:00 AM - 01:00 PM",
            subjectsHandled = listOf("Web Technology", "Information Security", "Software Engineering"),
            targetNodeId = "node_block_a"
        ),
        FacultyMember(
            id = "f5",
            name = "Dr. Suresh Chinnathampy M",
            department = "Electronics & Communication Engg.",
            designation = "HOD & Professor",
            cabinRoom = "Cabin B-201, Academic Block B",
            email = "hodece@velhightech.com",
            phone = "044-26840181",
            officeHours = "Mon - Fri: 02:00 PM - 04:00 PM",
            subjectsHandled = listOf("VLSI Design", "Embedded Systems", "Wireless Communication"),
            targetNodeId = "node_block_b"
        ),
        FacultyMember(
            id = "f6",
            name = "Dr. J. Iyyappan",
            department = "Biotechnology",
            designation = "HOD & Professor",
            cabinRoom = "Biotech Block, 1st Floor",
            email = "hodbiotech@velhightech.com",
            phone = "044-26840181",
            officeHours = "Mon - Fri: 10:00 AM - 12:30 PM",
            subjectsHandled = listOf("Bio-Process Engineering", "Genetic Engineering", "Immunology"),
            targetNodeId = "node_biotech_chem"
        ),
        FacultyMember(
            id = "f7",
            name = "Dr. J.B. Veeramalini",
            department = "Chemical Engineering",
            designation = "HOD & Professor",
            cabinRoom = "Chemical Block, 2nd Floor",
            email = "hodchem@velhightech.com",
            phone = "044-26840181",
            officeHours = "Mon - Fri: 02:00 PM - 04:00 PM",
            subjectsHandled = listOf("Chemical Reaction Engg", "Heat Transfer", "Mass Transfer"),
            targetNodeId = "node_biotech_chem"
        ),
        FacultyMember(
            id = "f8",
            name = "Prof. M. Manoj Kumar",
            department = "Civil Engineering",
            designation = "HOD & Assistant Professor",
            cabinRoom = "Cabin C-102, Academic Block C",
            email = "hodcivil@velhightech.com",
            phone = "044-26840181",
            officeHours = "Mon - Fri: 11:00 AM - 01:00 PM",
            subjectsHandled = listOf("Structural Analysis", "Surveying", "Concrete Technology"),
            targetNodeId = "node_block_c"
        ),
        FacultyMember(
            id = "f9",
            name = "Dr. Palani Samy",
            department = "Mechanical Engineering",
            designation = "HOD & Professor",
            cabinRoom = "Cabin C-201, Academic Block C",
            email = "hodmech@velhightech.com",
            phone = "044-26840181",
            officeHours = "Mon - Fri: 02:00 PM - 04:00 PM",
            subjectsHandled = listOf("Thermodynamics", "CAD/CAM", "Manufacturing Tech"),
            targetNodeId = "node_block_c"
        ),
        FacultyMember(
            id = "f10",
            name = "Prof. Pradeep Katta",
            department = "Science & Humanities",
            designation = "HOD & Assistant Professor",
            cabinRoom = "Admin Block Annexe",
            email = "hodsh@velhightech.com",
            phone = "044-26840181",
            officeHours = "Mon - Fri: 09:30 AM - 11:30 AM",
            subjectsHandled = listOf("Engineering Mathematics", "Transform Calculus"),
            targetNodeId = "node_admin"
        ),
        FacultyMember(
            id = "f11",
            name = "Prof. Nagarajan",
            department = "MBA - Department of Management Studies",
            designation = "HOD & Associate Professor",
            cabinRoom = "Admin Block 2nd Floor",
            email = "hodmba@velhightech.com",
            phone = "044-26840181",
            officeHours = "Mon - Fri: 02:00 PM - 04:00 PM",
            subjectsHandled = listOf("Financial Management", "Marketing Strategy", "Organizational Behavior"),
            targetNodeId = "node_admin"
        )
    )

    // --- VEL TECH HIGH TECH EMERGENCY & ADMIN CONTACTS ---
    val emergencyContacts: List<EmergencyContact> = listOf(
        EmergencyContact(
            id = "em1",
            title = "College Office / General Inquiry",
            category = "Administration",
            phoneNumber = "044-26840181",
            locationHint = "Admin Block Ground Floor",
            isPrimaryRedCard = true
        ),
        EmergencyContact(
            id = "em2",
            title = "Admissions Office Mobile Hotline",
            category = "Admissions Desk",
            phoneNumber = "9789037651",
            locationHint = "Admin Block Admissions Counter",
            isPrimaryRedCard = true
        ),
        EmergencyContact(
            id = "em3",
            title = "Campus Security Desk",
            category = "Primary Security",
            phoneNumber = "044-26840181",
            locationHint = "Main Gate Checkpost (Avadi Road)",
            isPrimaryRedCard = true
        ),
        EmergencyContact(
            id = "em4",
            title = "Medical Room / First Aid Center",
            category = "Health Emergency",
            phoneNumber = "044-26840181",
            locationHint = "Health & Welfare Building",
            isPrimaryRedCard = true
        ),
        EmergencyContact(
            id = "em5",
            title = "Anti-Ragging Helpline (Toll-Free)",
            category = "Student Safety",
            phoneNumber = "1800-180-5522",
            locationHint = "24/7 National Anti-Ragging Cell",
            isPrimaryRedCard = false
        ),
        EmergencyContact(
            id = "em6",
            title = "Women Empowerment Cell — Dr. M. Malleswari",
            category = "Women Safety & Support",
            phoneNumber = "044-26840181",
            locationHint = "Academic Block A (IT Dept)",
            isPrimaryRedCard = false
        ),
        EmergencyContact(
            id = "em7",
            title = "Local Police Emergency",
            category = "External Emergency",
            phoneNumber = "100",
            locationHint = "Avadi Police Jurisdiction",
            isPrimaryRedCard = false
        ),
        EmergencyContact(
            id = "em8",
            title = "Ambulance Emergency Dispatch",
            category = "External Emergency",
            phoneNumber = "108",
            locationHint = "Chennai Medical Response",
            isPrimaryRedCard = false
        )
    )

    // --- VEL TECH HIGH TECH KNOWLEDGE BASE ---
    val knowledgeBase: List<CampusKnowledge> = listOf(
        CampusKnowledge(
            id = "kb1",
            keywords = listOf("library", "book", "borrow", "timings", "volumes"),
            question = "What are the Central Library hours and features?",
            answer = "Vel Tech High Tech Central Library houses 85,000+ volumes, IEEE digital subscriptions, and quiet study pods. Open 8:00 AM - 8:00 PM (extended to 10:00 PM during autonomous exams).",
            relatedNodeId = "node_library"
        ),
        CampusKnowledge(
            id = "kb2",
            keywords = listOf("cse", "durgadevi", "computing", "computer science"),
            question = "Who is the HOD of CSE Department?",
            answer = "Dr. S. Durgadevi is the HOD of Computer Science & Engineering (email: hodcse@velhightech.com). Cabin located on 3rd Floor of Academic Block A.",
            relatedNodeId = "node_block_a"
        ),
        CampusKnowledge(
            id = "kb3",
            keywords = listOf("placement", "suresh", "recruiter", "training", "neopat"),
            question = "Where is the Training & Placement Cell?",
            answer = "Training & Placement Cell is headed by Dean Dr. R. Suresh. VTHT offers 720 hours of placement training with 360+ campus recruiters.",
            relatedNodeId = "node_placements"
        ),
        CampusKnowledge(
            id = "kb4",
            keywords = listOf("program", "courses", "ug", "pg", "branches"),
            question = "What UG and PG programs does VTHT offer?",
            answer = "Vel Tech High Tech offers 9 UG Programs (CSE, CSE AIML, AI&DS, IT, ECE, Biotech, Chemical, Civil, Mechanical) and 2 PG Programs (ME & MBA). Autonomous & NAAC Accredited.",
            relatedNodeId = "node_admin"
        ),
        CampusKnowledge(
            id = "kb5",
            keywords = listOf("cafeteria", "canteen", "food", "lunch"),
            question = "Where is the Campus Cafeteria?",
            answer = "The 200-seater VTHT Cafeteria is located at the center of the campus. Serves fresh South/North Indian meals, juices, and coffee from 7:30 AM to 7:30 PM.",
            relatedNodeId = "node_cafeteria"
        )
    )

    // --- DIJKSTRA PATHFINDING ---
    fun computePath(startNodeId: String, targetNodeId: String): PathResult? {
        return DijkstraPathfinder.findShortestPath(startNodeId, targetNodeId)
    }

    // --- NOTIFICATION ACTIONS ---
    suspend fun insertDemoAlert(title: String, message: String, type: String = "URGENT", actionNodeId: String? = null) {
        dao.insertNotification(
            NotificationEntity(
                title = title,
                message = message,
                type = type,
                timestamp = System.currentTimeMillis(),
                isRead = false,
                actionNodeId = actionNodeId
            )
        )
    }

    suspend fun markNotificationAsRead(id: Long) {
        dao.markNotificationAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() {
        dao.markAllNotificationsAsRead()
    }

    // Seed default notifications on launch if empty
    suspend fun seedInitialNotificationIfNeeded() {
        if (dao.getAllNotifications().first().isEmpty()) {
            dao.insertNotification(
                NotificationEntity(
                    title = "Welcome to Campora AI — VTHT Edition!",
                    message = "Your personalized campus intelligence system for Vel Tech High Tech Engineering College is active.",
                    type = "GENERAL",
                    timestamp = System.currentTimeMillis(),
                    isRead = false,
                    actionNodeId = "node_gate"
                )
            )
            dao.insertNotification(
                NotificationEntity(
                    title = "Heavy Rain Advisory Notice",
                    message = "Afternoon classes shift online today due to heavy rain. College buses depart at 4:00 PM.",
                    type = "URGENT",
                    timestamp = System.currentTimeMillis() - 1000 * 60 * 30,
                    isRead = false,
                    actionNodeId = "node_admin"
                )
            )
        }
    }

    // --- GEMINI AI CAMPUS ASSISTANT CALL WITH VTHT SYSTEM PROMPT ---
    suspend fun queryGeminiAssistant(userQuery: String, chatHistory: List<ChatMessage> = emptyList()): String {
        val apiKey = com.example.BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return matchLocalKnowledgeBase(userQuery)
        }

        return try {
            val systemPrompt = """
                You are Campora AI assistant for Vel Tech High Tech Dr. Rangarajan Dr. Sakunthala Engineering College, Avadi, Chennai (Autonomous Institution, TNEA Code: 1122, www.velhightech.com).
                
                VTHT Key Knowledge Base:
                - Location: No. 60, Avadi-Vel Tech Road, Vel Nagar, Avadi, Chennai – 600062.
                - Leadership: Principal Dr. E. Kamalanaban, Dean Academics Dr. V. R. Ravi, Dean T&P Dr. R. Suresh.
                - Contacts: Office 044-26840181 | Mobile 9789037651 | admission@velhightech.com
                - Academic Programs: 9 UG Programs (CSE, CSE AIML, AI&DS, IT, ECE, Biotech, Chemical, Civil, Mechanical) and 2 PG Programs (ME, MBA).
                - Departments & HODs:
                  * CSE: Dr. S. Durgadevi (hodcse@velhightech.com)
                  * CSE (AI & ML): Dr. M.S. Manoj Kumar
                  * AI & Data Science: Dr. Manoj Kumar D S
                  * IT: Dr. M. Malleswari (hodit@velhightech.com)
                  * ECE: Dr. Suresh Chinnathampy M (hodece@velhightech.com)
                  * Biotech: Dr. J. Iyyappan
                  * Chemical Engg: Dr. J.B. Veeramalini
                  * Civil Engg: Prof. M. Manoj Kumar
                  * Mechanical Engg: Dr. Palani Samy
                  * Science & Humanities: Prof. Pradeep Katta
                  * MBA: Prof. Nagarajan
                - Schools: School of Computing, School of Electronics & Information Engg, School of Biotech & Chemical Engg, School of Civil & Mechanical Engg.
                - Facilities: Central Library (85,000+ volumes), Computer Center (917 systems), AI & ML GPU Lab, 1000-seater Auditorium, 200-seater Cafeteria, Research & Incubation Center, Boys/Girls Hostels, Sports Complex.
                - Placements: 360+ Recruiters, 720 Hours of Structured Placement Training across 4 years.
                - Anti-Ragging Helpline: 1800-180-5522 | Women Empowerment Cell: Dr. M. Malleswari.
                
                Instructions: Answer questions concisely, politely, and accurately. When location-related, mention the building/block name clearly. Keep answers helpful and structured.
            """.trimIndent()

            val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 15000
            conn.readTimeout = 15000

            val jsonReq = JSONObject()
            val contentsArr = JSONArray()

            val contextHistory = chatHistory.takeLast(6).joinToString("\n") { msg ->
                val role = if (msg.sender == ChatMessage.Sender.USER) "User" else "Assistant"
                "$role: ${msg.text}"
            }

            val contentObj = JSONObject()
            val partsArr = JSONArray()
            val partObj = JSONObject()

            val fullPrompt = if (contextHistory.isNotBlank()) {
                "$systemPrompt\n\nRecent Conversation History:\n$contextHistory\n\nUser Question: $userQuery"
            } else {
                "$systemPrompt\n\nUser Question: $userQuery"
            }

            partObj.put("text", fullPrompt)
            partsArr.put(partObj)
            contentObj.put("parts", partsArr)
            contentsArr.put(contentObj)
            jsonReq.put("contents", contentsArr)

            val writer = OutputStreamWriter(conn.outputStream)
            writer.write(jsonReq.toString())
            writer.flush()
            writer.close()

            val responseCode = conn.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val stream = conn.inputStream.bufferedReader().use { it.readText() }
                val jsonRes = JSONObject(stream)
                val candidates = jsonRes.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val cand = candidates.getJSONObject(0)
                    val content = cand.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return parts.getJSONObject(0).optString("text", "No response text")
                    }
                }
                "Campora AI: I received your request, but could not parse the response."
            } else {
                matchLocalKnowledgeBase(userQuery)
            }
        } catch (e: Exception) {
            matchLocalKnowledgeBase(userQuery)
        }
    }

    private fun matchLocalKnowledgeBase(query: String): String {
        val qLower = query.lowercase()
        val matchedKb = knowledgeBase.find { kb ->
            kb.keywords.any { kw -> qLower.contains(kw) }
        }

        if (matchedKb != null) {
            return "${matchedKb.answer}\n\n📍 Related Location: ${CampusGraph.getNodeById(matchedKb.relatedNodeId ?: "")?.name ?: "Vel Tech High Tech Campus"}"
        }

        return when {
            qLower.contains("next class") -> "Your next scheduled class for CSE AIML (II-A) can be viewed in real-time on your Dashboard and Timetable tab, synchronized with IST."
            qLower.contains("in-charge") || qLower.contains("incharge") || qLower.contains("class teacher") -> "Mrs. J Mary Hanna Priyadharshini, Assistant Professor / CSE (AI & ML), is your Class In-Charge for II Year Section A (Hall J202)."
            qLower.contains("j202") || qLower.contains("classroom j202") -> "Classroom J202 is the home classroom for CSE (AI & ML) II-A, located on the 2nd Floor of Academic Block A.\n\n📍 Location: Classroom J202 (node_j202)"
            qLower.contains("l203") || qLower.contains("lab l203") -> "Lab L203 is the CSE AIML Data Science & AI Projects Lab, located on the 2nd Floor of Academic Block A.\n\n📍 Location: Lab L203 (node_l203)"
            qLower.contains("data structures using python") || qLower.contains("dsp") -> "Data Structures using Python (25HML34T) is taught by Mrs. J Mary Hanna Priyadharshini (AP/CSE AIML) in Hall J202."
            qLower.contains("mini project") || qLower.contains("mp i") -> "Mini Project I (6 hrs/week) takes place in Lab L203 under Dr. Manoj Kumar D S & Mrs. J Mary Hanna Priyadharshini (Mon H3-H4 & Thu H3-H5)."
            qLower.contains("linear algebra") || qLower.contains("lads") -> "Linear Algebra for Data Science (25MA05IT) is taught by Dr. Siva Kumar T (AP/Maths) in Hall J202."
            qLower.contains("foundations of artificial intelligence") || qLower.contains("fai") -> "Foundations of Artificial Intelligence (25ML35T) is taught by Dr. Manoj Kumar D S (ASP/CSE AIML) in Hall J202."
            qLower.contains("object oriented programming") || qLower.contains("oops") -> "Object Oriented Programming using Java (25HCS32T) is taught by Mrs. Noorul Julaiha A G (AP/CSE AIML) in Hall J202."
            qLower.contains("library") -> "Central Library at Vel Tech High Tech features 85,000+ volumes & digital lab. Open 8:00 AM - 8:00 PM."
            qLower.contains("hod") || qLower.contains("cse") -> "Dr. M.S. Manoj Kumar is HOD for CSE (AI & ML) and Dr. S. Durgadevi is HOD for CSE."
            qLower.contains("placement") || qLower.contains("training") -> "Training & Placement Cell is headed by Dean Dr. R. Suresh (360+ recruiters, 720 hrs training)."
            qLower.contains("program") || qLower.contains("course") -> "VTHT offers 9 UG Programs (CSE, CSE AIML, AI&DS, IT, ECE, Biotech, Chemical, Civil, Mech) and 2 PG Programs (ME & MBA)."
            qLower.contains("canteen") || qLower.contains("cafeteria") -> "200-seater VTHT Cafeteria offers South/North Indian food & coffee from 7:30 AM to 7:30 PM."
            qLower.contains("contact") || qLower.contains("phone") || qLower.contains("principal") -> "College Office: 044-26840181 | Admissions: 9789037651 | Principal: Dr. E. Kamalanaban."
            else -> "Campora AI (VTHT Edition): I am your personalized campus intelligence assistant for CSE AIML II-A! Ask me about your timetable, faculty, J202, L203, or tap any quick prompt below."
        }
    }
}
