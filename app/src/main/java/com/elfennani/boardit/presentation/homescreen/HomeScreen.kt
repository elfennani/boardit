package com.elfennani.boardit.presentation.homescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.UUID

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel()
){
    val folders by homeViewModel.folders.collectAsState()
    val isAddingFolder by homeViewModel.isAddingFolder

    Scaffold {
        Column(Modifier.padding(it)) {
            Text(text = "Greeting Nizar: ${folders.size} folders")
            Button(
                enabled = !isAddingFolder,
                onClick = {
                homeViewModel.addFolder(UUID.randomUUID().toString())
            }) {
                Text(text = "Add Folder")
            }
        }
    }
}