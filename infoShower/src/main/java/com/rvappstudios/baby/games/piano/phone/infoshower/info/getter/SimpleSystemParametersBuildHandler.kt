package com.rvappstudios.baby.games.piano.phone.infoshower.info.getter

import android.content.Context
import com.rvappstudios.baby.games.piano.phone.infoshower.info.entity.SystemEntity


interface SimpleSystemParametersBuildHandler : InfoLinkBuildHandler {
    suspend fun getSystemParameters(context : Context) : SystemEntity
}