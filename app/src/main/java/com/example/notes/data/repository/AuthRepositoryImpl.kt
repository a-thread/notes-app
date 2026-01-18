package com.example.notes.data.repository

import com.example.notes.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
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
                    status.session.user?.id.let(UUID::fromString)

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
            "notesapp://auth/confirm"
        ) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun resetPassword(email: String) {
        client.auth.resetPasswordForEmail(email, "notesapp://auth/reset")
    }

    override suspend fun signOut() {
        client.auth.signOut()
    }

}
