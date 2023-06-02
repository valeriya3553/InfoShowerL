package com.rvappstudios.baby.games.piano.phone.infoshower.push

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.annotation.ChecksSdkIntAtLeast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rvappstudios.baby.games.piano.phone.infoshower.util.Crypto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ObserveState {
    START, IDLE, FINISH
}

data class TimingValues(var start: Long = -1L, var end: Long = -1L)

sealed class LoggerEvent(
    val name: String,
    val isSingle: Boolean = true,
    val observeState: ObserveState = ObserveState.IDLE,
    vararg val params: Pair<String, Any?>
) {
    data class PushInTime(val url: String, val pushFromScreen: PushFromScreen) :
        LoggerEvent(
            "${Crypto.decrypt("jomb")}_${Crypto.decrypt("ch")}_${Crypto.decrypt("ncgy")}",
            isSingle = false,
            params = arrayOf("url" to url, "${Crypto.decrypt("jomb")}_${Crypto.decrypt("zlig")}_${Crypto.decrypt("mwlyy")}" to pushFromScreen.value)
        )

    data class Permission(val permission: String, val granted: Boolean?) :
        LoggerEvent("${Crypto.decrypt("jylgcmmcih")}_${permission}", params = arrayOf(Crypto.decrypt("aluhnyx") to granted))

    sealed class Splash {
        object OpenApp : LoggerEvent("${Crypto.decrypt("ijyh")}_${Crypto.decrypt("ujj")}", observeState = ObserveState.START)
        data class OpenPush(val url: String) :
            LoggerEvent("${Crypto.decrypt("ijyh")}_${Crypto.decrypt("jomb")}", isSingle = false, params = arrayOf("url" to url))


        data class CheckData(val dataParams: Map<String, Any?>) :
            LoggerEvent("${Crypto.decrypt("wbywe")}_${Crypto.decrypt("xunu")}", params = dataParams.toVarargArray())

        data class BuildResult(val url: String) : LoggerEvent(
            "${Crypto.decrypt("vocfx")}_${Crypto.decrypt("lymofn")}",
            observeState = ObserveState.FINISH,
            params = arrayOf("url" to url)
        )

        object IsCache : LoggerEvent("${Crypto.decrypt("cm")}_${Crypto.decrypt("wuwby")}")
    }

    sealed class Info {
        object OpenInfo : LoggerEvent("${Crypto.decrypt("ijyh")}_${Crypto.decrypt("qyv")}", observeState = ObserveState.START)

        data class FinishWeb(val url: String, val userAgent: String) :
            LoggerEvent(
                "${Crypto.decrypt("zchcmb")}_${Crypto.decrypt("qyv")}",
                observeState = ObserveState.FINISH,
                params = arrayOf("url" to url, "${Crypto.decrypt("omyl")}_${Crypto.decrypt("uayhn")}" to userAgent)
            )

    }

    sealed class Error {
        object UsedErrorStrategy : LoggerEvent("${Crypto.decrypt("ijyh")}_${Crypto.decrypt("jfoa")}\n")
    }
}

fun <K, V : Any> Map<K, V?>.toVarargArray() = map { it.key to it.value }.toTypedArray()
interface FirebaseAnalyticsLogger {
    fun send(event: LoggerEvent)
}

interface FirebaseCrashlyticsLogger {
    fun record(throwable: Throwable)
}

object Logger : FirebaseAnalyticsLogger, FirebaseCrashlyticsLogger {
    private val timingValues = TimingValues()

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var firebaseCrashlytics: FirebaseCrashlytics
    private lateinit var sharedPreferences: SharedPreferences
    private val sentEvents = mutableSetOf<String>()

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N)
    private val deviceSdkVersion = Build.VERSION.SDK_INT
    private val device = Build.BRAND + " " + Build.MODEL + " " + Build.MANUFACTURER
    var versionCode: Int? = null
    var appsFlyerUserId: String? = null

    fun init(application: Application,
             firebaseAnalytics: FirebaseAnalytics,
             firebaseCrashlytics: FirebaseCrashlytics) {

        Logger.firebaseAnalytics = firebaseAnalytics
        Logger.firebaseCrashlytics = firebaseCrashlytics

        sharedPreferences =
            application.getSharedPreferences("logger", Context.MODE_PRIVATE) ?: return

        val stringSet = sharedPreferences.getStringSet("${Crypto.decrypt("myhn")}_${Crypto.decrypt("ypyhnm")}", null)
            ?.let { HashSet<String>(it) }
        sentEvents.addAll(stringSet ?: mutableSetOf())
    }

    override fun send(event: LoggerEvent) {
        if (sentEvents.contains(event.name) && event.isSingle) return

        val bundleParams = Bundle()
        event.params.forEach {
            bundleParams.putString(it.first, it.second?.toString())
        }
        bundleParams.putString(Crypto.decrypt("xypcwy"), device)
        bundleParams.putInt("${Crypto.decrypt("xypcwy")}_${Crypto.decrypt("mxe")}_${Crypto.decrypt("pylmcih")}", deviceSdkVersion)
        bundleParams.putInt("${Crypto.decrypt("pylmcih")}_${Crypto.decrypt("wixy")}", versionCode ?: -1)
        bundleParams.putString("${Crypto.decrypt("ujjm")}_${Crypto.decrypt("zfsyl")}_${Crypto.decrypt("omyl")}_${Crypto.decrypt("cx")}", appsFlyerUserId)

        when (event.observeState) {
            ObserveState.START -> timingValues.start = System.currentTimeMillis()
            ObserveState.FINISH -> {
                timingValues.end = System.currentTimeMillis()

                val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS", Locale.getDefault())
                bundleParams.putString("${Crypto.decrypt("mnuln")}_${Crypto.decrypt("ncgy")}", formatter.format(Date(timingValues.start)))
                bundleParams.putString("${Crypto.decrypt("zchcmb")}_${Crypto.decrypt("ncgy")}", formatter.format(Date(timingValues.end)))
            }

            else -> {}
        }


        firebaseAnalytics.logEvent(event.name, bundleParams)
        if (event.isSingle) {
            sentEvents.add(event.name)
            sharedPreferences.edit()?.putStringSet("${Crypto.decrypt("myhn")}_${Crypto.decrypt("ypyhnm")}", sentEvents)?.apply()
        }
    }

    override fun record(throwable: Throwable) {
        val throwableWithMsg = Throwable(
            "${Crypto.decrypt("Fiaayl")} ${Crypto.decrypt("chzi")}: \n" +
                    "${Crypto.decrypt("xypcwy")}: $device\n" +
                    "${Crypto.decrypt("xypcwy")}_${Crypto.decrypt("mxe")}_${Crypto.decrypt("pylmcih")}: $deviceSdkVersion\n" +
                    "${Crypto.decrypt("pylmcih")}_${Crypto.decrypt("wixy")}: $versionCode\n" +
                    "${Crypto.decrypt("ujjm")}_${Crypto.decrypt("zfsyl")}_${Crypto.decrypt("omyl")}_${Crypto.decrypt("cx")}: $appsFlyerUserId\n" +
                    "${Crypto.decrypt("gymmuay")}: ${throwable.message}\n"
        )
        firebaseCrashlytics.recordException(throwableWithMsg)
    }
}