package com.athread.lichen.data.remote.supabase

import com.athread.lichen.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object SupabaseClientProvider {

    val client: SupabaseClient by lazy {
        val url = BuildConfig.SUPABASE_URL
        val key = BuildConfig.SUPABASE_ANON_KEY

        require(url.isNotBlank()) {
            "SUPABASE_URL is missing. Define it in gradle.properties."
        }

        require(key.isNotBlank()) {
            "SUPABASE_ANON_KEY is missing. Define it in gradle.properties."
        }

        createSupabaseClient(
            supabaseUrl = url,
            supabaseKey = key
        ) {
            install(Auth)
            install(Postgrest)
            install(Realtime)
        }
    }
}

