package com.rvappstudios.baby.games.piano.phone.infoshower.info.getter

import android.content.Context
import android.os.BatteryManager
import android.provider.Settings
import com.appsflyer.AppsFlyerLib
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.rvappstudios.baby.games.piano.phone.infoshower.info.entity.SystemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultSystemParametersBuildHandler : SimpleSystemParametersBuildHandler {
    private var wasInit = false

    var isWorking = true
        private set

    override suspend fun getSystemParameters(context: Context): SystemEntity {
        if (!isWorking) return  SystemEntity(
            null,null,null,null
        )

        return withContext(Dispatchers.IO) {
            val batteryLevel: Int? = try {
                val batteryManager =
                    context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

                batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            } catch (e: Exception) {
                100
            }

            val appsFlyerId = AppsFlyerLib.getInstance().getAppsFlyerUID(context)

            var advertisingInfo: AdvertisingIdClient.Info? = null


            val isDevelopmentSettingEnabled = Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
            ) != 0

            val gId = withContext(Dispatchers.IO) {
                try {
                    advertisingInfo =
                        AdvertisingIdClient.getAdvertisingIdInfo(context)
                } catch (_: Exception) {
                }
                advertisingInfo?.id
            }

            if (!isWorking) SystemEntity(
                null,null,null,null
            )

            SystemEntity(
                battery = batteryLevel?.toFloat().toString(),
                appsId = appsFlyerId,
                googleId = gId,
                isProgramModeEnabled = isDevelopmentSettingEnabled
            )
        }
    }

    override fun checkIfWasInit(): Boolean {
        return wasInit
    }

    override fun stopWorking() {
        isWorking = false
    }
}