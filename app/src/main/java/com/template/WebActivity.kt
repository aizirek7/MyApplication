package com.template

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.webkit.*
import android.webkit.WebView.WebViewTransport
import androidx.annotation.RequiresApi

class WebActivity : AppCompatActivity() {
    lateinit var webView: WebView;
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        supportActionBar!!.hide()
        webView = findViewById(R.id.webView)


        openWebView()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack()
        }
    }

    private fun getUrl(): String{
        Log.i(Utils.TAG, "url.toString()")
        val url = intent.getStringExtra("url").toString()
        return url
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetJavaScriptEnabled")
    private fun openWebView(){
        Log.i(Utils.TAG, "openWebView")
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView,true)

        webView.webViewClient = WebViewClient()
        webView.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(true)

        }
        webView.loadUrl(getUrl())
    }
}

