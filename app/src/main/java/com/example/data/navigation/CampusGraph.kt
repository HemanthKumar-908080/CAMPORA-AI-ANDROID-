package com.example.data.navigation

import com.example.data.model.MapEdge
import com.example.data.model.MapNode
import com.example.data.model.PoiCategory

object CampusGraph {

    val nodes: List<MapNode> = listOf(
        MapNode(
            id = "node_gate",
            name = "Main Gate (Avadi-Vel Tech Rd)",
            category = PoiCategory.OFFICE,
            floor = "Ground Floor",
            x = 500f,
            y = 920f,
            description = "No. 60 Avadi-Vel Tech Road Entrance, Security Checkpost & Visitor Registration Desk.",
            buildingName = "Security Checkpost",
            openHours = "24/7 Open",
            isMainLandmark = true
        ),
        MapNode(
            id = "node_parking",
            name = "Parking Area",
            category = PoiCategory.PARKING,
            floor = "Ground Level",
            x = 750f,
            y = 890f,
            description = "Covered parking zone for student 2-wheelers, faculty cars, and college buses.",
            buildingName = "East Parking Plaza",
            openHours = "7:00 AM - 8:30 PM"
        ),
        MapNode(
            id = "node_admin",
            name = "Admin Block / Principal Office",
            category = PoiCategory.OFFICE,
            floor = "Ground & 1st Floor",
            x = 500f,
            y = 800f,
            description = "Principal Dr. E. Kamalanaban, Dean Academics Dr. V.R. Ravi, Admission Desk & Fee Counter.",
            buildingName = "Administrative Block",
            openHours = "8:30 AM - 5:00 PM",
            isMainLandmark = true
        ),
        MapNode(
            id = "node_exam_cell",
            name = "Examination Cell",
            category = PoiCategory.OFFICE,
            floor = "1st Floor, Admin Block",
            x = 360f,
            y = 800f,
            description = "Controller of Examinations, Grade Sheet Dispatch, Autonomous Hall Tickets & Revaluation.",
            buildingName = "Administrative Block",
            openHours = "9:00 AM - 4:30 PM"
        ),
        MapNode(
            id = "node_open_air",
            name = "Open Air Auditorium",
            category = PoiCategory.AUDITORIUM,
            floor = "Ground Stage",
            x = 240f,
            y = 820f,
            description = "Open Air Cultural Stage for Vel Tech Runs for Her, College Fests, and Student Assemblies.",
            buildingName = "VTHT Open Arena",
            openHours = "7:00 AM - 8:00 PM"
        ),
        MapNode(
            id = "node_auditorium",
            name = "Auditorium (1000 Capacity)",
            category = PoiCategory.AUDITORIUM,
            floor = "Ground Level",
            x = 240f,
            y = 680f,
            description = "1000-Seater Fully Air-Conditioned Central Auditorium for National Conferences & Convocations.",
            buildingName = "VTHT Auditorium",
            openHours = "8:00 AM - 7:00 PM",
            isMainLandmark = true
        ),
        MapNode(
            id = "node_library",
            name = "Central Library (85,000+ Vols)",
            category = PoiCategory.LIBRARY,
            floor = "Ground & 1st Floor",
            x = 280f,
            y = 550f,
            description = "85,000+ Volumes, IEEE/IEEE Spectrum Digital Journals, Quiet Reading Pods & Wi-Fi Workspace.",
            buildingName = "Central Knowledge Complex",
            openHours = "8:00 AM - 8:00 PM (10 PM during Exams)",
            isMainLandmark = true
        ),
        MapNode(
            id = "node_placements",
            name = "Training & Placement Cell",
            category = PoiCategory.OFFICE,
            floor = "1st Floor, Placement Block",
            x = 400f,
            y = 680f,
            description = "Dean T&P Dr. R. Suresh, Neopat Assessment Suite, Interview Rooms, 360+ Recruiter Desk.",
            buildingName = "Placement & Career Center",
            openHours = "8:30 AM - 5:30 PM",
            isMainLandmark = true
        ),
        MapNode(
            id = "node_incubation",
            name = "Research & Incubation Center",
            category = PoiCategory.LAB,
            floor = "Ground Floor",
            x = 620f,
            y = 760f,
            description = "VTHT Startup Incubator, IPR Cell, MSME Innovation Hub, Student Startup Workspaces.",
            buildingName = "R&D Innovation Building",
            openHours = "9:00 AM - 6:00 PM"
        ),
        MapNode(
            id = "node_block_a",
            name = "Academic Block A (CSE & IT)",
            category = PoiCategory.CLASSROOM,
            floor = "Ground - 3rd Floor",
            x = 740f,
            y = 680f,
            description = "School of Computing: CSE, CSE(AIML), AI&DS, IT Classrooms, HOD Offices (Dr. S. Durgadevi, Dr. M. Malleswari).",
            buildingName = "Academic Block A",
            openHours = "8:00 AM - 6:00 PM",
            isMainLandmark = true
        ),
        MapNode(
            id = "node_computer_center",
            name = "Computer Center (917 Systems)",
            category = PoiCategory.LAB,
            floor = "1st & 2nd Floor, Block A",
            x = 850f,
            y = 620f,
            description = "Central Computer Center with 917 High-Performance Workstations & High-Speed Optical Fiber Internet.",
            buildingName = "Academic Block A",
            openHours = "8:00 AM - 6:00 PM",
            isMainLandmark = true
        ),
        MapNode(
            id = "node_aiml_lab",
            name = "AI & ML Lab (GPU Workstations)",
            category = PoiCategory.LAB,
            floor = "2nd Floor, Block A",
            x = 850f,
            y = 520f,
            description = "NVIDIA RTX GPU Workstation Lab for Deep Learning, Computer Vision & Data Science Projects.",
            buildingName = "Academic Block A",
            openHours = "8:30 AM - 5:30 PM"
        ),
        MapNode(
            id = "node_cafeteria",
            name = "Cafeteria (200 Seats)",
            category = PoiCategory.CANTEEN,
            floor = "Ground Floor",
            x = 500f,
            y = 500f,
            description = "200-Seater Campus Dining Center, South/North Indian Thali, Juices, Coffee & Snacks.",
            buildingName = "VTHT Campus Cafeteria",
            openHours = "7:30 AM - 7:30 PM",
            isMainLandmark = true
        ),
        MapNode(
            id = "node_biotech_chem",
            name = "Biotech & Chemical Block",
            category = PoiCategory.LAB,
            floor = "Ground - 2nd Floor",
            x = 280f,
            y = 400f,
            description = "School of Biotech & Chemical Engg: Bio-Process Lab, Fermentation Unit & Chemical Reaction Engineering Lab.",
            buildingName = "Biotech & Chemical Complex",
            openHours = "8:00 AM - 5:00 PM"
        ),
        MapNode(
            id = "node_block_b",
            name = "Academic Block B (ECE & VLSI)",
            category = PoiCategory.CLASSROOM,
            floor = "Ground - 3rd Floor",
            x = 740f,
            y = 400f,
            description = "School of Electronics: ECE Classrooms, HOD Dr. Suresh Chinnathampy M, Cadence VLSI Design Lab & Embedded IoT Benches.",
            buildingName = "Academic Block B",
            openHours = "8:00 AM - 6:00 PM",
            isMainLandmark = true
        ),
        MapNode(
            id = "node_block_c",
            name = "Academic Block C (Mech & Civil)",
            category = PoiCategory.CLASSROOM,
            floor = "Ground - 3rd Floor",
            x = 280f,
            y = 260f,
            description = "School of Civil & Mechanical Engg: HOD Dr. Palani Samy, CNC Lathe Workshop, Thermal Lab & Surveying Benches.",
            buildingName = "Academic Block C",
            openHours = "8:00 AM - 5:00 PM",
            isMainLandmark = true
        ),
        MapNode(
            id = "node_medical",
            name = "Medical / First Aid Room",
            category = PoiCategory.MEDICAL,
            floor = "Ground Floor",
            x = 400f,
            y = 260f,
            description = "24/7 First Aid & Medical Health Center, Resident Nurse, Emergency Beds & Ambulance Dispatch.",
            buildingName = "Health & Welfare Building",
            openHours = "24/7 Open",
            isMainLandmark = true
        ),
        MapNode(
            id = "node_boys_hostel",
            name = "Boys Hostel",
            category = PoiCategory.HOSTEL,
            floor = "G+4 Floors",
            x = 180f,
            y = 200f,
            description = "On-campus Boys Hostel with AC/Non-AC rooms, Wi-Fi, Mess Dining Hall & Recreation Lounge.",
            buildingName = "VTHT Boys Residence",
            openHours = "Gate Closes 9:00 PM",
            isMainLandmark = true
        ),
        MapNode(
            id = "node_girls_hostel",
            name = "Girls Hostel",
            category = PoiCategory.HOSTEL,
            floor = "G+4 Floors",
            x = 820f,
            y = 200f,
            description = "High-Security Girls Hostel with Warden Office, Study Rooms & Exclusive Dining Facility.",
            buildingName = "VTHT Girls Residence",
            openHours = "Gate Closes 8:30 PM",
            isMainLandmark = true
        ),
        MapNode(
            id = "node_sports",
            name = "Sports Complex & Gym",
            category = PoiCategory.AUDITORIUM,
            floor = "Ground Level",
            x = 500f,
            y = 150f,
            description = "Cricket Ground, Football Pitch, Outdoor Basketball Court, Indoor Badminton Arena & Fitness Gym.",
            buildingName = "VTHT Sports Arena",
            openHours = "6:00 AM - 8:00 PM",
            isMainLandmark = true
        ),
        MapNode(
            id = "node_j202",
            name = "Classroom J202 (CSE AIML Sec A)",
            category = PoiCategory.CLASSROOM,
            floor = "2nd Floor, Block A",
            x = 770f,
            y = 650f,
            description = "Classroom J202 — Home Classroom for CSE (AI & ML) Year II Section A. In-Charge: Mrs. J Mary Hanna Priyadharshini.",
            buildingName = "Academic Block A",
            openHours = "8:00 AM - 5:30 PM",
            isMainLandmark = true
        ),
        MapNode(
            id = "node_l203",
            name = "Lab L203 (CSE AIML AI/DS Lab)",
            category = PoiCategory.LAB,
            floor = "2nd Floor, Block A",
            x = 810f,
            y = 600f,
            description = "Lab L203 — CSE (AI & ML) Data Science & AI Projects Lab. Used for DSPL, OOPSL, IDSL & Mini Project I.",
            buildingName = "Academic Block A",
            openHours = "8:00 AM - 5:30 PM",
            isMainLandmark = true
        )
    )

