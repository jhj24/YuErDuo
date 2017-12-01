package com.jqyd.yuerduo.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.jqyd.yuerduo.BuildConfig;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhangfan on 16-6-30.
 */
public class FileUtil {

    private static final String PROJECT_DIR = "JQGYL";


    public static String getSDPath(String subDir) {

        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {

            String path = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();

            if (!path.endsWith("/"))
                path += "/";

            path += (PROJECT_DIR + "/");

            if (subDir != null && subDir.trim().length() > 0)
                path += (subDir + "/");

            File f = new File(path);

            if (!f.exists()) {
                if (f.mkdirs())
                    return path;
                else
                    return null;
            } else {
                if (f.isFile()) {
                    if (f.delete()) {
                        if (f.mkdir())
                            return path;
                        else
                            return null;
                    } else
                        return null;
                } else
                    return path;
            }
        }
        return null;
    }

    /**
     * 根据URI 获取路径
     *
     * @param context 当前环境
     * @param uri     uri
     * @return 路径
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static List<PackageInfo> getInstalledPackages(Context context) {
        return context.getPackageManager().getInstalledPackages(0);
    }

    /**
     * 打开文件
     *
     * @param type     intent的type属性
     * @param filePath 文件的路径
     */
    public static void lookFile(Context context, String type, String filePath) {
        File file = new File(filePath);
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //设置intent的Action属性
            intent.setAction(Intent.ACTION_VIEW);
            //设置intent的data和Type属性。
            //判断是否是AndroidN以及更高的版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file);
                intent.setDataAndType(contentUri, type);
            }else {
                intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
            }
            //跳转
            context.startActivity(intent);
        } catch (Exception e) {//无法打开该文件时 检测下载WPS
            checkWps(context, file.getName());
            e.printStackTrace();
        }
    }

    /**
     * 获取指定文件大小
     *
     * @param file 文件
     * @return 文件的大小 （字节）
     * @throws Exception
     */
    public static int getFileSize(File file) throws Exception {
        int size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
            fis.close();
        } else {
            file.createNewFile();
        }
        return size;
    }

    /**
     * 获取文件的名称
     *
     * @param path
     * @return
     */
    public static String getFileName(String path) {
        int start = path.lastIndexOf("/");
        int end = path.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return path.substring(start + 1);
        } else {
            return null;
        }
    }

    /**
     * 查看是否安装WPS
     *
     * @param name 文件名称
     * @return 是否安装
     */
    private static boolean checkWps(Context context, String name) {
        boolean isWps = true;
        int dotIndex = name.lastIndexOf(".");
        if (dotIndex != -1) {
            String fileType = name.substring(dotIndex);
            List<String> docTypes = Arrays.asList(".doc", ".docx", ".xls", ".xlsx", ".pdf", ".ppt", ".pptx");
            if (docTypes.contains(fileType) && !isInstalled(context, "cn.wps.moffice_eng")) {
                isWps = false;
                downLoadWps(context);
            }
        }
        return isWps;
    }

    /**
     * 下载安装WPS
     *
     * @param context 上下文环境
     */
    private static void downLoadWps(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage("您手机上没有安装wps,可能无法正常打开该文件,是否下载？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("http://125.46.106.52/download/wps.apk"));
                it.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                Activity activity = (Activity) context;
                activity.startActivity(it);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "无法打开该文件,请先下载WPS", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * 判断是否安装某个程序
     */
    private static boolean isInstalled(Context context, String pkg) {

        if (!isEmpty(pkg)) {
            List list = getInstalledPackages(context);
            if (null != list && list.size() > 0) {
                for (Object aList : list) {
                    PackageInfo pi = (PackageInfo) aList;
                    if (pkg.equalsIgnoreCase(pi.packageName)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean isEmpty(CharSequence str) {
        return null == str || str.length() == 0;
    }

    /**
     * 获取文件的MIME类型
     *
     * @param name 文件名
     * @return 文件类型
     */
    public static String getFileType(String name) {
        String fileType = "*/*";
        int dotIndex = name.lastIndexOf(".");
        if (dotIndex != -1) {
                /* 获取文件的后缀名*/
            String end = name.substring(dotIndex).toLowerCase();
            //在MIME和文件类型的匹配表中找到对应的MIME类型。
            for (String[] aMIME_MapTable : MIME_MapTable) { //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
                if (end.equals(aMIME_MapTable[0]))
                    fileType = aMIME_MapTable[1];
            }
        }
        return fileType;
    }

    private static final String[][] MIME_MapTable = {
            //{后缀名，MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };
}
