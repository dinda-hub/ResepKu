package com.example.resepapp.network

import com.example.resepapp.model.MealDetailResponse
import com.example.resepapp.model.MealSummaryResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApi {

    // Endpoint untuk filtering berdasarkan kategori (filter.php)
    @GET("filter.php")
    suspend fun getMealsByCategory(@Query("c") categoryName: String): MealSummaryResponse

    // Endpoint untuk mendapatkan detail resep lengkap berdasarkan ID (lookup.php)
    @GET("lookup.php")
    suspend fun getMealDetails(@Query("i") mealId: String): MealDetailResponse

    // ⭐ ENDPOINT BARU UNTUK PENCARIAN (search.php) ⭐
    @GET("search.php")
    suspend fun searchMealsByName(@Query("s") query: String): MealSummaryResponse

    // Endpoint random dipertahankan untuk referensi
    @GET("random.php")
    suspend fun getRandomMeal(): MealDetailResponse
}