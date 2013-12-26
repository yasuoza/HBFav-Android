package com.hbfav.android.ui.setting;

import android.os.Bundle;

import com.hbfav.android.R;
import com.hbfav.android.core.UserInfoManager;
import com.hbfav.android.ui.BaseActivity;

public class SettingActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);
        getFragmentManager().beginTransaction().replace(R.id.setting_fragment_container, new SettingFragment()).commit();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (UserInfoManager.getUserName() != null
                && !UserInfoManager.getUserName().isEmpty()
                && UserInfoManager.isAuthenticated()) {
            finish();
        }

    }
}
