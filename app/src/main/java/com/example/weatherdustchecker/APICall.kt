package com.example.weatherdustchecker

import android.os.AsyncTask
import java.net.HttpURLConnection
import java.net.URL

class APICall(val callback: APICall.APICallback) :
    AsyncTask<URL, Void, String>() {

    interface APICallback {
        fun onComplete(result: String)
    }

    override fun doInBackground(vararg params: URL?): String {
        // 여기서 URL에 요청 보내기
        val url = params.get(0)

        val conn = url?.openConnection() as HttpURLConnection
        conn.connect()

        var body = conn?.inputStream.bufferedReader().use {
            it.readText()
        }

        conn.disconnect()
        
        // 그리고 전달받은 JSON 문자열 반환하기
        return body
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        callback.onComplete(result!!)
    }

}
