package com.example.penjadwalan_sidang.utils

object Constants {
    // Base URL Backend
    const val BASE_URL = "https://simpensi-api.afif.dev/"

    // Endpoint URLs (Untuk referensi, actual call pakai ApiService)
    const val HEALTH = "health"
    const val PROFILE_ME = "api/profile/me"
    const val PROFILE_UPDATE = "api/profile"
    const val THESIS_CREATE = "api/thesis"
    const val THESIS_MY_ALL = "api/thesis/me/all"
    const val DOSEN_PENDING = "api/dosen/pending"
    const val DOSEN_ALL = "api/dosen/all"
    const val DOSEN_THESIS_DETAIL = "api/dosen/thesis/{id}"
    const val DOSEN_REVIEW = "api/dosen/review/{id}"
    const val DOSEN_SCHEDULE = "api/dosen/schedule/{id}"

    // Google OAuth
    const val GOOGLE_CLIENT_ID = "191478654932-24j21jk2pmtnvk42e7qrbt1c2ufoifnd.apps.googleusercontent.com"

    // SharedPreferences Keys (Sudah ada di SessionManager, ini backup)
    const val PREF_NAME = "user_session"
    const val KEY_TOKEN = "token"
    const val KEY_ROLE = "role"
    const val KEY_IS_LOGIN = "is_login"

    // Roles
    const val ROLE_MAHASISWA = "MAHASISWA"
    const val ROLE_DOSEN = "DOSEN"

    // Status Thesis
    const val STATUS_PENDING = "PENDING"
    const val STATUS_APPROVED = "APPROVED"
    const val STATUS_REJECTED = "REJECTED"
}