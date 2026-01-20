package com.example.lichen.data.repository

import android.net.Uri
import android.util.Log
import com.example.lichen.domain.repository.AuthRepository
import com.example.lichen.ui.auth.model.AuthDeepLinkResult
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class AuthRepositoryImpl(
    private val client: SupabaseClient
) : AuthRepository {

    /* ─────────────────────────────────────────────
     * Auth state stream
     * ───────────────────────────────────────────── */

    override val userId: Flow<UUID?> =
        client.auth.sessionStatus.map { status ->
            when (status) {
                is SessionStatus.Authenticated ->
                    status.session.user?.id?.let(UUID::fromString)
                else -> null
            }
        }

    override fun currentSessionOrNull() =
        client.auth.currentSessionOrNull()

    /* ─────────────────────────────────────────────
     * Actions
     * ───────────────────────────────────────────── */

    override suspend fun signIn(email: String, password: String) {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signUp(email: String, password: String) {
        client.auth.signUpWith(
            Email,
            redirectUrl = "lichenapp://auth/confirm"
        ) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun resetPassword(email: String) {
        client.auth.resetPasswordForEmail(
            email,
            redirectUrl = "lichenapp://auth/reset"
        )
    }

    override suspend fun updatePassword(password: String) {
        client.auth.updateUser {
            this.password = password
        }
    }

    override suspend fun handleDeepLink(uri: Uri): AuthDeepLinkResult? {
        val params = uri.getAllParameters()

        val accessToken = params["access_token"]
        val refreshToken = params["refresh_token"]
        val type = params["type"]
        val expiresIn = params["expires_in"]?.toLongOrNull() ?: 3600L

        if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
            return null
        }

        return try {
            client.auth.importSession(
                UserSession(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    user = null,
                    tokenType = "bearer",
                    expiresIn = expiresIn
                )
            )

            when (type) {
                "recovery" -> AuthDeepLinkResult.ResetPassword
                "signup" -> AuthDeepLinkResult.SignupConfirmed
                "confirm" -> AuthDeepLinkResult.SignupConfirmed
                else -> null
            }

        } catch (e: Exception) {
            Log.e("AuthRepositoryIml", "Failed to import session", e)
            null
        }
    }

    override suspend fun signOut() {
        client.auth.signOut()
    }

    private fun Uri.getAllParameters(): Map<String, String> {
        val params = mutableMapOf<String, String>()

        // Query params (?a=1)
        queryParameterNames.forEach { key ->
            getQueryParameter(key)?.let { params[key] = it }
        }

        // Fragment params (#a=1)
        fragment
            ?.split("&")
            ?.mapNotNull {
                val parts = it.split("=", limit = 2)
                if (parts.size == 2) parts[0] to parts[1] else null
            }
            ?.forEach { (k, v) -> params[k] = v }

        return params
    }

}