    val edges: List<MapEdge> = listOf(
        MapEdge("e1", "node_gate", "node_admin", 80, "Main Entrance Boulevard"),
        MapEdge("e2", "node_gate", "node_parking", 90, "East Parking Corridor"),
        MapEdge("e3", "node_gate", "node_open_air", 110, "West Gate Path"),
        MapEdge("e4", "node_admin", "node_exam_cell", 40, "Admin East Corridor"),
        MapEdge("e5", "node_admin", "node_incubation", 70, "Innovation Walkway"),
        MapEdge("e6", "node_exam_cell", "node_open_air", 60, "West Quad Path"),
        MapEdge("e7", "node_open_air", "node_auditorium", 50, "Auditorium Walkway"),
        MapEdge("e8", "node_auditorium", "node_library", 65, "Library Lane"),
        MapEdge("e9", "node_admin", "node_placements", 75, "Placement Corridor"),
        MapEdge("e10", "node_placements", "node_library", 55, "Central Knowledge Path"),
        MapEdge("e11", "node_incubation", "node_block_a", 60, "Computing Science Walk"),
        MapEdge("e12", "node_parking", "node_block_a", 100, "East Campus Link"),
        MapEdge("e13", "node_block_a", "node_computer_center", 45, "Block A Central Hall"),
        MapEdge("e14", "node_computer_center", "node_aiml_lab", 40, "GPU Lab Elevator Hall"),
        MapEdge("e15", "node_library", "node_cafeteria", 80, "Green Lawn Avenue"),
        MapEdge("e16", "node_placements", "node_cafeteria", 65, "Central Quad Walk"),
        MapEdge("e17", "node_block_a", "node_cafeteria", 85, "School of Computing Pathway"),
        MapEdge("e18", "node_cafeteria", "node_biotech_chem", 90, "Biotech West Road"),
        MapEdge("e19", "node_cafeteria", "node_block_b", 90, "Electronics East Road"),
        MapEdge("e20", "node_aiml_lab", "node_block_b", 75, "Engineering Inter-Block Link"),
        MapEdge("e21", "node_biotech_chem", "node_block_c", 85, "North-West Academic Way"),
        MapEdge("e22", "node_biotech_chem", "node_medical", 70, "Health Center Path"),
        MapEdge("e23", "node_cafeteria", "node_medical", 95, "Central Health Avenue"),
        MapEdge("e24", "node_block_b", "node_girls_hostel", 110, "Girls Hostel Gate Link"),
        MapEdge("e25", "node_block_c", "node_boys_hostel", 80, "Boys Hostel Link Road"),
        MapEdge("e26", "node_block_c", "node_medical", 60, "Medical Corridor"),
        MapEdge("e27", "node_medical", "node_sports", 90, "North Sports Walkway"),
        MapEdge("e28", "node_boys_hostel", "node_sports", 100, "Boys Residence Sports Path"),
        MapEdge("e29", "node_girls_hostel", "node_sports", 100, "Girls Residence Sports Path"),
        MapEdge("e30", "node_library", "node_biotech_chem", 85, "West Science Corridor"),
        MapEdge("e31", "node_computer_center", "node_parking", 95, "East Computer Center Way"),
        MapEdge("e32", "node_exam_cell", "node_placements", 50, "Admin Inner Corridor"),
        MapEdge("e33", "node_block_a", "node_j202", 20, "Block A 2nd Floor Corridor"),
        MapEdge("e34", "node_j202", "node_l203", 15, "2nd Floor Lab Corridor"),
        MapEdge("e35", "node_l203", "node_aiml_lab", 20, "2nd Floor AI Wing Link")
    )

    fun getNodeById(id: String): MapNode? = nodes.find { it.id == id }
}
