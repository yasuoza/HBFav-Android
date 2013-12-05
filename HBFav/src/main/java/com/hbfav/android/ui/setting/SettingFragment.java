package com.hbfav.android.ui.setting;

import android.app.Activity;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.hbfav.BuildConfig;
import com.hbfav.R;
import com.hbfav.android.Constants;
import com.hbfav.android.core.UserInfoManager;
import com.hbfav.android.ui.MainActivity;

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
