package com.rvappstudios.baby.games.piano.phone.infoshower.push

import com.rvappstudios.baby.games.piano.phone.infoshower.util.Crypto

enum class PushFromScreen(val value: String) {
    SPLASH(Crypto.decrypt("mjfumb")),
    INFO(Crypto.decrypt("qyv")),
    MAIN(Crypto.decrypt("jfoa")),
}