package com.hbfav.android.ui.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hbfav.R;
import com.hbfav.android.Constants;
import com.hbfav.android.core.UserInfoManager;
import com.hbfav.android.ui.MainActivity;

public class SettingFragment extends Fragment {
    TextView mTextViewAccount;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SettingFragment newInstance(int sectionNumber) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(Constants.ARG_SECTION_NUMBER));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting_view, container, false);
        mTextViewAccount = (TextView) rootView.findViewById(R.id.text_view_account);
        mTextViewAccount.setText(UserInfoManager.getUserName());
        rootView.findViewById(R.id.username_linear_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputUserNamePopUp();
            }
        });
        return rootView;
    }

    private void showInputUserNamePopUp() {
        Context context = getActivity();
        View promptsView = LayoutInflater.from(context).inflate(R.layout.prompt_edit_username, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
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
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
    }
}
