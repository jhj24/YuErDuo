package com.jqyd.yuerduo.util

import com.jqyd.yuerduo.BuildConfig
import com.jqyd.yuerduo.MyApplication
import com.jqyd.yuerduo.constant.URLConstant
import org.jetbrains.anko.toast
import java.io.File
import java.io.RandomAccessFile
import java.net.URL
import java.util.*
import java.util.zip.ZipFile

/**
 * Created by zhangfan on 2016/12/29 0029.
 */
object ApkBuildUtil {

    val ServiceHost: String by lazy value@{
        val defaultServiceHost = BuildConfig.ServerHost
        var text = readFileContentFromApk("host")
        if (!text.startsWith("http://") || text.length < 8) {
            return@value defaultServiceHost
        }
        if (text.endsWith("/")) {
            text = text.substring(0, text.length - 1)
        }
//        MyApplication.instance.toast("$text")
        return@value text
    }


    fun readFileContentFromApk(fileName: String): String {
        val applicationInfo = MyApplication.instance.applicationInfo
        val sourceDir = applicationInfo.sourceDir
        val fileFullName = "META-INF/$fileName"
        val zipFile = ZipFile(sourceDir)
        zipFile.use { file ->
            val entries = file.entries()
            while (entries.hasMoreElements()) {
                val nextElement = entries.nextElement()
                if (fileFullName == nextElement.name) {
                    val inputStream = file.getInputStream(nextElement)
                    return inputStream.use {
                        it.reader().readText()
                    }
                }
            }
        }
        return ""
    }
}