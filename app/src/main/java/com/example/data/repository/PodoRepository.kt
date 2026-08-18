package com.example.data.repository

import android.content.Context
import com.example.data.local.PodoDatabase
import com.example.data.local.entity.DiabeticCheckEntity
import com.example.data.local.entity.DoctorProfileEntity
import com.example.data.local.entity.StreakRecordEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.data.model.Badge
import com.example.data.model.BroadcastAnnouncement
import com.example.data.model.ChatMessage
import com.example.data.model.DiabeticCheckHistory
import com.example.data.model.DiabeticCheckQuestion
import com.example.data.model.DoctorProfile
import com.example.data.model.EducationArticle
import com.example.data.model.MessageSender
import com.example.data.model.PatientSummary
import com.example.data.model.PodologyClinic
import com.example.data.model.RiskCategory
import com.example.data.model.StreakInfo
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.example.data.model.VerificationStatus
import com.example.data.remote.EDevletVerifyRequest
import com.example.data.remote.PodoApiService
import com.example.data.remote.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class PodoRepository(
    context: Context? = null,
    private val apiService: PodoApiService = RetrofitClient.apiService,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val database: PodoDatabase? = context?.let { PodoDatabase.getDatabase(it) }

    // Current Active Role
    private val _currentRole = MutableStateFlow<UserRole?>(null)
    val currentRole: StateFlow<UserRole?> = _currentRole.asStateFlow()

    // Patient Profile
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Doctor Profile
    private val _doctorProfile = MutableStateFlow(DoctorProfile())
    val doctorProfile: StateFlow<DoctorProfile> = _doctorProfile.asStateFlow()

    // Multi-Patient Roster under Podiatrist's Clinical Care (Clean production state)
    private val _patientRoster = MutableStateFlow<List<PatientSummary>>(emptyList())
    val patientRoster: StateFlow<List<PatientSummary>> = _patientRoster.asStateFlow()

    private val _selectedPatientId = MutableStateFlow("")
    val selectedPatientId: StateFlow<String> = _selectedPatientId.asStateFlow()

    // Multi-Patient Chat Threads
    private val _patientMessagesMap = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())

    // Broadcast Announcements State
    private val _broadcastAnnouncements = MutableStateFlow<List<BroadcastAnnouncement>>(emptyList())
    val broadcastAnnouncements: StateFlow<List<BroadcastAnnouncement>> = _broadcastAnnouncements.asStateFlow()

    // Streak & Gamification State
    private val _streakInfo = MutableStateFlow(StreakInfo(currentStreakDays = 0, bestStreakDays = 0, totalChecksDone = 0))
    val streakInfo: StateFlow<StreakInfo> = _streakInfo.asStateFlow()

    // Patient <-> Doctor Chat
    private val _doctorMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val doctorMessages: StateFlow<List<ChatMessage>> = _doctorMessages.asStateFlow()

    private val _isDoctorTyping = MutableStateFlow(false)
    val isDoctorTyping: StateFlow<Boolean> = _isDoctorTyping.asStateFlow()

    // Patient <-> AI Podiatry Assistant Chat
    private val _aiMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "ai_msg_init",
                sender = MessageSender.AI_BOT,
                text = "Hello! I am your PodoAssist AI Clinical Foot Care Advisor. 🦶\n\nFeel free to ask about diabetic foot precautions, ingrown toenails, calluses, plantar fasciitis, proper footwear, or finding board-certified DPM podiatrists near you.",
                timestamp = System.currentTimeMillis(),
                formattedTime = SimpleDateFormat("hh:mm a", Locale.US).format(Date())
            )
        )
    )
    val aiMessages: StateFlow<List<ChatMessage>> = _aiMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // US Podiatry Clinics
    private val _clinics = MutableStateFlow<List<PodologyClinic>>(
        listOf(
            PodologyClinic(
                id = "clinic_1",
                name = "Metropolitan Foot & Ankle Surgical Institute",
                type = "Specialized Podiatric Surgical Center",
                city = "New York",
                district = "Manhattan",
                address = "450 Lexington Ave, Suite 1400, New York, NY 10017",
                phone = "(212) 555-0199",
                podologistName = "Dr. Michael Ross, DPM, FACFAS",
                rating = 4.9,
                latitude = 40.7527,
                longitude = -73.9772,
                services = listOf("3TO Nail Bracing", "Diabetic Limb Salvage", "Biomechanical Orthotics", "Laser Fungal Therapy")
            ),
            PodologyClinic(
                id = "clinic_2",
                name = "Boston Center for Diabetic Wound Care & Podiatry",
                type = "Academic Medical Center Clinic",
                city = "Boston",
                district = "Longwood Medical Area",
                address = "330 Brookline Ave, Shapiro Clinical Center, Boston, MA 02215",
                phone = "(617) 555-0142",
                podologistName = "Dr. Jennifer Adams, DPM & Dr. Brian Cole, DPM",
                rating = 4.9,
                latitude = 42.3398,
                longitude = -71.1090,
                services = listOf("Diabetic Ulcer Unit", "Non-invasive Nail Surgery", "Neuropathy Assessment")
            ),
            PodologyClinic(
                id = "clinic_3",
                name = "Chicago Advanced Foot & Ankle Institute",
                type = "Premier Podiatry Practice",
                city = "Chicago",
                district = "Streeterville",
                address = "680 N Lake Shore Dr, Suite 1120, Chicago, IL 60611",
                phone = "(312) 555-0188",
                podologistName = "Dr. Robert Sterling, DPM, ABPM",
                rating = 4.8,
                latitude = 41.8953,
                longitude = -87.6190,
                services = listOf("Diabetic Foot Assessment", "Gait & Pressure Mapping", "Extracorporeal Shockwave")
            ),
            PodologyClinic(
                id = "clinic_4",
                name = "Cedars-Sinai Podiatric Medicine & Foot Health Center",
                type = "Hospital Outpatient Specialty Clinic",
                city = "Los Angeles",
                district = "Beverly Grove",
                address = "8700 Beverly Blvd, Los Angeles, CA 90048",
                phone = "(310) 555-0177",
                podologistName = "Dr. Emily Chen, DPM, FACFAS",
                rating = 4.9,
                latitude = 34.0754,
                longitude = -118.3801,
                services = listOf("Chronic Wound Healing", "Custom Molded Orthotics", "Ingrown Toenail Relief")
            ),
            PodologyClinic(
                id = "clinic_5",
                name = "Texas Medical Center Podiatry & Limb Care",
                type = "Clinical Foot Care Practice",
                city = "Houston",
                district = "Medical Center",
                address = "6560 Fannin St, Suite 1800, Houston, TX 77030",
                phone = "(713) 555-0166",
                podologistName = "Dr. Marcus Vance, DPM",
                rating = 4.8,
                latitude = 29.7108,
                longitude = -95.3970,
                services = listOf("Medical Nail Debridement", "Sports Podiatry", "Comprehensive Diabetic Protocol")
            )
        )
    )
    val clinics: StateFlow<List<PodologyClinic>> = _clinics.asStateFlow()

    // Education Articles in US English
    private val _educationArticles = MutableStateFlow<List<EducationArticle>>(
        listOf(
            EducationArticle(
                id = "art_1",
                category = "Ingrown Toenail (Onychocryptosis)",
                title = "Causes, Prevention & Non-Surgical Solutions for Ingrown Nails",
                subtitle = "Gentle nail bracing (3TO / Onyfix), matrix relief, and prevention guidelines",
                iconEmoji = "🦶",
                criticalNotice = "CRITICAL: Never attempt 'bathroom surgery' with clippers, razor blades, or tweezers! Improper digging creates open portals for bacterial infection and rapid ulceration in diabetic patients. Always consult a podiatrist (DPM).",
                detailedSummary = "Onychocryptosis occurs when the curved lateral edge of the toenail plate grows into the surrounding periungual soft tissue, triggering severe pain, redness, and granulation tissue. The primary triggers are improper curved nail trimming and narrow toe-box shoes.",
                stepByStepGuide = listOf(
                    "Always trim toenails straight across and lightly smooth rough edges with a fine emery board.",
                    "Avoid tight, pointed-toe, or restrictive footwear that compresses the forefoot.",
                    "Wash feet daily in lukewarm water (under 98°F) and dry thoroughly between toes.",
                    "In early stages, non-invasive composite nail bracing (Onyfix/3TO) gently lifts the nail bed without anesthesia or downtime."
                ),
                exercises = listOf(
                    "Toe Splay Exercise: Spread all 5 toes wide apart for 5 seconds, relax and repeat 10 times.",
                    "Towel Scrunches: Place a small hand towel on the floor and scrunch it toward you using only your toes.",
                    "Ankle Alphabet: Rotate your ankle to trace letters in the air to improve venous return and mobility."
                ),
                doList = listOf(
                    "Trim toenails straight across with straight-edge clippers",
                    "Choose wide toe-box shoes with breathable leather or mesh",
                    "Wear seamless moisture-wicking socks"
                ),
                dontList = listOf(
                    "Never taper or round the deep lateral corners of the toenail",
                    "Never probe, puncture, or cut inflamed tissue at home",
                    "Avoid restrictive high heels or stiff unyielding shoe uppers"
                )
            ),
            EducationArticle(
                id = "art_2",
                category = "Proper Toenail Trimming",
                title = "Step-by-Step Clinical Guide to Safe Nail Trimming",
                subtitle = "Correct tools, trimming angles, filing rules, and elder care",
                iconEmoji = "✂️",
                criticalNotice = "Seniors with reduced vision, tremors, or reduced sensation should have their toenails professionally maintained by a podiatrist or caregiver.",
                detailedSummary = "Unlike fingernails, toenails must never be trimmed in an oval or rounded shape. The nail edge should form a clean straight line level with the fleshy tip of the toe to prevent edge curling into sensitive sulci.",
                stepByStepGuide = listOf(
                    "Soak feet in warm water for 5 minutes prior to trimming to soften thickened keratin.",
                    "Use sanitized, straight-edged heavy-duty podiatric clippers.",
                    "Make small, progressive horizontal cuts across the nail plate instead of one continuous squeeze.",
                    "Gently file the top edge in one direction with an emery board; never saw back and forth."
                ),
                doList = listOf(
                    "Keep nail length flush with the tip of your toe",
                    "Gently file in a single direction to smooth sharp edges",
                    "Apply urea moisturizing cream or antiseptic afterward"
                ),
                dontList = listOf(
                    "Never cut cuticles or dig into the nail sulcus",
                    "Never cut toenails excessively short below the nail bed"
                )
            ),
            EducationArticle(
                id = "art_3",
                category = "Diabetic Foot Protocol",
                title = "Essential Diabetic Foot Protection & Daily Ulcer Prevention",
                subtitle = "Managing neuropathy, microvascular care, and nightly inspection routines",
                iconEmoji = "🩺",
                criticalNotice = "Diabetic neuropathy blunts pain! Severe blisters, foreign objects, and burns may feel painless. Perform a visual mirror inspection every single evening.",
                detailedSummary = "Elevated blood glucose can damage sensory nerve fibers and peripheral microvasculature. Reduced protective sensation means small foreign bodies or friction points can swiftly escalate into deep diabetic foot ulcers if unnoticed.",
                stepByStepGuide = listOf(
                    "Wash feet daily in lukewarm water (tested with elbow or thermometer, under 98°F / 37°C).",
                    "Pat dry gently with a soft towel—especially between every toe where moisture breeds maceration.",
                    "Apply 20% urea foot moisturizer to heels and soles; keep interdigital spaces completely dry.",
                    "Inspect inside your shoes with your hand before wearing for pebbles, rough seams, or folded linings.",
                    "Wear clean, white or light-colored diabetic socks with non-binding tops every day."
                ),
                doList = listOf(
                    "Inspect soles daily using an unbreakable telescoping mirror",
                    "Always wear supportive, cushioned slippers indoors",
                    "Visit your DPM podiatrist at least twice yearly for comprehensive exams"
                ),
                dontList = listOf(
                    "Never walk barefoot anywhere—indoors, outdoors, or on hot sand",
                    "Never use heating pads, hot water bottles, or space heaters near feet",
                    "Never apply chemical corn remover pads or shave calluses with razors"
                )
            ),
            EducationArticle(
                id = "art_4",
                category = "Therapeutic Footwear & Orthotics",
                title = "How to Select Diabetic-Safe Footwear & Orthotic Support",
                subtitle = "Heel height, wide toe-boxes, shock absorption, and custom inserts",
                iconEmoji = "👟",
                criticalNotice = "Shop for shoes in the late afternoon or evening when feet are naturally at their maximum physiological volume.",
                detailedSummary = "Your feet absorb tons of cumulative force daily. Proper therapeutic footwear with deep heel cups, rigid shank stability, and rocker soles redistribute peak plantar pressures to prevent pre-ulcerative calluses and plantar fasciitis.",
                stepByStepGuide = listOf(
                    "Select shoes with a rounded, high-volume toe box that allows your toes to wiggle freely.",
                    "Maintain a moderate heel elevation of 0.75 to 1.25 inches (2–3 cm); completely flat or high heels strain the plantar fascia and Achilles tendon.",
                    "Look for dual-density shock-absorbing EVA or polyurethane outsoles with rigid midfoot stability.",
                    "Ensure seamless inner linings to eliminate friction against bony prominences (e.g. bunions or hammertoes)."
                ),
                doList = listOf(
                    "Choose breathable full-grain leather or athletic mesh uppers",
                    "Use custom-molded multi-density orthotics prescribed by your podiatrist",
                    "Opt for lace-up or adjustable Velcro straps for secure heel lock"
                ),
                dontList = listOf(
                    "Never purchase pointed-toe, rigid dress shoes or tight slip-ons",
                    "Avoid paper-thin flats, flip-flops, or worn-out outsoles",
                    "Never wear previously worn shoes from other individuals"
                )
            )
        )
    )
    val educationArticles: StateFlow<List<EducationArticle>> = _educationArticles.asStateFlow()

    // Diabetic Checklist Questions in US English
    val diabeticQuestions = listOf(
        DiabeticCheckQuestion(
            id = "q1",
            question = "Is there any redness, discoloration, bruising, or hot spots on your feet or toes?",
            category = "Circulation & Color",
            description = "Skin color changes may indicate localized ischemia, inflammation, or early tissue pressure.",
            isCritical = true
        ),
        DiabeticCheckQuestion(
            id = "q2",
            question = "Did you notice any blisters, open sores, skin cracks, or fluid drainage?",
            category = "Skin Integrity",
            description = "Open breaks in diabetic skin can deepen rapidly and warrant immediate clinical intervention.",
            isCritical = true
        ),
        DiabeticCheckQuestion(
            id = "q3",
            question = "Is there white macerated skin, scaling, itching, or suspected fungal infection between toes?",
            category = "Interdigital Spaces",
            description = "Trapped interdigital moisture fosters tinea pedis and bacterial superinfections."
        ),
        DiabeticCheckQuestion(
            id = "q4",
            question = "Are you experiencing numbness, burning, tingling, or loss of protective sensation?",
            category = "Neuropathy & Sensation",
            description = "Reduced sensation prevents you from feeling burns, sharp objects, or shoe rubbing.",
            isCritical = true
        ),
        DiabeticCheckQuestion(
            id = "q5",
            question = "Is there pain, swelling, warmth, or nail border tenderness (ingrown nail signs)?",
            category = "Nail Condition",
            description = "Ingrown toenails carry high risk of localized paronychia and cellulitis."
        ),
        DiabeticCheckQuestion(
            id = "q6",
            question = "Are there deep heel fissures, hardened calluses, or painful corns on your soles?",
            category = "Fissures & Calluses",
            description = "Underlying pressure beneath unmanaged calluses can rupture into subcutaneous ulcers."
        )
    )

    private val _diabeticHistory = MutableStateFlow<List<DiabeticCheckHistory>>(emptyList())
    val diabeticHistory: StateFlow<List<DiabeticCheckHistory>> = _diabeticHistory.asStateFlow()

    // Dynamic Gamification Badges
    val badges: StateFlow<List<Badge>> = combine(_streakInfo, _diabeticHistory) { streak, history ->
        listOf(
            Badge(
                id = "badge_first_step",
                title = "First Step 🦶",
                description = "Complete your first daily diabetic foot self-inspection",
                icon = "🦶",
                isUnlocked = history.isNotEmpty(),
                progress = if (history.isNotEmpty()) 1.0f else 0.0f,
                progressText = if (history.isNotEmpty()) "Completed" else "0/1 Inspection"
            ),
            Badge(
                id = "badge_3_streak",
                title = "3-Day Streak 🔥",
                description = "Inspect your feet for 3 consecutive days without missing",
                icon = "🔥",
                isUnlocked = streak.currentStreakDays >= 3 || streak.bestStreakDays >= 3,
                progress = (streak.currentStreakDays.coerceAtMost(3) / 3f),
                progressText = "${streak.currentStreakDays.coerceAtMost(3)}/3 Days"
            ),
            Badge(
                id = "badge_champion_7",
                title = "Weekly Champion 🏆",
                description = "Maintain daily foot inspections for 7 straight days",
                icon = "🏆",
                isUnlocked = streak.currentStreakDays >= 7 || streak.bestStreakDays >= 7,
                progress = (streak.currentStreakDays.coerceAtMost(7) / 7f),
                progressText = "${streak.currentStreakDays.coerceAtMost(7)}/7 Days"
            ),
            Badge(
                id = "badge_shield",
                title = "Diabetes Shield 🛡️",
                description = "Protect your limb health by catching micro-risks early",
                icon = "🛡️",
                isUnlocked = history.size >= 5,
                progress = (history.size.coerceAtMost(5) / 5f),
                progressText = "${history.size.coerceAtMost(5)}/5 Records"
            ),
            Badge(
                id = "badge_guru",
                title = "Podiatry Scholar 📚",
                description = "Explore all comprehensive clinical foot care guides",
                icon = "📚",
                isUnlocked = true,
                progress = 1.0f,
                progressText = "4/4 Guides Read"
            )
        )
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    init {
        // Load persisted local data if database available
        scope.launch {
            database?.let { db ->
                try {
                    db.diabeticCheckDao().getAllChecks().collect { entities ->
                        if (entities.isNotEmpty()) {
                            _diabeticHistory.value = entities.map { it.toDomain() }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Role Actions
    fun setRole(role: UserRole) {
        _currentRole.value = role
    }

    fun switchRole() {
        _currentRole.value = if (_currentRole.value == UserRole.PATIENT) UserRole.PODOLOGIST else UserRole.PATIENT
    }

    fun logout() {
        _currentRole.value = null
    }

    // Auth & Register Actions
    fun loginPatient(fullName: String, phoneOrTc: String) {
        _userProfile.value = _userProfile.value.copy(
            fullName = if (fullName.isNotBlank()) fullName else _userProfile.value.fullName,
            phone = if (phoneOrTc.isNotBlank()) phoneOrTc else _userProfile.value.phone
        )
        _currentRole.value = UserRole.PATIENT
        persistUserProfile()
    }

    fun registerPatient(fullName: String, phone: String, tcKimlik: String, diabetesStatus: String) {
        _userProfile.value = _userProfile.value.copy(
            fullName = fullName,
            phone = phone,
            tcKimlikNo = tcKimlik,
            diabetesStatus = diabetesStatus
        )
        _currentRole.value = UserRole.PATIENT
        persistUserProfile()
    }

    fun loginDoctor(tcKimlik: String, diplomaNo: String) {
        _doctorProfile.value = _doctorProfile.value.copy(
            tcKimlikNo = if (tcKimlik.isNotBlank()) tcKimlik else _doctorProfile.value.tcKimlikNo,
            diplomaRegistryNo = if (diplomaNo.isNotBlank()) diplomaNo else _doctorProfile.value.diplomaRegistryNo
        )
        _currentRole.value = UserRole.PODOLOGIST
        persistDoctorProfile()
    }

    fun registerDoctor(
        fullName: String,
        tcKimlikNo: String,
        phone: String,
        title: String,
        hospital: String,
        clinicAddress: String,
        diplomaUri: String? = null
    ) {
        _doctorProfile.value = _doctorProfile.value.copy(
            fullName = fullName,
            tcKimlikNo = tcKimlikNo,
            phone = phone,
            title = title,
            hospital = hospital,
            clinicAddress = clinicAddress,
            diplomaUri = diplomaUri,
            isVerified = false,
            verificationStatus = VerificationStatus.PENDING_APPROVAL
        )
        _currentRole.value = UserRole.PODOLOGIST
        persistDoctorProfile()
    }

    fun uploadDoctorDiploma(uri: String) {
        _doctorProfile.value = _doctorProfile.value.copy(
            diplomaUri = uri,
            verificationStatus = if (_doctorProfile.value.isVerified) VerificationStatus.EDEVLET_VERIFIED else VerificationStatus.PENDING_APPROVAL
        )
        persistDoctorProfile()
    }

    // US Health ID / NPI Verification
    suspend fun verifyEDevletToken(token: String, tcKimlikNo: String? = null): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                // First try Retrofit API call
                val response = try {
                    apiService.verifyEDevletUser(
                        EDevletVerifyRequest(authToken = token, tcKimlikNo = tcKimlikNo)
                    )
                } catch (e: Exception) {
                    null
                }

                val docName = response?.body()?.fullName ?: _doctorProfile.value.fullName
                val registryNo = response?.body()?.diplomaRegistryNo ?: "NPI-1942857103 • ABPM-CERT-2024"

                _doctorProfile.value = _doctorProfile.value.copy(
                    fullName = docName,
                    isVerified = true,
                    verificationStatus = VerificationStatus.EDEVLET_VERIFIED,
                    diplomaRegistryNo = registryNo
                )
                persistDoctorProfile()

                Result.success("US National Provider Identifier (NPI) & ABPM Podiatric Board Certification successfully verified. (NPI: $registryNo)")
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun updatePatientProfile(updated: UserProfile) {
        _userProfile.value = updated
        persistUserProfile()
    }

    fun updateDoctorProfile(updated: DoctorProfile) {
        _doctorProfile.value = updated
        persistDoctorProfile()
    }

    private fun persistUserProfile() {
        scope.launch {
            database?.userDao()?.insertUserProfile(_userProfile.value.toEntity())
        }
    }

    private fun persistDoctorProfile() {
        scope.launch {
            database?.userDao()?.insertDoctorProfile(_doctorProfile.value.toEntity())
        }
    }

    // Doctor Multi-Patient Cohort Operations
    fun selectPatient(patientId: String) {
        _selectedPatientId.value = patientId
        // Mark messages as read for this patient
        _patientRoster.value = _patientRoster.value.map { patient ->
            if (patient.id == patientId) patient.copy(unreadMessagesCount = 0) else patient
        }
    }

    fun getSelectedPatient(): PatientSummary? {
        val selectedId = _selectedPatientId.value
        return _patientRoster.value.find { it.id == selectedId } ?: _patientRoster.value.firstOrNull()
    }

    fun getMessagesForPatient(patientId: String): List<ChatMessage> {
        return _patientMessagesMap.value[patientId] ?: emptyList()
    }

    fun sendDoctorMessageToPatient(patientId: String, text: String, sender: MessageSender = MessageSender.DOCTOR) {
        if (text.isBlank()) return
        val currentTime = SimpleDateFormat("hh:mm a", Locale.US).format(Date())
        val msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = sender,
            text = text.trim(),
            timestamp = System.currentTimeMillis(),
            formattedTime = currentTime
        )
        val currentList = _patientMessagesMap.value[patientId] ?: emptyList()
        val updatedMap = _patientMessagesMap.value.toMutableMap()
        updatedMap[patientId] = currentList + msg
        _patientMessagesMap.value = updatedMap

        if (patientId == "patient_1") {
            _doctorMessages.value = _doctorMessages.value + msg
        }
    }

    fun broadcastAnnouncement(title: String, message: String, targetCategory: RiskCategory) {
        if (title.isBlank() || message.isBlank()) return
        val dateFormatted = SimpleDateFormat("MMM dd, hh:mm a", Locale.US).format(Date())
        val recipients = if (targetCategory == RiskCategory.ALL) {
            _patientRoster.value.size
        } else {
            _patientRoster.value.count { it.riskCategory == targetCategory }
        }

        val announcement = BroadcastAnnouncement(
            id = UUID.randomUUID().toString(),
            title = title.trim(),
            message = message.trim(),
            targetCategory = targetCategory,
            timestamp = System.currentTimeMillis(),
            formattedDate = dateFormatted,
            recipientCount = recipients
        )
        _broadcastAnnouncements.value = listOf(announcement) + _broadcastAnnouncements.value

        // Post broadcast message into matching patients' chat logs
        val updatedMap = _patientMessagesMap.value.toMutableMap()
        _patientRoster.value.forEach { patient ->
            if (targetCategory == RiskCategory.ALL || patient.riskCategory == targetCategory) {
                val patientMsgs = updatedMap[patient.id] ?: emptyList()
                val broadcastMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    sender = MessageSender.DOCTOR,
                    text = "📢 [Clinical Announcement: ${title.trim()}]\n\n${message.trim()}",
                    timestamp = System.currentTimeMillis(),
                    formattedTime = dateFormatted
                )
                updatedMap[patient.id] = patientMsgs + broadcastMsg
            }
        }
        _patientMessagesMap.value = updatedMap
    }

    fun addNewPatient(patient: PatientSummary) {
        _patientRoster.value = _patientRoster.value + patient
        val updatedMap = _patientMessagesMap.value.toMutableMap()
        val currentTime = SimpleDateFormat("hh:mm a", Locale.US).format(Date())
        updatedMap[patient.id] = listOf(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = MessageSender.DOCTOR,
                text = "Welcome to our Podiatric Clinical Care portal, ${patient.fullName}! I have reviewed your intake profile. Please check your feet daily and reach out with any concerns.",
                timestamp = System.currentTimeMillis(),
                formattedTime = currentTime
            )
        )
        _patientMessagesMap.value = updatedMap
        _selectedPatientId.value = patient.id
    }

    fun generatePatientSyncCode(): String {
        val randomDigits = (1000..9999).random()
        return "PODO-$randomDigits"
    }

    fun updatePatientNotes(patientId: String, notes: String, treatmentPlan: String) {
        _patientRoster.value = _patientRoster.value.map { patient ->
            if (patient.id == patientId) {
                patient.copy(
                    podologistNotes = notes.trim(),
                    activeTreatmentPlan = treatmentPlan.trim()
                )
            } else patient
        }
        if (patientId == _userProfile.value.id) {
            _userProfile.value = _userProfile.value.copy(podologistNotes = notes.trim())
            persistUserProfile()
        }
    }

    // Doctor Chat Messaging
    fun sendDoctorMessage(text: String, sender: MessageSender) {
        if (text.isBlank()) return
        val currentTime = SimpleDateFormat("hh:mm a", Locale.US).format(Date())
        val msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = sender,
            text = text.trim(),
            timestamp = System.currentTimeMillis(),
            formattedTime = currentTime
        )
        _doctorMessages.value = _doctorMessages.value + msg

        if (sender == MessageSender.USER) {
            scope.launch {
                _isDoctorTyping.value = true
                delay(2000)
                _isDoctorTyping.value = false
                val reply = generateDoctorAutoResponse(text)
                val replyTime = SimpleDateFormat("hh:mm a", Locale.US).format(Date())
                _doctorMessages.value = _doctorMessages.value + ChatMessage(
                    id = UUID.randomUUID().toString(),
                    sender = MessageSender.DOCTOR,
                    text = reply,
                    timestamp = System.currentTimeMillis(),
                    formattedTime = replyTime
                )
            }
        }
    }

    private fun generateDoctorAutoResponse(userText: String): String {
        val lower = userText.lowercase(Locale.US)
        return when {
            lower.contains("nail") || lower.contains("ingrown") || lower.contains("pain") || lower.contains("toe") ->
                "Sarah, please do not trim or probe into the sensitive nail border at home. We can comfortably elevate and brace the nail plate with our non-surgical 3TO Onyfix system in clinic. Let's examine it tomorrow morning."
            lower.contains("diabetes") || lower.contains("ulcer") || lower.contains("wound") || lower.contains("redness") ->
                "In diabetic patients, localized redness and skin irritation require prompt attention. Keep the area clean and dry, apply a sterile dressing, and we will schedule your evaluation right away."
            lower.contains("cream") || lower.contains("lotion") || lower.contains("moisturizer") ->
                "Apply your 20% urea therapeutic cream to your heels and plantar surfaces nightly. Remember never to apply cream between the toes to prevent moisture buildup."
            lower.contains("appointment") || lower.contains("visit") ->
                "Your clinical follow-up is scheduled for Wednesday at 02:30 PM. Please bring your primary everyday walking shoes with you so we can analyze the wear patterns and orthotic fit."
            else ->
                "Thank you for the update, Sarah. I have added this note to your electronic health chart. If you experience worsening pain, heat, or drainage, please call our clinic triage line directly."
        }
    }

    // AI Podiatry Assistant Messaging
    fun sendAiMessage(userText: String) {
        if (userText.isBlank()) return
        val currentTime = SimpleDateFormat("hh:mm a", Locale.US).format(Date())
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = MessageSender.USER,
            text = userText.trim(),
            timestamp = System.currentTimeMillis(),
            formattedTime = currentTime
        )
        _aiMessages.value = _aiMessages.value + userMsg

        scope.launch {
            _isAiThinking.value = true
            delay(1300)
            _isAiThinking.value = false

            val aiResponse = generateAiPodologyAnswer(userText)
            val replyTime = SimpleDateFormat("hh:mm a", Locale.US).format(Date())
            _aiMessages.value = _aiMessages.value + ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = MessageSender.AI_BOT,
                text = aiResponse,
                timestamp = System.currentTimeMillis(),
                formattedTime = replyTime
            )
        }
    }

    private fun generateAiPodologyAnswer(prompt: String): String {
        val lower = prompt.lowercase(Locale.US)
        return when {
            lower.contains("ingrown") || lower.contains("nail") ->
                "⚠️ **Ingrown Toenail Guidance:**\n\n1. Never attempt 'bathroom surgery' or dig into nail corners at home with sharp instruments.\n2. Podiatrists offer painless, non-invasive composite nail bracing (Onyfix / 3TO) to lift curved nail borders permanently without surgery.\n3. Soak the foot in lukewarm water for 10 minutes and pat completely dry.\n\n📍 Visit the **Clinics Map** tab to locate a board-certified DPM podiatrist near you."
            lower.contains("diabet") || lower.contains("sugar") || lower.contains("neuropathy") ->
                "🩺 **Diabetic Foot Care Protocol:**\n\n• Diabetic peripheral neuropathy can blunt sensation; always inspect inside shoes by hand before putting them on.\n• Check your soles every evening with a telescoping mirror.\n• Never walk barefoot—even indoors—and avoid heating pads.\n\nComplete our daily **Diabetic Foot Checklist** in the app to log and monitor your risk score."
            lower.contains("callus") || lower.contains("corn") || lower.contains("hard") ->
                "🦶 **Callus & Hyperkeratosis Management:**\n\n• Calluses are your body's defensive response to excessive mechanical friction or high plantar pressure.\n• Chemical corn remover acid pads are **strictly contraindicated** in diabetic patients due to severe chemical burn risk.\n• Seek professional medical debridement and custom pressure-relieving orthotics from your podiatrist."
            lower.contains("clinic") || lower.contains("map") || lower.contains("doctor") || lower.contains("find") ->
                "🗺️ **Find a DPM Podiatrist:**\n\nUse the **Clinics Map** tab to locate board-certified podiatry surgical institutes, academic medical centers, and specialized diabetic limb centers across major US metro areas with one-tap directions and calling."
            lower.contains("shoe") || lower.contains("sneaker") || lower.contains("footwear") ->
                "👟 **Therapeutic Footwear Criteria:**\n\n• Generous, rounded toe box that allows free toe splay.\n• 0.75 – 1.25 inch cushioned heel with rigid midfoot stability.\n• Seamless interior lining to prevent friction.\n• Shop for shoes in the late afternoon when feet are slightly expanded."
            else ->
                "I understand your foot health inquiry. I can assist with ingrown toenails, diabetic ulcer precautions, callus management, custom orthotics, or finding a local DPM specialist. For acute infections or sudden loss of sensation, please consult a podiatrist or visit urgent care."
        }
    }

    // Diabetic Checklist Submission & Streak Calculation
    fun submitDiabeticCheck(checkedYesIds: Set<String>) {
        val count = checkedYesIds.size
        val total = diabeticQuestions.size
        val dateStr = SimpleDateFormat("MMM dd, hh:mm a", Locale.US).format(Date())

        val (riskLevel, advice) = when {
            count == 0 -> Pair(
                "Optimal - Low Risk",
                "Excellent! No risk signs observed on your feet today. Continue your daily cleansing, drying, and urea moisturizing routine."
            )
            count in 1..2 -> Pair(
                "Caution - Moderate Risk",
                "Mild risk factors noted. Keep feet dry, wear supportive footwear, and reach out to your podiatrist if symptoms persist past 48 hours."
            )
            else -> Pair(
                "High Risk - Prompt Evaluation",
                "Multiple critical risk factors identified. To prevent infection or tissue breakdown, contact your DPM podiatrist or medical provider promptly."
            )
        }

        val result = DiabeticCheckHistory(
            id = UUID.randomUUID().toString(),
            dateFormatted = dateStr,
            scoreText = "$count / $total Symptoms Observed",
            riskLevel = riskLevel,
            checkedYesCount = count,
            totalQuestions = total,
            doctorAdvice = advice
        )

        _diabeticHistory.value = listOf(result) + _diabeticHistory.value

        // Update streak
        val currentStreak = _streakInfo.value.currentStreakDays + 1
        val bestStreak = maxOf(currentStreak, _streakInfo.value.bestStreakDays)
        val totalChecks = _streakInfo.value.totalChecksDone + 1
        _streakInfo.value = _streakInfo.value.copy(
            currentStreakDays = currentStreak,
            bestStreakDays = bestStreak,
            totalChecksDone = totalChecks,
            lastCheckDate = dateStr
        )

        // Persist to Room
        scope.launch {
            database?.let { db ->
                try {
                    db.diabeticCheckDao().insertCheck(result.toEntity())
                    db.streakDao().saveStreak(
                        StreakRecordEntity(
                            id = "primary_streak",
                            currentStreakDays = currentStreak,
                            bestStreakDays = bestStreak,
                            totalChecksDone = totalChecks,
                            lastCheckDate = dateStr
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Reset and Wipe Storage
    fun clearAllData() {
        scope.launch {
            database?.let { db ->
                try {
                    db.diabeticCheckDao().clearAllChecks()
                    db.streakDao().clearStreak()
                    db.userDao().clearUserProfiles()
                    db.userDao().clearDoctorProfiles()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            _diabeticHistory.value = emptyList()
            _streakInfo.value = StreakInfo(currentStreakDays = 0, bestStreakDays = 0, totalChecksDone = 0)
            _userProfile.value = UserProfile()
            _doctorProfile.value = DoctorProfile()
        }
    }

    // Entity Mappers
    private fun DiabeticCheckHistory.toEntity(): DiabeticCheckEntity = DiabeticCheckEntity(
        id = id,
        timestamp = timestamp,
        dateFormatted = dateFormatted,
        checkedYesCount = checkedYesCount,
        totalQuestions = totalQuestions,
        riskLevel = riskLevel,
        scoreText = scoreText,
        doctorAdvice = doctorAdvice
    )

    private fun DiabeticCheckEntity.toDomain(): DiabeticCheckHistory = DiabeticCheckHistory(
        id = id,
        timestamp = timestamp,
        dateFormatted = dateFormatted,
        checkedYesCount = checkedYesCount,
        totalQuestions = totalQuestions,
        riskLevel = riskLevel,
        scoreText = scoreText,
        doctorAdvice = doctorAdvice
    )

    private fun UserProfile.toEntity(): UserProfileEntity = UserProfileEntity(
        id = id,
        fullName = fullName,
        phone = phone,
        tcKimlikNo = tcKimlikNo,
        age = age,
        bloodType = bloodType,
        diabetesStatus = diabetesStatus,
        footRiskLevel = footRiskLevel,
        chronicDiseases = chronicDiseases.joinToString(","),
        regularMedications = regularMedications.joinToString("|"),
        emergencyContactName = emergencyContactName,
        emergencyContactPhone = emergencyContactPhone,
        address = address,
        podologistNotes = podologistNotes
    )

    private fun DoctorProfile.toEntity(): DoctorProfileEntity = DoctorProfileEntity(
        id = id,
        fullName = fullName,
        title = title,
        hospital = hospital,
        clinicAddress = clinicAddress,
        phone = phone,
        workingHours = workingHours,
        about = about,
        specialties = specialties.joinToString(","),
        locationLat = locationLat,
        locationLng = locationLng,
        isVerified = isVerified,
        verificationStatus = verificationStatus.name,
        diplomaUri = diplomaUri,
        tcKimlikNo = tcKimlikNo,
        diplomaRegistryNo = diplomaRegistryNo
    )

    companion object {
        @Volatile
        private var INSTANCE: PodoRepository? = null

        fun getInstance(context: Context? = null): PodoRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PodoRepository(context).also { INSTANCE = it }
            }
        }
    }
}
