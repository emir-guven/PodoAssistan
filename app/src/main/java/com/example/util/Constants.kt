package com.example.util

object Constants {
    // Web Integration & Backend API
    const val BASE_URL = "https://podo.fixoriaai.xyz/api/v1/"
    const val LANDING_PAGE_URL = "https://podo.fixoriaai.xyz"
    const val DOCTOR_PORTAL_URL = "https://podo.fixoriaai.xyz/provider-portal"
    const val PRIVACY_POLICY_URL = "https://podo.fixoriaai.xyz/privacy"
    const val SUPPORT_EMAIL = "support@podoassist.com"

    // US Health ID & NPI / Board of Podiatric Medicine Integration
    const val US_HEALTH_CLIENT_ID = "podoassist_us_med_auth_v1"
    const val US_HEALTH_REDIRECT_URI = "https://podo.fixoriaai.xyz/auth/us-health/callback"
    const val US_HEALTH_AUTH_URL = "https://myhealth.cms.gov/OAuth2/v1/auth"
    const val US_HEALTH_SIMULATION_URL = "https://npiregistry.cms.hhs.gov/podiatry-verification"

    // Local Database
    const val DATABASE_NAME = "podo_assist_db"
}

