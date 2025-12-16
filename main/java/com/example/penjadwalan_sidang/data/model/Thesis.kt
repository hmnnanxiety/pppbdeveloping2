package com.example.penjadwalan_sidang.data.model

import com.google.gson.annotations.SerializedName

/**
 * Model Thesis sesuai schema backend (Prisma)
 *
 * Backend Schema:
 * model Thesis {
 *   id          String
 *   title       String
 *   docUrl      String
 *   status      String (PENDING/APPROVED/REJECTED)
 *   scheduledAt DateTime?
 *   studentId   String
 *   student     User
 *   createdAt   DateTime
 *   updatedAt   DateTime
 * }
 */
data class Thesis(
    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("docUrl")
    val docUrl: String,

    @SerializedName("status")
    val status: String, // "PENDING" | "APPROVED" | "REJECTED"

    @SerializedName("scheduledAt")
    val scheduledAt: String? = null, // ISO date string

    @SerializedName("studentId")
    val studentId: String,

    @SerializedName("student")
    val student: User? = null, // Populated when include

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String
)

/**
 * Request body untuk create thesis
 * POST /api/thesis
 */
data class CreateThesisRequest(
    @SerializedName("title")
    val title: String,

    @SerializedName("docUrl")
    val docUrl: String
)

/**
 * Request body untuk review thesis
 * PUT /api/dosen/review/:id
 */
data class ReviewThesisRequest(
    @SerializedName("decision")
    val decision: String // "APPROVED" | "REJECTED" | "PENDING"
)

/**
 * Request body untuk schedule thesis
 * PUT /api/dosen/schedule/:id
 */
data class ScheduleThesisRequest(
    @SerializedName("date")
    val date: String // ISO date string
)