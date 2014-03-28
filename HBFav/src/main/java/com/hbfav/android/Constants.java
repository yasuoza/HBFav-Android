package com.hbfav.android;


import com.hbfav.android.ui.MainActivity;

public class Constants {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

    public static final String HBFAV_BASE_URL = "http://hbfav-android.herokuapp.com/";

    public static final String BASE_THUMBNAIL_URL = "http://www.st-hatena.com/users/";

    public static final String ISSUES_URL = "https://github.com/yasuoza/HBFav-Android/issues";

    public static final int MAX_TAG_COUNT = 10;
    public static final int MAX_MY_TAG_COUNT = 100;

    public static final String HATENA_API_KEY = "";
    public static final String HATENA_API_KEY_SECRET = "";

    private static String[] categories;

    public static String getPrefUserName() {
        return MainActivity.getContextOfApplication().getString(R.string.pref_hatena_user_name);
    }

    public static String[] getCategories() {
        if (categories == null) {
            categories = MainActivity.getContextOfApplication()
                    .getResources()
                    .getStringArray(R.array.params_entry_category);
        }
        return categories;
    }
}
