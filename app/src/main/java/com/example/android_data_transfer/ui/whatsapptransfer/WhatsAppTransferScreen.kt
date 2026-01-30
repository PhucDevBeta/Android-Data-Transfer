package com.example.android_data_transfer.ui.whatsapptransfer

import android.graphics.Bitmap
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.android_data_transfer.R
import com.example.android_data_transfer.ui.phonetransfer.SelectedFileItem
import com.example.android_data_transfer.ui.phonetransfer.TransferMethodButton
import com.example.android_data_transfer.ui.theme.TextStyleCommon
import com.example.android_data_transfer.ui.theme.fontLexendBold
import com.example.android_data_transfer.utils.TransferServer
import com.example.android_data_transfer.utils.system.clickableOnce
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsAppTransferScreen() {
    val whatsappTransferViewModel: WhatsappTransferViewmodel = hiltViewModel()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var selectedFiles by remember { mutableStateOf<List<File>>(emptyList()) }
    var shareUrl by remember { mutableStateOf("") }
    var showSheet by remember { mutableStateOf(false) }
    var isQrMode by remember { mutableStateOf(false) }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        val newFiles = uris.mapNotNull { mkUri ->
            var fileName = "wa_file_${System.currentTimeMillis()}"
            context.contentResolver.query(mkUri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) fileName = cursor.getString(nameIndex)
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
                    text = if (isQrMode) "Quét mã để TẢI dữ liệu" else "Truy cập link để TẢI",
                    style = TextStyleCommon(
                        fontSize = 20.sp,
                        fontFamily = fontLexendBold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
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
                                    text = "Đường dẫn tải dữ liệu",
                                    style = TextStyleCommon(
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.4f)
                                    )
                                )
                                Text(
                                    text = shareUrl,
                                    color = Color(0xFF25D366),
                                    style = TextStyleCommon(fontSize = 15.sp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Deco
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF25D366).copy(alpha = 0.1f), Color.Transparent),
                    center = Offset(size.width * 1f, size.height * 0.2f),
                    radius = size.width * 0.6f
                ),
                center = Offset(size.width * 1f, size.height * 0.2f),
                radius = size.width * 0.6f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "WhatsApp Transfer",
                        style = TextStyleCommon(
                            fontSize = 28.sp,
                            fontFamily = fontLexendBold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Di chuyển dữ liệu WhatsApp an toàn",
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
                        .background(Color(0xFF25D366).copy(alpha = 0.1f), CircleShape)
                        .border(1.dp, Color(0xFF25D366).copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_foreground), // Replace with WhatsApp icon if available
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(Color(0xFF25D366))
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Info Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF25D366).copy(alpha = 0.1f))
                    .border(1.dp, Color(0xFF25D366).copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Cách chuyển dữ liệu nhanh nhất",
                        style = TextStyleCommon(
                            fontSize = 18.sp,
                            fontFamily = fontLexendBold,
                            color = Color.White
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Để chuyển tin nhắn và hình ảnh WhatsApp qua ứng dụng này, hãy thực hiện các bước sau trong ứng dụng WhatsApp:",
                        style = TextStyleCommon(
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Steps Section
            StepItem(
                number = "01",
                title = "Mở WhatsApp",
                description = "Đi tới Cài đặt > Trò chuyện > Sao lưu trò chuyện."
            )

            StepItem(
                number = "02",
                title = "Sao lưu dữ liệu",
                description = "Đảm bảo bạn đã sao lưu dữ liệu mới nhất lên Google Drive hoặc bộ nhớ máy."
            )

            StepItem(
                number = "03",
                title = "Tìm thư mục sao lưu",
                description = "Tại máy cũ, tìm thư mục: Android > media > com.whatsapp > WhatsApp. Chọn các tệp trong Databases và Media để gửi đi."
            )

            StepItem(
                number = "04",
                title = "Khôi phục tại máy mới",
                description = "Sau khi nhận file ở máy mới, hãy chuyển chúng vào đúng đường dẫn tương tự máy cũ TRƯỚC KHI đăng nhập WhatsApp để được hỏi khôi phục."
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Action Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF25D366), Color(0xFF128C7E))
                        )
                    )
                    .clickableOnce {
                        try {
                            // SUGGEST the WhatsApp directory for Android 11+
                            // primary:Android/media/com.whatsapp/WhatsApp
                            val waUri = DocumentsContract.buildChildDocumentsUri(
                                "com.android.externalstorage.documents",
                                "primary:Android/media/com.whatsapp/WhatsApp"
                            )
                            filePickerLauncher.launch(arrayOf("*/*"))
                        } catch (e: Exception) {
                            filePickerLauncher.launch(arrayOf("*/*"))
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Bắt đầu chọn file",
                    style = TextStyleCommon(
                        fontSize = 16.sp,
                        fontFamily = fontLexendBold,
                        color = Color.White
                    )
                )
            }

            if (selectedFiles.isNotEmpty()) {
                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tệp WhatsApp đã chọn",
                        style = TextStyleCommon(
                            fontSize = 18.sp,
                            fontFamily = fontLexendBold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Xóa hết",
                        style = TextStyleCommon(fontSize = 13.sp, color = Color(0xFFF87171)),
                        modifier = Modifier.clickableOnce { selectedFiles = emptyList() }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    selectedFiles.forEach { file ->
                        SelectedFileItem(
                            file = file,
                            onRemove = { selectedFiles = selectedFiles - file }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Transfer Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TransferMethodButton(
                        modifier = Modifier.weight(1f),
                        title = "Gửi qua Wifi",
                        subtitle = "Nhanh & Ổn định",
                        iconRes = R.drawable.ic_launcher_foreground,
                        enabled = true,
                        primaryColor = Color(0xFF25D366)
                    ) {
                        TransferServer.startServer(
                            filesToShare = selectedFiles,
                            onStarted = { url ->
                                shareUrl = url
                                isQrMode = false
                                showSheet = true
                            }
                        )
                    }

                    TransferMethodButton(
                        modifier = Modifier.weight(1f),
                        title = "Gửi qua QR",
                        subtitle = "Quét để nhận",
                        iconRes = R.drawable.ic_launcher_foreground,
                        enabled = true,
                        primaryColor = Color(0xFF2DD4BF)
                    ) {
                        TransferServer.startServer(
                            filesToShare = selectedFiles,
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

            Spacer(modifier = Modifier.height(200.dp))
        }
    }
}

@Composable
fun StepItem(
    number: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = number,
            style = TextStyleCommon(
                fontSize = 24.sp,
                fontFamily = fontLexendBold,
                color = Color(0xFF25D366).copy(alpha = 0.5f)
            ),
            modifier = Modifier.width(40.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = TextStyleCommon(
                    fontSize = 16.sp,
                    fontFamily = fontLexendBold,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = TextStyleCommon(
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            )
        }
    }
}
