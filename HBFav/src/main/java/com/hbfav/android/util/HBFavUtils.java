package com.hbfav.android.util;

public class HBFavUtils {

    public static String boolToString(boolean b) {
        return b ? "1" : "0";
    }

    public static String usersToString(int users) {
        switch (users) {
            case 0:
                return "";
            case 1:
                return "1 user";
            default:
                return users + " users";
        }
    }
}
