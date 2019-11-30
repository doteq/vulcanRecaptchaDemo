package com.doteq.captchatest

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.webkit.JavascriptInterface
import java.io.InputStream
import android.util.Log
import android.view.View
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

var email = ""
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttonSubmit.setOnClickListener{
            emailLayout.visibility = View.GONE
            captchaWebView.visibility = View.VISIBLE
            val inputStream: InputStream = assets.open("captcha.html")
            val captchaHTML = inputStream.bufferedReader().use{it.readText()}
            captchaWebView.settings.javaScriptEnabled = true
            captchaWebView.addJavascriptInterface(WebAppInterface(this), "Android")
            captchaWebView.loadDataWithBaseURL("https://cufs.vulcan.net.pl/Default/AccountManage/UnlockAccount", captchaHTML, "text/html", "UTF-8", null)
            email = emailInput.text.toString()
        }

    }
    class WebAppInterface(private val mContext: Context) {
        /** Show a toast from the web page  */
        @JavascriptInterface
        fun recaptchaCallback(recaptchaResponse: String) {
            Log.d("aaa", email)
            Log.d("aaa",recaptchaResponse)

            val client = OkHttpClient()
            val mediaType = "application/x-www-form-urlencoded".toMediaType()
            val body = "Email=$email&g-recaptcha-response=$recaptchaResponse".toRequestBody(mediaType)
            val request = Request.Builder()
                .url("https://cufs.vulcan.net.pl/Default/AccountManage/UnlockAccount")
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build()
            val response = client.newCall(request).execute();
        }
    }

}
