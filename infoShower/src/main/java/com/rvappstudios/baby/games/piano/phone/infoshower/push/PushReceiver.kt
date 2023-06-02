package com.rvappstudios.baby.games.piano.phone.infoshower.push

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Patterns
import android.webkit.URLUtil
import com.onesignal.OneSignal
import com.rvappstudios.baby.games.piano.phone.infoshower.common.Data
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object PushReceiver {
    var pushFromScreen: PushFromScreen = PushFromScreen.SPLASH
    var pushIntent: Intent? = null

    inline fun <reified T : Activity> init(application: Application, title : String) {
        OneSignal.setNotificationOpenedHandler {
            val launchUrl: String? = it?.notification?.launchURL
            if (launchUrl.isNullOrBlank() || launchUrl == "null") {
                return@setNotificationOpenedHandler
            }

            val isValidUrl =
                launchUrl.startsWith("www.") ||
                        URLUtil.isHttpUrl(launchUrl) ||
                        URLUtil.isHttpsUrl(launchUrl) ||
                        Patterns.WEB_URL.matcher(launchUrl).matches()
            if (!isValidUrl) {
                return@setNotificationOpenedHandler
            }

            val event = when (pushFromScreen) {
                PushFromScreen.SPLASH -> LoggerEvent.Splash.OpenPush(launchUrl)
                PushFromScreen.INFO -> LoggerEvent.PushInTime(launchUrl, pushFromScreen)
                else -> return@setNotificationOpenedHandler
            }
            Logger.send(event)


            val activityIntent = Intent(application, T::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(Data.CACHE_NAME, launchUrl)
                putExtra(Data.IS_CASHING_NECESSARY_NAME_EXTRA, false)
                putExtra(Data.ERROR_DATA_NAME_EXTRA,title)
            }

            pushIntent = activityIntent
            if (pushFromScreen == PushFromScreen.INFO) {
                application.navigateWithCheckPush()
            }
        }
    }


    fun Context.navigateWithCheckPush(intent: Intent? = null, isCache: Boolean = false, onFinish:(()->Unit)? = null) {
        CoroutineScope(SupervisorJob()).launch(Dispatchers.Main.immediate) {
            if (isCache) delay(1000)
            if (pushIntent != null) {
                startActivity(pushIntent)
                pushIntent = null
                onFinish?.invoke()
            } else {
                intent?.let {
                    startActivity(intent)
                    onFinish?.invoke()
                }
            }
        }
    }

}