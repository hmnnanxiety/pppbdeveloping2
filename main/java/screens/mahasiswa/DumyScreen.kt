package com.example.penjadwalan_sidang.screens.mahasiswa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

// 🔑 Import BottomNavigationBar di sini karena DummyScreen menggunakannya
import com.example.penjadwalan_sidang.screens.mahasiswa.BottomNavigationBar

@Composable
fun DummyScreen(
    name: String,
    onNavigateToDashboard: () -> Unit,
    onNavigateToForm: () -> Unit
) {
    Scaffold(
        bottomBar = {
            // Index 2 = Profile
            BottomNavigationBar(selectedTab = 2) { tab ->
                when (tab) {
                    0 -> onNavigateToDashboard()
                    1 -> onNavigateToForm()
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF90CAF9))
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(name, color = Color.Black, fontSize = 28.sp)
        }
    }
}