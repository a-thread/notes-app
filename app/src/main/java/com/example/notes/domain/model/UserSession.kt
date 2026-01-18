package com.example.notes.domain.model

import java.util.UUID


data class UserSession(
    val userId: UUID,
    val email: String
)