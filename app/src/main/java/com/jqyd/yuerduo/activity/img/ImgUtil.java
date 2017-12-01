package com.jqyd.yuerduo.activity.img;

import android.graphics.Bitmap;

import com.jqyd.yuerduo.util.FileUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 保存图片的工具类
 * Created by jianhaojie on 2016/9/20.
 */
public class ImgUtil {

    /**
     * 保存图片
     */
    public static String saveBitmapFile(Bitmap bitmap) {
        String path = getSDImagePath();
        if (path == null) {
            return null;
        }

        String strImgPath = path
                + UUID.randomUUID().toString().replaceAll("-", "") + ".jpg";
        File file = new File(strImgPath);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return strImgPath;
    }


    public  static String getSDImagePath() {
        return FileUtil.getSDPath("image");
    }


    /**
     * 判断路径是否存在
     * @param path
     * @return
     */
    public static boolean isExist(String path) {

        if (path == null || path.trim().equals(""))
            return false;

        File f = new File(path);

        if (!f.exists())
            return false;

        return true;
    }

    /**
     * 每次调用都会新建一个Options对象，在适配器中注意避免多次调用
     *
     * @param defaltImgResource
     * @return
     */
    public static DisplayImageOptions getOption(int defaltImgResource) {

        return new DisplayImageOptions.Builder()
                .showImageOnLoading(defaltImgResource)
                .showImageForEmptyUri(defaltImgResource)
                .showImageOnFail(defaltImgResource)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
    }
}
