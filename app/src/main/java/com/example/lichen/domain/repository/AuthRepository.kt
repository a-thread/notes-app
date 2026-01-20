package com.example.lichen.domain.repository

import android.net.Uri
import com.example.lichen.ui.auth.model.AuthDeepLinkResult
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface AuthRepository {

    // Auth actions
    suspend fun signIn(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    suspend fun resetPassword(email: String)
    suspend fun updatePassword(password: String)
    suspend fun handleDeepLink(uri: Uri): AuthDeepLinkResult?
    suspend fun signOut()

    // Auth state
    val userId: Flow<UUID?>
    fun currentSessionOrNull(): Any?
}


