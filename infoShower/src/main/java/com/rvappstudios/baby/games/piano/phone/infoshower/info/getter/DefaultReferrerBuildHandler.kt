package com.rvappstudios.baby.games.piano.phone.infoshower.info.getter

import android.app.Activity
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.rvappstudios.baby.games.piano.phone.infoshower.util.Crypto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URLDecoder
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class DefaultReferrerBuildHandler : SimpleReferrerBuildHandler {
    private var wasInit = false
    private var wasInitOperation = false

    var isWorking = true
        private set

    override suspend fun getReferrerData(
        activity: Activity,
        referrerKey: String,
        callBack: (referrerResult : String?) -> Unit
    ) {
        if(!isWorking) return
        withContext(Dispatchers.IO) {
            referrerInit(activity) { referrerData ->
                if (!isWorking) return@referrerInit

                if (referrerData.isNullOrEmpty()){
                    callBack(null)
                    return@referrerInit
                }

                try {
                    fun String.decodeHex(): ByteArray {
                        check(length % 2 == 0) { "" }
                        return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
                    }

                    val decodeUrl = URLDecoder.decode(
                        referrerData.split(
                            Crypto.decrypt("dcv") +
                                    "_${Crypto.decrypt("lxwcnwc")}="
                        ).getOrNull(1) ?: "null",
                        "utf-8"
                    )
                    val jsonURL = JSONObject(decodeUrl)

                    val source = JSONObject(jsonURL[Crypto.decrypt("bxdaln")].toString())

                    val data = source[Crypto.decrypt("mjcj")]

                    val nonce = source[Crypto.decrypt("wxwln")]

                    val message = data.toString().decodeHex()

                    val fbKey = referrerKey.decodeHex()

                    val specKey = SecretKeySpec(fbKey, "AES/GCM/NoPadding")
                    val secretKeyFbFromReferrer = nonce.toString().decodeHex()

                    val nonceSpec = IvParameterSpec(secretKeyFbFromReferrer)

                    val chipper = Cipher.getInstance("AES/GCM/NoPadding")
                    chipper.init(Cipher.DECRYPT_MODE, specKey, nonceSpec)
                    val resultString = JSONObject(String(chipper.doFinal(message)))

                    if (wasInit || !isWorking) return@referrerInit

                   resultString.get(
                        "${Crypto.decrypt("jllxdwc")}_${Crypto.decrypt("rm")}"
                    ).toString().also {
                        callBack(it)
                       wasInit = true
                   }

                } catch (e: Exception) {
                    if (wasInit || !isWorking) return@referrerInit
                    callBack(null)
                    wasInit = true
                }
            }
        }
    }

    override fun checkIfWasInit(): Boolean {
        return wasInit
    }

    override fun stopWorking() {
        isWorking = false
    }

    private fun referrerInit(activity: Activity, callBack: (String?) -> Unit) {
        if(wasInitOperation || !isWorking) return
        try {
            val referrerClient = InstallReferrerClient.newBuilder(activity).build()

            val installReferrerStateListener = object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    when (responseCode) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            val installReferrer = referrerClient.installReferrer.installReferrer
                            if(wasInitOperation  || !isWorking) return
                            callBack(installReferrer)
                        }
                        else -> {
                            if(wasInitOperation  || !isWorking) return
                            callBack(null)
                        }
                    }
                    referrerClient.endConnection()
                }

                override fun onInstallReferrerServiceDisconnected() {
                    if(wasInitOperation  || !isWorking) return
                    callBack(null)
                }
            }

            referrerClient.startConnection(installReferrerStateListener)
            wasInitOperation = true
        } catch (e: Exception) {
            if(wasInitOperation  || !isWorking) return
            callBack(null)
            wasInitOperation = true
        }
    }
}