package com.hbfav.android.util;

import java.util.Comparator;
import java.util.Map;

public class IntegerMapComparator implements Comparator<String> {
    private Map<String, Integer> map;
    public IntegerMapComparator(Map<String, Integer> map) {
        this.map = map;
    }

    public int compare(String key1, String key2) {
        int value1 = map.get(key1);
        int value2 = map.get(key2);
        if(value1 == value2) {
            return key1.toLowerCase().compareTo(key2.toLowerCase());
        }
        else if(value1 < value2) {
            return 1;
        }
        else {
            return -1;
        }
    }
}
