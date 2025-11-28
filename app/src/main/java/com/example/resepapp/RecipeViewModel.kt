package com.example.resepapp

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.resepapp.model.MealDetail
import com.example.resepapp.model.MealSummary
import com.example.resepapp.model.MealSummaryResponse
import com.example.resepapp.network.MealApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecipeViewModel(private val api: MealApi) : ViewModel() {

    // State untuk Daftar Resep (untuk List Screen)
    private val _mealDetailListState = mutableStateOf(MealDetailListState())
    val mealDetailListState: State<MealDetailListState> = _mealDetailListState

    // State untuk Detail Resep Saat Ini (untuk Detail Screen)
    private val _currentMealDetail = mutableStateOf<MealDetail?>(null)
    val currentMealDetail: State<MealDetail?> = _currentMealDetail

    // State Loading Detail
    private val _isDetailLoading = mutableStateOf(false)
    val isDetailLoading: State<Boolean> = _isDetailLoading

    // ⭐ STATE BARU UNTUK PENCARIAN (PENTING) ⭐
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        // Panggilan Awal: Muat kategori default ("Beef" untuk memulai)
        fetchMealsByCategory("Beef")
    }

    // --- Helper Function untuk Mapping MealSummary ke MealDetail ---
    private fun mapSummaryToDetail(summaryList: List<MealSummary>?, category: String): List<MealDetail> {
        return summaryList?.map { summary: MealSummary ->
            MealDetail(
                idMeal = summary.idMeal,
                strMeal = summary.strMeal,
                strMealThumb = summary.strMealThumb,
                strCategory = category,
                strArea = "Global",
                strInstructions = null,
                strYoutube = null,
                strSource = null,

                // Set semua 20 pasang bahan/takaran ke null karena tidak ada di response filter/search
                strIngredient1 = null, strMeasure1 = null, strIngredient2 = null, strMeasure2 = null,
                strIngredient3 = null, strMeasure3 = null, strIngredient4 = null, strMeasure4 = null,
                strIngredient5 = null, strMeasure5 = null, strIngredient6 = null, strMeasure6 = null,
                strIngredient7 = null, strMeasure7 = null, strIngredient8 = null, strMeasure8 = null,
                strIngredient9 = null, strMeasure9 = null, strIngredient10 = null, strMeasure10 = null,
                strIngredient11 = null, strMeasure11 = null, strIngredient12 = null, strMeasure12 = null,
                strIngredient13 = null, strMeasure13 = null, strIngredient14 = null, strMeasure14 = null,
                strIngredient15 = null, strMeasure15 = null, strIngredient16 = null, strMeasure16 = null,
                strIngredient17 = null, strMeasure17 = null, strIngredient18 = null, strMeasure18 = null,
                strIngredient19 = null, strMeasure19 = null, strIngredient20 = null, strMeasure20 = null,
            )
        } ?: emptyList()
    }

    // --- Fungsi Filtering (untuk Kategori) ---
    fun fetchMealsByCategory(categoryName: String) {
        viewModelScope.launch {
            _mealDetailListState.value = _mealDetailListState.value.copy(isLoading = true)

            val apiCategoryName = when (categoryName) {
                "Semua" -> "Beef"
                "Makanan Utama" -> "Beef"
                "Lauk Pauk" -> "Chicken"
                "Sayur" -> "Vegetarian"
                "Dessert" -> "Dessert"
                else -> "Beef"
            }

            try {
                val response: MealSummaryResponse = api.getMealsByCategory(apiCategoryName)

                val mealDetails = mapSummaryToDetail(response.meals, apiCategoryName)

                _mealDetailListState.value = _mealDetailListState.value.copy(
                    list = mealDetails,
                    isLoading = false,
                    error = null // Hapus error lama
                )
            } catch (e: Exception) {
                _mealDetailListState.value = _mealDetailListState.value.copy(
                    error = "Gagal memuat resep untuk $categoryName: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    // --- ⭐ FUNGSI LOGIKA PENCARIAN API ⭐ ---
    fun searchMeals(query: String) {
        if (query.isBlank()) {
            // Ini ditangani oleh updateSearchQuery, kita biarkan kosong di sini.
            return
        }

        viewModelScope.launch {
            _mealDetailListState.value = _mealDetailListState.value.copy(isLoading = true)

            try {
                val response: MealSummaryResponse = api.searchMealsByName(query)
                // MASALAH POTENSIAL: Jika response.meals adalah null (tidak ada hasil),
                // mapSummaryToDetail mengembalikan emptyList(), yang bagus, tetapi...
                val mealDetails = mapSummaryToDetail(response.meals, "Search")

                _mealDetailListState.value = _mealDetailListState.value.copy(
                    list = mealDetails,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _mealDetailListState.value = _mealDetailListState.value.copy(
                    error = "Gagal mencari resep: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    // ⭐ FUNGSI PUBLIK UNTUK DIPANGGIL DARI COMPOSE (MENGGABUNGKAN UPDATE STATE & SEARCH) ⭐
    fun updateSearchQuery(query: String) {
        // 1. Perbarui State yang diamati oleh UI
        _searchQuery.value = query

        // 2. Memicu Logika
        if (query.isBlank()) {
            // Jika kosong, muat ulang list kategori default
            fetchMealsByCategory("Beef")
        } else {
            // Jika ada isinya, lakukan pencarian
            searchMeals(query)
        }
    }


    // --- Fungsi Detail (untuk Detail Screen) ---
    fun fetchMealDetails(mealId: String) {
        viewModelScope.launch {
            _isDetailLoading.value = true
            _currentMealDetail.value = null
            try {
                val response = api.getMealDetails(mealId)
                _currentMealDetail.value = response.meals?.firstOrNull()
            } catch (e: Exception) {
                Log.e("ViewModel", "Gagal memuat detail resep: ${e.message}")
            } finally {
                _isDetailLoading.value = false
            }
        }
    }

    fun clearDetail() {
        _currentMealDetail.value = null
    }
}

data class MealDetailListState(
    val isLoading: Boolean = false,
    val list: List<MealDetail> = emptyList(),
    val error: String? = null
)

class RecipeViewModelFactory(private val api: MealApi) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            return RecipeViewModel(api) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}