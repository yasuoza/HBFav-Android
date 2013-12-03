package com.hbfav.android;

import android.app.ListFragment;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.hbfav.BuildConfig;

abstract public class BaseListFragment extends ListFragment {
    abstract protected String getPageTitle();

    public void onStart() {
        super.onStart();

        if (!BuildConfig.DEBUG) {
            // Google analytics
            sendAnalyticsTracker();
        }
    }

    public void onStop() {
        super.onStop();

        if (!BuildConfig.DEBUG) {
            // Google analytics
            EasyTracker.getInstance(getActivity()).activityStop(getActivity());
        }
    }

    protected void sendAnalyticsTracker() {
        EasyTracker tracker = EasyTracker.getInstance(getActivity());
        tracker.set(Fields.SCREEN_NAME, getPageTitle());
        tracker.send(MapBuilder.createAppView().build());
    }
}
