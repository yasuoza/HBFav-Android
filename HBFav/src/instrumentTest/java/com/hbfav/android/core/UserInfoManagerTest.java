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
        setActivityInitialTouchMode(false);
        getActivity(); // Start activity
        mOrigName = UserInfoManager.getUserName();
    }

    @Override
    protected void tearDown() throws Exception {
        UserInfoManager.setUserName(mOrigName);
        super.tearDown();
    }

    public UserInfoManagerTest() {
        super(MainActivity.class);
    }

    public void testSetGetUserName() {
        String name = "kogaidan";
        UserInfoManager.setUserName(name);
        Assert.assertEquals(UserInfoManager.getUserName(), name);
    }

    public void testGetThumbUrl() {
        assertEquals(1, 1);
        String name = "kogaidan";
        UserInfoManager.setUserName(name);
        assertEquals(UserInfoManager.getThumbUrl(), Constants.BASE_THUMBNAIL_URL + name + "/profile.gif");
    }
}
