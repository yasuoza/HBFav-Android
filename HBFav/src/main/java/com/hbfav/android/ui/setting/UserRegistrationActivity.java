package com.hbfav.android.ui.setting;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hbfav.R;
import com.hbfav.android.core.UserInfoManager;

public class UserRegistrationActivity extends Activity {
    TextView mTextViewAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_regstrasion);

        mTextViewAccount = (TextView) findViewById(R.id.text_view_account);
        mTextViewAccount.setText(UserInfoManager.getUserName());

        findViewById(R.id.username_linear_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputUserNamePopUp();
            }
        });
    }


    private void showInputUserNamePopUp() {
        final UserRegistrationActivity activity = this;

        View promptsView = LayoutInflater.from(this).inflate(R.layout.prompt_edit_username, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.setting_account));
        alert.setView(promptsView);
        final EditText input = (EditText) promptsView.findViewById(R.id.prompt_edit_username);
        input.setText(UserInfoManager.getUserName());
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (input.getText() == null) {
                    return;
                }
                UserInfoManager.setUserName(input.getText().toString());
                mTextViewAccount.setText(UserInfoManager.getUserName());
                if (!input.getText().toString().isEmpty()) {
                    activity.finish();
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
    }
}
