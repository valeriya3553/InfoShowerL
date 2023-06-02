package com.rvappstudios.baby.games.piano.phone.infoshower.info.getter

import android.app.Activity
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.rvappstudios.baby.games.piano.phone.infoshower.util.Crypto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DefaultAppsBuildHandler : SimpleAppsBuildHandler {
    var isWorking = true
        private set

    private var wasInit = false

    override suspend fun getAppsData(
        activity: Activity,
        afKey: String,
        callBack: (List<String?>?) -> Unit
    ) {
        if (!isWorking) return
        val resultAppsList = mutableListOf<String?>()

        val conversionListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                if(!isWorking || wasInit) return
                try {
                    resultAppsList.add(
                        (data?.getOrDefault(
                            Crypto.decrypt("jo") +
                                    "_${Crypto.decrypt("bcjcdb")}", null
                        ) as String?)
                    )
                    resultAppsList.add(
                        data?.getOrDefault(
                            Crypto.decrypt("jo") +
                                    "_${Crypto.decrypt("lqjwwnu")}", null
                        ) as String?
                    )
                    resultAppsList.add(
                        data?.getOrDefault(Crypto.decrypt("ljvyjrpw"), null)
                                as String?
                    )
                    resultAppsList.add(
                        data?.getOrDefault(
                            Crypto.decrypt("vnmrj") +
                                    "_${Crypto.decrypt("bxdaln")}", null
                        ) as String?
                    )
                    resultAppsList.add(
                        data?.getOrDefault(
                            Crypto.decrypt("jo") +
                                    "_${Crypto.decrypt("jm")}", null
                        ) as String?
                    )
                    resultAppsList.add(
                        data?.getOrDefault(
                            Crypto.decrypt("ljvyjrpw") +
                                    "_${Crypto.decrypt("rm")}", null
                        ) as String?
                    )
                    resultAppsList.add(
                        data?.getOrDefault(
                            Crypto.decrypt("jmbnc") +
                                    "_${Crypto.decrypt("rm")}", null
                        ) as String?
                    )
                    resultAppsList.add(
                        data?.getOrDefault(
                            Crypto.decrypt("jm") +
                                    "_${Crypto.decrypt("rm")}", null
                        ) as String?
                    )
                    resultAppsList.add(
                        data?.getOrDefault(Crypto.decrypt("jmbnc"), null)
                                as String?
                    )
                    callBack(resultAppsList)

                    isWorking = false
                    wasInit = true
                }catch (e : Exception){
                    callBack(null)
                    wasInit = true
                }
            }

            override fun onConversionDataFail(error: String?) {
                if(!isWorking || wasInit) return
                callBack(null)
                wasInit = true
            }

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                if(!isWorking || wasInit) return
                callBack(null)
                wasInit = true
            }

            override fun onAttributionFailure(error: String?) {
                if(!isWorking || wasInit) return
                callBack(null)
                wasInit = true
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            launch {
                AppsFlyerLib
                    .getInstance()
                    .init(afKey, conversionListener, activity)
                    .start(activity)
            }.join()
        }
    }

    override fun checkIfWasInit(): Boolean {
        return wasInit
    }

    override fun stopWorking() {
        isWorking = false
    }
}