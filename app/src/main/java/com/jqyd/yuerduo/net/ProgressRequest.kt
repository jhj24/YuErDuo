package com.jqyd.yuerduo.net

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.*

/**
 * 带有进度监听的请求体
 * Created by zhangfan on 16-7-12.
 */
class ProgressRequest(val request: MultipartBody, var callBack: (Long, Long) -> Unit) : RequestBody() {

    override fun writeTo(sink: BufferedSink) {
        val countingSink = CountingSink(sink)
        val bufferedSink = Okio.buffer(countingSink)
        request.writeTo(bufferedSink)
        bufferedSink.flush()
    }

    override fun contentType(): MediaType? {
        return request.contentType()
    }

    override fun contentLength(): Long {
        try {
            return request.contentLength()
        } catch(e: Exception) {
        }
        return super.contentLength()
    }

    inner class CountingSink(delegate: Sink) : ForwardingSink(delegate) {
        var bytesWritten = 0L;
        override fun write(source: Buffer?, byteCount: Long) {
            super.write(source, byteCount)
            bytesWritten += byteCount;
            callBack(bytesWritten, contentLength())
        }
    }
}