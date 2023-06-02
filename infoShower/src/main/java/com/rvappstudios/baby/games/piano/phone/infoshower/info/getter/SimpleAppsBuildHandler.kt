package com.rvappstudios.baby.games.piano.phone.infoshower.info.getter

import android.app.Activity

interface SimpleAppsBuildHandler : InfoLinkBuildHandler {
    suspend fun getAppsData(
        activity: Activity,
        afKey: String,
        callBack: (List<String?>?) -> Unit
    )
}