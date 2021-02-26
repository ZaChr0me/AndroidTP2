package com.tp.tp2

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors.joining

class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        val authButton=findViewById<Button>(R.id.AuthButton)

        //bind the starting of a thread running the authentication to the button to prevent locking the ui
        authButton?.setOnClickListener(){v -> Thread(Runnable {Authenticate()  }).start()
        }
        }
    //authentication function
    fun Authenticate() {
        var url: URL? = null

        val basicAuth = "Basic " + Base64.encodeToString(
            //get values from login and password
            findViewById<EditText>(R.id.LoginText).text.toString().plus(":").plus(findViewById<EditText>(R.id.PasswordText).text.toString()).toByteArray(),
            Base64.NO_WRAP
        )


        try {
            url = URL("https://httpbin.org/basic-auth/bob/sympa")
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.setRequestProperty("Authorization", basicAuth)
            try {
                val `in`: InputStream = BufferedInputStream(urlConnection.getInputStream())
                val s: String = readStream(`in`)
                Log.i("JFL", s)

                //get the info we want from the result
                val json=JSONObject(s)
                val res="are you authenticated?".plus(json["authenticated"].toString())
                
                //update ui with request result
                this@AuthenticationActivity.runOnUiThread(Runnable {
                    findViewById<TextView>(R.id.resultTextView).text=res
                })

            } finally {
                urlConnection.disconnect()
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun readStream(input:InputStream):String{
        var reader:BufferedReader= BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8),1000)
        var returnVal:String=reader.lines().collect(joining("\n"));
        input.close()
        return returnVal
    }


}