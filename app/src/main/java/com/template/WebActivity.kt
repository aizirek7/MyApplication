package com.template

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import androidx.annotation.RequiresApi

class WebActivity : AppCompatActivity() {
    lateinit var webView: WebView;
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        webView = findViewById(R.id.webView)
        openWebView()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack()
        }
    }

    private fun getUrl(): String{
        Log.i("TAG", "url.toString()")
        val url = intent.getStringExtra("URL").toString()
        return url
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun openWebView(){
        Log.i("TAG", "openWebView")
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView,true)
        webView.settings.javaScriptEnabled = true
        Log.i("TAG", "url")
        webView.loadUrl(getUrl())
    }
}