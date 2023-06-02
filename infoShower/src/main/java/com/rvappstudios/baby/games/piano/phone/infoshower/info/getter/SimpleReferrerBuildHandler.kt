package com.rvappstudios.baby.games.piano.phone.infoshower.info.getter

import android.app.Activity

interface SimpleReferrerBuildHandler : InfoLinkBuildHandler {
    suspend fun getReferrerData(
        activity: Activity,
        referrerKey: String,
        callBack: (referrerResult : String?) -> Unit
    )
}