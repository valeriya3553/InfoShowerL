package com.rvappstudios.baby.games.piano.phone.infoshower.info.getter

import android.app.Activity
import com.facebook.FacebookSdk
import com.facebook.applinks.AppLinkData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultDeepLinkBuildHandler : SimpleDeepLinkBuildHandler{
    private var wasInit = false

    var isWorking = true
        private set

    override suspend fun getDeepLink(
        activity : Activity,
        fbId:String,
        fbToken : String,
        cullBack : (String?)->Unit
    ){
        if(!isWorking) return
        withContext(Dispatchers.IO){
            FacebookSdk.apply {
                setApplicationId(fbId)
                setClientToken(fbToken)
                @Suppress("DEPRECATION")
                sdkInitialize(activity)
                setAdvertiserIDCollectionEnabled(true)
                setAutoInitEnabled(true)
                fullyInitialize()
            }

            AppLinkData.fetchDeferredAppLinkData(activity) {
                if(!isWorking || wasInit) return@fetchDeferredAppLinkData
                cullBack(it?.targetUri?.toString())
                wasInit = true
            }
        }
    }

    override fun checkIfWasInit(): Boolean {
        return  wasInit
    }

    override fun stopWorking() {
        isWorking = false
    }

}