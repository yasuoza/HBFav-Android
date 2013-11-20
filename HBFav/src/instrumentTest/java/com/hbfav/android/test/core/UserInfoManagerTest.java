package com.hbfav.android.test.core;

import android.test.ActivityInstrumentationTestCase2;

import com.hbfav.android.core.UserInfoManager;
import com.hbfav.android.ui.MainActivity;

import junit.framework.Assert;

public class UserInfoManagerTest extends ActivityInstrumentationTestCase2<MainActivity> {
    @Override
    protected void setUp() throws Exception {
        setActivityInitialTouchMode(false);
        getActivity(); // Start activity
    }

    public UserInfoManagerTest() {
        super("com.hbfav.android.MainActivity", MainActivity.class);
    }

    public void testSetGetUserName() {
        String name = "kogaidan";
        UserInfoManager.setUserName(name);
        Assert.assertEquals(UserInfoManager.getUserName(), name);
    }
}
