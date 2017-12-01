package com.jqyd.yuerduo.fragment.contacts;

import java.util.Comparator;

/**
 * @author xiaanming
 */
public class PinyinComparator implements Comparator<SortModel> {


    @Override
    public int compare(SortModel o1, SortModel o2) {
        String srt1 = getCharacter(o1.getSortLetters());
        String str2 = getCharacter(o2.getSortLetters());
        return srt1.compareTo(str2);
    }

    private String getCharacter(String string) {
        if (string.startsWith("#")) {
            return new StringBuilder(String.valueOf(string)).insert(0, "~").toString();
        } else {
            return string;
        }
    }

}
