package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DoctorDto(
    @Json(name = "id") val id: String,
    @Json(name = "full_name") val fullName: String,
    @Json(name = "title") val title: String,
    @Json(name = "hospital") val hospital: String,
    @Json(name = "clinic_address") val clinicAddress: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "working_hours") val workingHours: String,
    @Json(name = "about") val about: String,
    @Json(name = "specialties") val specialties: List<String>,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "is_verified") val isVerified: Boolean = false,
    @Json(name = "verification_status") val verificationStatus: String = "PENDING_APPROVAL"
)

@JsonClass(generateAdapter = true)
data class ClinicDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "podologist_name") val podologistName: String,
    @Json(name = "city") val city: String,
    @Json(name = "district") val district: String,
    @Json(name = "address") val address: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "rating") val rating: Double,
    @Json(name = "services") val services: List<String>,
    @Json(name = "type") val type: String
)

@JsonClass(generateAdapter = true)
data class ArticleDto(
    @Json(name = "id") val id: String,
    @Json(name = "category") val category: String,
    @Json(name = "title") val title: String,
    @Json(name = "subtitle") val subtitle: String,
    @Json(name = "icon_emoji") val iconEmoji: String,
    @Json(name = "critical_notice") val criticalNotice: String = "",
    @Json(name = "detailed_summary") val detailedSummary: String,
    @Json(name = "step_by_step_guide") val stepByStepGuide: List<String> = emptyList(),
    @Json(name = "exercises") val exercises: List<String> = emptyList(),
    @Json(name = "do_list") val doList: List<String> = emptyList(),
    @Json(name = "dont_list") val dontList: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class EDevletVerifyRequest(
    @Json(name = "auth_token") val authToken: String,
    @Json(name = "tc_kimlik_no") val tcKimlikNo: String? = null,
    @Json(name = "client_id") val clientId: String = "podoasistan_med_auth_v1"
)

@JsonClass(generateAdapter = true)
data class EDevletVerifyResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "tc_kimlik_no") val tcKimlikNo: String,
    @Json(name = "full_name") val fullName: String,
    @Json(name = "profession") val profession: String, // e.g. "Podolog", "Diyabetik Ayak Uzmanı"
    @Json(name = "diploma_registry_no") val diplomaRegistryNo: String,
    @Json(name = "institution") val institution: String,
    @Json(name = "is_active_practitioner") val isActivePractitioner: Boolean,
    @Json(name = "verified_at") val verifiedAt: Long,
    @Json(name = "message") val message: String
)

@JsonClass(generateAdapter = true)
data class DoctorRegisterRequest(
    @Json(name = "full_name") val fullName: String,
    @Json(name = "tc_kimlik_no") val tcKimlikNo: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "title") val title: String,
    @Json(name = "hospital") val hospital: String,
    @Json(name = "clinic_address") val clinicAddress: String,
    @Json(name = "diploma_number") val diplomaNumber: String?,
    @Json(name = "e_devlet_token") val eDevletToken: String?
)

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "success") val success: Boolean,
    @Json(name = "data") val data: T?,
    @Json(name = "message") val message: String?
)
