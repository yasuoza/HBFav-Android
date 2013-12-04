package com.hbfav.android.core;

import android.test.ActivityInstrumentationTestCase2;

import com.hbfav.android.Constants;
import com.hbfav.android.ui.MainActivity;

import junit.framework.Assert;

public class UserInfoManagerTest extends ActivityInstrumentationTestCase2<MainActivity> {
    private String mOrigName = "";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity(); // Start activity
        mOrigName = UserInfoManager.getUserName();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        UserInfoManager.setUserName(mOrigName);
    }

    public UserInfoManagerTest() {
        super(MainActivity.class);
    }

    public void testSetGetUserName() {
        String name = "dankogai";
        UserInfoManager.setUserName(name);
        Assert.assertEquals(UserInfoManager.getUserName(), name);
        assertEquals(UserInfoManager.getThumbUrl(), Constants.BASE_THUMBNAIL_URL + name + "/profile.gif");
    }
}
