package com.example.android_data_transfer.utils

import android.content.Context
import android.provider.ContactsContract
import android.provider.MediaStore
import android.provider.Telephony
import com.example.android_data_transfer.models.Contact
import com.example.android_data_transfer.models.SMS
import java.io.File

object DataFetcher {

    fun fetchContacts(context: Context): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (it.moveToNext()) {
                val name = it.getString(nameIndex) ?: "Unknown"
                val number = it.getString(numberIndex) ?: ""
                contacts.add(Contact(name, number))
            }
        }
        return contacts
    }

    fun fetchSMS(context: Context): List<SMS> {
        val smsList = mutableListOf<SMS>()
        val cursor = context.contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        cursor?.use {
            val addressIndex = it.getColumnIndex(Telephony.Sms.ADDRESS)
            val bodyIndex = it.getColumnIndex(Telephony.Sms.BODY)
            val dateIndex = it.getColumnIndex(Telephony.Sms.DATE)
            val typeIndex = it.getColumnIndex(Telephony.Sms.TYPE)
            while (it.moveToNext()) {
                val address = it.getString(addressIndex) ?: "Unknown"
                val body = it.getString(bodyIndex) ?: ""
                val date = it.getLong(dateIndex)
                val type = it.getInt(typeIndex)
                smsList.add(SMS(address, body, date, type))
            }
        }
        return smsList
    }

    fun fetchPhotos(context: Context): List<File> {
        val photos = mutableListOf<File>()
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )
        cursor?.use {
            val dataIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            while (it.moveToNext()) {
                val path = it.getString(dataIndex)
                if (path != null) {
                    val file = File(path)
                    if (file.exists()) {
                        photos.add(file)
                    }
                }
            }
        }
        return photos
    }

    fun fetchDocuments(context: Context): List<File> {
        val documents = mutableListOf<File>()
        val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ?"
        val selectionArgs = arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_NONE.toString()) // Usually for files that aren't media
        
        // This is a bit simplified, usually documents have specific mime types
        val cursor = context.contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection,
            null,
            null,
            null
        )
        cursor?.use {
            val dataIndex = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            while (it.moveToNext()) {
                val path = it.getString(dataIndex)
                if (path != null) {
                    val file = File(path)
                    if (file.exists() && isDocumentFile(file)) {
                        documents.add(file)
                    }
                }
            }
        }
        return documents
    }

    private fun isDocumentFile(file: File): Boolean {
        val extensions = listOf("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt")
        return extensions.contains(file.extension.lowercase())
    }
}
