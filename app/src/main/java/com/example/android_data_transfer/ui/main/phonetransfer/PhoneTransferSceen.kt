package com.example.android_data_transfer.ui.main.phonetransfer

import android.graphics.Bitmap
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.android_data_transfer.R
import com.example.android_data_transfer.ui.theme.TextStyleCommon
import com.example.android_data_transfer.ui.theme.fontLexendBold
import com.example.android_data_transfer.utils.TransferServer
import com.example.android_data_transfer.utils.system.clickableOnce
import java.io.File
import java.io.FileOutputStream

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneTransferSceen(
) {
    val phoneTransferViewModel: PhoneTransferViewModel = hiltViewModel()
    val scrollable = rememberScrollState()
    val context = LocalContext.current
    var selectedFiles by remember { mutableStateOf<List<File>>(emptyList()) }
    var shareUrl by remember { mutableStateOf("") }
    var showSheet by remember { mutableStateOf(false) }
    var isQrMode by remember { mutableStateOf(false) }
    var isReceiveMode by remember { mutableStateOf(false) }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        val newFiles = uris.mapNotNull { mkUri ->
            // Try to get the real file name with extension
            var fileName = "shared_file_${System.currentTimeMillis()}"
            context.contentResolver.query(mkUri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex =
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex)
                    }
                }
            }
            // Create file in cache with original name to preserve extension
            val file = File(context.cacheDir, fileName)
            try {
                context.contentResolver.openInputStream(mkUri)?.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
                file
            } catch (e: Exception) {
                null
            }
        }
        selectedFiles = selectedFiles + newFiles
    }

    val sheetState = rememberModalBottomSheetState()

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showSheet = false
                isReceiveMode = false
                TransferServer.stopServer()
            },
            sheetState = sheetState,
            containerColor = Color(0xFF1E293B),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp, top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isReceiveMode) {
                        "Quét mã để GỬI file"
                    } else {
                        if (isQrMode) "Quét mã để TẢI file" else "Truy cập link để TẢI"
                    },
                    style = TextStyleCommon(
                        fontSize = 20.sp,
                        fontFamily = fontLexendBold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (isReceiveMode) "Sử dụng thiết bị khác để quét" else "Đảm bảo cả hai đang dùng chung Wifi",
                    style = TextStyleCommon(
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (isQrMode && qrBitmap != null) {
                    Box(
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(24.dp))
                            .padding(16.dp)
                    ) {
                        Image(
                            bitmap = qrBitmap!!.asImageBitmap(),
                            contentDescription = "QR Code",
                            modifier = Modifier.size(200.dp)
                        )
                    }
                } else {
                    val clipboardManager = LocalClipboardManager.current
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                            .clickableOnce {
                                clipboardManager.setText(AnnotatedString(shareUrl))
                                Toast.makeText(context, "Đã sao chép link!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Đường dẫn tải file",
                                    style = TextStyleCommon(
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.4f)
                                    )
                                )
                                Text(
                                    text = shareUrl,
                                    color = Color(0xFF38BDF8),
                                    style = TextStyleCommon(fontSize = 15.sp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.White.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.ic_copy),
                                    contentDescription = "Copy Link",
                                    modifier = Modifier.size(18.dp),
                                    colorFilter = ColorFilter.tint(
                                        Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Helper text
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF38BDF8).copy(alpha = 0.1f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_copy), // Info icon would be better
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(Color(0xFF38BDF8))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Nhấn vào để sao chép đường dẫn nhanh",
                        style = TextStyleCommon(
                            fontSize = 12.sp,
                            color = Color(0xFF38BDF8)
                        )
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B),
                        Color(0xFF0F172A)
                    )
                )
            )
            .statusBarsPadding()
    ) {
        // Decorative Blurs (Background "Alpha" elements)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF38BDF8).copy(alpha = 0.15f), Color.Transparent),
                    center = Offset(size.width * 0.9f, size.height * 0.1f),
                    radius = size.width * 0.6f
                ),
                center = Offset(size.width * 0.9f, size.height * 0.1f),
                radius = size.width * 0.6f
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF818CF8).copy(alpha = 0.1f), Color.Transparent),
                    center = Offset(size.width * 0.1f, size.height * 0.8f),
                    radius = size.width * 0.7f
                ),
                center = Offset(size.width * 0.1f, size.height * 0.8f),
                radius = size.width * 0.7f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollable)
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Data Transfer",
                        style = TextStyleCommon(
                            fontSize = 28.sp,
                            fontFamily = fontLexendBold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Gửi và nhận file siêu tốc",
                        style = TextStyleCommon(
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                        .clickableOnce {
                            val options = GmsBarcodeScannerOptions.Builder()
                                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                                .enableAutoZoom()
                                .build()

                            val scanner = GmsBarcodeScanning.getClient(context, options)

                            scanner.startScan()
                                .addOnSuccessListener { barcode ->
                                    val rawValue = barcode.rawValue
                                    if (!rawValue.isNullOrEmpty()) {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(rawValue))
                                            context.startActivity(intent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Link không hợp lệ", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Scan thất bại", Toast.LENGTH_SHORT).show()
                                }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_scan),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Upload Area (Glassmorphism look)
            val hasFiles = selectedFiles.isNotEmpty()
            val dashColor =
                if (hasFiles) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.3f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .drawBehind {
                        drawRoundRect(
                            color = dashColor,
                            style = Stroke(
                                width = 4f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 15f), 0f)
                            ),
                            cornerRadius = CornerRadius(24.dp.toPx())
                        )
                    }
                    .clickableOnce { filePickerLauncher.launch("*/*") },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (hasFiles) Color(0xFF38BDF8).copy(alpha = 0.2f)
                                else Color.White.copy(alpha = 0.1f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_upload),
                            contentDescription = "Upload",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(
                                if (hasFiles) Color(0xFF38BDF8) else Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (hasFiles) "Chọn thêm file (${selectedFiles.size} đã chọn)" else "Chọn file để gửi",
                        style = TextStyleCommon(
                            fontSize = 16.sp,
                            fontFamily = fontLexendBold,
                            color = if (hasFiles) Color(0xFF38BDF8) else Color.White
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (selectedFiles.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Danh sách file",
                        style = TextStyleCommon(
                            fontSize = 16.sp,
                            fontFamily = fontLexendBold,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    )
                    Text(
                        text = "Xóa hết",
                        style = TextStyleCommon(
                            fontSize = 13.sp,
                            color = Color(0xFFF87171)
                        ),
                        modifier = Modifier.clickableOnce { selectedFiles = emptyList() }
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    selectedFiles.forEach { file ->
                        SelectedFileItem(
                            file = file,
                            onRemove = { selectedFiles = selectedFiles - file }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Transfer Methods Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Wifi Button
                TransferMethodButton(
                    modifier = Modifier.weight(1f),
                    title = "Wifi",
                    subtitle = "Gửi qua IP",
                    iconRes = R.drawable.ic_copy,
                    enabled = selectedFiles.isNotEmpty(),
                    primaryColor = Color(0xFF818CF8)
                ) {
                    if (selectedFiles.isNotEmpty()) {
                        TransferServer.startServer(
                            filesToShare = selectedFiles,
                            onFileDownloaded = { downloadedFile ->
                                phoneTransferViewModel.saveHistory(
                                    fileName = downloadedFile.name,
                                    fileSize = downloadedFile.length(),
                                    type = "SEND"
                                )
                            },
                            onStarted = { url ->
                                shareUrl = url
                                isQrMode = false
                                showSheet = true
                            }
                        )
                    }
                }

                // QR Code Button
                TransferMethodButton(
                    modifier = Modifier.weight(1f),
                    title = "Mã QR",
                    subtitle = "Quét cực nhanh",
                    iconRes = R.drawable.ic_copy,
                    enabled = selectedFiles.isNotEmpty(),
                    primaryColor = Color(0xFF2DD4BF)
                ) {
                    if (selectedFiles.isNotEmpty()) {
                        TransferServer.startServer(
                            filesToShare = selectedFiles,
                            onFileDownloaded = { downloadedFile ->
                                phoneTransferViewModel.saveHistory(
                                    fileName = downloadedFile.name,
                                    fileSize = downloadedFile.length(),
                                    type = "SEND"
                                )
                            },
                            onStarted = { url ->
                                shareUrl = url
                                qrBitmap = TransferServer.generateQRCode(url)
                                isQrMode = true
                                showSheet = true
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Receive Section
            Text(
                text = "Nhận dữ liệu",
                style = TextStyleCommon(
                    fontSize = 20.sp,
                    fontFamily = fontLexendBold,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF3B82F6).copy(alpha = 0.2f),
                                Color(0xFF1D4ED8).copy(alpha = 0.1f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF3B82F6).copy(alpha = 0.5f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clickableOnce {
                        val storageDir = File(
                            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                            "Received"
                        )
                        if (storageDir != null && !storageDir.exists()) storageDir.mkdirs()

                        if (storageDir != null) {
                            TransferServer.startReceiveServer(
                                storageDir = storageDir,
                                onFileReceived = { file ->
                                    val uri = TransferServer.saveToDownloads(context, file)
                                    if (uri != null) {
                                        Toast.makeText(
                                            context,
                                            "Đã lưu vào Downloads: ${file.name}",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        // Save to history
                                        // Save to history
                                        phoneTransferViewModel.saveHistory(
                                            fileName = file.name,
                                            fileSize = file.length(),
                                            type = "RECEIVE"
                                        )

                                        file.delete()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Lỗi khi lưu file: ${file.name}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                },
                                onStarted = { url ->
                                    shareUrl = url
                                    qrBitmap = TransferServer.generateQRCode(url)
                                    isQrMode = true
                                    isReceiveMode = true
                                    showSheet = true
                                }
                            )
                        }
                    }
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFF3B82F6).copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_upload),
                            contentDescription = "Receive",
                            modifier = Modifier
                                .size(28.dp)
                                .rotate(180f),
                            colorFilter = ColorFilter.tint(
                                Color(
                                    0xFF60A5FA
                                )
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Tạo Server Nhận",
                            style = TextStyleCommon(
                                fontSize = 18.sp,
                                fontFamily = fontLexendBold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Mở link/QR từ thiết bị khác để nhận file",
                            style = TextStyleCommon(
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Image(
                        painter = painterResource(R.drawable.ic_copy), // Arrow icon would be better if available
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(180f),
                        colorFilter = ColorFilter.tint(
                            Color.White.copy(
                                alpha = 0.3f
                            )
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

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
            // Using first letter of category as a placeholder icon since we don't have enough icons
            Text(
                text = category.name.take(1),
                style = TextStyleCommon(
                    fontSize = 18.sp,
                    fontFamily = fontLexendBold,
                    color = category.color
                )
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
                    style = TextStyleCommon(fontSize = 11.sp, color = Color.White.copy(alpha = 0.4f))
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

data class FileCategory(
    val name: String,
    val color: Color
)

fun getFileCategory(fileName: String): FileCategory {
    val ext = fileName.substringAfterLast(".", "").lowercase()
    return when (ext) {
        "jpg", "jpeg", "png", "gif", "webp" -> FileCategory("Image", Color(0xFFFB923C))
        "mp4", "mkv", "mov", "avi" -> FileCategory("Video", Color(0xFFF87171))
        "mp3", "wav", "ogg", "m4a" -> FileCategory("Audio", Color(0xFF818CF8))
        "pdf" -> FileCategory("PDF", Color(0xFFEF4444))
        "doc", "docx" -> FileCategory("Word", Color(0xFF3B82F6))
        "xls", "xlsx" -> FileCategory("Excel", Color(0xFF22C55E))
        "ppt", "pptx" -> FileCategory("Powerpoint", Color(0xFFF97316))
        "zip", "rar", "7z" -> FileCategory("Archive", Color(0xFFA855F7))
        "txt" -> FileCategory("Text", Color(0xFF94A3B8))
        else -> FileCategory("File", Color(0xFF94A3B8))
    }
}

fun formatFileSize(size: Long): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
    return java.text.DecimalFormat("#,##0.#").format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
}

@Composable
fun TransferMethodButton(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    iconRes: Int,
    enabled: Boolean,
    primaryColor: Color,
    onClick: () -> Unit,
) {
    val alpha = if (enabled) 1f else 0.4f
    Column(
        modifier = modifier
            .graphicsLayer(alpha = alpha)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(
                1.dp,
                if (enabled) primaryColor.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(20.dp)
            )
            .clickableOnce(enabled = enabled) { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(primaryColor.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = title,
                modifier = Modifier.size(22.dp),
                colorFilter = ColorFilter.tint(primaryColor)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            style = TextStyleCommon(
                fontSize = 16.sp,
                fontFamily = fontLexendBold,
                color = Color.White
            )
        )
        Text(
            text = subtitle,
            style = TextStyleCommon(
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.5f)
            )
        )
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
@Preview(showSystemUi = true)
fun PhoneTransferSceenP(
) {
    PhoneTransferSceen()
}