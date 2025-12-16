package com.example.penjadwalan_sidang.screens.dosen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlaceholderDosenScreen(title: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBack) { Text("Kembali") }
    }
}
