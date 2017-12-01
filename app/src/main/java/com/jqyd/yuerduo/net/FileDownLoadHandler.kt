package com.jqyd.yuerduo.net

import android.os.Handler
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * Created by zhangfan on 16-6-7.
 */
abstract class FileDownLoadHandler(var file: File) : Callback {

    companion object {
        var error: HashMap<Int, String> = HashMap()

        init {
            error.put(1, "连接失败，请重试")
            error.put(2, "下载失败，手机空间不足")
            error.put(3, "下载失败，请重试")
        }
    }

    var hanlder = Handler()

    override fun onFailure(call: Call?, e: IOException?) {
        hanlder.post { callFailure(1) }
    }

    override fun onResponse(call: Call?, response: Response) {
        if (!response.isSuccessful) throw IOException("Unexpected code " + response);

        val byteStream = response.body().byteStream()
        val length = response.body().contentLength();
        val os: FileOutputStream
        try {
            os = FileOutputStream(file);
        } catch(e: Exception) {
            hanlder.post { callFailure(2) }
            return
        }
        var bytesRead = -1;
        val buffer = ByteArray(1024)
        var process = 0L;

        try {
            do {
                bytesRead = byteStream.read(buffer)
                if (bytesRead == -1) break;
                process += bytesRead
                os.write(buffer, 0, bytesRead);
                hanlder.post {
                    onProcess(process, length)
                }
            } while (true)
        } catch(e: Exception) {
            if(file.exists()){
                file.delete()
            }
            hanlder.post {
                callFailure(3)
            }
            return
        }


        hanlder.post {
            onSuccess(file)
            onFinish()
        }
    }

    fun callFailure(errorCode: Int, msg: String? = null) {
        onFailure(msg ?: error[errorCode] ?: "", errorCode)
        onFinish()
    }


    abstract fun onProcess(current: Long, total: Long)

    open fun onFinish() {
    }

    abstract fun onSuccess(file: File)

    abstract fun onFailure(msg: String, errorCode: Int);
}
