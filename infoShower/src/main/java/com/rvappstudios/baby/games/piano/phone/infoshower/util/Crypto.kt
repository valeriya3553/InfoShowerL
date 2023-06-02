package com.rvappstudios.baby.games.piano.phone.infoshower.util

internal object Crypto {
    fun decrypt(string : String ,offset : Int = 20): String {
        val sb = StringBuilder()
        for (element in string) {
            if (element in 'A'..'Z') {
                var t1 = element.code - 'A'.code - offset
                if (t1 < 0) t1 += 26
                sb.append((t1 + 'A'.code).toChar())
            } else if (element in 'a'..'z') {
                var t1 = element.code - 'a'.code - offset
                if (t1 < 0) t1 += 26
                sb.append((t1 + 'a'.code).toChar())
            }
        }
        return sb.toString()
    }
}