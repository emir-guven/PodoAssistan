package com.example.data.model

enum class UserRole {
    PATIENT,
    PODOLOGIST
}

enum class MessageSender {
    USER,
    DOCTOR,
    AI_BOT
}

enum class VerificationStatus {
    UNVERIFIED,
    PENDING_APPROVAL,
    EDEVLET_VERIFIED,
    APPROVED,
    REJECTED
}

data class UserProfile(
    val id: String = "",
    val fullName: String = "",
    val phone: String = "",
    val tcKimlikNo: String = "",
    val age: Int = 0,
    val bloodType: String = "",
    val diabetesStatus: String = "",
    val footRiskLevel: String = "Routine Foot Inspection",
    val chronicDiseases: List<String> = emptyList(),
    val regularMedications: List<String> = emptyList(),
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val address: String = "",
    val podologistNotes: String = ""
)

data class DoctorProfile(
    val id: String = "",
    val fullName: String = "",
    val title: String = "Podiatric Physician & Specialist",
    val hospital: String = "",
    val clinicAddress: String = "",
    val phone: String = "",
    val workingHours: String = "Mon-Fri 09:00 AM - 05:00 PM",
    val about: String = "",
    val specialties: List<String> = listOf(
        "Diabetic Foot Care & Wound Management",
        "Ingrown Toenail Correction",
        "Orthonyxia & Nail Bracing",
        "Gait & Biomechanical Orthotics"
    ),
    val locationLat: Double = 40.7128,
    val locationLng: Double = -74.0060,
    val isVerified: Boolean = false,
    val verificationStatus: VerificationStatus = VerificationStatus.UNVERIFIED,
    val diplomaUri: String? = null,
    val tcKimlikNo: String = "",
    val diplomaRegistryNo: String = ""
)

data class ChatMessage(
    val id: String,
    val sender: MessageSender,
    val text: String,
    val timestamp: Long,
    val formattedTime: String,
    val isDoctorRead: Boolean = true
)

data class PodologyClinic(
    val id: String,
    val name: String,
    val podologistName: String,
    val city: String,
    val district: String,
    val address: String,
    val phone: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Double,
    val services: List<String>,
    val type: String
)

data class EducationArticle(
    val id: String,
    val title: String,
    val category: String,
    val iconEmoji: String,
    val subtitle: String,
    val detailedSummary: String,
    val criticalNotice: String = "",
    val stepByStepGuide: List<String> = emptyList(),
    val exercises: List<String> = emptyList(),
    val doList: List<String> = emptyList(),
    val dontList: List<String> = emptyList()
)

data class DiabeticCheckQuestion(
    val id: String,
    val question: String,
    val description: String,
    val category: String,
    val isCritical: Boolean = false
)

data class DiabeticCheckHistory(
    val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val dateFormatted: String,
    val checkedYesCount: Int,
    val totalQuestions: Int = 6,
    val riskLevel: String,
    val scoreText: String,
    val doctorAdvice: String
)

data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean,
    val progress: Float = 1.0f,
    val progressText: String = ""
)

data class StreakInfo(
    val currentStreakDays: Int = 7,
    val bestStreakDays: Int = 14,
    val lastCheckDate: String = "",
    val totalChecksDone: Int = 12
)

enum class RiskCategory {
    ALL,
    CRITICAL_ALERT,
    MODERATE_RISK,
    ROUTINE_CARE,
    POST_OP_BRACING
}

data class PatientSummary(
    val id: String,
    val fullName: String,
    val age: Int,
    val phone: String,
    val tcKimlikNo: String,
    val bloodType: String,
    val diabetesStatus: String,
    val footRiskLevel: String,
    val riskCategory: RiskCategory,
    val lastInspectionDate: String,
    val unreadMessagesCount: Int = 0,
    val chronicDiseases: List<String> = emptyList(),
    val regularMedications: List<String> = emptyList(),
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val address: String = "",
    val podologistNotes: String = "",
    val activeTreatmentPlan: String = "",
    val avatarColorHex: Long = 0xFF00897B
)

data class BroadcastAnnouncement(
    val id: String,
    val title: String,
    val message: String,
    val targetCategory: RiskCategory,
    val timestamp: Long = System.currentTimeMillis(),
    val formattedDate: String,
    val recipientCount: Int
)

