package com.example.android_data_transfer.ui.main.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.android_data_transfer.R
import com.example.android_data_transfer.models.local.entity.TransferHistory
import com.example.android_data_transfer.ui.theme.TextStyleCommon

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val histories by viewModel.historyList.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Lịch sử truyền file", style = TextStyleCommon(fontSize = 24.sp, color = Color.White))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                Text("xin chào ")
            }
            items(histories) { item ->
                HistoryItem(item)
            }
        }
    }
}

@Composable
fun HistoryItem(item: TransferHistory) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val icon = if (item.type == "SEND") R.drawable.ic_upload else R.drawable.ic_launcher_foreground
         Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
             Text(text = item.fileName, color = Color.White, style = TextStyleCommon(fontSize = 16.sp))
             Text(text = "${item.fileSize} bytes", color = Color.Gray, style = TextStyleCommon(fontSize = 12.sp))
         }
    }
}