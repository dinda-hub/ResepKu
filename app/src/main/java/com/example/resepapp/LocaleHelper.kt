package com.example.resepapp

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

// Fungsi ini harus menjadi SATU-SATUNYA definisi
fun Context.createLocaleContext(languageCode: String): Context {
    val locale = Locale(languageCode)
    val config = Configuration()
    config.setLocale(locale)

    // createConfigurationContext mengembalikan Context baru yang disesuaikan
    return this.createConfigurationContext(config)
}