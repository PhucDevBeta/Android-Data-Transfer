package com.example.android_data_transfer.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respondFile
import io.ktor.server.response.respondText
import io.ktor.server.response.header
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.application.call
import java.io.File
import java.net.NetworkInterface
import java.net.InetAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLConnection
import java.text.DecimalFormat

object TransferServer {
    private var server: ApplicationEngine? = null

    /**
     *  MODE 1: SEND FILE (Hosting files for others to download)
     */
    fun startServer(filesToShare: List<File>, port: Int = 8080, onFileDownloaded: (File) -> Unit = {}, onStarted: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            stopServer()
            val ip = getLocalIpAddress() ?: "127.0.0.1"
            val fileUrl = "http://${ip}:$port"

            try {
                server = embeddedServer(CIO, port = port) {
                    routing {
                        get("/") {
                            val html = buildDownloadHtml(filesToShare)
                            call.respondText(html, ContentType.Text.Html)
                        }
                        get("/download/{filename}") {
                            val filename = call.parameters["filename"]
                            val file = filesToShare.find { it.name == filename }
                            if (file != null) {
                                call.response.header(
                                    HttpHeaders.ContentDisposition,
                                    ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, file.name).toString()
                                )
                                call.respondFile(file)
                                // Notify that file was downloaded
                                onFileDownloaded(file)
                            } else {
                                call.respondText("File not found", status = HttpStatusCode.NotFound)
                            }
                        }
                    }
                }.start(wait = false)

                CoroutineScope(Dispatchers.Main).launch {
                    onStarted(fileUrl)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    /**
     * MODE 2: RECEIVE FILE (Hosting a page for others to upload files)
     */
    fun startReceiveServer(storageDir: File, port: Int = 8080, onFileReceived: (File) -> Unit, onStarted: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            stopServer()
            val ip = getLocalIpAddress() ?: "127.0.0.1"
            val serverUrl = "http://$ip:$port"

            try {
                server = embeddedServer(CIO, port = port) {
                    routing {
                        // 1. Landing Page: Upload Form
                        get("/") {
                            val html = buildUploadHtml()
                            call.respondText(html, ContentType.Text.Html)
                        }

                        // 2. Handle File Upload
                        post("/upload") {
                            val multipart = call.receiveMultipart()
                            var receivedFile: File? = null

                            multipart.forEachPart { part ->
                                if (part is PartData.FileItem) {
                                    val fileName = part.originalFileName ?: "received_file_${System.currentTimeMillis()}"
                                    // Sanitize filename to simple characters just in case
                                    val safeFileName = File(fileName).name
                                    
                                    val file = File(storageDir, safeFileName)
                                    
                                    // Ensure unique filename
                                    var finalFile = file
                                    var counter = 1
                                    while (finalFile.exists()) {
                                        val nameWithoutExt = file.nameWithoutExtension
                                        val ext = file.extension
                                        finalFile = File(storageDir, "$nameWithoutExt($counter).$ext")
                                        counter++
                                    }

                                    part.streamProvider().use { input ->
                                        finalFile.outputStream().use { output ->
                                            input.copyTo(output)
                                        }
                                    }
                                    receivedFile = finalFile
                                }
                                part.dispose()
                            }

                            if (receivedFile != null) {
                                // Notify on Main format if needed, but callback handles it usually
                                CoroutineScope(Dispatchers.Main).launch {
                                    onFileReceived(receivedFile!!)
                                }
                                call.respondText(buildSuccessHtml(receivedFile!!.name), ContentType.Text.Html)
                            } else {
                                call.respondText("File upload failed", status = HttpStatusCode.BadRequest)
                            }
                        }
                    }
                }.start(wait = false)

                CoroutineScope(Dispatchers.Main).launch {
                    onStarted(serverUrl)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopServer() {
        try {
            server?.stop(1000, 2000)
            server = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    /**
     * HTML GENERATORS
     */
    private fun buildDownloadHtml(files: List<File>): String {
        val filesHtml = files.joinToString("") { file ->
            val fileSize = formatFileSize(file.length())
            """
                <div class="file-item">
                    <div class="file-info">
                        <span class="file-name">${file.name}</span>
                        <span class="file-size">$fileSize</span>
                    </div>
                    <a href="/download/${file.name}" class="btn-mini">Download</a>
                </div>
            """.trimIndent()
        }

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Download Files</title>
                ${getCommonCss()}
            </head>
            <body>
                <div class="card">
                    <div class="icon">⬇️</div>
                    <h2>Shared Files</h2>
                    <div class="file-list">
                        $filesHtml
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    private fun buildUploadHtml(): String {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Send Files to Android</title>
                ${getCommonCss()}
            </head>
            <body>
                <div class="card">
                    <div class="icon">⬆️</div>
                    <h2>Send to Android</h2>
                    <form action="/upload" method="post" enctype="multipart/form-data">
                        <label for="file-upload" class="custom-file-upload">
                            Choose Files
                        </label>
                        <input id="file-upload" type="file" name="file" required multiple onchange="updateFileName(this)"/>
                        <div id="file-list-preview" class="file-list"></div>
                        <button type="submit" class="btn" style="margin-top: 1rem;">Send All Files</button>
                    </form>
                </div>
                <script>
                    function updateFileName(input) {
                        var preview = document.getElementById("file-list-preview");
                        preview.innerHTML = "";
                        for (var i = 0; i < input.files.length; i++) {
                            var div = document.createElement("div");
                            div.className = "file-item-preview";
                            div.innerText = input.files[i].name;
                            preview.appendChild(div);
                        }
                    }
                </script>
            </body>
            </html>
        """.trimIndent()
    }

    private fun buildSuccessHtml(fileName: String): String {
         return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Success</title>
                ${getCommonCss()}
            </head>
            <body>
                <div class="card">
                    <div class="icon">✅</div>
                    <h2>Received Successfully!</h2>
                    <h1 style="color: #28a745;">$fileName</h1>
                    <p style="color: #666; margin-bottom: 2rem;">The file has been saved to the Android device.</p>
                    <a href="/" class="btn">Send Another File</a>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    private fun getCommonCss(): String {
        return """
            <style>
                body {
                    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    min-height: 100vh;
                    margin: 0;
                    color: #333;
                    padding: 20px;
                }
                .card {
                    background: white;
                    padding: 2rem;
                    border-radius: 20px;
                    box-shadow: 0 10px 25px rgba(0,0,0,0.1);
                    text-align: center;
                    width: 100%;
                    max-width: 450px;
                }
                .icon { font-size: 64px; margin-bottom: 1rem; }
                h1 { font-size: 1.1rem; word-break: break-word; margin: 10px 0; }
                h2 { margin-top: 0; color: #444; margin-bottom: 1.5rem; }
                .file-list {
                    text-align: left;
                    max-height: 300px;
                    overflow-y: auto;
                    margin-bottom: 1.5rem;
                    border: 1px solid #eee;
                    border-radius: 10px;
                    padding: 10px;
                }
                .file-item {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    padding: 10px;
                    border-bottom: 1px solid #f0f0f0;
                }
                .file-item:last-child { border-bottom: none; }
                .file-info { display: flex; flex-direction: column; flex: 1; min-width: 0; }
                .file-name { font-weight: 500; font-size: 0.9rem; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
                .file-size { font-size: 0.8rem; color: #888; }
                .file-item-preview { font-size: 0.85rem; padding: 5px 0; border-bottom: 1px dashed #eee; }
                .btn {
                    display: inline-block;
                    background: #007bff;
                    color: white;
                    text-decoration: none;
                    padding: 12px 30px;
                    border-radius: 50px;
                    font-weight: bold;
                    border: none;
                    cursor: pointer;
                    font-size: 1rem;
                    box-shadow: 0 4px 6px rgba(0,123,255,0.3);
                    transition: all 0.2s;
                    width: 100%;
                    box-sizing: border-box;
                }
                .btn-mini {
                    background: #28a745;
                    color: white;
                    text-decoration: none;
                    padding: 5px 12px;
                    border-radius: 5px;
                    font-size: 0.8rem;
                    font-weight: 600;
                }
                .btn:hover { transform: translateY(-2px); box-shadow: 0 6px 8px rgba(0,123,255,0.4); }
                input[type="file"] { display: none; }
                .custom-file-upload {
                    border: 2px dashed #ccc;
                    display: inline-block;
                    padding: 20px;
                    cursor: pointer;
                    border-radius: 10px;
                    margin-bottom: 15px;
                    width: 100%;
                    box-sizing: border-box;
                    color: #555;
                    font-weight: 500;
                    background: #fafafa;
                }
                .custom-file-upload:hover { border-color: #007bff; color: #007bff; background: #f0f8ff; }
            </style>
        """
    }

    private fun formatFileSize(size: Long): String {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
    }

    private fun getLocalIpAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address is InetAddress && address.hostAddress.indexOf(':') < 0) {
                        return address.hostAddress
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

    fun generateQRCode(text: String): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveToDownloads(context: Context, file: File): Uri? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
            // Try to detect MimeType, fallback to binary
            val mime = URLConnection.guessContentTypeFromName(file.name) ?: "application/octet-stream"
            put(MediaStore.MediaColumns.MIME_TYPE, mime)
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let { targetUri ->
            try {
                resolver.openOutputStream(targetUri)?.use { outputStream ->
                    file.inputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(targetUri, contentValues, null, null)
                }
                return targetUri
            } catch (e: Exception) {
                e.printStackTrace()
                // Clean up empty file if failed
                resolver.delete(targetUri, null, null)
            }
        }
        return null
    }
}
