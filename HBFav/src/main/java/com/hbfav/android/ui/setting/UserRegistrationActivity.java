package com.hbfav.android.ui.setting;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.hbfav.R;

public class UserRegistrationActivity extends Activity {
    private final String PREF_USER_NAME = "hatena_bookmark_user_name";
    private EditText mEditTextUserName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_regstrasion);

        mEditTextUserName =  (EditText) findViewById(R.id.edit_text_user_name);
        mEditTextUserName.setText(PreferenceManager.getDefaultSharedPreferences(this).getString(PREF_USER_NAME, ""));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_user_registration, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                String userName = mEditTextUserName.getText().toString();
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                sp.edit().putString(PREF_USER_NAME, userName).apply();
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
