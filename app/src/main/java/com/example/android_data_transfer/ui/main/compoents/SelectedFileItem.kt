package com.example.android_data_transfer.ui.main.compoents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android_data_transfer.R
import com.example.android_data_transfer.models.getFileCategory
import com.example.android_data_transfer.ui.theme.TextStyleCommon
import com.example.android_data_transfer.utils.formatFileSize
import com.example.android_data_transfer.utils.system.clickableOnce
import java.io.File

@Composable
fun SelectedFileItem(
    file: File,
    onRemove: () -> Unit
) {
    val category = getFileCategory(file.name)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(category.color.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(category.iconRes),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(category.color)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = file.name,
                style = TextStyleCommon(fontSize = 14.sp, color = Color.White),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row {
                Text(
                    text = category.name,
                    style = TextStyleCommon(fontSize = 11.sp, color = category.color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatFileSize(file.length()),
                    style = TextStyleCommon(
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                )
            }
        }

        Image(
            painter = painterResource(R.drawable.ic_copy), // Using ic_copy as a remove icon placeholder
            contentDescription = "Remove",
            modifier = Modifier
                .size(20.dp)
                .rotate(45f) // Rotate it to look like an X
                .clickableOnce { onRemove() },
            colorFilter = ColorFilter.tint(Color.White.copy(alpha = 0.3f))
        )
    }
}