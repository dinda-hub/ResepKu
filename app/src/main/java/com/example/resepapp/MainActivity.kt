package com.example.resepapp

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.resepapp.model.MealDetail
import com.example.resepapp.network.RetrofitClient
import com.example.resepapp.ui.theme.ResepAppTheme
import java.util.Locale
import androidx.compose.runtime.CompositionLocalProvider // Tambahan untuk inject context
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalConfiguration // Tambahan untuk inject context


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ResepAppTheme {
                RecipeAppScreen()
            }
        }
    }
}

// --- FUNGSI UTAMA & NAVIGASI STATE ---
// Fungsi helper non-Composable untuk mengubah Locale
fun setAppLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)

    // Memperbarui konfigurasi resources
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}
@Composable
fun RecipeAppScreen() {
    val baseContext = LocalContext.current
    val viewModelFactory = RecipeViewModelFactory(RetrofitClient.mealApi)
    val viewModel: RecipeViewModel = viewModel(factory = viewModelFactory)

    // ⭐ MENGAMBIL STATE PENCARIAN DARI VIEWMODEL ⭐
    val searchQuery by viewModel.searchQuery.collectAsState()

    var isDarkTheme by rememberSaveable { mutableStateOf(false) }

    // Inisialisasi dengan string dari context dasar (AMAN)
    val selectedCategory = rememberSaveable { mutableStateOf(baseContext.getString(R.string.category_all)) }

    val selectedMealId = rememberSaveable { mutableStateOf<String?>(null) }

    // ⭐ PERBAIKAN 1: Menggunakan state boolean untuk bahasa (untuk memicu recomposition) ⭐
    var languageToggle by rememberSaveable { mutableStateOf(false) }
    val languageCode = if (languageToggle) "en" else "in"

    val toggleLanguage: () -> Unit = {
        val newLang = if (languageToggle) "in" else "en"

        // Panggil fungsi helper non-Composable
        setAppLocale(baseContext, newLang)

        // Memicu recomposition
        languageToggle = !languageToggle
    }

    // ⭐ PERBAIKAN 2: Mendapatkan Context yang Dimodifikasi ⭐
    // Context yang dimodifikasi ini yang akan dibaca oleh stringResource
    val localizedContext = baseContext.createLocaleContext(languageCode)
    val newConfiguration = localizedContext.resources.configuration

    // --- WRAPPER UTAMA: MENGINJECT CONTEXT BARU ---
    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalConfiguration provides newConfiguration
    ) {
        ResepAppTheme(darkTheme = isDarkTheme) {
            val backgroundColor = MaterialTheme.colorScheme.background

            Scaffold(
                modifier = Modifier.background(backgroundColor),
                topBar = {
                    if (selectedMealId.value == null) {
                        TopAppBarContent(
                            selectedCategory = selectedCategory,
                            viewModel = viewModel,
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = { isDarkTheme = !isDarkTheme },
                            onToggleLanguage = toggleLanguage,
                            // ⭐ MENERUSKAN STATE & CALLBACK BARU ⭐
                            searchQuery = searchQuery,
                            onSearchQueryChange = viewModel::updateSearchQuery
                        )
                    }
                }
            ) { padding ->
                if (selectedMealId.value == null) {
                    RecipeListContent(
                        viewModel = viewModel,
                        padding = padding,
                        onRecipeClick = { mealId ->
                            selectedMealId.value = mealId
                            viewModel.fetchMealDetails(mealId)
                        }
                    )
                } else {
                    DetailScreen(
                        viewModel = viewModel,
                        padding = padding,
                        onBackClick = {
                            selectedMealId.value = null
                            viewModel.clearDetail()
                        }
                    )
                }
            }
        }
    }
}

// --- HEADER DAN CHIP KATEGORI (FILTERING) ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarContent(
    selectedCategory: MutableState<String>,
    viewModel: RecipeViewModel,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onToggleLanguage: () -> Unit,
    // ⭐ TAMBAHAN PARAMETER BARU ⭐
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    // Definisi Categories dari Resources
    val categoryResourceIds = listOf(
        R.string.category_all, R.string.category_main_dish, R.string.category_side_dish,
        R.string.category_vegetables, R.string.category_drinks, R.string.category_dessert
    )
    val categories = categoryResourceIds.map { stringResource(id = it) }

    // Ambil kode bahasa saat ini untuk teks tombol EN/ID
    val currentLocaleCode = LocalConfiguration.current.locale.language

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.taste_gallery_title),
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.padding(start = 50.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Tombol Bahasa
                Button(
                    onClick = onToggleLanguage,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        if (currentLocaleCode == "en") "EN" else "ID",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))

                // Tombol Tema
                IconButton(onClick = onToggleTheme) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = stringResource(if (isDarkTheme) R.string.toggle_theme_dark else R.string.toggle_theme_light),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Kotak Pencarian
        OutlinedTextField(
            value = searchQuery, // ✅ Menggunakan state yang diteruskan
            onValueChange = onSearchQueryChange, // ✅ Memanggil fungsi update di ViewModel
            placeholder = { Text(stringResource(R.string.search_hint), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Pilihan Kategori
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEachIndexed { index, category ->
                FilterChip(
                    label = category,
                    isSelected = selectedCategory.value == category,
                    onClick = {
                        val apiCategoryName = when (index) {
                            0 -> "Beef"
                            1 -> "Beef"
                            2 -> "Chicken"
                            3 -> "Vegetarian"
                            4 -> "Dessert"
                            else -> "Beef"
                        }
                        selectedCategory.value = category
                        viewModel.fetchMealsByCategory(apiCategoryName)
                    }
                )
            }
        }
    }
}

