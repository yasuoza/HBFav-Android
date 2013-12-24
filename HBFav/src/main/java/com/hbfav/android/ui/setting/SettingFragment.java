package com.hbfav.android.ui.setting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.hbfav.android.BuildConfig;
import com.hbfav.android.Constants;
import com.hbfav.android.R;
import com.hbfav.android.core.HatenaApiManager;
import com.hbfav.android.core.UserInfoManager;
import com.hbfav.android.ui.HatenaOauthActivity;
import com.hbfav.android.ui.MainActivity;

import org.scribe.model.Token;

public class SettingFragment extends PreferenceFragment {
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

        // SettingFragment will be attached to MainActivity or SettingActivity
        // Execute following code only when attached into MainActivity
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(Constants.ARG_SECTION_NUMBER));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        EditTextPreference editTextPreference = (EditTextPreference) findPreference(getString(R.string.pref_hatena_user_name));
        if (editTextPreference != null) {
            if (!UserInfoManager.getUserName().isEmpty()) {
                editTextPreference.setSummary(UserInfoManager.getUserName());
            }
            editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(newValue.toString());

                    // Close view if opened in SettingActivity
                    Activity activity = getActivity();
                    if (activity instanceof SettingActivity) {
                        activity.finish();
                    }

                    return true;
                }
            });
        }

        Preference logoutPreference = findPreference(getString(R.string.pref_logout_from_hatena_service));
        if (logoutPreference != null) {
            logoutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    (new AsyncTask<Void, Void, Void>() {
                        ProgressDialog mProgressDialog;
                        @Override
                        protected void onPreExecute() {
                            mProgressDialog = new ProgressDialog(getActivity());
                            mProgressDialog.setMessage(getString(R.string.processing));
                            mProgressDialog.setCancelable(false);
                            mProgressDialog.setIndeterminate(true);
                            mProgressDialog.show();
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            HatenaApiManager.refreshRequestToken();
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void param) {
                            mProgressDialog.dismiss();
                            Intent intent = new Intent(getActivity(), HatenaOauthActivity.class);
                            startActivity(intent);
                        }
                    }).execute();
                    return true;
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!BuildConfig.DEBUG) {
            // Google analytics
            EasyTracker tracker = EasyTracker.getInstance(getActivity());
            tracker.set(Fields.SCREEN_NAME, getString(R.string.page_setting));
            tracker.send(MapBuilder.createAppView().build());
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (!BuildConfig.DEBUG) {
            // Google analytics
            EasyTracker.getInstance(getActivity()).activityStop(getActivity());
        }
    }

}
