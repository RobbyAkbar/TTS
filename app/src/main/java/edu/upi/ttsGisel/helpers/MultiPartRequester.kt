package edu.upi.ttsGisel.helpers

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import cz.msebera.android.httpclient.client.HttpClient
import cz.msebera.android.httpclient.client.methods.HttpPost
import cz.msebera.android.httpclient.entity.ContentType
import cz.msebera.android.httpclient.entity.mime.MIME
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient
import cz.msebera.android.httpclient.params.HttpConnectionParams
import cz.msebera.android.httpclient.util.EntityUtils
import edu.upi.ttsGisel.listeners.AsyncTaskCompleteListener
import java.io.File

class MultiPartRequester(
        private val activity: Activity, private val map: MutableMap<String, String>,
        private val serviceCode: Int, asyncTaskCompleteListener: AsyncTaskCompleteListener
) {
    private var mAsynclistener: AsyncTaskCompleteListener? = null
    private var httpclient: HttpClient? = null
    private var request: AsyncHttpRequest? = null

    init {
        mAsynclistener = asyncTaskCompleteListener
        request = AsyncHttpRequest().execute(map["url"]) as AsyncHttpRequest
    }

    internal inner class AsyncHttpRequest : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg urls: String): String? {
            map.remove("url")
            try {
                val httppost = HttpPost(urls[0])
                httpclient = DefaultHttpClient()

                HttpConnectionParams.setConnectionTimeout(
                        httpclient!!.params, 600000
                )

                val builder = MultipartEntityBuilder
                        .create()

                for (key in map.keys) {

                    if (key.equals("filename", ignoreCase = true)) {
                        val f = map[key]?.let { File(it) }

                        builder.addBinaryBody(
                                key, f,
                                ContentType.MULTIPART_FORM_DATA, f?.name
                        )
                    } else {
                        builder.addTextBody(
                                key, map[key], ContentType
                                .create("text/plain", MIME.DEFAULT_CHARSET)
                        )
                    }
                    Log.d("TAG", key + "---->" + map[key])
                }

                httppost.entity = builder.build()

                val manager = activity
                        .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

                if (manager.memoryClass < 25) {
                    System.gc()
                }
                val response = httpclient!!.execute(httppost)

                return EntityUtils.toString(
                        response.entity, "UTF-8"
                )

            } catch (e: Exception) {
                e.printStackTrace()
            } catch (oume: OutOfMemoryError) {
                System.gc()

                Toast.makeText(
                        activity.parent.parent,
                        "Run out of memory please colse the other background apps and try again!",
                        Toast.LENGTH_LONG
                ).show()
            } finally {
                if (httpclient != null)
                    httpclient!!.connectionManager.shutdown()

            }
            return null
        }

        override fun onPostExecute(response: String) {
            if (mAsynclistener != null) {
                mAsynclistener!!.onTaskCompleted(response, serviceCode)
            }
        }
    }

}