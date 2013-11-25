package com.hbfav.android;


import com.hbfav.R;
import com.hbfav.android.ui.MainActivity;

public class Constants {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

    public static final String PREF_USER_NAME = "hatena_bookmark_user_name";

    public static final String HBFAV_BASE_URL = "http://feed.hbfav.com/";

    public static final String BASE_THUMBNAIL_URL = "http://www.st-hatena.com/users/";

    public static final String ISSUES_URL = "https://github.com/yasuoza/HBFav-Android/issues";

    public static final String[] MENUS = new String[]{
            MainActivity.getContextOfApplication().getString(R.string.title_section0),
            MainActivity.getContextOfApplication().getString(R.string.title_section1),
            MainActivity.getContextOfApplication().getString(R.string.title_section2),
            MainActivity.getContextOfApplication().getString(R.string.title_section3),
            MainActivity.getContextOfApplication().getString(R.string.title_section4)
    };

    public static final String[] CATEGORIES = MainActivity.getContextOfApplication()
            .getResources()
            .getStringArray(R.array.params_entry_category);
}
