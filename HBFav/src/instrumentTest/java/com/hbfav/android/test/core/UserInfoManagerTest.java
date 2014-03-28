package com.hbfav.android.test.core;

import android.test.ActivityInstrumentationTestCase2;

import com.hbfav.android.core.UserInfoManager;
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
    }
}
