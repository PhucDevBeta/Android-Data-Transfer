package com.example.android_data_transfer.ui.video_transfer

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.android_data_transfer.R
import com.example.android_data_transfer.component.topbar_common.TopBarCommon
import com.example.android_data_transfer.models.local.entity.TransferHistory
import com.example.android_data_transfer.ui.phonetransfer.SelectedFileItem
import com.example.android_data_transfer.ui.phonetransfer.TransferMethodButton
import com.example.android_data_transfer.ui.theme.TextStyleCommon
import com.example.android_data_transfer.ui.theme.fontLexendBold
import com.example.android_data_transfer.utils.TransferServer
import com.example.android_data_transfer.utils.system.clickableOnce
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import com.example.android_data_transfer.ui.phonetransfer.getFileCategory

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoTransferScreen(
    videoTransferViewModel: VideoTransferViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Gửi", "Nhận", "Lịch sử")

    var selectedFiles by remember { mutableStateOf<List<File>>(emptyList()) }
    var shareUrl by remember { mutableStateOf("") }
    var showSheet by remember { mutableStateOf(false) }
    var isQrMode by remember { mutableStateOf(false) }
    var isReceiveMode by remember { mutableStateOf(false) }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    val historyEntries by videoTransferViewModel.historyEntries.collectAsState(initial = emptyList())

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        val newFiles = uris.mapNotNull { mkUri ->
            var fileName = "video_${System.currentTimeMillis()}"
            context.contentResolver.query(mkUri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex)
                    }
                }
            }
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
                    text = if (isReceiveMode) "Quét mã để GỬI video" else {
                        if (isQrMode) "Quét mã để TẢI video" else "Truy cập link để TẢI"
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Đường dẫn tải video",
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
                                    colorFilter = ColorFilter.tint(Color.White)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF0F172A))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBarCommon(
                title = stringResource(R.string.videos_transfer),
                logoRes = R.drawable.ic_premium
            )

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF38BDF8),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFF38BDF8)
                    )
                },
                divider = {
                    Divider(color = Color.White.copy(alpha = 0.1f))
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = TextStyleCommon(
                                    fontSize = 14.sp,
                                    fontFamily = fontLexendBold,
                                    color = if (selectedTab == index) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.6f)
                                )
                            )
                        }
                    )
                }
            }

            Crossfade(targetState = selectedTab, modifier = Modifier.weight(1f)) { tab ->
                when (tab) {
                    0 -> SendTabContent(
                        selectedFiles = selectedFiles,
                        onSelectFiles = { filePickerLauncher.launch("video/*") },
                        onRemoveFile = { file -> selectedFiles = selectedFiles - file },
                        onClearAll = { selectedFiles = emptyList() },
                        onStartTransfer = { mode ->
                            if (selectedFiles.isNotEmpty()) {
                                TransferServer.startServer(
                                    filesToShare = selectedFiles,
                                    onFileDownloaded = { downloadedFile ->
                                        videoTransferViewModel.saveHistory(
                                            fileName = downloadedFile.name,
                                            fileSize = downloadedFile.length(),
                                            type = "SEND"
                                        )
                                    },
                                    onStarted = { url ->
                                        shareUrl = url
                                        if (mode == "QR") {
                                            qrBitmap = TransferServer.generateQRCode(url)
                                            isQrMode = true
                                        } else {
                                            isQrMode = false
                                        }
                                        showSheet = true
                                    }
                                )
                            }
                        }
                    )
                    1 -> ReceiveTabContent(
                        onStartReceive = {
                            val storageDir = File(
                                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                                "Received_Videos"
                            )
                            if (!storageDir.exists()) storageDir.mkdirs()

                            TransferServer.startReceiveServer(
                                storageDir = storageDir,
                                onFileReceived = { file ->
                                    val uri = TransferServer.saveToDownloads(context, file)
                                    if (uri != null) {
                                        videoTransferViewModel.saveHistory(
                                            fileName = file.name,
                                            fileSize = file.length(),
                                            type = "RECEIVE"
                                        )
                                        file.delete()
                                        Toast.makeText(context, "Đã nhận: ${file.name}", Toast.LENGTH_SHORT).show()
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
                    )
                    2 -> HistoryTabContent(historyEntries)
                }
            }
        }
    }
}

@Composable
fun SendTabContent(
    selectedFiles: List<File>,
    onSelectFiles: () -> Unit,
    onRemoveFile: (File) -> Unit,
    onClearAll: () -> Unit,
    onStartTransfer: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Upload Area
        val hasFiles = selectedFiles.isNotEmpty()
        val dashColor = if (hasFiles) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.3f)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
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
                .clickableOnce { onSelectFiles() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            if (hasFiles) Color(0xFF38BDF8).copy(alpha = 0.2f) else Color.White.copy(
                                alpha = 0.1f
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_upload),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        colorFilter = ColorFilter.tint(if (hasFiles) Color(0xFF38BDF8) else Color.White)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (hasFiles) "Đã chọn ${selectedFiles.size} video" else "Chọn video để gửi",
                    style = TextStyleCommon(
                        fontSize = 16.sp,
                        fontFamily = fontLexendBold,
                        color = if (hasFiles) Color(0xFF38BDF8) else Color.White
                    )
                )
            }
        }

        if (hasFiles) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Video đã chọn",
                    style = TextStyleCommon(fontSize = 16.sp, fontFamily = fontLexendBold, color = Color.White)
                )
                Text(
                    text = "Xoá tất cả",
                    style = TextStyleCommon(fontSize = 13.sp, color = Color(0xFFF87171)),
                    modifier = Modifier.clickableOnce { onClearAll() }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                selectedFiles.forEach { file ->
                    SelectedFileItem(file = file, onRemove = { onRemoveFile(file) })
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TransferMethodButton(
                modifier = Modifier.weight(1f),
                title = "Wifi",
                subtitle = "Gửi qua link",
                iconRes = R.drawable.ic_copy,
                enabled = hasFiles,
                primaryColor = Color(0xFF818CF8),
                onClick = { onStartTransfer("WIFI") }
            )
            TransferMethodButton(
                modifier = Modifier.weight(1f),
                title = "Mã QR",
                subtitle = "Quét để tải",
                iconRes = R.drawable.ic_copy,
                enabled = hasFiles,
                primaryColor = Color(0xFF2DD4BF),
                onClick = { onStartTransfer("QR") }
            )
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun ReceiveTabContent(onStartReceive: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
                        colors = listOf(Color(0xFF3B82F6).copy(alpha = 0.5f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .clickableOnce { onStartReceive() }
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color(0xFF3B82F6).copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_upload),
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .rotate(180f),
                        colorFilter = ColorFilter.tint(Color(0xFF60A5FA))
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Tạo Server Nhận Video",
                    style = TextStyleCommon(fontSize = 20.sp, fontFamily = fontLexendBold, color = Color.White)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Cho phép thiết bị khác gửi video đến máy này",
                    style = TextStyleCommon(fontSize = 14.sp, color = Color.White.copy(alpha = 0.6f)),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun HistoryTabContent(history: List<TransferHistory>) {
    if (history.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Chưa có lịch sử chuyển video",
                style = TextStyleCommon(fontSize = 15.sp, color = Color.White.copy(alpha = 0.4f))
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(history) { item ->
                HistoryItem(item)
            }
        }
    }
}

@Composable
fun HistoryItem(history: TransferHistory) {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val dateStr = sdf.format(Date(history.timestamp))
    val category = getFileCategory(history.fileName)
    
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
                .size(44.dp)
                .background(
                    category.color.copy(alpha = 0.1f),
                    RoundedCornerShape(12.dp)
                ),
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
                text = history.fileName,
                style = TextStyleCommon(fontSize = 14.sp, color = Color.White),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.ic_upload),
                    contentDescription = null,
                    modifier = Modifier.size(10.dp).rotate(if (history.type == "SEND") 0f else 180f),
                    colorFilter = ColorFilter.tint(if (history.type == "SEND") Color(0xFF38BDF8) else Color(0xFF2DD4BF))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (history.type == "SEND") "Đã gửi" else "Đã nhận",
                    style = TextStyleCommon(
                        fontSize = 11.sp, 
                        color = if (history.type == "SEND") Color(0xFF38BDF8) else Color(0xFF2DD4BF)
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = dateStr,
                    style = TextStyleCommon(fontSize = 11.sp, color = Color.White.copy(alpha = 0.4f))
                )
            }
        }
    }
}
