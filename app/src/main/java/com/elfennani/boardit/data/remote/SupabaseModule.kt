package com.elfennani.boardit.data.remote

import android.content.Context
import android.util.Log
import com.elfennani.boardit.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SupabaseModule {
    @Provides
    @Singleton
    fun provideSupabaseClient(@ApplicationContext context: Context): SupabaseClient {
        val SUPABASE_URL = context.getString(R.string.SUPABASE_URL)
        val SUPABASE_ANON_KEY = context.getString(R.string.SUPABASE_ANON_KEY)
        val SUPABASE_SERVER_CLIENT_ID = context.getString(R.string.SUPABASE_SERVER_CLIENT_ID)

        Log.d("TESTINGKEYS", "$SUPABASE_URL \n $SUPABASE_ANON_KEY \n $SUPABASE_SERVER_CLIENT_ID")

        return createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(ComposeAuth){
                googleNativeLogin(SUPABASE_SERVER_CLIENT_ID)
            }
        }
    }
}