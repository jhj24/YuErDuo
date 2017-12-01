package com.jqyd.yuerduo.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import com.nostra13.universalimageloader.utils.L;
import com.orhanobut.logger.LogTool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * 操作日志记录工具
 * Created by zhangfan on 2016/1/18.
 */
public class FileLogTool implements LogTool {
    private final Context context;

    public FileLogTool(Context context) {
        this.context = context;
    }

    public void d(String tag, String message) {
        Log.d(tag, message);
        writeLogFile(tag, message);
    }

    public void e(String tag, String message) {
        Log.e(tag, message);
        writeLogFile(tag, message);
    }

    public void w(String tag, String message) {
        Log.w(tag, message);
        writeLogFile(tag, message);
    }

    public void i(String tag, String message) {
        Log.i(tag, message);
        writeLogFile(tag, message);
    }

    public void v(String tag, String message) {
        Log.v(tag, message);
        writeLogFile(tag, message);
    }

    public void wtf(String tag, String message) {
        Log.wtf(tag, message);
        writeLogFile(tag, message);
    }

    private SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat myLogSdf = new SimpleDateFormat("HH:mm:ss=>>", Locale.getDefault());

    /**
     * 打开日志文件并写入日志
     **/
    private void writeLogFile(String tag, String message) {// 新建或打开日志文件
        Date now = new Date();
        String needWriteFile = logfile.format(now);
        String needWriteMessage = myLogSdf.format(now) + message;

        File appDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException | IncompatibleClassChangeError e) {
            externalStorageState = "";
        }

        if (MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
            appDir = getExternalLogDir();
        }
        if (appDir == null) return;
        File file = new File(appDir, needWriteFile + ".txt");

        try {
            FileWriter filerWriter = new FileWriter(file, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getExternalLogDir() {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "YuErDuo"), "data");
        File appLogDir = new File(dataDir, "log");
        if (!appLogDir.exists()) {
            if (!appLogDir.mkdirs()) {
                L.w("Unable to create external Log directory");
                return null;
            }
        }
        return appLogDir;
    }

    private boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

}
