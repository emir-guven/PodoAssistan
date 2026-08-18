package com.example.ui.navigation

sealed class Screen(val route: String) {
    data object RoleSelect : Screen("role_select")
    data object Auth : Screen("auth")
    data object EDevletAuth : Screen("edevlet_auth")
    data object PendingApproval : Screen("pending_approval")
    data object PatientHome : Screen("patient_home")
    data object DoctorDashboard : Screen("doctor_dashboard")
    data object DoctorDetail : Screen("doctor_detail")
    data object DoctorChat : Screen("doctor_chat")
    data object AiBot : Screen("ai_bot")
    data object Map : Screen("map")
    data object Education : Screen("education")
    data object DiabeticFoot : Screen("diabetic_foot")
    data object Profile : Screen("profile")
    data object EditProfile : Screen("edit_profile")
}
