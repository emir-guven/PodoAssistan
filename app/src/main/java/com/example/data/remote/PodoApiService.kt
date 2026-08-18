package com.example.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PodoApiService {

    @GET("doctors")
    suspend fun getDoctors(): Response<List<DoctorDto>>

    @GET("clinics")
    suspend fun getClinics(@Query("city") city: String? = null): Response<List<ClinicDto>>

    @GET("education/articles")
    suspend fun getEducationArticles(): Response<List<ArticleDto>>

    @POST("auth/edevlet/verify")
    suspend fun verifyEDevletUser(
        @Body request: EDevletVerifyRequest
    ): Response<EDevletVerifyResponse>

    @POST("doctors/register")
    suspend fun registerDoctor(
        @Body request: DoctorRegisterRequest
    ): Response<ApiResponse<DoctorDto>>
}
