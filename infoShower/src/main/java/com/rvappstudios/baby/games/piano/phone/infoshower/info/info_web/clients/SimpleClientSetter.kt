package com.rvappstudios.baby.games.piano.phone.infoshower.info.info_web.clients

import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient

interface SimpleClientSetter {
    fun setWebInfoClients(webView : WebView)

    val webViewClient : WebViewClient
    val webChromeClient : WebChromeClient
}