package com.example.resepapp.model

import com.google.gson.annotations.SerializedName

data class MealSummaryResponse(
    val meals: List<MealSummary>? // HARUS nullable
)

data class MealSummary(
    @SerializedName("idMeal")
    val idMeal: String,
    @SerializedName("strMeal")
    val strMeal: String,
    @SerializedName("strMealThumb")
    val strMealThumb: String
)