package com.template

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.xml.sax.Parser
import retrofit2.HttpException
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.lang.NullPointerException
import java.net.URL
import java.util.*
import java.util.zip.GZIPInputStream

class MyInitialActivity : AppCompatActivity() {
    val ONESIGNAL_APP_ID = "f9db4054-450c-4628-ab97-f6680d1e9d0d"
    lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    val str = "TAG"
    val putSP = "put"
    val id = UUID.randomUUID()
    val gson = Gson()
    var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        firebaseAnalytics = Firebase.analytics

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_initial)

        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        getData()
//        saveSharedPref(id, putSP)
    }

    private fun getData() {
        remoteConfig.fetch(0).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Log.i(str, "isComplete")
                remoteConfig.activate()
            } else {
                Log.i(str, "Fetch failed")
            }
            showText()
        }
    }

    private fun showText() {
        val Str = remoteConfig.getString("check_link")
        if (Str.isNotEmpty()) {
            Log.i(str, Str)
            makeUpURL(Str)
        } else {
            Log.i(str, "check_link is empty")
        }
    }

    private fun setActivity(activity: Activity) {
        val intent = Intent(this, activity::class.java)
        startActivity(intent)
    }

    private fun saveSharedPref(id: String, key: String) {
        val sharedPreferences = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply {
            putString(key, id)
        }.apply()
        Toast.makeText(this, "save SharedPreferences", Toast.LENGTH_SHORT).show()
    }

    private fun loadSharedPref(key: String): String {
        val sharedPreferences = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        val UUID = sharedPreferences.getString(key, null)
        Toast.makeText(this, "load SharedPreferences", Toast.LENGTH_SHORT).show()
        return UUID.toString()
    }

    private fun makeUpURL(url: String) {
        try {
            val timeZone = TimeZone.getDefault()
            val link = "${url}/?packageid=${packageName}&usserid=${loadSharedPref(putSP)}" +
                    "&getz=${timeZone.id}&getr=utm_source=google-play&utm_medium=organic"
            Log.i(str, link)

            val jsonAnswer = gson.fromJson(link, JsonAnswer::class.java)
            this.url = jsonAnswer.url
            setActivityWithExtra(WebActivity(),jsonAnswer.url)


        } catch (e: HttpException) {
            Log.i(str, e.message.toString())

        } catch (e: Exception) {
            Log.i(str, e.message.toString())
            setActivity(MainActivity())
        }
    }

    private fun setActivityWithExtra(activity: Activity, url: String){
        val intent = Intent(this, activity::class.java)
        intent.putExtra("URL", url)
        startActivity(intent)
    }

    private fun initOneSignal(){
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)

    }
}
