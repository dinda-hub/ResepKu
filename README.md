# 🍲 Resepku: Paspor Kuliner Digital Anda 🌍

[![GitHub License](https://img.shields.io/badge/License-MIT-4169E1?style=for-the-badge)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android)](https://developer.android.com/)
[![Tech Stack](https://img.shields.io/badge/UI_Framework-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![API](https://img.shields.io/badge/Data_Source-TheMealDB-FF5733?style=for-the-badge&logo=rss)](https://www.themealdb.com/api.php)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin)](https://kotlinlang.org/)

---

## ✨ Pendahuluan

**Resepku** adalah aplikasi Android *cutting-edge* yang dirancang untuk membawa cita rasa global ke ujung jari Anda. Dibangun sepenuhnya menggunakan **Jetpack Compose** dan arsitektur modern (MVVM), Resepku menawarkan pengalaman menjelajah resep yang **cepat, intuitif, dan sangat elegan**.

Aplikasi ini berfungsi sebagai demonstrasi terbaik dalam mengintegrasikan:
* Pustaka Jetpack terbaru.
* Pola desain terstruktur dan teruji.
* Integrasi data *real-time* dari **TheMealDB API**.

> 💡 **Fokus Utama:** Menyajikan resep internasional dengan visual yang menawan dan langkah-langkah yang mudah diikuti.

---

## 📸 Galeri Visual

Desain yang bersih dengan dukungan mode gelap/terang membuat Resepku nyaman digunakan kapan saja.

### Tampilan Portrait

Kami menggunakan tata letak *grid* yang rapi untuk memamerkan antarmuka pengguna (atur `width` sesuai keinginan, misalnya 250-300px).

| Mode Terang | Mode Gelap | Isi Resep |
| :---: | :---: | :---: |
| <img src="![modeTerang](https://github.com/user-attachments/assets/ab745ed2-3e5f-4fd4-a6db-2f7025a288f3)" width="280" alt="Tampilan Beranda Mode Terang"/> | <img src="![modeGelap](https://github.com/user-attachments/assets/0dfa48a3-d203-4615-b014-8d82cd66f6a3)" width="280" alt="Tampilan Beranda Mode Gelap"/> | <img src="![isiResep](https://github.com/user-attachments/assets/c21d7c39-3a3a-4f95-aba8-c5862ffd73e2)" width="280" alt="Tampilan Isi Resep"/> |

### Tampilan Landscape

| Landscape Terang | Landscape Gelap | Landscape Isi Resep |
| :---: | :---: | :---: |
| <img src="![landTerang](https://github.com/user-attachments/assets/6ab1aaca-acef-4d6e-8424-7323110540a7)" width="300" alt="Tampilan Landscape Terang"/> | <img src="![landGelap](https://github.com/user-attachments/assets/ca7863ff-70e8-4918-ae18-2296a58afbda)" width="300" alt="Tampilan Landscape Gelap"/> | <img src="![landIsiResep](https://github.com/user-attachments/assets/1a0104e3-18fc-45db-afed-e051227c965a)" width="300" alt="Tampilan Landscape Isi Resep"/> |

---

### 🌟 Fitur Unggulan

Resepku hadir dengan fungsionalitas inti yang kuat:

* **🔍 Pencarian Cerdas:** Cari resep berdasarkan nama, bahan, atau kata kunci spesifik.
* **🗺️ Filter Global:** Telusuri resep berdasarkan Kategori (misalnya, *Dessert*, *Pasta*) dan Area (misalnya, *Italian*, *Mexican*).
* **📖 Petunjuk Detail:** Tampilan resep yang memisahkan bahan dan langkah-langkah memasak secara jelas.
* **🌙 Dukungan Tema:** Transisi otomatis antara Mode Terang dan Gelap berdasarkan pengaturan sistem.
* **🚀 Performa Tinggi:** UI yang dibangun dengan Compose memastikan *scrolling* dan navigasi yang sangat mulus.

---

### 🏗️ Stack Teknologi & Arsitektur

Proyek ini mewakili standar modern pengembangan Android.

| Kategori | Teknologi | Deskripsi & Manfaat |
| :--- | :--- | :--- |
| **UI Framework** | **Jetpack Compose** | Mengembangkan antarmuka deklaratif, mengurangi *boilerplate* XML. |
| **Arsitektur** | **MVVM / Clean Architecture** | Pemisahan kekhawatiran untuk pengujian dan pemeliharaan kode yang mudah. |
| **API Integration** | **Retrofit & OkHttp** | Klien HTTP yang *typesafe* dan kuat untuk TheMealDB API. |
| **Concurrency** | **Kotlin Coroutines & Flow** | Manajemen *thread* asinkron yang terstruktur dan reaktif. |
| **Dependency Injection** | **Hilt (Dagger)** | Memudahkan pengelolaan dependensi dan *scope lifecycle*. |
| **Image Loading** | **Coil** | Pemuatan gambar yang cepat, ringan, dan berbasis Coroutine. |

---

### 💻 Instalasi & Menjalankan Proyek

### ⚙️ Prerequisites

* **Android Studio** (Versi terbaru disarankan).
* **JDK 17+**
* **Android SDK Build Tools 34.0.0**

### Langkah-Langkah

1.  **Kloning Repositori:**
    ```bash
    git clone [https://github.com/your-username/Resepku-App.git](https://github.com/your-username/Resepku-App.git)
    cd Resepku-App
    ```

2.  **Buka di Android Studio** dan tunggu proses sinkronisasi Gradle selesai.

3.  **Jalankan Aplikasi:** Pilih emulator atau perangkat fisik dan klik tombol Run (▶️).

> 🔑 **Catatan API:** Aplikasi ini menggunakan *endpoint* publik dari TheMealDB dan tidak memerlukan kunci API.

---

## 🤝 Kontribusi

Kami sangat menghargai kontribusi dari komunitas!

1.  *Fork* repositori ini.
2.  Buat *branch* fitur Anda (`git checkout -b feature/nama-fitur-baru`).
3.  *Commit* perubahan Anda (`git commit -m 'feat: menambahkan fitur baru X'`).
4.  *Push* ke *branch* (`git push origin feature/nama-fitur-baru`).
5.  Buka **Pull Request** ke *branch* `main`.

***
> Dibuat dengan semangat kuliner oleh **Dinda Aulia** | &copy; 2026