@Composable
fun FilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val borderStroke = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline)

    AssistChip(
        onClick = onClick,
        label = {
            Text(
                label,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        ),
        border = borderStroke
    )
}

// --- DAFTAR RESEP (LIST SCREEN) ---

@Composable
fun RecipeListContent(
    viewModel: RecipeViewModel,
    padding: PaddingValues,
    onRecipeClick: (String) -> Unit
) {
    val state = viewModel.mealDetailListState.value
    val onBackgroundColor = MaterialTheme.colorScheme.onBackground

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> CircularProgressIndicator(color = onBackgroundColor)
            state.error != null -> Text("${stringResource(R.string.loading_error)} ${state.error}", color = MaterialTheme.colorScheme.error)
            state.list.isEmpty() -> Text(stringResource(R.string.no_recipes_found), color = onBackgroundColor)
            else -> {
                LazyColumn(contentPadding = PaddingValues(10.dp)) {
                    items(state.list) { meal ->
                        RecipeListItem(
                            meal = meal,
                            onRecipeClick = onRecipeClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeListItem(meal: MealDetail, onRecipeClick: (String) -> Unit) {

    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val surfaceColor = MaterialTheme.colorScheme.surface
    val secondaryTextColor = onSurfaceColor.copy(alpha = 0.7f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onRecipeClick(meal.idMeal) },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = surfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(meal.strMealThumb).crossfade(true).build(),
                contentDescription = meal.strMeal,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    meal.strMeal,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceColor,
                    fontSize = 17.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Kategori & Area
                Text(
                    "${meal.strCategory ?: stringResource(R.string.category_main_dish)}, ${meal.strArea ?: "Global"}",
                    color = secondaryTextColor,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// --- LAYAR DETAIL RESEP ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: RecipeViewModel,
    padding: PaddingValues,
    onBackClick: () -> Unit
) {
    val detail = viewModel.currentMealDetail.value
    val isLoading = viewModel.isDetailLoading.value

    val onBackgroundColor = MaterialTheme.colorScheme.onBackground

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(detail?.strMeal ?: stringResource(R.string.app_name), color = MaterialTheme.colorScheme.onPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_button), tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator(color = onBackgroundColor)
                detail == null -> Text(stringResource(R.string.detail_not_found), color = onBackgroundColor)
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        item {
                            // Gambar Resep
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current).data(detail.strMealThumb).crossfade(true).build(),
                                contentDescription = detail.strMeal,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth().height(250.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Konten Detail
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                Text(detail.strMeal, style = MaterialTheme.typography.headlineLarge, color = onBackgroundColor)
                                Text("${detail.strCategory} | ${detail.strArea}", style = MaterialTheme.typography.titleMedium, color = onBackgroundColor.copy(alpha = 0.7f))
                                Spacer(modifier = Modifier.height(16.dp))

                                // Bahan-bahan
                                Text(stringResource(R.string.detail_ingredients), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(formatIngredients(detail), style = MaterialTheme.typography.bodyLarge, color = onBackgroundColor)
                                Spacer(modifier = Modifier.height(24.dp))

                                // Cara Membuat
                                Text(stringResource(R.string.detail_instructions), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(detail.strInstructions ?: stringResource(R.string.detail_not_found), style = MaterialTheme.typography.bodyLarge, color = onBackgroundColor)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Fungsi Helper untuk memformat Bahan (TIDAK @Composable)
fun formatIngredients(detail: MealDetail): String {
    val ingredients = mutableListOf<String>()

    val measures = listOf(
        detail.strMeasure1, detail.strMeasure2, detail.strMeasure3, detail.strMeasure4, detail.strMeasure5,
        detail.strMeasure6, detail.strMeasure7, detail.strMeasure8, detail.strMeasure9, detail.strMeasure10,
        detail.strMeasure11, detail.strMeasure12, detail.strMeasure13, detail.strMeasure14, detail.strMeasure15,
        detail.strMeasure16, detail.strMeasure17, detail.strMeasure18, detail.strMeasure19, detail.strMeasure20
    )
    val ingredientNames = listOf(
        detail.strIngredient1, detail.strIngredient2, detail.strIngredient3, detail.strIngredient4, detail.strIngredient5,
        detail.strIngredient6, detail.strIngredient7, detail.strIngredient8, detail.strIngredient9, detail.strIngredient10,
        detail.strIngredient11, detail.strIngredient12, detail.strIngredient13, detail.strIngredient14, detail.strIngredient15,
        detail.strIngredient16, detail.strIngredient17, detail.strIngredient18, detail.strIngredient19, detail.strIngredient20
    )

    for (i in 0 until 20) {
        val name = ingredientNames.getOrNull(i)?.trim()
        val measure = measures.getOrNull(i)?.trim()

        if (!name.isNullOrBlank()) {
            val line = if (measure.isNullOrBlank()) name else "$measure $name"
            ingredients.add("• $line")
        }
    }

    // Ini mengembalikan string mentah non-localized.
    return if (ingredients.isEmpty()) "Data bahan tidak lengkap." else ingredients.joinToString("\n")
}