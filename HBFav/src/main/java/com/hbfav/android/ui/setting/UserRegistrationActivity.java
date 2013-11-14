package com.hbfav.android.ui.setting;


import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.hbfav.R;
import com.hbfav.android.Constants;
import com.hbfav.android.core.UserInfoManager;

public class UserRegistrationActivity extends Activity {
    private EditText mEditTextUserName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_regstrasion);

        mEditTextUserName =  (EditText) findViewById(R.id.edit_text_user_name);
        mEditTextUserName.setText(UserInfoManager.getUserName());
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
                UserInfoManager.setUserName(userName);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
