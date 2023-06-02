package com.rvappstudios.baby.games.piano.phone.infoshower.info.info_web.cash_data

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.ChecksSdkIntAtLeast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class DefaultInfoCashRepository : InfoCashRepository {
    val sharedPraferencesName = "InfoCashRepositorySharedPreferences"

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
    private val isHighLvl = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    override suspend fun savePhotoAndGetUri(context: Context, photo: Bitmap): Uri? {
        return withContext(Dispatchers.IO) {
            val imageMediaStore = if (isHighLvl) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            val name = "JPEG_" + SimpleDateFormat.getDateInstance().format(Date()) + "_"

            val intentData = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$name.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            }

            var myUri: Uri? = null
            try {
                context.contentResolver.insert(imageMediaStore, intentData)?.also { uri ->
                    context.contentResolver.openOutputStream(uri).use { outputStream ->
                        if (!photo.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                            throw IOException()
                        }
                        myUri = uri
                    }
                } ?: throw IOException()
                myUri
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun saveCash(context: Context, cash: String, key : String) {
        withContext(Dispatchers.IO){
            val sP = context.getSharedPreferences(
                sharedPraferencesName,
                Context.MODE_PRIVATE
            )

            val edit = sP.edit()

            edit.putString(key,cash)

            edit.apply()
        }
    }

    override suspend fun getCash(context: Context , key : String): String? {
        return withContext(Dispatchers.IO){
            val sP = context.getSharedPreferences(
                sharedPraferencesName,
                Context.MODE_PRIVATE
            )

            sP.getString(key,null)
        }
    }

}