package com.hbfav.android.test;


import android.app.ActionBar;
import android.test.ActivityInstrumentationTestCase2;

import com.hbfav.android.views.MainActivity;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private MainActivity mActivity;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    public void setUp() throws Exception {
        mActivity = getActivity();
    }

    public void testStringForDisplay() throws Exception {
        ActionBar actionBar = mActivity.getActionBar();
        String result = actionBar.getTitle().toString();
        assertEquals(result, "Section 1");
    }
}
