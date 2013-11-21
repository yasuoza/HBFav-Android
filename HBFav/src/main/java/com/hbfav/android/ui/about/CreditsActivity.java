package com.hbfav.android.ui.about;


import android.app.Activity;
import android.os.Bundle;

import com.hbfav.R;

public class CreditsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_credits);

        getActionBar().setTitle(getString(R.string.about_credits));
    }
}
