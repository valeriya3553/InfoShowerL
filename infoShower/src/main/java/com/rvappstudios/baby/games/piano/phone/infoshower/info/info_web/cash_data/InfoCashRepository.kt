package com.rvappstudios.baby.games.piano.phone.infoshower.info.info_web.cash_data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri

interface InfoCashRepository {
    suspend fun savePhotoAndGetUri(context : Context, photo: Bitmap): Uri?

    suspend fun saveCash(context: Context,cash : String, key : String)

    suspend fun getCash(context: Context, key : String) : String?
}