package com.jqyd.yuerduo.net

import android.content.Context
import android.util.Log
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.jqyd.yuerduo.util.SystemEnv
import okhttp3.*
import org.jetbrains.anko.onUiThread
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * 网络请求接口
 * Created by zhangfan on 16-6-7.
 */
object HttpCall {
    private val MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private val HEAD_PNG = Headers.of("Content-Disposition", "form-data; filename=\"img.png\"");
    //文件
    private val MEDIA_TYPE_FILE = MediaType.parse("file")


    private val okclient = OkHttpClient.Builder()
            .addNetworkInterceptor(StethoInterceptor())
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(200, TimeUnit.SECONDS)
            .build()

    fun post(context: Context, url: String, params: Map<String, String>?, files: Map<String, List<File>>?, callback: Callback, progress: ((Long, Long) -> Unit)? = null): Call {
        val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)

        val paramsF = params.orEmpty()
        val filesF = files.orEmpty()

        for ((key, value) in paramsF) {
            builder.addFormDataPart(key, value)
        }
        val login = SystemEnv.getLogin(context)
        if (login != null) {
            if (paramsF["memberId"].isNullOrBlank()) {
                builder.addFormDataPart("memberId", login.memberId.toString())
            }
            if (paramsF["storeId"].isNullOrBlank()) {
                builder.addFormDataPart("storeId", login.storeId.toString())
            }

        }

        for ((key, value) in filesF) {
            if (value.isEmpty()) continue
            val filesBuilder = MultipartBody.Builder();
            for (oneFile in value) {
                val filename = oneFile.name
                val str = "form-data; filename=\"%s\""
                val format = String.format(str, filename)
                val header = Headers.of("Content-Disposition", format)

                filesBuilder.addPart(header, RequestBody.create(MEDIA_TYPE_FILE, oneFile))

                //filesBuilder.addPart(HEAD_PNG, RequestBody.create(MEDIA_TYPE_PNG, oneFile))
            }
            builder.addFormDataPart(key, null, filesBuilder.build())
        }

        val formBody: RequestBody
        if (callback is GsonProgressHttpCallback<*> || progress != null) {
            formBody = ProgressRequest(builder.build(), { current, total ->
                Log.i("xxx", "$current,$total")
                context.onUiThread {
                    (callback as? GsonProgressHttpCallback<*>)?.onProgress(current, total)
                    progress?.invoke(current, total)
                }
            })
        } else {
            formBody = builder.build()
        }

        val request = Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        val call = okclient.newCall(request);
        if (callback is GsonHttpCallback<*>) callback.onStart()
        call.enqueue(callback);
        return call;
    }

    /**
     * 普通请求
     */
    fun request(context: Context, url: String, params: Map<String, String>?, callback: Callback): Call {

        val builder = FormBody.Builder();
        val paramsF = params.orEmpty()

        for ((key, value) in paramsF) {
            builder.add(key, value)
        }
        val login = SystemEnv.getLogin(context)
        if (login != null) {
            if (paramsF["memberId"].isNullOrBlank()) {
                builder.add("memberId", login.memberId.toString())
            }
            if (paramsF["storeId"].isNullOrBlank()) {
                builder.add("storeId", login.storeId.toString())
            }
        }

        val formBody = builder.build();
        val request = Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        val call = okclient.newCall(request);
        if (callback is GsonHttpCallback<*>) callback.onStart()
        call.enqueue(callback);
        return call;
    }

    /**
     * 文件下载
     */
    fun downLoad(url: String, callback: Callback) {
        val request = Request.Builder()
                .url(url)
                .build();

        okclient.newCall(request).enqueue(callback);
    }
}