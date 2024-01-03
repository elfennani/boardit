package com.elfennani.boardit.data.usecases

import android.util.Log
import com.elfennani.boardit.domain.model.Category
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
private data class GetNotesCount(val userid: String)

class LoadCategoriesUseCase(
    private val supabaseClient: SupabaseClient
) {
    suspend operator fun invoke(): List<Category> {
        val user = checkNotNull(supabaseClient.auth.currentUserOrNull())

        return supabaseClient.postgrest.rpc("get_note_count_by_category", GetNotesCount(user.id))
            .decodeList<Category>()

//        return supabaseClient.from("category").select {
//            filter {
//                Category::userId eq user.id
//            }
//        }.decodeList<Category>()
    }
}