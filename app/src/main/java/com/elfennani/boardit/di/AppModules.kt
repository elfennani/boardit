package com.elfennani.boardit.di

import android.content.Context
import android.os.Build
import androidx.compose.ui.res.stringResource
import com.elfennani.boardit.R
import com.elfennani.boardit.data.usecases.AddFolderUseCase
import com.elfennani.boardit.data.usecases.GetFoldersUseCase
import com.elfennani.boardit.domain.dao.FolderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModules {
    @Provides
    fun provideGetFoldersUseCase(folderDao: FolderDao): GetFoldersUseCase {
        return GetFoldersUseCase(folderDao)
    }

    @Provides
    fun provideAddFolderUseCase(folderDao: FolderDao): AddFolderUseCase {
        return AddFolderUseCase(folderDao)
    }

    @Provides
    @Singleton
    fun provideSupabaseClient(@ApplicationContext context: Context): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = context.getString(R.string.SUPABASE_URL),
            supabaseKey = context.getString(R.string.SUPABASE_ANON_KEY)
        ) {
            install(Auth)
            install(Postgrest)
            install(ComposeAuth){
                googleNativeLogin(context.getString(R.string.SUPABASE_SERVER_CLIENT_ID))
            }
        }
    }
}