package com.example.penjadwalan_sidang.data.model

import com.google.gson.annotations.SerializedName

/**
 * Model User sesuai schema backend (Prisma)
 *
 * Backend Schema:
 * model User {
 *   id        String
 *   email     String
 *   name      String?
 *   role      String
 *   prodi     String?
 *   theses    Thesis[]
 *   createdAt DateTime
 * }
 */
data class User(
    @SerializedName("id")
    val id: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("role")
    val role: String, // "MAHASISWA" atau "DOSEN"

    @SerializedName("prodi")
    val prodi: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null
)

/**
 * Request body untuk update profile
 * POST /api/profile
 */
data class UpdateProfileRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("prodi")
    val prodi: String
)