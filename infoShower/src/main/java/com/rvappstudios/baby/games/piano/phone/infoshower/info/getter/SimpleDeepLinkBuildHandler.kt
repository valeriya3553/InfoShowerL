package com.rvappstudios.baby.games.piano.phone.infoshower.info.getter

import android.app.Activity

interface SimpleDeepLinkBuildHandler : InfoLinkBuildHandler {
    suspend fun getDeepLink(
        activity: Activity,
        fbId: String,
        fbToken: String,
        cullBack: (String?) -> Unit
    )
}