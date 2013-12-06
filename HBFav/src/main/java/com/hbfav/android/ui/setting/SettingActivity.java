package com.hbfav.android.ui.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.hbfav.android.R;

public class SettingActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_setting);
        getFragmentManager().beginTransaction().replace(R.id.setting_fragment_container, new SettingFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
