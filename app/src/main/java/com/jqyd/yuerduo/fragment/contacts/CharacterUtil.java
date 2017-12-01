package com.jqyd.yuerduo.fragment.contacts;

import com.jqyd.yuerduo.MyApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 汉字解读拼音
 * Created by jianhaojie on 2017/3/20.
 */

public class CharacterUtil {

    private static Properties p = new Properties();
    private static CharacterUtil characterUtil = new CharacterUtil();

    private CharacterUtil() {
        decodeSpelling();
    }

    public static CharacterUtil getInstance() {
        return characterUtil;
    }

    /**
     * 获取字符串的拼音，取出满足条件的
     *
     * @param chinese     要转化的字符串
     * @param isOnlyFirst true 只对字符串的第一个字符操作
     * @return String
     */
    public String getStringSpelling(String chinese, boolean isOnlyFirst) {
        int leng;
        if (chinese == null) {
            return null;
        }
        char[] chs = chinese.trim().toCharArray();
        if (isOnlyFirst) {
            leng = 1;
        } else {
            leng = chs.length;
        }
        StringBuilder spellBuilder = new StringBuilder();
        for (int i = 0; i < leng; i++) {
            String[] spellArray = getHanyuPinyins(chs[i]);
            if (spellArray.length > 0) {
                spellBuilder.append(spellArray[0]);
            }
        }
        return spellBuilder.toString();
    }

    public String[] getArray(String chinese) {
        if (chinese == null) {
            return null;
        }
        char[] chs = chinese.trim().toCharArray();
        String[] array = new String[chs.length];
        for (int i = 0; i < chs.length; i++) {
            String[] spellArray = getHanyuPinyins(chs[i]);
            if (spellArray.length > 0) {
                array[i] = spellArray[0];
            }
        }
        return array;
    }

    private static String[] getHanyuPinyins(char c) {
        String key = Integer.toHexString((int) c).toUpperCase();
        String str = (String) p.get(key);
        //当字符c为符号时，str为空返回原字符
        if (str == null) {
            return new String[]{String.valueOf(c)};
        }
        return str.split(",");
    }

    /**
     * 获取汉字字典
     */
    private static void decodeSpelling() {
        try {
            InputStream in = MyApplication.instance.getAssets().open("pinyin.txt");
            InputStreamReader reader = new InputStreamReader(in, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(reader);
            p.load(bufferedReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
