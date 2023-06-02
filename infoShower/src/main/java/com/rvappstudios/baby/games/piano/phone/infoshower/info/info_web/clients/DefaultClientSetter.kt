package com.rvappstudios.baby.games.piano.phone.infoshower.info.info_web.clients

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.rvappstudios.baby.games.piano.phone.infoshower.common.Data
import com.rvappstudios.baby.games.piano.phone.infoshower.util.Crypto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DefaultClientSetter(
    val errorName : String?,
    val context: Activity,
    val onError : () -> Unit,
    private val cacheCallBack : (cache : String) -> Unit,
    private val shouldOverrideUrlLoadingStrategy : ((url : String?) -> Boolean)? = null
) : SimpleClientSetter{

    override val webViewClient: WebViewClient
        get() = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (
                    errorName == null ||
                    view?.title?.contains(
                        errorName
                    ) == true

                ) {
                    onError()
                } else {
                    if (url == null || url == "" || url == "null") {
                        onError()
                    }else{
                        cacheCallBack(url)
                    }
                }
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url?.toString()
                shouldOverrideUrlLoadingStrategy?.let {
                    return it(url)
                } ?: return try {
                    if(url == null) return false
                    if (url.startsWith(
                            Crypto.decrypt("bnnjm") +
                                    "://${Crypto.decrypt("n")}.${Crypto.decrypt("gy")}" +
                                    "/${Crypto.decrypt("dichwbun")}")) {

                        Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                            context.startActivity(this)
                        }
                    }
                    when {
                        url.startsWith("${Crypto.decrypt("bnnj")}://") || url.startsWith("${Crypto.decrypt("bnnjm")}://") -> false
                        else -> {
                            when {
                                url.startsWith("${Crypto.decrypt("gucfni")}:") -> {
                                    Intent(Intent.ACTION_SEND).apply {
                                        type = "plain/text"
                                        putExtra(
                                            Intent.EXTRA_EMAIL, url.replace("${Crypto.decrypt("gucfni")}:", "")
                                        )
                                        Intent.createChooser(this, Crypto.decrypt("Gucf")).run {
                                            context.startActivity(this)
                                        }
                                    }
                                }
                                url.startsWith("${Crypto.decrypt("nyf")}:") -> {
                                    Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse(url)
                                        Intent.createChooser(this, Crypto.decrypt("Wuff")).run {
                                            context.startActivity(this)
                                        }
                                    }
                                }
                            }

                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                            true
                        }
                    }
                } catch (e: Exception) {
                    true
                }
            }
        }
    override val webChromeClient: WebChromeClient
        get() = object : WebChromeClient(){
            private lateinit var takePictureIntent: Intent

            @SuppressLint("AnnotateVersionCheck")
            private val isVersionHigh = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

            private var permissionList: List<String> = if (isVersionHigh){
                listOf(Manifest.permission.CAMERA)
            }else{
                listOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                )
            }

            private var mCameraPhotoPath: String? = null

            private var myOwnFilePath = ValueCallback<Array<Uri?>?> { }

            var mFilePathCallback: ValueCallback<Array<Uri?>?>? = null

             var isAllPermissionsGranted = false

            override fun onShowFileChooser(
                view: WebView?,
                filePath: ValueCallback<Array<Uri?>?>,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                myOwnFilePath = filePath

                Dexter.withContext(context)
                    .withPermissions(permissionList)
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                            isAllPermissionsGranted = p0?.areAllPermissionsGranted() == true
                            openChooser()
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            p0: MutableList<PermissionRequest>?,
                            p1: PermissionToken?
                        ) {
                            p1?.continuePermissionRequest()
                        }
                    }).check()
                return true
            }

            @SuppressLint("InlinedApi", "QueryPermissionsNeeded")
            private fun openChooser() {

                mFilePathCallback = myOwnFilePath

                takePictureIntent =
                    if (isAllPermissionsGranted) {
                        Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    } else {
                        Intent(MediaStore.ACTION_PICK_IMAGES)
                    }

                if (isVersionHigh) {
                    CoroutineScope(Dispatchers.IO).launch {

                        if (takePictureIntent.resolveActivity(context.packageManager) != null) {
                            try {
                                takePictureIntent.putExtra("takePictureIntent", mCameraPhotoPath)
                            } catch (_: Exception) { }
                        }
                    }
                }

                val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                contentSelectionIntent.type = "image/*"
                val intentArray: Array<Intent?> =
                    arrayOf(takePictureIntent)
                val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Choose")
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                context.startActivityForResult(chooserIntent, Data.CHOOSE_PHOTO_REQUEST)
            }
        }


    override fun setWebInfoClients(webView: WebView) {
        webView.webViewClient = this.webViewClient
        webView.webChromeClient = this.webChromeClient
    }


}